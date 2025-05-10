package com.app.fyp.qrbasedattendanceapp.ScanQr;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.app.fyp.qrbasedattendanceapp.AttendanceRecord;
import com.app.fyp.qrbasedattendanceapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScanActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference attendanceRef;
    private Button btnScan;
    private String sessionIdScanned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        btnScan = findViewById(R.id.btnScan);
        database = FirebaseDatabase.getInstance();
        attendanceRef = database.getReference("attendance");

        btnScan.setOnClickListener(v -> startQRScan());
    }

    private void startQRScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan QR Code");
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(true);
        integrator.initiateScan(); // No setRequestCode()!
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                sessionIdScanned = result.getContents();
                showStudentDialog();
            } else {
                Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void showStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Student Information");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_student_info, null);
        EditText etName = view.findViewById(R.id.etStudentName);
        EditText etRoll = view.findViewById(R.id.etRollNo);
        TextView tvDateTime = view.findViewById(R.id.tvDateTime);

        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        tvDateTime.setText(currentDateTime);

        builder.setView(view);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = etName.getText().toString();
            String rollNo = etRoll.getText().toString();
            String attendance = "Present";

            if (!name.isEmpty() && !rollNo.isEmpty()) {
                markAttendance(name, rollNo, currentDateTime,attendance);
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void markAttendance(String name, String rollNo, String timestamp, String attendance) {
        AttendanceRecord record = new AttendanceRecord(name, rollNo, timestamp,attendance);
        attendanceRef.child(sessionIdScanned).child(rollNo).setValue(record)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Attendance Marked", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to Save", Toast.LENGTH_SHORT).show());
    }
}
