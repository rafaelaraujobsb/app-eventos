package br.com.catlangos.eventando.fragmentos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import br.com.catlangos.eventando.R;
import br.com.catlangos.eventando.evento.Evento;

import java.util.List;

public class MeuAdaptador extends RecyclerView.Adapter<MeuViewHolder> {

    private List<Evento> eventos;

    public MeuAdaptador(List<Evento> eventos) {
        this.eventos = eventos;
    }

    @Override
    public MeuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.meus_eventos, parent,false);

        MeuViewHolder viewHolder = new MeuViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MeuViewHolder viewHolder, int position) {
        Evento evento = eventos.get(position);
        viewHolder.txtNomeEvento.setText(evento.getNome());
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }
}
