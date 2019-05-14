package br.com.catlangos.eventando.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.com.catlangos.eventando.R
import br.com.catlangos.eventando.home.HomeActivity
import br.com.catlangos.eventando.utils.Utils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity() {

    private var autenticacao = FirebaseAuth.getInstance()
    private val utils = Utils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnEntrar?.setOnClickListener { view -> eventoLogin(view) }
        txtResetarSenha?.setOnClickListener { resetarSenha() }
    }

    private fun eventoLogin(view: View) {
        //TODO Mensagem mais amigavel com interface melhor
        utils.apresentarMensagem(view, "Autenticando...")

        if(!utils.isNull(txtEmail.text.toString()) && !utils.isNull(txtSenha.text.toString())) {
            autenticacao?.signInWithEmailAndPassword(txtEmail.text.toString(), txtSenha.text.toString())?.addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                if(task.isSuccessful) {
                    utils.apresentarMensagem(view, "Autenticado com Sucesso!")
                    intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    //TODO Mensagem mais amigavel com interface melhor
                    utils.apresentarMensagem(view, "Ops... Falha ao autenticar!")
                }
            })
        }
    }

    private fun resetarSenha() {
        intent = Intent(this, RedefinicaoSenhaActivity::class.java)
        startActivity(intent)
    }
}
