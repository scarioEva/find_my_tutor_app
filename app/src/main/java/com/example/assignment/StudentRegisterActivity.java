package com.example.assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class StudentRegisterActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);

        Intent intent = getIntent();
        this.userId = intent.getStringExtra(RegisterActivity.userIdValue);
    }

    public void onSubmit(View view) {
        EditText nameId = findViewById(R.id.studentNameId);
        String nameInput = nameId.getText().toString();

        Map<String, Object> userData = new HashMap<>();
        userData.put("name", nameInput);
        userData.put("uId", userId);

        db.collection("student").add(userData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("MainActivity", "DocumentSnapshot addedwith ID: " + documentReference.getId());
                        Intent intent = new Intent(StudentRegisterActivity.this,StudentMainActivity.class);
                        intent.putExtra("uId",userId);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("MainActivity", "Error adding document", e);
                    }
                });
    }
}