package io.fluffistar.ANiFLi.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import io.fluffistar.ANiFLi.*
import io.fluffistar.ANiFLi.Backend.DownloadController
import io.fluffistar.ANiFLi.Backend.Verwaltung
import kotlinx.android.synthetic.main.settingfragment.*

class SettingFragment : Fragment() {

    lateinit var downloadController : DownloadController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updatebtn.setOnClickListener {
            var update = Verwaltung.getUpdate(requireContext())
            if(update.first) {
                downloadController = DownloadController(requireContext(), update.second)
                checkStoragePermission()
            }else
                Snackbar.make(view,"No Update Found",Snackbar.LENGTH_SHORT).show()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.settingfragment, container, false) as (ViewGroup)
        val autoplay = root.findViewById<androidx.appcompat.widget.SwitchCompat>(R.id.autoplay_settings)




        autoplay.isChecked = Verwaltung.Settings.autplay

        autoplay.setOnClickListener {
            Log.d("Autoplay" , autoplay.isChecked.toString())
            Verwaltung.Settings.autplay = autoplay.isChecked
            Verwaltung.saveSettings(root.context)
        }

        return root
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == SplashScreen.PERMISSION_REQUEST_STORAGE) {
            // Request for camera permission.
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // start downloading
                downloadController.enqueueDownload()
            } else {
                // Permission request was denied.
                settingfragment.showSnackbar(R.string.storage_permission_denied, Snackbar.LENGTH_SHORT)
            }
        }
    }
    private fun checkStoragePermission() {
        // Check if the storage permission has been granted
        if ((requireActivity() as AppCompatActivity).checkSelfPermissionCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
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
        if ((requireActivity() as AppCompatActivity).shouldShowRequestPermissionRationaleCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            settingfragment.showSnackbar(
                R.string.storage_access_required,
                Snackbar.LENGTH_INDEFINITE, R.string.ok
            ) {
                (requireActivity() as AppCompatActivity).requestPermissionsCompat(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    SplashScreen.PERMISSION_REQUEST_STORAGE
                )
            }
        } else {
            (requireActivity() as AppCompatActivity).requestPermissionsCompat(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                SplashScreen.PERMISSION_REQUEST_STORAGE
            )
        }
    }
}