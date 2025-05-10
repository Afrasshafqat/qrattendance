package com.app.fyp.qrbasedattendanceapp.Authentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.fyp.qrbasedattendanceapp.MainActivity;
import com.app.fyp.qrbasedattendanceapp.R;

import com.app.fyp.qrbasedattendanceapp.ScanQr.ScanActivity;
import com.app.fyp.qrbasedattendanceapp.TeacherDahsboard;
import com.app.fyp.qrbasedattendanceapp.genrateqr.GenerateQRActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private TextView signupText, forgetPassword;
    private Button signinButton;
    private EditText emailEditText, passwordEditText;
    private ProgressBar progressBar;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authListener;

    private DatabaseReference userRef;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Views
        signupText = findViewById(R.id.tvSignup);
        forgetPassword = findViewById(R.id.tvForgetPassword);
        signinButton = findViewById(R.id.btnLogin);
        emailEditText = findViewById(R.id.etUserName);
        passwordEditText = findViewById(R.id.etPassword);
        progressBar = findViewById(R.id.progressbar);


        progressBar.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");


        // Auto-login if checkbox was selected previously

        // Show/hide password
        passwordEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Drawable drawable = passwordEditText.getCompoundDrawablesRelative()[DRAWABLE_RIGHT];
                if (drawable != null && event.getRawX() >= (passwordEditText.getRight() - drawable.getBounds().width())) {
                    if (passwordEditText.getTransformationMethod() instanceof PasswordTransformationMethod) {
                        passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    } else {
                        passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                    passwordEditText.setSelection(passwordEditText.length());
                    return true;
                }
            }
            return false;
        });

        // Sign up navigation
        signupText.setOnClickListener(v -> startActivity(new Intent(Login.this, Signup.class)));

        // Sign in logic
        signinButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();



            if (!validateInput(email, password)) return;

            progressBar.setVisibility(View.VISIBLE);
            signinButton.setEnabled(false);

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                progressBar.setVisibility(View.INVISIBLE);
                signinButton.setEnabled(true);

                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();


                    if (user != null) {
                        checkUserRole(user.getUid());
                    }
                } else {
                    Toast.makeText(Login.this, "Login failed. Check credentials.", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private boolean validateInput(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email required");
            emailEditText.requestFocus();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid email");
            emailEditText.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password required");
            passwordEditText.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return false;
        }
        return true;
    }

    private void checkUserRole(String uid) {
        userRef.child(uid).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String role = snapshot.getValue(String.class);
                if (role == null) {
                    Toast.makeText(Login.this, "User role not found.", Toast.LENGTH_SHORT).show();
                    return;
                }

                switch (role) {
                    case "Student":
                        startActivity(new Intent(Login.this, ScanActivity.class));
                        break;
                    case "Teacher":
                        startActivity(new Intent(Login.this, TeacherDahsboard.class));
                        break;
                    default:
                        Toast.makeText(Login.this, "Unknown role: " + role, Toast.LENGTH_SHORT).show();
                }
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Login.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
