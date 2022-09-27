package io.fluffistar.ANiFLi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import io.fluffistar.ANiFLi.Backend.DownloadController
import io.fluffistar.ANiFLi.Backend.Verwaltung

import io.fluffistar.ANiFLi.Backendv2.Start
import io.fluffistar.ANiFLi.ui.LoginPage.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlinx.android.synthetic.main.splashlayout.*

class SplashScreen : AppCompatActivity() {


    companion object {
        const val PERMISSION_REQUEST_STORAGE = 0
    }
    lateinit var downloadController: DownloadController

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.splashlayout)

        findViewById<TextView>(R.id.appVersion).text = "Version ${ packageManager.getPackageInfo(this.packageName, 0).versionName}";

        Thread(Runnable {

            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                // Update UI here
                GlobalScope.launch(Dispatchers.IO) {
                    var update = Verwaltung.getUpdate(this@SplashScreen)
                    if(update.first) {
                        runOnUiThread {
                            //popup menu if u wanna download or not
                            MaterialAlertDialogBuilder(this@SplashScreen)
                                .setTitle("Neues Update")
                                .setMessage("Downlaod Now")
                                .setNeutralButton("Later") { _, _ ->
                                    // Respond to neutral button press
                                }
                                .setPositiveButton("Yes") { _, _ ->
                                    downloadController = DownloadController(this@SplashScreen, update.second)
                                    checkStoragePermission()

                                }.show()

                    }}
           //     Thread {
                  Start.setup(this@SplashScreen.applicationContext)
            //    }.start()
             //   while (!Verwaltung.laoded){}
                    val sharedPref = this@SplashScreen.applicationContext.getSharedPreferences(  "data", MODE_PRIVATE)
                val email_txt = sharedPref.getString( "email", "")
                val password_txt = sharedPref.getString( "password", "")
                val url =  ( Start.Domain +  "/login")
                Log.d("RESZULTz","password: $password_txt and email: $email_txt")
                val asynclogin = Fuel.post(
                    url,
                    listOf("email" to "$email_txt", "password" to "$password_txt")
                ).responseString { request, response, result  ->
                    when(result){
                        is  Result.Failure -> {
                            val intent = Intent(this@SplashScreen, LoginActivity::class.java)
                            startActivity(intent)
                            finish()

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

                                    apply()


                            }
                                val intent = Intent(this@SplashScreen, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }catch (ex :Exception){
                                Log.d("ERRORD",ex.message.toString())
                                val intent = Intent(this@SplashScreen, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                }


                    asynclogin.join()
                }

            }, 100) //it will wait 10 sec before upate ui
        }).start()


    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            // Request for camera permission.
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // start downloading
                downloadController.enqueueDownload()
            } else {
                // Permission request was denied.
                splashLayout.showSnackbar(R.string.storage_permission_denied, Snackbar.LENGTH_SHORT)
            }
        }
    }
    private fun checkStoragePermission() {
        // Check if the storage permission has been granted
        if (checkSelfPermissionCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            // start downloading
            downloadController.enqueueDownload()
        } else {
            // Permission is missing and must be requested.
            requestStoragePermission()
        }
    }
    private fun requestStoragePermission() {
        if (shouldShowRequestPermissionRationaleCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            splashLayout.showSnackbar(
                R.string.storage_access_required,
                Snackbar.LENGTH_INDEFINITE, R.string.ok
            ) {
                requestPermissionsCompat(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_STORAGE
                )
            }
        } else {
            requestPermissionsCompat(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_STORAGE
            )
        }
    }

}