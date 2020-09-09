package com.vinu.snapchat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class OpenSnapActivity : AppCompatActivity() {

    var snapImageView: ImageView? = null
    var captionTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_snap)

        setTitle("Snap opened")

        snapImageView = findViewById(R.id.snapImageView)
        captionTextView = findViewById(R.id.snapTextView)

        /** set captionTextView **/
        val caption = intent.getStringExtra("caption")
        if (caption.equals("")) {
            captionTextView?.visibility = View.INVISIBLE
        } else {
            captionTextView?.text = intent.getStringExtra("caption")
        }

        /** get image url from Firebase Storage & set snapImageView **/
        val taskSnapshot: StorageReference = FirebaseStorage.getInstance().getReference().child("images").child(intent.getStringExtra("uniqueImageName"))

        taskSnapshot.downloadUrl.addOnSuccessListener(OnSuccessListener<Any> { uri ->
            val imageUrl = uri.toString() // Got the Image URL!
            val task = ImageDownloader()
            val myImage: Bitmap
            try {
                myImage = task.execute(imageUrl).get()!! //calls ImageDownloader
                snapImageView?.setImageBitmap(myImage) //then sets image!
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }).addOnFailureListener(OnFailureListener {
            Toast.makeText(applicationContext, "Couldn't download URL", Toast.LENGTH_SHORT).show()
        })

    }

    /** Since we need to download image from a URL, we will use the AsyncTask to do this. We could have used the code from 'Google Books' app, but there is less code here. **/
    class ImageDownloader : AsyncTask<String?, Void?, Bitmap?>() {
        override fun doInBackground(vararg urls: String?): Bitmap? {
            return try {
                val url = URL(urls[0])
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.connect()
                val `in`: InputStream = connection.inputStream
                BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

}