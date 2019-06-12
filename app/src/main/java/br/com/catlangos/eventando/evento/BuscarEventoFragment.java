package br.com.catlangos.eventando.evento;

import android.content.DialogInterface;
import android.content.Intent;
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
    private Button btnAcontecendoAgora;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_buscar_evento, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buscarCategorias();
        configurarBotoes(view);
    }

    private void configurarBotoes(final View view) {
        btnTodos = view.findViewById(R.id.btnTodos);
        btnCategoria = view.findViewById(R.id.btnCategoria);
        btnAcontecendoAgora = view.findViewById(R.id.btnAcontecendoAgora);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Selecione a categoria");

                final String[] categoriasArray = categorias.toArray(new String[categorias.size()]);
                builder.setItems(categoriasArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            Intent intent = new Intent(requireActivity(), MapsBuscarEvento.class);
                            intent.putExtra(CODIGO_DE_BUSCA, CATEGORIA);
                            intent.putExtra(CATEGORIA_SELECIONADA, categoriasArray[which]);
                            startActivity(intent);
                        }catch (Exception e){
                            Toast.makeText(requireContext(), "Erro ao executar transação", Toast.LENGTH_LONG).show();
                        }

                    }
                });
                builder.show();
            }
        });

        btnAcontecendoAgora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), MapsBuscarEvento.class);
                intent.putExtra(CODIGO_DE_BUSCA, ACONTECENDO_AGORA);
                startActivity(intent);
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
