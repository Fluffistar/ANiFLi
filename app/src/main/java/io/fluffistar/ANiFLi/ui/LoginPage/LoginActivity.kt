package io.fluffistar.ANiFLi.ui.LoginPage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import io.fluffistar.ANiFLi.Backendv2.Start
import io.fluffistar.ANiFLi.MainActivity
import io.fluffistar.ANiFLi.R
import java.lang.Exception


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPref = this.applicationContext.getSharedPreferences(  "data",Context.MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginlayout)
        findViewById<TextView>(R.id.login_txt_register).movementMethod = LinkMovementMethod.getInstance();
        findViewById<TextView>(R.id.login_txt_forgot_pw).movementMethod = LinkMovementMethod.getInstance();
        val password = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.login_txt_input_pw)
        val email = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.login_txt_input_email)

        val loginbtn = findViewById<Button>(R.id.login_btn)
        loginbtn.setOnClickListener {

            val url =  ( Start.Domain +  "/login")
            val emai_txt = "${email.editText?.text}"
            val password_txt = "${password.editText?.text}"
            val asynclogin = Fuel.post(
                url,
                listOf("email" to emai_txt, "password" to password_txt )
            ).responseString {  request, response, result ->
                when(result){
                    is  Result.Failure -> {
                        email.error = "Falsche Email oder Password"
                        password.error = "Falsche Email oder Password"
                        password.editText?.setText("")
                        email.editText?.setText("")
                        //Toast.makeText(this,"Login Failed", Toast.LENGTH_LONG).show()
                    }
                    is Result.Success -> {
                        try {
                            Log.d("RESZULTD",result.get())
                            if(result.get() != "")
                                throw Exception()
                            val session = response.headers.get("Set-Cookie").asIterable().toList()[1].split(";")[0]

                            Log.d("Login_Html" , result.get())
                            with(sharedPref!!.edit()) {
                                putString("SessionID", session)
                                putString("password",  password_txt)
                                putString("email", emai_txt )
                                apply()

                            }
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }catch (ex : Exception){
                            email.error = "Falsche Email oder Password"
                            password.error = "Falsche Email oder Password"
                            password.editText?.setText("")
                            email.editText?.setText("")
                        }
                    }
                }
            }

            if(!email.editText?.text.isNullOrEmpty() && !password.editText?.text.isNullOrEmpty()   )
                asynclogin.join()
            else {
                email.error = "Feld darf nicht leer sein"
                password.error = "Feld darf nicht leer sein"

            }
        }
    }
}