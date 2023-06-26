package com.geolocatepoop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.geolocation.R;

//@Suppress("DEPRECATION")
class PictureActivity extends AppCompatActivity {
    private val cameraRequest = 1888;
    lateinit var
    imageView:ImageView;
    override fun

    onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this @com.geolocatepoop.PictureActivity,
            arrayOf(android.Manifest.permission.CAMERA), cameraRequest);
        }
        imageView = findViewById(R.id.imageView);
        findViewById<Button> (R.id.capturar).setOnClickListener {
            startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), cameraRequest);
        }
        ;
    }

    @SuppressLint("CutPasteId")
    override fun

    onActivityResult(requestCode:Int, resultCode:Int, data:Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == cameraRequest) {

            try {
                imageView.setImageBitmap(data ?.extras ?.get("data") as Bitmap);
            } catch (e:Exception){
                findViewById<Button> (R.id.accept).isEnabled = false;
            } finally{
                var accept = findViewById < Button > (R.id.accept)
                        accept.isEnabled = true;
                val bunddle:Bundle ? = null;
                val byteArrayOutputStream = ByteArrayOutputStream();
                (data ?.extras ?.get("data") as Bitmap).
                compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                val byteArray = byteArrayOutputStream.toByteArray();
                bunddle ?.putByteArray("img", byteArray);
                println(data.extras ?.get("data"));
                accept.setOnClickListener {
                    startActivity(Intent(this @com.geolocatepoop.PictureActivity,MainActivity:: class.java).
                    putExtra("extras", bunddle));
                } ;

            }
        }
    }

}