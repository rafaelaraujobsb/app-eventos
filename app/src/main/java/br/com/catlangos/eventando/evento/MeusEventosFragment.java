package br.com.catlangos.eventando.evento;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.com.catlangos.eventando.R;
import br.com.catlangos.eventando.fragmentos.MeuAdaptador;
import br.com.catlangos.eventando.fragmentos.MeuViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class MeusEventosFragment extends Fragment implements MeuViewHolder.OnEventoClickListener {

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private Query query;
    private Query query2;
    private DatabaseReference reference2;
    List<Evento> eventos = new ArrayList<>();
    MeuViewHolder.OnEventoClickListener ctx;
    private String idUsuario;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        reference2 = database.getReference();
        query = reference.child("Usuarios").orderByChild("email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        return inflater.inflate(R.layout.fragmento_meus_eventos, container, false);
    }

    public static <T extends List<?>> T cast(Object obj) {
        return (T) obj;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ctx = this;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot issue : dataSnapshot.getChildren()) {
                        idUsuario = issue.getKey();
                    }
                }

                query2 = reference2.child("Eventos").orderByChild("participantes").equalTo(idUsuario);
                query2.addListenerForSingleValueEvent(new ValueEventListener() {
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
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onEventoClick(int position) {
        Evento evento = eventos.get(position);
        Intent intent = new Intent(requireActivity(), VisualizarEventoActivity.class);
        intent.putExtra("evento", evento);
        startActivity(intent);
    }
}
