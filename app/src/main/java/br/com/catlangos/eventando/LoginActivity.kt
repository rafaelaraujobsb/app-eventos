package br.com.catlangos.eventando

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    var autenticacao = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //TODO transformar para orientado a objeto
        var txtEmail = findViewById<EditText>(R.id.txtEmail)
        var txtSenha = findViewById<EditText>(R.id.txtSenha)
        var btnEntrar = findViewById<Button>(R.id.btnEntrar);
        var txtResetarSenha = findViewById<TextView>(R.id.txtResetarSenha)

        btnEntrar?.setOnClickListener { view -> eventoLogin(view, txtEmail.text.toString(), txtSenha.text.toString()) }
        //TODO criar resete de senha
        //txtResetarSenha?.setOnClickListener { view -> resetarSenha(view, ) }
    }

    fun eventoLogin(view: View, txtEmail: String, txtSenha: String) {
        apresentarMensagem(view, "Autenticando...")

        //TODO Implementar validação do formulário
        autenticacao.signInWithEmailAndPassword(txtEmail, txtSenha).addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
            if(task.isSuccessful) {
                var user = autenticacao.currentUser
                apresentarMensagem(view, "Autenticado com Sucesso!")
                //intent = Intent(LoginActivity, colocarAquiActivity.class)
                //startActivity(intent)
            } else {
                apresentarMensagem(view, "Ops... Falha ao autenticar!")
            }
        })
    }

    fun apresentarMensagem(view:View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).setAction("Action", null).show()
    }
}
