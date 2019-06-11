package br.com.catlangos.eventando.evento;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.catlangos.eventando.Mapas.MapsBuscarEvento;
import br.com.catlangos.eventando.R;
import br.com.catlangos.eventando.login.CadastroActivity;
import br.com.catlangos.eventando.utils.Utils;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

import static br.com.catlangos.eventando.Mapas.MapsBuscarEvento.*;

public class BuscarEventoFragment extends Fragment {

    private List<String> categorias = new ArrayList<>();
    private String categoriaSelecionada;
    private DatabaseReference reference;
    private Button btnTodos;
    private Button btnCategoria;
    private Button btnNome;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_buscar_evento, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


//        AlertDialog.Builder builder = new AlertDialog.Builder(BuscarEventoFragment.this);
//        builder.setTitle("Categorias de Interesse");
//        builder.setMultiChoiceItems(categorias, interessesSelecionados, new DialogInterface.OnMultiChoiceClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int position, boolean isChecked) {
//                if(isChecked) {
//                    if(!userItems.contains(position)) {
//                        userItems.add(position);
//                    } else {
//                        userItems.remove(position);
//                    }
//                }
//            }
//        });
//        builder.setCancelable(false);
//        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int witch) {
//                String item = "";
//                for(Integer i = 0; i < userItems.size(); i++) {
//                    item = item + interessesSelecionados[userItems.get(i)];
//                    if(i != userItems.size() - 1) {
//                        item = item + ", ";
//                    }
//                }
//            }
//        });
//        builder.setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//        builder.setNeutralButton("Limpar", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                for(Integer i = 0; i < interessesSelecionados.length; i++) {
//                    interessesSelecionados[i] = false;
//                    userItems.clear();
//                }
//            }
//        });
//
//        AlertDialog mDialog = builder.create();
//        mDialog.show();

        buscarCategorias();

        configurarBotoes(view);
    }

    private void configurarBotoes(final View view) {
        btnTodos = view.findViewById(R.id.btnTodos);
        btnCategoria = view.findViewById(R.id.btnCategoria);
        btnNome = view.findViewById(R.id.btnNome);

        btnTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), MapsBuscarEvento.class);
                intent.putExtra(CODIGO_DE_BUSCA, TODOS);
                startActivity(intent);
            }
        });

        btnCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext());
                View mView = getLayoutInflater().inflate(R.layout.spinner_dialog, null);
                alertDialog.setTitle("Selecione a categoria");

                final Spinner spinner = mView.findViewById(R.id.spinnerDoDialog);

                final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(requireActivity(), R.layout.spinner_item,categorias){
                    @Override
                    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if(position == 0){
                            tv.setTextColor(Color.GRAY);
                        }else{
                            tv.setTextColor(Color.BLACK);
                        }
                        return view;
                    }
                };


                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //String selectedItemText = (String) parent.getItemAtPosition(position);
                        if(position!=0){
                            TextView tv = (TextView) view;
                            tv.setTextColor(Color.BLACK);
                            categoriaSelecionada = spinner.getSelectedItem().toString();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dialog);
                spinner.setAdapter(spinnerArrayAdapter);
                alertDialog.show();
//                Intent intent = new Intent(requireActivity(), MapsBuscarEvento.class);
//                intent.putExtra(CODIGO_DE_BUSCA, CATEGORIA);
//                startActivity(intent);
            }
        });
    }

    private void buscarCategorias(){
        reference = FirebaseDatabase.getInstance().getReference("/categorias");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                categorias.clear();
                //dataSnapShot contem os dados no local especificado pela referencia
                for (DataSnapshot ref : dataSnapshot.getChildren()) {
                    if(!Utils.Companion.isNull(ref)){
                        categorias.add(ref.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}
