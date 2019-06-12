package br.com.catlangos.eventando.fragmentos;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import br.com.catlangos.eventando.R;

public class MeuViewHolder extends RecyclerView.ViewHolder {
    public TextView txtNomeEvento;

    public MeuViewHolder(View view) {
        super(view);
        txtNomeEvento = view.findViewById(R.id.txtNomeEvento);
    }
}
