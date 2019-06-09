package br.com.catlangos.eventando.fragmentos;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import br.com.catlangos.eventando.R;

public class MeuViewHolder extends RecyclerView.ViewHolder {
    public TextView txtNomeEvento;
    public TextView idEvento;
    public TextView categoriaEvento;
    public TextView dataEvento;
    public TextView horaEvento;

    public MeuViewHolder(View view) {
        super(view);
        txtNomeEvento = view.findViewById(R.id.txtNomeEvento);
        //idEvento = view.findViewById(R.id.txtIdEvento);
        categoriaEvento = view.findViewById(R.id.txtCategoriaEvento);
        dataEvento = view.findViewById(R.id.txtDataEvento);
        //horaEvento = view.findViewById(R.id.txtHoraEvento);
    }

    public TextView getTxtNomeEvento() {
        return txtNomeEvento;
    }

    public void setTxtNomeEvento(TextView txtNomeEvento) {
        this.txtNomeEvento = txtNomeEvento;
    }

    public TextView getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(TextView idEvento) {
        this.idEvento = idEvento;
    }

    public TextView getCategoriaEvento() {
        return categoriaEvento;
    }

    public void setCategoriaEvento(TextView categoriaEvento) {
        this.categoriaEvento = categoriaEvento;
    }

    public TextView getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(TextView dataEvento) {
        this.dataEvento = dataEvento;
    }

    public TextView getHoraEvento() {
        return horaEvento;
    }

    public void setHoraEvento(TextView horaEvento) {
        this.horaEvento = horaEvento;
    }
}
