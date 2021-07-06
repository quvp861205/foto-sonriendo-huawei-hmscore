package com.pquinonezv.hselfiecamera.auth

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.pquinonezv.hselfiecamera.R
import com.pquinonezv.hselfiecamera.main.MainActivity
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        btnLogin.setOnClickListener {
            loginHuaweiIdAuth()
        }
    }

    private fun loginHuaweiIdAuth(){
        val mAuthParams = HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setEmail()
            .setAccessToken()
            .setProfile()
            .setIdToken()
            .setUid()
            .setId()
            .createParams()
        val mAuthManager = HuaweiIdAuthManager.getService(this, mAuthParams)
        startActivityForResult(mAuthManager.signInIntent, 1000) // es el numero de identificacion de login
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000){
            if (resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this, "Login Cancelado", Toast.LENGTH_LONG).show()
            } else if (resultCode == Activity.RESULT_OK){
                val authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data)
                if (authHuaweiIdTask.isSuccessful){
                    //Toast.makeText(this, "Login Exitoso!", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Login Fallo!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}