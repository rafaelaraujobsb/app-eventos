package br.com.catlangos.eventando.evento;

import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import br.com.catlangos.eventando.R;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VisualizarEventoActivity extends AppCompatActivity implements Serializable {

    TextView nome;
    TextView cep;
    TextView estado;
    TextView cidade;
    TextView bairro;
    TextView rua;
    TextView complemento;
    TextView descricao;
    TextView categoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_evento);

        Evento evento = (Evento) getIntent().getSerializableExtra(Evento.EVENTO);

        nome = findViewById(R.id.txtNome);
        cep = findViewById(R.id.txtCep);
        estado = findViewById(R.id.txtEstado);
        cidade = findViewById(R.id.txtCidade);
        bairro = findViewById(R.id.txtBairro);
        rua = findViewById(R.id.txtRua);
        complemento = findViewById(R.id.txtComplemento);
        descricao = findViewById(R.id.txtDescricao);
        categoria = findViewById(R.id.txtCategoria);

        nome.setText(evento.getNome());
        cep.setText(evento.getCep());
        estado.setText(evento.getEstado());
        cidade.setText(evento.getCidade());
        bairro.setText(evento.getBairro());
        rua.setText(evento.getRua());
        complemento.setText(evento.getComplemento());
        descricao.setText(evento.getDescricao());
        categoria.setText(evento.getCategoria());

    }
}
