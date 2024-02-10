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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    public final static String userIdValue="";
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

    private void redirectPage(String user, String userId) {
        Intent intent = new Intent(RegisterActivity.this, user.equals("Tutor") ? TutorRegisterActivity.class : StudentRegisterActivity.class);
        intent.putExtra(userIdValue, userId);
        startActivity(intent);
    }


    public void onSignIn(View view){
        Intent intentLogin = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intentLogin);
    }
    public void signup(String email, String password, String uType) {


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

                            redirectPage(uType, user.getUid());
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
        TextView email = findViewById(R.id.emailId);
        TextView password = findViewById(R.id.passId);
        TextView confirmPassword = findViewById(R.id.cnfPassId);

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        Log.w("MainAct", selectedId + "sad");

        RadioButton radioButton = findViewById(selectedId);

        String userType = radioButton.getText().toString();

        String sEmail = email.getText().toString();
        String sPassword = password.getText().toString();
        String cPassword = confirmPassword.getText().toString();

        Log.w("MainAct", sEmail);


        if (!sEmail.equals("") && !sPassword.equals("") && !cPassword.equals("")) {
            if (sPassword.equals(cPassword)) {
                signup(sEmail, sPassword, userType);
            } else {
                setErrorMessage("Password and Confirm Password did not match.");
            }
        } else {
            setErrorMessage("Please enter all the required fileds");
        }
    }


}