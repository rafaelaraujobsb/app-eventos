package br.com.catlangos.eventando.login.loginVO

import android.widget.EditText

class LoginVO {
    var txtEmail: String;
    var txtSenha: String;

    constructor(txtEmail: EditText, txtSenha: EditText) {
        this.txtEmail = txtEmail.text.toString()
        this.txtSenha = txtSenha.text.toString()
    }
}