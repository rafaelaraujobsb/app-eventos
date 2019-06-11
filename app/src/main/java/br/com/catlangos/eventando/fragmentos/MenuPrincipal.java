package br.com.catlangos.eventando.fragmentos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.com.catlangos.eventando.R;
import br.com.catlangos.eventando.evento.CriarEventoFragment;
import br.com.catlangos.eventando.evento.Evento;
import br.com.catlangos.eventando.evento.VisualizarEventoActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class MenuPrincipal extends Fragment implements MeuViewHolder.OnEventoClickListener {

    private FirebaseDatabase database;
    private DatabaseReference reference;
    List<Evento> eventos = new ArrayList<>();
    private FloatingActionButton btnCadastrarEvento;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("/Eventos");


        return inflater.inflate(R.layout.fragment_principal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final MeuViewHolder.OnEventoClickListener ctx = this;

        btnCadastrarEvento = view.findViewById(R.id.btnCadastrarEvento);

        btnCadastrarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fr = new CriarEventoFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fr);
                fragmentTransaction.commit();
            }
        });

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ref : dataSnapshot.getChildren()){
                    Evento evento = ref.getValue(Evento.class);
                    if(evento != null){
                        eventos.add(evento);
                    }
                }
                RecyclerView recyclerView = requireActivity().findViewById(R.id.recyclerViewMenuPrincipal);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(new MeuAdaptador(eventos, ctx));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onEventoClick(int position) {
        Evento evento = eventos.get(position);
        Intent intent = new Intent(requireActivity(), VisualizarEventoActivity.class);
        intent.putExtra("evento", evento);
        startActivity(intent);
    }
}
