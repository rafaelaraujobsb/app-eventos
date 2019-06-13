package br.com.catlangos.eventando.fragmentos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import br.com.catlangos.eventando.R;
import br.com.catlangos.eventando.evento.Evento;

import java.util.HashMap;
import java.util.List;

public class MeuAdaptador extends RecyclerView.Adapter<MeuViewHolder> {

    private List<Evento> eventos;
    private HashMap<Integer, Evento> mapEvento = new HashMap<>();
    private MeuViewHolder.OnEventoClickListener onEventoClickListener;

    public MeuAdaptador(List<Evento> eventos, MeuViewHolder.OnEventoClickListener onEventoClickListener) {
        this.eventos = eventos;
        this.onEventoClickListener = onEventoClickListener;
    }

    @Override
    public MeuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.meus_eventos, parent,false);

        MeuViewHolder viewHolder = new MeuViewHolder(v, onEventoClickListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MeuViewHolder viewHolder, int position) {
        Evento evento = eventos.get(position);
        viewHolder.txtNomeEvento.setText(evento.getNome());
        viewHolder.dataEvento.setText(evento.getDataInicio());
        viewHolder.categoriaEvento.setText(evento.getCategoria());
        mapEvento.put(position, evento);
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }
}
