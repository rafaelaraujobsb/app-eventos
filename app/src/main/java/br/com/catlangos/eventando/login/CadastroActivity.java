package br.com.catlangos.eventando.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import br.com.catlangos.eventando.R;
import br.com.catlangos.eventando.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

public class CadastroActivity extends AppCompatActivity {

    private Perfil perfil;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        txtNome = findViewById(R.id.txtNome);
        txtEmail = findViewById(R.id.txtEmail);
        txtConfirmarEmail = findViewById(R.id.txtConfirmarEmail);
        numCpf = findViewById(R.id.numCpf);
        dateDataNascimento = findViewById(R.id.dateDataNascimento);
        txtSenha = findViewById(R.id.txtSenha);
        txtConfirmarSenha = findViewById(R.id.txtConfirmarSenha);
        btnCadastrar = findViewById(R.id.btnCadastrar);

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
                        cadastrar();
                    }else{
                        Toast.makeText(CadastroActivity.this, "Os emails devem ser iguais!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(CadastroActivity.this, "As senhas devem ser iguais!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void cadastrar(){
        auth.createUserWithEmailAndPassword(
                perfil.getEmail(),
                perfil.getSenha()
        ).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //TODO Validar todas possibilidades de erro no cadastro
                        if(task.isSuccessful()){
                            insereUsuario(perfil);
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

    private void insereUsuario(Perfil p){
        try {
            reference = FirebaseDatabase.getInstance().getReference().child("Usuarios");
            reference.push().setValue(p);
            Toast.makeText(CadastroActivity.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_LONG).show();

        }catch (Exception e){
            Toast.makeText(CadastroActivity.this, "Erro ao gravar usuário!", Toast.LENGTH_LONG).show();
        }
    }
}
