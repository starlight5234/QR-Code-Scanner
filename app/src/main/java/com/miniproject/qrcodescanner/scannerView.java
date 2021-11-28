package com.miniproject.qrcodescanner;

import android.Manifest;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class scannerView extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_view);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        scannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        MainActivity.scanResult.setText(R.string.camera_denied);
                        onBackPressed();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    public void handleResult(Result rawResult) {
        MainActivity.scanResult.setText(rawResult.getText());
        Button Copy = MainActivity.dialogBox.findViewById(R.id.buttonCopy);
        TextView dialogText = MainActivity.dialogBox.findViewById(R.id.scanned_result);
        if (rawResult.getText().contains(MainActivity.linkUnsecure) || rawResult.getText().contains(MainActivity.linkSecure)
            || rawResult.getText().contains(MainActivity.smsTO) || rawResult.getText().contains(MainActivity.callTel)){
            dialogText.setText(R.string.open_link);
            Copy.setText(R.string.open);
        } else {
            dialogText.setText(R.string.copy_result_to_clipboard);
            Copy.setText(R.string.copy);
        }

        try {
            Toast.makeText(this, "QR Code scanned", Toast.LENGTH_SHORT).show();
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }
}