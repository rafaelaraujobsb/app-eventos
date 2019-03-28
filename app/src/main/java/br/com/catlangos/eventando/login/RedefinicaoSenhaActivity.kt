package br.com.catlangos.eventando.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import br.com.catlangos.eventando.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_redefinicao_senha.*

class RedefinicaoSenhaActivity : AppCompatActivity() {
    private var autenticacao = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redefinicao_senha)

        txtEmail.setText(intent.getStringExtra("email"))

        btnRedefinir?.setOnClickListener { view -> redefinirSenha(view) }
    }

    private fun redefinirSenha(view: View) {
        autenticacao?.sendPasswordResetEmail(txtEmail.text.toString())?.addOnCompleteListener { task ->
            if(task.isSuccessful) {
                //intent = Intent(this, RedefinicaoSenhaActivity::class.java)
                //intent.putExtra("email", autenticacao.currentUser?.email.toString())
            } else {
//                intent = Intent(this, RedefinicaoSenhaActivity::class.java)
//                intent.putExtra("email", autenticacao.currentUser?.email.toString())
            }
        }
    }
}
