package br.com.catlangos.eventando.evento;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import br.com.catlangos.eventando.R;
import br.com.catlangos.eventando.utils.AlertType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.io.Serializable;
import java.util.*;

public class VisualizarEventoActivity extends AppCompatActivity implements Serializable {

    Button btnParticiparEvento;
    Button btnDeixarFeedback;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private DatabaseReference reference2;
    private DatabaseReference reference3;
    private Query query;
    private Query eventoQuery;
    private Query feedbackQuery;
    private String chaveUsuario;
    private String email;
    private List<String> usuarios = new ArrayList<>();
    private Evento evento;
    private String chaveEvento;
    TextView nome;
    TextView cep;
    TextView estado;
    TextView cidade;
    TextView bairro;
    TextView rua;
    TextView complemento;
    TextView descricao;
    TextView categoria;
    TextView dataInicio;
    TextView dataTermino;
    TextView horarioInicio;
    TextView horarioTermino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_evento);

        evento = (Evento) getIntent().getSerializableExtra(Evento.EVENTO);

        nome = findViewById(R.id.txtNome);
        cep = findViewById(R.id.txtCep);
        estado = findViewById(R.id.txtEstado);
        cidade = findViewById(R.id.txtCidade);
        bairro = findViewById(R.id.txtBairro);
        rua = findViewById(R.id.txtRua);
        complemento = findViewById(R.id.txtComplemento);
        descricao = findViewById(R.id.txtDescricao);
        categoria = findViewById(R.id.txtCategoria);
        dataInicio = findViewById(R.id.txtDataInicio);
        dataTermino = findViewById(R.id.txtDataTermino);
        horarioInicio = findViewById(R.id.txtHorarioInicio);
        horarioTermino = findViewById(R.id.txtHorarioTermino);
        btnParticiparEvento = findViewById(R.id.btnParticiparEvento);
        btnDeixarFeedback = findViewById(R.id.btnFeedback);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        reference2 = database.getReference();
        query = reference.child("Usuarios").orderByChild("email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        nome.setText(evento.getNome());
        cep.setText(evento.getCep());
        estado.setText(evento.getEstado());
        cidade.setText(evento.getCidade());
        bairro.setText(evento.getBairro());
        rua.setText(evento.getRua());
        complemento.setText(evento.getComplemento());
        descricao.setText(evento.getDescricao());
        categoria.setText(evento.getCategoria());
        dataInicio.setText(evento.getDataInicio());
        dataTermino.setText(evento.getDataTermino());
        horarioInicio.setText(evento.getHorarioInicio());
        horarioTermino.setText(evento.getHorarioTermino());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        chaveUsuario = issue.getKey();
                        email = issue.child("email").getValue().toString();
                    }
                }

                eventoQuery = reference2.child("Eventos").orderByChild("nome").equalTo(evento.getNome());
                eventoQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                chaveEvento = issue.getKey();
                                if(issue.child("participantes").getValue() != null) {
                                    try {
                                        List<String> lst = cast(issue.child("participantes").getValue());
                                        for(int i = 0; i < lst.size(); i++) {
                                            usuarios.add(lst.get(i));
                                        }
                                    } catch (ClassCastException e) {
                                        usuarios.add(issue.child("participantes").getValue().toString());
                                    }
                                }
                            }
                        }

                        if(usuarios.contains(chaveUsuario)) {
                            btnParticiparEvento.setText("Sair do Evento");
                        }
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

        btnParticiparEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventoQuery = reference2.child("Eventos").orderByChild("nome").equalTo(evento.getNome());
                eventoQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot issue : dataSnapshot.getChildren()) {
                            if(usuarios.contains(chaveUsuario)) {
                                for(int i = 0; i < usuarios.size(); i++) {
                                    if(usuarios.get(i).equals(chaveUsuario)) {
                                        usuarios.remove(i);
                                        btnParticiparEvento.setText("Participar do Evento");
                                        break;
                                    }
                                }
                            } else {
                                usuarios.add(chaveUsuario);
                                btnParticiparEvento.setText("Sair do Evento");
                            }

                            for(int i = 0; i < usuarios.size(); i++) {
                                Map<String, Object> map = new HashMap<>();
                                map.put(String.valueOf(i), usuarios.get(i));
                                issue.getRef().child("participantes").updateChildren(map);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        final EditText editText = new EditText(this);
        btnDeixarFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog("Deixe seu comentário abaixo:", VisualizarEventoActivity.this, editText, null, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showSimpleDialog(editText.getText().toString(),VisualizarEventoActivity.this, null);
                    }
                });
            }
        });
    }
    public static <T extends List<?>> T cast(Object obj) {
        return (T) obj;
    }

    public static void showSimpleDialog(String message, Context context, AlertType alertType) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(message);
        alertDialog.setNeutralButton("OK", null);
        if (alertType == null) {
            alertDialog.setIcon(AlertType.INFO.getDrawable());
            alertDialog.setTitle(AlertType.INFO.getTitle());
        } else {
            alertDialog.setIcon(alertType.getDrawable());
            alertDialog.setTitle(alertType.getTitle());
        }
        alertDialog.show();
    }

    public static void showInputDialog(String message, final Context context, EditText edt, AlertType alertType, DialogInterface.OnClickListener okClick) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(message);
        alertDialog.setView(edt);
        alertDialog.setPositiveButton("Ok", okClick);
        if (alertType == null) {
            alertDialog.setIcon(AlertType.INFO.getDrawable());
            alertDialog.setTitle(AlertType.INFO.getTitle());
        } else {
            alertDialog.setIcon(alertType.getDrawable());
            alertDialog.setTitle(alertType.getTitle());
        }
        alertDialog.show();
    }
}
