package com.ksrsac.photolatlng;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class TestActivity extends AppCompatActivity {
    private static final String TAG = "TestActivity";
    File mFile;
    private EditText editTextLat, editTextLng;
    Button button, photoButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        editTextLat = findViewById(R.id.et_lat);
        editTextLng = findViewById(R.id.et_lng);
        button = findViewById(R.id.cal);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> gridCodes = AppUtils.GetGridCode(Double.parseDouble(editTextLat.getText().toString()), Double.parseDouble(editTextLng.getText().toString()),1);
                Log.d(TAG, "onClick: "+gridCodes.get("1M"));

            }
        });
        photoButton = findViewById(R.id.photo);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestActivity.this, PhotoCaptureActivity.class);
                mFile = getOutputMediaFile();
                intent.putExtra("FILE", mFile.toString());
                startActivityForResult(intent, 1001);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==1001){
            mFile = new File(data.getStringExtra("FILE"));
            ImageView im = findViewById(R.id.ph);
            im.setImageURI(Uri.fromFile(mFile));
        }
    }

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Camera2TestNew");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

}