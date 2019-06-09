package br.com.catlangos.eventando.evento;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.catlangos.eventando.Mapas.MapsBuscarEvento;
import br.com.catlangos.eventando.R;

public class BuscarEventoFragment extends Fragment {

    Button btnTodos;
    Button btnCategoria;
    Button btnNome;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_buscar_evento, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configurarBotoes(view);
    }

    private void configurarBotoes(View view) {
        btnTodos = view.findViewById(R.id.btnTodos);
        btnCategoria = view.findViewById(R.id.btnCategoria);
        btnNome = view.findViewById(R.id.btnNome);

        btnTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), MapsBuscarEvento.class);
                startActivity(intent);
            }
        });
    }
}
