package com.dasharath.hatisamaj

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.dasharath.hatisamaj.utils.CommonUtils
import com.dasharath.hatisamaj.utils.ConnectivityReceiver
import com.dasharath.hatisamaj.utils.Utils.toast
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.toolbar_app.view.*
import java.io.ByteArrayOutputStream
import java.util.*

class CreatePostActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private var postImage: StorageReference? = null
    private var postImageUrl: String = ""

    var docId: String = ""
    var db: FirebaseFirestore? = null
    var mAuth: FirebaseAuth? = null
    var currentUserId: String? = null

    private var snackBar: Snackbar? = null
    private var result: CropImage.ActivityResult? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        init()
        listeners()
    }

    private fun init() {


        registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        postImage = FirebaseStorage.getInstance().reference.child(CommonUtils.POST_IMAGE)
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        currentUserId = mAuth?.currentUser?.uid
        docId = db?.collection(CommonUtils.PEOPLE)?.document()?.id.toString()

        toolbar.ivBack.visibility = View.VISIBLE

        var isFromURL = intent.getBooleanExtra(CommonUtils.IS_FROM_URL,false)
        if(isFromURL){
            toolbar.tvTitle.text = "Create URL post"
            linearImagePost.visibility = View.GONE
            linearUrlPost.visibility = View.VISIBLE
        } else {
            toolbar.tvTitle.text = "Create post"
            linearImagePost.visibility = View.VISIBLE
            linearUrlPost.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    private fun listeners() {
        toolbar.ivBack.setOnClickListener {
            onBackPressed()
        }

        imgPost.setOnClickListener {
            openGalleryDialog()
        }

        btnCreatePost.setOnClickListener {
            if(result == null)
                toast("Please select image first")
            else
                putFileToFirebase(result!!)

        }

        btnPostURL.setOnClickListener {
            if(isValidForm()){
                storeURLPostDetail()
            } else {
                toast("Please fill require fields")
            }
        }
    }

    private fun storeURLPostDetail() {
        aviLoading.show()
        val postDetail = hashMapOf<String,String>(
            CommonUtils.DESCRIPTION to etUrlPostDescription.text.toString(),
            CommonUtils.POST_DATE to DateFormat.format("hh:mm a - MMM d", Date().time).toString(),
            CommonUtils.TITLE to etPostURLTitle.text.toString(),
            CommonUtils.URL to etPostUrl.text.toString(),
            CommonUtils.DOC_ID to docId
        )

        db?.collection(CommonUtils.POST_URL)?.document(docId)?.set(postDetail)
            ?.addOnSuccessListener {
                aviLoading.hide()
                setResult(Activity.RESULT_OK, Intent())
                onBackPressed()
                toast("Post uploaded successfully")
            }
            ?.addOnFailureListener { e ->
                aviLoading.hide()
                Log.w("TAG", "Error adding document", e)
            }
    }

    private fun isValidForm(): Boolean {
        if (!etPostURLTitle.checkTextValue()) return false
        if (!etPostUrl.checkTextValue(isURL = true)) return false
        if (!etUrlPostDescription.checkTextValue()) return false
        return true
    }

    private fun EditText.checkTextValue(isURL: Boolean = false): Boolean {

        val value = this.text.toString()
        if (value == "") {
            this.error = "This field can't be blank"
            return false
        }

        if (isURL) {
            if (!Patterns.WEB_URL.matcher(value).matches()) {
                this.error = "Enter valid URL"
                return false
            }
        }

        return true
    }

    private fun openGalleryDialog() {
        val statusRead = ContextCompat.checkSelfPermission(
            this@CreatePostActivity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val statusCamera =
            ContextCompat.checkSelfPermission(this@CreatePostActivity, Manifest.permission.CAMERA)
        if (statusRead == PackageManager.PERMISSION_GRANTED && statusCamera == PackageManager.PERMISSION_GRANTED) {
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this)
        } else {
            ActivityCompat.requestPermissions(
                this@CreatePostActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                101
            )
        }
    }

    private fun putFileToFirebase(data: CropImage.ActivityResult) {
        aviLoading.show()

        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data.uri)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 16, baos)
        val newByteArray = baos.toByteArray()

        val filePath = postImage?.child(System.currentTimeMillis().toString() + ".jpg")
        filePath?.putBytes(newByteArray)?.addOnCompleteListener {
            if (it.isSuccessful) {
                filePath.downloadUrl.addOnSuccessListener { uri ->
                    postImageUrl = uri.toString()
                    val postDetail = hashMapOf<String,String>(
                        CommonUtils.IMAGE_URL to postImageUrl,
                        CommonUtils.DESCRIPTION to etPostDescription.text.toString(),
                        CommonUtils.POST_DATE to DateFormat.format("hh:mm a - MMM d", Date().time).toString(),
                        CommonUtils.LIKE_COUNT to "0",
                        CommonUtils.LIKED_USER to "",
                        CommonUtils.DOC_ID to docId
                    )

                    db?.collection(CommonUtils.POSTS)?.document(docId)?.set(postDetail)
                        ?.addOnSuccessListener {
                            aviLoading.hide()
                            setResult(Activity.RESULT_OK, Intent())
                            onBackPressed()
                            toast("Post uploaded successfully")
                        }
                        ?.addOnFailureListener { e ->
                            aviLoading.hide()
                            Log.w("TAG", "Error adding document", e)
                        }
                }
            } else {
                aviLoading.hide()
                toast( "Error: " + it.exception.toString())
            }
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showNetworkMessage(isConnected)
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            snackBar = Snackbar.make(findViewById(R.id.relativeCreatePost), "You are offline", Snackbar.LENGTH_LONG)
            val params = snackBar!!.view.layoutParams as FrameLayout.LayoutParams
            params.setMargins(10,0,10,145)
            snackBar!!.view.layoutParams = params
            snackBar!!.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackBar?.show()
        } else {
            snackBar?.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                result = CropImage.getActivityResult(data)
                Glide.with(this@CreatePostActivity).load(result!!.uri).into(imgPost)
            }
        }
    }
}
