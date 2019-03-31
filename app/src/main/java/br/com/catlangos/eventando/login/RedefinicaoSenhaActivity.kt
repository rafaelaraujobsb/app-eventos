package br.com.catlangos.eventando.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import br.com.catlangos.eventando.R
import br.com.catlangos.eventando.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_redefinicao_senha.*

class RedefinicaoSenhaActivity : AppCompatActivity() {

    private var autenticacao = FirebaseAuth.getInstance()
    private val utils = Utils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redefinicao_senha)

        txtEmail.setText(intent.getStringExtra("email"))

        btnRedefinir?.setOnClickListener { view -> redefinirSenha(view) }
    }

    private fun redefinirSenha(view: View) {
        if(!utils.isNull(txtEmail.text.toString())) {
            autenticacao?.sendPasswordResetEmail(txtEmail.text.toString())?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    utils.apresentarMensagem(view, "Nova senha enviada para " + txtEmail.text.toString())
                    intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    utils.apresentarMensagem(view, "Email não encontrado em nossos registros")
                }
            }
        }
    }
}
