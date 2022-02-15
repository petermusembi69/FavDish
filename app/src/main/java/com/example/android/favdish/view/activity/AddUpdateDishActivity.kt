package com.example.android.favdish.view.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.android.favdish.R
import com.example.android.favdish.application.FavDishApplication
import com.example.android.favdish.databinding.ActivityAddUpdateDishBinding
import com.example.android.favdish.databinding.DialogCustomImageSelectionBinding
import com.example.android.favdish.databinding.DialogCustomListBinding
import com.example.android.favdish.model.database.FavDishRepository
import com.example.android.favdish.model.entities.FavDish
import com.example.android.favdish.utils.Constants
import com.example.android.favdish.view.adapters.CustomListItemAdapter
import com.example.android.favdish.viewmodel.FavDishViewModel
import com.example.android.favdish.viewmodel.FavDishViewModelFactory
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.Exception
import java.sql.Wrapper
import java.util.*

class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val CAMERA = 1;
        private const val GALLERY = 2;

        private const val IMAGE_DIRECTORY = "FavDishImages"
    }

    private lateinit var mBinding: ActivityAddUpdateDishBinding
    private var mImagePath: String = ""
    private lateinit var mCustomDialog: Dialog

    private var mFavDishDetails: FavDish? = null

    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((application as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        if (intent.hasExtra(Constants.EXTRA_DISH_DETAILS)) {
            mFavDishDetails = intent.getParcelableExtra(Constants.EXTRA_DISH_DETAILS)
        }

        setupActionBar()

        mFavDishDetails?.let {
            if(it.id != 0 ){
                mImagePath = it.image
                Glide.with(this@AddUpdateDishActivity).load(mImagePath).centerCrop().into(mBinding.ivDishImage)
                mBinding.etTitle.setText(it.title)
                mBinding.etType.setText(it.type)
                mBinding.etCategory.setText(it.category)
                mBinding.etCookingTime.setText(it.cookingTime)
                mBinding.etDirectionToCook.setText(it.directionToCook)
                mBinding.etIngredients.setText(it.ingredients)

                mBinding.btnAddDish.text = resources.getString(R.string.lbl_update_dish)
            }
        }
        mBinding.ivAddDishImage.setOnClickListener (this)
        mBinding.etType.setOnClickListener(this)
        mBinding.etCategory.setOnClickListener(this)
        mBinding.etCookingTime.setOnClickListener(this)
        mBinding.btnAddDish.setOnClickListener(this)

    }

    private fun setupActionBar() {
        setSupportActionBar(mBinding.toolbarAddDishActivity)
        if(mFavDishDetails != null && mFavDishDetails!!.id != 0) {
            supportActionBar?.let {
                it.title = resources.getString(R.string.title_edit_dish)
            }
        } else {
            supportActionBar?.let {
                it.title = resources.getString(R.string.title_add_dish)
            }
        }

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

                R.id.et_type -> {
                    customItemsDialog(resources.getString(R.string.title_select_dish_type),Constants.dishTypes(),Constants.DISH_TYPE)
                    return
                }
                R.id.et_category -> {
                    customItemsDialog(resources.getString(R.string.title_select_dish_category),Constants.dishCategories(),Constants.DISH_CATEGORY)
                    return
                }
                R.id.et_cooking_time -> {
                    customItemsDialog(resources.getString(R.string.err_msg_select_dish_cooking_time),Constants.cookingTime(),Constants.DISH_COOKING_TIME)
                    return
                }
                R.id.btn_add_dish -> {
                    val title = mBinding.etTitle.text.toString().trim {it <= ' '}
                    val type = mBinding. etType.text.toString().trim {it <= ' '}
                    val category = mBinding.etCategory.text.toString().trim {it <= ' '}
                    val ingredients = mBinding.etIngredients.text.toString().trim {it <= ' '}
                    val cookingTimeInMinutes = mBinding.etCookingTime.text.toString().trim {it <= ' '}
                    val cookingDirection = mBinding.etDirectionToCook.text.toString().trim {it <= ' '}

                    when {
                        TextUtils.isEmpty(mImagePath) -> {
                            Toast.makeText(this@AddUpdateDishActivity,resources.getString(R.string.err_msg_select_dish_image),Toast.LENGTH_SHORT).show()
                        }TextUtils.isEmpty(title) -> {
                            Toast.makeText(this@AddUpdateDishActivity,resources.getString(R.string.err_msg_enter_dish_title),Toast.LENGTH_SHORT).show()
                        }TextUtils.isEmpty(type) -> {
                            Toast.makeText(this@AddUpdateDishActivity,resources.getString(R.string.err_msg_select_dish_type),Toast.LENGTH_SHORT).show()
                        }TextUtils.isEmpty(category) -> {
                            Toast.makeText(this@AddUpdateDishActivity,resources.getString(R.string.err_msg_select_dish_category),Toast.LENGTH_SHORT).show()
                        }TextUtils.isEmpty(ingredients) -> {
                            Toast.makeText(this@AddUpdateDishActivity,resources.getString(R.string.err_msg_enter_dish_ingredients),Toast.LENGTH_SHORT).show()
                        }TextUtils.isEmpty(cookingTimeInMinutes) -> {
                            Toast.makeText(this@AddUpdateDishActivity,resources.getString(R.string.err_msg_select_dish_cooking_time),Toast.LENGTH_SHORT).show()
                        }TextUtils.isEmpty(cookingDirection) -> {
                            Toast.makeText(this@AddUpdateDishActivity,resources.getString(R.string.err_msg_enter_dish_cooking_instructions),Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            var dishID = 0
                            var imageSource = Constants.DISH_IMAGE_SOURCE_LOCAL
                            var favoriteDish = false

                            mFavDishDetails?.let {
                                if(it.id != 0 ) {
                                    dishID = it.id
                                    imageSource = it.imageSource
                                    favoriteDish = it.favoriteDish
                                }
                            }
                            val favDishDetails: FavDish = FavDish(
                                mImagePath,
                                imageSource,
                                title,
                                type,
                                category,
                                ingredients,
                                cookingTimeInMinutes,
                                cookingDirection,
                                favoriteDish,
                                dishID
                            )

                            if(dishID == 0) {
                                mFavDishViewModel.insert(favDishDetails)

                                Toast.makeText(
                                    this@AddUpdateDishActivity,
                                    "You successfully added your favorite dish details.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e("Insertion", "Success")
                            } else  {
                                mFavDishViewModel.update(favDishDetails)

                                Toast.makeText(
                                    this@AddUpdateDishActivity,
                                    "You successfully updated your favorite dish details.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e("Update", "Success")
                            }
                            finish()
                        }
                    }
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
                    Glide.with(this).load(thumbnail).centerCrop().into(mBinding.ivDishImage)
                    mImagePath = saveImageToInternalStorage(thumbnail)
                    mBinding.ivAddDishImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_edit_24))
                }
            }
            if (requestCode == GALLERY) {
                data?.let {
                    val selectedPhotoUri = data.data
                    Glide.with(this).load(selectedPhotoUri)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .listener(object: RequestListener<Drawable>{
                                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {return false}

                                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                    resource?.let {
                                        val bitmap: Bitmap = resource.toBitmap()
                                        mImagePath = saveImageToInternalStorage(bitmap)
                                        Log.i("ImagePath", mImagePath)
                                    }
                                    return false
                                }
                            })
                            .centerCrop().into(mBinding.ivDishImage)
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

    private fun saveImageToInternalStorage(bitmap :Bitmap):String {
        val wrapper =  ContextWrapper(applicationContext)

        var file = wrapper.getDir(IMAGE_DIRECTORY, ContextWrapper.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream : OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e: IOException) {
            e.printStackTrace()
        }

        return file.absolutePath
    }

    private fun customItemsDialog(title: String,listItems: List<String>, selection: String ) {
        mCustomDialog = Dialog(this@AddUpdateDishActivity)
        val binding : DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)

        mCustomDialog.setContentView(binding.root)
        binding.tvTitle.text = title
        binding.rvList.layoutManager = LinearLayoutManager(this)

        val adapter = CustomListItemAdapter(this,null,listItems,selection)
        binding.rvList.adapter = adapter
        mCustomDialog.show()
    }

    fun selectedListItem(item: String,selection: String) {
        when(selection) {
            Constants.DISH_TYPE -> {
                mCustomDialog.dismiss()
                mBinding.etType.setText(item)
            }
            Constants.DISH_CATEGORY -> {
                mCustomDialog.dismiss()
                mBinding.etCategory.setText(item)
            }
            else -> {
                mCustomDialog.dismiss()
                mBinding.etCookingTime.setText(item)
            }
        }
    }


}