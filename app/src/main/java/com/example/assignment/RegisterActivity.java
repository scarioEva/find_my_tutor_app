package com.example.assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
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
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
        }
    }

//    private void redirectPage(String user, String userId, String documentId) {
//        Intent intent = new Intent(RegisterActivity.this, user.equals("Tutor") ? TutorRegisterActivity.class : StudentRegisterActivity.class);
//        intent.putExtra(userIdValue, userId);
//        intent.putExtra("docId",documentId);
//        startActivity(intent);
//    }


    public void onSignIn(View view) {
        Intent intentLogin = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intentLogin);
    }

    private void redirectVerificationPage(String email) {
        Intent intent = new Intent(RegisterActivity.this, EmailVerificationActivity.class);
//        intent.putExtra("uId", user.getUid());
        intent.putExtra("email", email);
        startActivity(intent);
    }


    private void addDb(String type, String sName, FirebaseUser userData) {

        Map<String, Object> data = new HashMap<>();
        data.put("name", sName);
        data.put("uId", userData.getUid().toString());
        if (type.equals("Tutor")) {
            data.put("department", "");
        }
        String path = type.toLowerCase();

        db.collection(path).add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        userData.sendEmailVerification();
                        redirectVerificationPage(userData.getEmail());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        setErrorMessage("Something went wrong. Please try again.");
                        Log.w("MainActivity", "Error adding document", e);
                    }
                });
    }

    private void addUser(String type, String name) {
        FirebaseUser user = mAuth.getCurrentUser();
        Map<String, Object> data = new HashMap<>();
        data.put("type", type.toLowerCase());
        data.put("uid", user.getUid());
        db.collection("users").add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        addDb(type, name, user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        setErrorMessage("Something went wrong. Please try again.");
                    }
                });
    }

    private void signup(String email, String password, String uType, String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.w("MainActivity", task.toString());
                        if (task.isSuccessful()) {
                            Log.d("MainActivity", "createUserWithEmail:success");

                            setErrorMessage("");
                            addUser(uType, name);

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

    private void setBorderRed(TextInputLayout textInput) {
        GradientDrawable borderDrawable = new GradientDrawable();
        borderDrawable.setStroke(1, Color.RED);
        borderDrawable.setCornerRadius(8);
        EditText editText = textInput.getEditText();
        editText.setBackground(borderDrawable);
    }

    public void signupButtonClicked(View view) {
        TextInputLayout email = findViewById(R.id.emailId);
        TextInputLayout password = findViewById(R.id.passId);
        TextInputLayout confirmPassword = findViewById(R.id.cnfPassId);
        TextInputLayout name = findViewById(R.id.nameId);
        String sName = name.getEditText().getText().toString();

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        int selectedId = radioGroup.getCheckedRadioButtonId();

        RadioButton radioButton = findViewById(selectedId);

        String userType = radioButton.getText().toString();


        String sEmail = email.getEditText().getText().toString();
        String sPassword = password.getEditText().getText().toString();
        String cPassword = confirmPassword.getEditText().getText().toString();


        if (sEmail.equals("")) {
            setBorderRed(email);
        }
        if (sName.equals("")) {
            setBorderRed(name);
        }
        if (sPassword.equals("")) {
            setBorderRed(password);
        }
        if (cPassword.equals("")) {
            setBorderRed(confirmPassword);
        }

        if (!sEmail.equals("") && !sPassword.equals("") && !cPassword.equals("") && !sName.equals("")) {
//            Email Validation
//            https://stackoverflow.com/questions/12947620/email-address-validation-in-android-on-edittext
            if(Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
                if (sPassword.equals(cPassword)) {
                    signup(sEmail, sPassword, userType, sName);
                } else {
                    setErrorMessage("Password and Confirm Password did not match.");
                }
            }
            else{
                setErrorMessage("Please enter valid email");
            }
        } else {
            setErrorMessage("Please enter all the required fileds");
        }
    }


}