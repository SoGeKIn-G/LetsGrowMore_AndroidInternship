package com.example.facedetection

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.facedetection.databinding.ActivityMainBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        // Takes permission for using camera for capturing photo.
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA), 100
            )
        }

        binding.captureImgButton.setOnClickListener {
            startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), 100)
        }
    }

    // Gets image from the camera.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 100) {
            val capturedImage: Any? = data?.extras?.get("data")
            Glide.with(this)
                .load(capturedImage)
                .into(binding.ivCapturedPhoto)
            detectFace(capturedImage as Bitmap)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    // Applying ML for counting faces.
    private fun detectFace(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val options = FaceDetectorOptions.Builder().build()
        val detector = FaceDetection.getClient(options)
        detector.process(image)
            .addOnSuccessListener { result ->
                var count = 0
                for (face in result) {
                    count++
                }
                Toast.makeText(this, "$count Face Found", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }
}