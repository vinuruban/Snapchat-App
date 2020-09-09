package com.vinu.snapchat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


class CreateSnapActivity : AppCompatActivity() {

    var snapImageView: ImageView? = null //ImageView is optional and initially set to null
    var captionEditText: EditText? = null //EditText is optional and initially set to null
    val uniqueImageName = UUID.randomUUID().toString() + ".jpg" //random + unique name for each imgs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)

        snapImageView = findViewById(R.id.imageView)
        captionEditText = findViewById(R.id.addCaption)

        setTitle("Create a new snap")
    }

    fun onClickSelectImage(view: View) {
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) { //IF PERMISSION WASN'T GRANTED,...
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1); //...THEN ASK FOR PERMISSION ( IN onRequestPermissionsResult() )
        } else { //IF PERMISSION IS GRANTED,...
            selectPhoto(); //...THEN ALLOW USERS TO SELECT PHOTOS
        }
    }

    /** here we act on the result we got from the intent code in selectPhoto() **/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val selectedImage: Uri? = data!!.data
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                snapImageView?.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /** asks for user permission to access photos **/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //IF PERMISSION WAS GRANTED
                selectPhoto()
            }
        }
    }

    /** this intent code will open up the "Select photo" pop up  */
    fun selectPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    fun onClickNext(view: View) {
// Get the data from an ImageView as bytes
        // Get the data from an ImageView as bytes
        snapImageView?.setDrawingCacheEnabled(true)
        snapImageView?.buildDrawingCache()
        val bitmap = (snapImageView?.getDrawable() as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos) //converts bitmap to jpeg
        val data: ByteArray = baos.toByteArray()

        /** Create snap **/
        val uploadTask: UploadTask = FirebaseStorage.getInstance().getReference().child("images").child(uniqueImageName).putBytes(data) // CLOUD STORAGE - creates an "images" folder (if it wasn't previously created) and stores an img inside

        //What to do when successfully/unsuccessfully uploaded
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(applicationContext, "Couldn't upload snap", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
            val intent = Intent(this, ChooseUserActivity::class.java)
            intent.putExtra("uniqueImageName", uniqueImageName)
            intent.putExtra("caption", captionEditText?.text.toString())
            startActivity(intent)
        }

    }
}