package br.com.catlangos.eventando.evento;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import br.com.catlangos.eventando.mapas.MapaCriarEvento;
import br.com.catlangos.eventando.R;
import br.com.catlangos.eventando.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class CriarEventoFragment extends Fragment {

    private FirebaseDatabase fireBaseDatabase;
    private DatabaseReference dataBase;
    private TextView txthorarioInicio;
    private TextView txthorarioTermino;
    private TextView txtDataInicio;
    private TextView txtDataTermino;
    private Button btnAdicionarLocal;
    private Button btnCriarEvento;
    private EditText nome;
    private EditText descricao;
    private EditText cep;
    private EditText estado;
    private EditText cidade;
    private EditText bairro;
    private EditText rua;
    private EditText complemento;
    private Double latitude;
    private Double longitude;
    private Spinner spinner;
    private String categoria;
    private int REQUEST_CODE = 1;
    private List<String> categorias = new ArrayList<>();
    private TimePickerDialog timePickerDialog;
    private DatePickerDialog datePickerDialog;
    private String dataInicioEnvio;
    private String dataTerminoEnvio;
    private String horarioInicioEnvio;
    private String horarioTerminoEnvio;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.criar_evento_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.mainLayout).requestFocus();
        fireBaseDatabase = FirebaseDatabase.getInstance();
        //Localiza o nó no banco de dados do firebase e guarda sua referência
        dataBase = fireBaseDatabase.getReference("/categorias");

        nome = view.findViewById(R.id.txtNome);
        estado = view.findViewById(R.id.txtEstado);
        cep = view.findViewById(R.id.txtCep);
        cidade = view.findViewById(R.id.txtCidade);
        bairro = view.findViewById(R.id.txtBairro);
        rua = view.findViewById(R.id.txtRua);
        complemento = view.findViewById(R.id.txtComplemento);
        btnCriarEvento = view.findViewById(R.id.btnCriar);
        btnAdicionarLocal = view.findViewById(R.id.btnLocal);
        descricao = view.findViewById(R.id.txtDescricao);
        txthorarioInicio = view.findViewById(R.id.txtHorarioInicio);
        txthorarioTermino = view.findViewById(R.id.txtHorarioTermino);
        txtDataInicio = view.findViewById(R.id.txtDataInicio);
        txtDataTermino = view.findViewById(R.id.txtDataTermino);

        configurarCalendario();
        configurarTxtHorarios();
        configurarBtnCriarEvento();
        configurarBtnAdicionarLocal(view);



        dataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categorias.clear();

                //dataSnapShot contem os dados no local especificado pela referencia
                for (DataSnapshot ref : dataSnapshot.getChildren()) {
                    if(!Utils.Companion.isNull(ref)){
                        categorias.add(ref.getValue().toString());
                    }
                }
                updateCategoria(view);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }


    private void updateCategoria(View view) {
        spinner = view.findViewById(R.id.spnCategoria);
        if(!spinner.isAttachedToWindow()){
            return;
        }
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(requireActivity(), R.layout.spinner_item, categorias){

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position==0){
                    tv.setTextColor(Color.GRAY);
                }else{
                    tv.setTextColor(Color.WHITE);
                }
                return view;
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String selectedItemText = (String) parent.getItemAtPosition(position);
                if(position!=0){
                    TextView tv = (TextView) view;
                    tv.setTextColor(Color.GRAY);
                    categoria = spinner.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void configurarBtnAdicionarLocal(View view){
        btnAdicionarLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapaCriarEvento.class);
                intent.putExtra(MapaCriarEvento.TIPO, MapaCriarEvento.CRIAR);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    private void configurarBtnCriarEvento(){
        btnCriarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(latitude == null || longitude==null){
                    Toast.makeText(requireContext(), "É necessário selecionar um local no mapa!", Toast.LENGTH_LONG).show();
                    return;
                }
                Evento evento = preencherEvento();
                cadastrar(evento);
            }
        });
    }

    private void cadastrar(Evento evento) {
        try {
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Eventos");
            final String chave = reference.push().getKey();
            evento.setId(chave);
            reference.child(chave).setValue(evento).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    final DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("evento_user");
                    EventoUser eventoUser = new EventoUser();
                    eventoUser.setUsuario(FirebaseAuth.getInstance().getUid());
                    eventoUser.setEvento(chave);
                    reference2.push().setValue(eventoUser);
                }
            });
            Toast.makeText(requireContext(), "Evento criado com sucesso!", Toast.LENGTH_LONG).show();
            getActivity().finish();
        }catch (Exception e){
            Toast.makeText(requireContext(), "Erro ao criar evento, tente novamente mais tarde!", Toast.LENGTH_LONG).show();
        }
    }

    private Evento preencherEvento() {
        Evento evento = new Evento();
        evento.setBairro(Utils.Companion.editTextToString(bairro));
        evento.setCep(Utils.Companion.editTextToString(cep));
        evento.setComplemento(Utils.Companion.editTextToString(complemento));
        evento.setCidade(Utils.Companion.editTextToString(cidade));
        evento.setEstado(Utils.Companion.editTextToString(estado));
        evento.setCategoria(categoria);
        evento.setDescricao(Utils.Companion.editTextToString(descricao));
        evento.setNome(Utils.Companion.editTextToString(nome));
        evento.setRua(Utils.Companion.editTextToString(rua));
        evento.setLatitude(latitude);
        evento.setLongitude(longitude);
        evento.setDataInicio(dataInicioEnvio);
        evento.setDataTermino(dataTerminoEnvio);

        if(horarioInicioEnvio.equals("") || horarioInicioEnvio==null){
            evento.setHorarioInicio("");
        }else{
            evento.setHorarioInicio(horarioInicioEnvio);
        }

        if(horarioTerminoEnvio.equals("") || horarioTerminoEnvio==null){
            evento.setHorarioTermino("");
        }else{
            evento.setHorarioTermino(horarioTerminoEnvio);
        }

        if(dataInicioEnvio.equals("") || dataInicioEnvio==null){
            evento.setDataInicio("");
        }else{
            evento.setDataInicio(dataInicioEnvio);
        }

        if(dataTerminoEnvio.equals("") || dataTerminoEnvio==null){
            evento.setDataTermino("");
        }else{
            evento.setDataTermino(dataTerminoEnvio);
        }
        return evento;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            if(resultCode == RESULT_OK){
                try{
                    //TODO MELHORAR ARMAZENAMENTO DE LOCALIZACAO
                    latitude = (Double) data.getExtras().get(MapaCriarEvento.LATITUDE);
                    longitude = (Double) data.getExtras().get(MapaCriarEvento.LONGITUDE);

                    Geocoder geocoder = new Geocoder(requireContext());
                    List<Address> enderecos = geocoder.getFromLocation(latitude, longitude, 1);
                    Address endereco = enderecos.get(0);

                    cep.setText("");
                    estado.setText("");
                    cidade.setText("");
                    bairro.setText("");

                    if(!Utils.Companion.isNull(endereco.getPostalCode())){
                        cep.setText(endereco.getPostalCode());
                    }
                    if(!Utils.Companion.isNull(endereco.getAdminArea())){
                        estado.setText(endereco.getAdminArea());
                    }
                    if(!Utils.Companion.isNull(endereco.getSubAdminArea())){
                        cidade.setText(endereco.getSubAdminArea());
                    }
                    if(!Utils.Companion.isNull(endereco.getSubLocality())){
                        bairro.setText(endereco.getSubLocality());
                    }
                }catch(Exception e){

                }
            }
        }
    }

    private void configurarTxtHorarios(){
        horarioInicioEnvio = "";
        horarioTerminoEnvio= "";
        txthorarioInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog = new TimePickerDialog(requireContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                        String horarioInicio = formatter.format(calendar.getTime());
                        //String[] horaMinuto =  horarioInicio.split(":");

                        txthorarioInicio.setText(getString(R.string.horarioInicio) + " " + horarioInicio);
                        horarioInicioEnvio = horarioInicio;
                    }
                }, 0, 0, true);
                timePickerDialog.show();
            }
        });

        txthorarioTermino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog = new TimePickerDialog(requireContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                        String horarioTermino = formatter.format(calendar.getTime());
                        //String[] horaMinuto =  horarioTermino.split(":");

                        txthorarioTermino.setText(getString(R.string.horarioTermino) + " " + horarioTermino);
                        horarioTerminoEnvio= horarioTermino;
                    }
                }, 0, 0, true);
                timePickerDialog.show();
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void configurarCalendario(){
        dataInicioEnvio = "";
        dataTerminoEnvio = "";
        txtDataInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendarAux = Calendar.getInstance();
                datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.YEAR, year);
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        dataInicioEnvio = formatter.format(calendar.getTime());
                        txtDataInicio.setText(getString(R.string.dataInicio) + " " + dataInicioEnvio);
                    }
                }, calendarAux.get(Calendar.YEAR), calendarAux.get(Calendar.MONTH), calendarAux.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        txtDataTermino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendarAux = Calendar.getInstance();
                datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.YEAR, year);
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        dataTerminoEnvio = formatter.format(calendar.getTime());
                        txtDataTermino.setText(getString(R.string.dataTermino) + " " + dataTerminoEnvio);
                    }
                }, calendarAux.get(Calendar.YEAR), calendarAux.get(Calendar.MONTH), calendarAux.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

    }
}
