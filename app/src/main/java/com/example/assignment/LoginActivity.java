package com.example.assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }

    public void onRegister(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void setErrorMessage(String msg) {
        TextView errMsg = findViewById(R.id.errMessage);
        errMsg.setText(msg);
    }

    private void checkUserTypeExists(String uid, String type){
        String path=type.toLowerCase();
        db.collection(path).whereEqualTo("uId", uid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            if (document.getDocuments().size() != 0) {
                                Intent intent = new Intent(LoginActivity.this,type.equals("Tutor")? TutorMainActivity.class:StudentMainActivity.class);
                                intent.putExtra("uId",uid);
                                startActivity(intent);
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
                        Log.w("MainActivity", "Error adding document", e);
                    }
                });
    }

    private void login(String email, String pass, String type) {
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String uId=user.getUid();
                    checkUserTypeExists(uId, type);
                } else {
                    setErrorMessage(task.getException().getMessage());
                }
            }
        });
    }

    public void onLogin(View view) {
        TextView email = findViewById(R.id.lognMailId);
        TextView password = findViewById(R.id.loginPassId);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);

        String userType = radioButton.getText().toString();
        String sEmail = email.getText().toString();
        String sPassword = password.getText().toString();

        if (!sEmail.equals("") && !sPassword.equals("")) {
            login(sEmail, sPassword, userType);
        } else {
            setErrorMessage("Please enter all the required fileds");
        }
    }
}