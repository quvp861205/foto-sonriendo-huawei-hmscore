package com.pquinonezv.hselfiecamera.main


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.pquinonezv.hselfiecamera.R
import com.pquinonezv.hselfiecamera.auth.AuthActivity
import com.pquinonezv.hselfiecamera.face.LiveFaceActivityCamera
import kotlinx.android.synthetic.main.activity_home.*
import java.lang.Exception
import java.lang.RuntimeException

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST = 1
        private fun isPermissionGranted(
            context: Context,
            permission: String?
        ): Boolean {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission!!
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                return true
            }
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        if (!allPermissionsGranted()) {
            runtimePermission
        }

        btnLogout.setOnClickListener {
            logoutWithHuaweiID()
        }

        mostPeople.setOnClickListener {
            val intent = Intent(this@MainActivity, LiveFaceActivityCamera::class.java)
            intent.putExtra("detect_mode", 1002)
            startActivity(intent)
        }

        nearestPeople.setOnClickListener {
            val intent = Intent(this@MainActivity, LiveFaceActivityCamera::class.java)
            intent.putExtra("detect_mode", 1003)
            startActivity(intent)
        }
    }

    private val requiredPermissions: Array<String?>
        get() = try {
            val info = this.packageManager
                .getPackageInfo(this.packageName, PackageManager.GET_PERMISSIONS)
            val ps = info.requestedPermissions
            if (ps != null && ps.isNotEmpty()) {
                ps
            } else {
                arrayOfNulls(0)
            }
        } catch (e: RuntimeException) {
            throw e
        } catch (e: Exception) {
            arrayOfNulls(0)
        }

    private fun allPermissionsGranted(): Boolean {
        for (permission in requiredPermissions) {
            if (!isPermissionGranted(this, permission)) {
                return false
            }
        }
        return true
    }

    private val runtimePermission: Unit
        get() {
            val allNeedPermissions: MutableList<String?> = ArrayList()
            for (permission in requiredPermissions) {
                if (!isPermissionGranted(this, permission)) {
                    allNeedPermissions.add(permission)
                }
            }
            if (allNeedPermissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    this,
                    allNeedPermissions.toTypedArray(),
                    PERMISSION_REQUEST
                )
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != PERMISSION_REQUEST) {
            return
        }
        var isNeedShowDialog = false
        for (i in permissions.indices) {
            if (permissions[i] == Manifest.permission.READ_EXTERNAL_STORAGE && grantResults[i]
                != PackageManager.PERMISSION_GRANTED
            ) {
                isNeedShowDialog = true
            }
        }
        if (isNeedShowDialog && !ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            val dialog: AlertDialog = AlertDialog.Builder(this)
                .setMessage("Esta aplicacion requiere de acceso a tu carpeta de medios y tu camara para poder funcionar")
                .setPositiveButton("Configuracion") { _, _ ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:$packageName")
                    startActivityForResult(intent, 200)
                    startActivity(intent)
                }
                .setNegativeButton("Cancel")
                { _, _ -> finish() }.create()
            dialog.show()
        }
    }


    private fun logoutWithHuaweiID() {
        val mAuthParams = HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .createParams()
        val mAuthManager = HuaweiIdAuthManager.getService(this, mAuthParams)
        val logoutTask = mAuthManager.signOut()
        logoutTask.addOnSuccessListener {
            startActivity(Intent(this@MainActivity, AuthActivity::class.java))
            finish()
        }
        logoutTask.addOnFailureListener {
            Toast.makeText(this, "Logout Fallo!", Toast.LENGTH_LONG).show()
        }

    }

    override fun onBackPressed() {
        //Do nothing
    }
}