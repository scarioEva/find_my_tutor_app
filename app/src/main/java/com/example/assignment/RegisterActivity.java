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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public final static String userIdValue = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();


    }

    public void setErrorMessage(String msg) {
        TextView errMsg = findViewById(R.id.errMessage);
        errMsg.setText(msg);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //User is signed in use an intent to move to another activity
        }
    }

    private void redirectPage(String user, String userId, String documentId) {
        Intent intent = new Intent(RegisterActivity.this, user.equals("Tutor") ? TutorRegisterActivity.class : StudentRegisterActivity.class);
        intent.putExtra(userIdValue, userId);
        intent.putExtra("docId",documentId);
        startActivity(intent);
    }


    public void onSignIn(View view) {
        Intent intentLogin = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intentLogin);
    }

    private void addDb(String uid, String type, String sName) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", sName);
        userData.put("uId", uid);
        String path=type.toLowerCase();

        db.collection(path).add(userData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("MainActivity", "DocumentSnapshot addedwith ID: " + documentReference.getId());
                        String documentId=documentReference.getId();
                        redirectPage(type, uid, documentId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("MainActivity", "Error adding document", e);
                    }
                });
    }

    private void signup(String email, String password, String uType, String name) {


        Log.w("MainActivity", "called" + email + ", " + password);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.w("MainActivity", task.toString());
                        if (task.isSuccessful()) {
                            Log.d("MainActivity", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(RegisterActivity.this,
                                    "Authentication success. Use an intent to move to a new activity",
                                    Toast.LENGTH_SHORT).show();
                            //user has been signed in, use an intent to move to the next activity
                            setErrorMessage("");
                            addDb(user.getUid(), uType, name);

                        } else {
                            // If sign in fails, display a message to the user.
                            setErrorMessage(task.getException().getMessage());
                            Log.w("MainActivity", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this,
                                    task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signupButtonClicked(View view) {
        TextInputLayout email = findViewById(R.id.emailId);
        TextInputLayout password = findViewById(R.id.passId);
        TextInputLayout confirmPassword = findViewById(R.id.cnfPassId);
        TextInputLayout name = findViewById(R.id.nameId);
        String sName = name.getEditText().getText().toString();

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        Log.w("MainAct", selectedId + "sad");

        RadioButton radioButton = findViewById(selectedId);

        String userType = radioButton.getText().toString();


        String sEmail = email.getEditText().getText().toString();
        String sPassword = password.getEditText().getText().toString();
        String cPassword = confirmPassword.getEditText().getText().toString();

        Log.w("MainAct", sEmail);


        if (!sEmail.equals("") && !sPassword.equals("") && !cPassword.equals("") && !sName.equals("")) {
            if (sPassword.equals(cPassword)) {
                signup(sEmail, sPassword, userType, sName);
            } else {
                setErrorMessage("Password and Confirm Password did not match.");
            }
        } else {
            setErrorMessage("Please enter all the required fileds");
        }
    }


}