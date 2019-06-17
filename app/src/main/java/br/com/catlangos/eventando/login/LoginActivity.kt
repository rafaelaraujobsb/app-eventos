package br.com.catlangos.eventando.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import br.com.catlangos.eventando.R
import br.com.catlangos.eventando.home.HomeActivity
import br.com.catlangos.eventando.utils.Utils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
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
        txtCadastrar?.setOnClickListener { cadastrar() }
        txtResetarSenha?.setOnClickListener { resetarSenha() }
    }

    private fun eventoLogin(view: View) {
        //TODO Mensagem mais amigavel com interface melhor

        val inputMethodManager = this.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            this.currentFocus.windowToken, 0
        )

        utils.apresentarMensagem(view, "Autenticando...")


        if(!Utils.isNull(txtEmail.text.toString()) && !Utils.isNull(txtSenha.text.toString())) {
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
        }else{
            utils.apresentarMensagem(view, "Insira o e-mail e a senha")
        }
    }

    private fun resetarSenha() {
        intent = Intent(this, RedefinicaoSenhaActivity::class.java)
        startActivity(intent)
    }

    private fun cadastrar(){
        intent = Intent(this, CadastroActivity::class.java)
        startActivity(intent)
    }

    private fun apresentarMensagem(view:View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).setAction("Action", null).show()
    }
}
