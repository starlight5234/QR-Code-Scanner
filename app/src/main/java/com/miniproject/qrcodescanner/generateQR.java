package com.miniproject.qrcodescanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class generateQR extends AppCompatActivity {

    protected ImageView qrCodeIV;
    protected EditText qrInputData;
    protected Button generateQrBtn;
    private final String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";
    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;
    private AppCompatActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);

        qrCodeIV = findViewById(R.id.IVQrcode);
        qrInputData = findViewById(R.id.qrInputData);
        generateQrBtn = findViewById(R.id.BtnGenerateQR);
        activity = this;

        generateQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(qrInputData.getText().toString())) {
                    Toast.makeText(generateQR.this, "Enter some text to generate QR Code", Toast.LENGTH_SHORT).show();
                } else {
                    WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

                    Display display = manager.getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);

                    int width = point.x;
                    int height = point.y;
                    int smallDimen = Math.min(width, height);
                    smallDimen = smallDimen * 3 / 4;

                    qrgEncoder = new QRGEncoder(qrInputData.getText().toString(), null, QRGContents.Type.TEXT, smallDimen);
                    try {
                        bitmap = qrgEncoder.encodeAsBitmap();
                        qrCodeIV.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        Log.e("Tag", e.toString());
                    }

                }
            }
        });

        qrCodeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        boolean save = new QRGSaver().save(savePath, qrInputData.getText().toString().trim(), bitmap, QRGContents.ImageType.IMAGE_PNG);
                        String qr_result = save ? "Image Saved" : "Image Not Saved";
                        Toast.makeText(generateQR.this, qr_result, Toast.LENGTH_SHORT).show();
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            }
        });
    }
}