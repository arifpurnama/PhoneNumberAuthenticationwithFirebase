package com.arifpurnama.phonenumberauthenticationwithfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OtpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private String mAuthVerificationId;
    private EditText mOtpText;
    private Button mVerifyButton;

    private ProgressBar mOtpProgress;

    private TextView mOtpFeedBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mAuthVerificationId = getIntent().getStringExtra("AuthCredentials");

        mOtpFeedBack = findViewById(R.id.otp_form_feedback);
        mOtpProgress = findViewById(R.id.otp_progress_bar);
        mOtpText = findViewById(R.id.otp_text_view);

        mVerifyButton = findViewById(R.id.verify_btn);

        mVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = mOtpText.getText().toString();

                if (otp.isEmpty()) {
                    mOtpFeedBack.setVisibility(View.VISIBLE);
                    mOtpFeedBack.setText("Please fill in the form and try again");
                } else {
                    mOtpProgress.setVisibility(View.VISIBLE);
                    mVerifyButton.setEnabled(false);

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mAuthVerificationId, otp);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(OtpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendUsertoHome();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                mOtpFeedBack.setVisibility(View.VISIBLE);
                                mOtpFeedBack.setText("There was as error verifiying OTP");
                            }
                        }
                        mOtpProgress.setVisibility(View.VISIBLE);
                        mVerifyButton.setEnabled(true);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser != null) {
            sendUsertoHome();
        }
    }

    public void sendUsertoHome() {
        Intent otpIntent = new Intent(OtpActivity.this, MainActivity.class);
        otpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        otpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(otpIntent);
        finish();
    }
}