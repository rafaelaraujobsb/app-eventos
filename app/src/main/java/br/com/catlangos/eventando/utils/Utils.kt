package br.com.catlangos.eventando.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

class Utils {

    public fun isNull(value: Any?) = if (value == null || value == "") true else false

    public fun  apresentarMensagem(view: View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).setAction("Action", null).show()
    }
}