package com.miniproject.qrcodescanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.text.ClipboardManager;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    // Create a dialog box
    protected static Dialog dialogBox;

    @SuppressLint("StaticFieldLeak")
    protected static TextView scanResult;
    static String linkUnsecure = "http://";
    static String linkSecure = "https://";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView generateButton = findViewById(R.id.generateButton);
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity((new Intent(getApplicationContext(), generateQR.class)));
            }
        });
        scanResult = (TextView)findViewById(R.id.scanResult);
        // Set margins of scanResult programmatically
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(30,200,30,325);
        scanResult.setLayoutParams(params);

        // Create a dialog box to copy QR Data
        dialogBox = new Dialog(MainActivity.this);
        dialogBox.setContentView(R.layout.result_dialog);
        dialogBox.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background));
        dialogBox.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogBox.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        scanResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBox.show();
            }
        });

        // Scan button which opens launches QR Scanner class
        Button scanButton = this.findViewById(R.id.scanButton);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),scannerView.class));
            }
        });

        // Dialog functions and buttons
        Button Okay = dialogBox.findViewById(R.id.buttonOkay);
        Button Copy = dialogBox.findViewById(R.id.buttonCopy);

        Okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBox.dismiss();
            }
        });

        Copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Objects.equals(scanResult.getText().toString(), getString(R.string.no_qr_code_found))){
                    Toast.makeText(MainActivity.this, "Nothing to copy!", Toast.LENGTH_SHORT).show();
                }else{
                    final String scanData = scanResult.getText().toString();
                    if(scanData.contains(linkUnsecure) || scanData.contains(linkSecure)) {
                        Uri uriURL = Uri.parse(scanData);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uriURL);
                        startActivity(intent);
                    } else {
                        ClipboardManager cm = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setText(scanResult.getText().toString());
                        Toast.makeText(MainActivity.this, "Copied to clipboard!", Toast.LENGTH_SHORT).show();
                    }
                    dialogBox.dismiss();
                }
            }
        });

    }
}