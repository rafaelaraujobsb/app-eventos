package br.com.catlangos.eventando.login;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import br.com.catlangos.eventando.R;
import br.com.catlangos.eventando.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CadastroActivity extends AppCompatActivity {

    private Perfil perfil;
    private Interesses interesses;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference reference = db.getReference();

    EditText txtNome;
    EditText txtEmail;
    EditText txtConfirmarEmail;
    EditText numCpf;
    EditText dateDataNascimento;
    EditText txtSenha;
    EditText txtConfirmarSenha;
    Button btnCadastrar;
    Button btnInteresses;
    String[] listaInteresses;
    boolean[] interessesSelecionados;
    ArrayList<Integer> userItems = new ArrayList<>();
    ArrayList<Interesses> lstInteresses = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        //Localiza o nó no banco de dados do firebase e guarda sua referência
        reference = db.getReference("depara/categorias");

        txtNome = findViewById(R.id.txtNome);
        txtEmail = findViewById(R.id.txtEmail);
        txtConfirmarEmail = findViewById(R.id.txtDescricao);
        numCpf = findViewById(R.id.numCpf);
        dateDataNascimento = findViewById(R.id.dateDataNascimento);
        txtSenha = findViewById(R.id.txtSenha);
        txtConfirmarSenha = findViewById(R.id.txtConfirmarSenha);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        btnInteresses = findViewById(R.id.btnInteresses);

        // Recuperar a lista de interesses
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer index = 0;
                ArrayList<String> lst = new ArrayList<>();
                //dataSnapShot contem os dados no local especificado pela referencia
                for (DataSnapshot ref : dataSnapshot.getChildren()) {
                    if(!Utils.Companion.isNull(ref)){
                        interesses = ref.getValue(Interesses.class);
                        lst.add(interesses.getNome());
                        lstInteresses.add(interesses);
                        index++;
                    }
                }
                listaInteresses = new String[lst.size()];
                lst.toArray(listaInteresses);
                interessesSelecionados = new boolean[listaInteresses.length];
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        btnInteresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CadastroActivity.this);
                builder.setTitle("Categorias de Interesse");
                builder.setMultiChoiceItems(listaInteresses, interessesSelecionados, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                        if(isChecked) {
                            if(!userItems.contains(position)) {
                                userItems.add(position);
                            } else {
                                userItems.remove(position);
                            }
                        }
                    }
                });
                builder.setCancelable(false);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int witch) {
                        String item = "";
                        for(Integer i = 0; i < userItems.size(); i++) {
                            item = item + interessesSelecionados[userItems.get(i)];
                            if(i != userItems.size() - 1) {
                                item = item + ", ";
                            }
                        }
                    }
                });
                builder.setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setNeutralButton("Limpar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(Integer i = 0; i < interessesSelecionados.length; i++) {
                            interessesSelecionados[i] = false;
                            userItems.clear();
                        }
                    }
                });

                AlertDialog mDialog = builder.create();
                mDialog.show();
            }
        });

        //TODO Validar todas possibilidades de erro no cadastro
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.Companion.isEqualsEditText(txtSenha, txtConfirmarSenha)){
                    if(Utils.Companion.isEqualsEditText(txtEmail, txtConfirmarEmail)){
                        perfil = new Perfil();
                        perfil.setEmail(Utils.Companion.editTextToString(txtEmail));
                        perfil.setSenha(Utils.Companion.editTextToString(txtSenha));
                        perfil.setNome(Utils.Companion.editTextToString(txtNome));
                        perfil.setCpf(Utils.Companion.editTextToString(numCpf));
                        perfil.setDataNascimento(Utils.Companion.editTextToString(dateDataNascimento));
                        cadastrar(v);
                    }else{
                        Toast.makeText(CadastroActivity.this, "Os emails devem ser iguais!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(CadastroActivity.this, "As senhas devem ser iguais!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void cadastrar(View view){
        final View v = view;
        auth.createUserWithEmailAndPassword(
                perfil.getEmail(),
                perfil.getSenha()
        ).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //TODO Validar todas possibilidades de erro no cadastro
                        if(task.isSuccessful()){
                            String chaveUsuario = insereUsuario(perfil);
                            ArrayList<Interesses> interessesSelecionados = new ArrayList<>();
                            for(Integer i = 0; i < userItems.size(); i++) {
                                interessesSelecionados.add(lstInteresses.get(userItems.get(i)));
                            }

                            String url = "http://35.199.125.165:1346/api/sugestao-grupo";
                            ServicoTask servicoTask = new ServicoTask(CadastroActivity.this, url, v, chaveUsuario, interessesSelecionados);
                            servicoTask.execute();
                        }else{
                            String erroExcecao = "";
                            try{
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){
                                erroExcecao = "Digite uma senha mais forte";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                erroExcecao = "O email digitado é invalido";
                            }catch (FirebaseAuthUserCollisionException e){
                                erroExcecao = "Email já cadastrado!";
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(CadastroActivity.this, erroExcecao, Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    private String insereUsuario(Perfil p){
        try {
            reference = FirebaseDatabase.getInstance().getReference().child("Usuarios");
            String chave = reference.push().getKey();
            reference.child(chave).setValue(p);
            Toast.makeText(CadastroActivity.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_LONG).show();
            return chave;
        }catch (Exception e){
            Toast.makeText(CadastroActivity.this, "Erro ao gravar usuário!", Toast.LENGTH_LONG).show();
        }
        return  null;
    }
}
