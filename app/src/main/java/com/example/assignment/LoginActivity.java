package com.example.assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Loader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loader = new Loader(LoginActivity.this);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loader.startLoading();
            getUserType(currentUser.getUid());
        }

        Button forgotPass = findViewById(R.id.forgotPass);

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onForgotPass();
            }
        });
    }

    private void getUserType(String uid) {
        loader.startLoading();
        db.collection("users").whereEqualTo("uid", uid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
//                            loader.stopLoading();
                            QuerySnapshot document = task.getResult();
                            if (document.getDocuments().size() != 0) {
                                String userType = document.getDocuments().get(0).get("type").toString();
                                checkUserTypeExists(userType);
                            } else {
                                Log.d("MainActivity", "No such document");
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loader.stopLoading();
                        setErrorMessage("Something went wrong. Please try again.");
                        Log.w("MainActivity", "Error adding document", e);
                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    public void onRegister(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void setErrorMessage(String msg) {
        TextView errMsg = findViewById(R.id.errMessage);
        errMsg.setText(msg);
    }


    private void redirectVerificationPage(String email) {
        Intent intent = new Intent(LoginActivity.this, EmailVerificationActivity.class);
//        intent.putExtra("uId", user.getUid());
        intent.putExtra("email", email);
        startActivity(intent);
    }

    private void redirectRegistrationPage(String user, String documentId, String uid) {
        Intent intent = new Intent(LoginActivity.this, user.equals("tutor") ? TutorRegisterActivity.class : StudentRegisterActivity.class);
        intent.putExtra("uId", uid);
        intent.putExtra("docId", documentId);
        startActivity(intent);
    }

    private void checkUserTypeExists(String type) {
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();
//        loader.startLoading();
        String path = type.toLowerCase();
        db.collection(path).whereEqualTo("uId", uid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            loader.stopLoading();
                            QuerySnapshot document = task.getResult();
                            if (document.getDocuments().size() != 0) {
                                if (user.isEmailVerified()) {
                                    if ((path.equals("student") && !document.getDocuments().get(0).contains("course")) || (path.equals("tutor") && document.getDocuments().get(0).get("department").toString().equals(""))) {
                                        redirectRegistrationPage(path, document.getDocuments().get(0).getReference().getId(), uid);
                                    } else {
                                        Intent intent = new Intent(LoginActivity.this, path.equals("tutor") ? TutorMainActivity.class : StudentMainActivity.class);
                                        intent.putExtra("uId", uid);
                                        startActivity(intent);
                                    }

                                } else {
                                    Log.d("MainAct", "redirect");
                                    redirectVerificationPage(user.getEmail());
                                }
                            } else {
                                setErrorMessage("No user Exists");
                                Log.d("MainActivity", "No such document");
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loader.stopLoading();
                        setErrorMessage("Something went wrong. Please try again.");
                        Log.w("MainActivity", "Error adding document", e);
                    }
                });
    }

    private void login(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String uid = user.getUid();
                    getUserType(uid);

                } else {
                    setErrorMessage(task.getException().getMessage());
                }
            }
        });
    }

    private void setBorderRed(TextInputLayout textInput) {
        GradientDrawable borderDrawable = new GradientDrawable();
        borderDrawable.setStroke(1, Color.RED);
        borderDrawable.setCornerRadius(8);
        EditText editText = textInput.getEditText();
        editText.setBackground(borderDrawable);
    }

    private void onForgotPass() {
        TextInputLayout email = findViewById(R.id.loginMailId);
        String emailAddr = email.getEditText().getText().toString();
        if (emailAddr.equals("")) {
            setErrorMessage("Please enter email id");
        } else {
            mAuth.sendPasswordResetEmail(emailAddr)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            setErrorMessage("");
                            Toast.makeText(LoginActivity.this, "Password reset email sent successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            setErrorMessage("Failed to send password reset email");
                        }
                    });
        }
    }

    public void onLogin(View view) {

        TextInputLayout email = findViewById(R.id.loginMailId);
        TextInputLayout password = findViewById(R.id.loginPassId);

        String sEmail = email.getEditText().getText().toString();

        String sPassword = password.getEditText().getText().toString();

        if (sEmail.equals("")) {
            setBorderRed(email);
        }

        if (sPassword.equals("")) {
            setBorderRed(password);
        }

        if (!sEmail.equals("") && !sPassword.equals("")) {
            login(sEmail, sPassword);
        } else {
            setErrorMessage("Please enter all the required fields.");
        }
    }
}