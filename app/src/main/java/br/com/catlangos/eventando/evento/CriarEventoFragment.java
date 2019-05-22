package br.com.catlangos.eventando.evento;

import android.content.Intent;
import android.graphics.Color;
import android.widget.*;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import br.com.catlangos.eventando.Mapa;
import br.com.catlangos.eventando.R;
import br.com.catlangos.eventando.utils.Utils;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class CriarEventoFragment extends Fragment {

    private Button button;
    private List<String> categorias = new ArrayList<>();

    public CriarEventoFragment(){
        // Required empty public constructor
    }

    public static CriarEventoFragment newInstance() {
        return new CriarEventoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.criar_evento_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button = view.findViewById(R.id.btnLocal);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Mapa.class);
                startActivity(intent);
                //Navigation.findNavController(v).navigate(R.id.action_mapa);
            }
        });

        FirebaseDatabase fireBaseDatabase =  FirebaseDatabase.getInstance();

        //Localiza o nó no banco de dados do firebase e guarda sua referência
        DatabaseReference dataBase = fireBaseDatabase.getReference("/categorias");

        dataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
            public void onCancelled(DatabaseError databaseError) { }
        });
    }


    private void updateCategoria(View view) {
        Spinner spinner = view.findViewById(R.id.spnCategoria);
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, categorias){

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position==0){
                    tv.setTextColor(Color.GRAY);
                }else{
                    tv.setTextColor(Color.BLACK);
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
                    tv.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
