package com.app.fyp.qrbasedattendanceapp.genrateqr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.fyp.qrbasedattendanceapp.History;
import com.app.fyp.qrbasedattendanceapp.R;
import com.app.fyp.qrbasedattendanceapp.ScanQr.ScanActivity;

public class GenerateQRActivity extends AppCompatActivity {
    private ImageView qrImageView;
    private Button generateButton;
    private EditText sessionIdEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qractivity);
        qrImageView = findViewById(R.id.qrImageView);
        generateButton = findViewById(R.id.generateButton);
        sessionIdEditText = findViewById(R.id.sessionIdEditText);

        generateButton.setOnClickListener(v -> {
            String sessionId = sessionIdEditText.getText().toString().trim();
            if (!sessionId.isEmpty()) {
                Bitmap qrBitmap = QRCodeHelper.generateQRCode(sessionId);
                qrImageView.setImageBitmap(qrBitmap);
            } else {
                Toast.makeText(this, "Enter Session ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void Scan(View view) {
        Intent reviewIntent = new Intent(GenerateQRActivity.this, History.class);
         startActivity(reviewIntent);
    }
}