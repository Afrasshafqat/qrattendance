package com.app.fyp.qrbasedattendanceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.app.fyp.qrbasedattendanceapp.genrateqr.GenerateQRActivity;

public class TeacherDahsboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dahsboard);
    }

    public void GenrateQr(View view) {
        Intent intent= new Intent(TeacherDahsboard.this, GenerateQRActivity.class);
        startActivity(intent);

    }
}