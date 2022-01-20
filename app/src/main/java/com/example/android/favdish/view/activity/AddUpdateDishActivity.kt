package com.example.android.favdish.view.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.android.favdish.R
import com.example.android.favdish.databinding.ActivityAddUpdateDishBinding
import com.example.android.favdish.databinding.DialogCustomImageSelectionBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.util.jar.Manifest

class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val CAMERA = 1;
        private const val GALLERY = 2;
    }

    private lateinit var mBinding: ActivityAddUpdateDishBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setupActionBar()

        mBinding.ivAddDishImage.setOnClickListener (this)

    }

    private fun setupActionBar() {
        setSupportActionBar(mBinding.toolbarAddDishActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mBinding.toolbarAddDishActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        if(v != null) {
            when(v.id) {
                R.id.iv_add_dish_image -> {
                    customImageSelection()
                    return
                }
            }

        }
    }

    private fun customImageSelection() {
        val dialog = Dialog(this)
        val binding: DialogCustomImageSelectionBinding = DialogCustomImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.show()

        binding.tvCamera.setOnClickListener {
            Dexter.withContext(this).withPermissions(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA
            ).withListener(object: MultiplePermissionsListener{
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                  report?.let {
                      if (report.areAllPermissionsGranted()) {
                          val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                          startActivityForResult(intent, CAMERA)
                      }
                  }
                }
                override fun onPermissionRationaleShouldBeShown(permission: MutableList<PermissionRequest>?, token: PermissionToken?) {
                    showRationalDialogForPermissions()
                }

            }).onSameThread().check()
            dialog.dismiss()
        }

        binding.tvGallery.setOnClickListener {
            Dexter.withContext(this).withPermission(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
            ).withListener(object: PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    val gallerIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(gallerIntent, GALLERY)
                }
                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {}
                override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?) {}
            }).onSameThread().check()
            dialog.dismiss()
        }

    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA) {
                data?.extras?.let {
                    val thumbnail: Bitmap = data.extras!!.get("data") as Bitmap
                    mBinding.ivDishImage.setImageBitmap(thumbnail)
                    mBinding.ivAddDishImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_edit_24))
                }
            }
            if (requestCode == GALLERY) {
                data?.let {
                    val selectedPhotoUri = data.data
                    mBinding.ivDishImage.setImageURI(selectedPhotoUri)
                    mBinding.ivAddDishImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_edit_24))
                }
            }
        } else if(resultCode == Activity.RESULT_CANCELED) {

        }
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this).setMessage("It looks like you have turned oof Permission required for his feature. It can be enabled under Application Settings")
                .setPositiveButton("GO TO SETTINGS")
                {_,_,->
                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("pacakge", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }

                }
                .setNegativeButton("Cancel") {
                    dialog,_,-> dialog.dismiss()
                }.show()

    }


}