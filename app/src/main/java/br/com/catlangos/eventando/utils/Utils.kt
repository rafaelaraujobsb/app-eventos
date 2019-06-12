package br.com.catlangos.eventando.utils

import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar

class Utils {

    public fun  apresentarMensagem(view: View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).setAction("Action", null).show()
    }

    //Tudo que estiver aqui dentro será estatico
    companion object {
        public fun isNull(value: Any?) = if (value == null || value == "") true else false

        fun isEqualsEditText(v1 : EditText , v2 : EditText) : Boolean{
            if(v1.text.toString().equals(v2.text.toString())){
                return true
            }
            return false
        }

        //Transforma edit text em String
        fun editTextToString(v1 : EditText) : String{
            if(v1 != null){
                return v1.text.toString()
            }
            return ""
        }

        fun textViewToString(v1 : TextView) : String{
            if(v1 != null){
                return v1.text.toString()
            }
            return ""
        }
    }

}