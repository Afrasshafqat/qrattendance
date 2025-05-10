package com.app.fyp.qrbasedattendanceapp.Authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.fyp.qrbasedattendanceapp.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {
    EditText etFullName, etMobile, etEmail, etAddress, etPassword, etConfirmPassword;
    Button btnSignUp;
    RadioGroup radioGroup;
    RadioButton selectedRadioButton;
    TextView tvAlreadyHaveAccount;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // Initialize UI components
        etFullName = findViewById(R.id.etFullName);
        etMobile = findViewById(R.id.etMobile);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        radioGroup = findViewById(R.id.radioGroup);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvAlreadyHaveAccount = findViewById(R.id.tvAlreadyHaveAccount);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        RadioButton radioDriver = findViewById(R.id.radioFemale); // assuming it's Driver


// Show spinner only if 'Driver' is selected
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioFemale) {
                etMobile.setVisibility(View.GONE);
            } else {
                etMobile.setVisibility(View.VISIBLE);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        tvAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Signup.this, Login.class));
                finish();
            }
        });
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }
        selectedRadioButton = findViewById(selectedId);
        String role = selectedRadioButton.getText().toString();

        // Validations
        if (TextUtils.isEmpty(fullName) ||  TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Name validation (only letters and spaces)
        if (!fullName.matches("[a-zA-Z ]+")) {
            Toast.makeText(this, "Full name should only contain letters and spaces", Toast.LENGTH_SHORT).show();
            return;
        }


        // Email validation
        if (!email.contains("@")) {
            Toast.makeText(this, "Email must contain '@' symbol", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.contains(".")) {
            Toast.makeText(this, "Email must contain a domain like '.com'", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Confirm password
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }


        progressDialog.setMessage("Registering... Please wait");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    String userId = user.getUid();

                                    // Check if the user is a Driver and get driver type
                                    String rollno = "";
                                    if (role.equalsIgnoreCase("Student")) {
                                         rollno = etMobile.getText().toString().trim();
                                    }

                                    // Save user info including driverType if applicable
                                    User newUser = new User(fullName, rollno, email, role);
                                    databaseReference.child(userId).setValue(newUser);

                                    Toast.makeText(Signup.this, "Registration successful! Please Login.", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(Signup.this, Login.class));
                                    finish();
                                } else {
                                    Toast.makeText(Signup.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(Signup.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}

class User {
    public String fullName, mobile, email, role;

    public User() {
    }

    public User(String fullName, String mobile, String email, String role) {
        this.fullName = fullName;
        this.mobile = mobile;
        this.email = email;
        this.role = role;
    }
}

