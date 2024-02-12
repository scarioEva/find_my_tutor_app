package com.example.assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TutorRegisterActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userId = "";
    String documentId = "";

//    private ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_register);
        Intent intent = getIntent();
        this.userId = intent.getStringExtra(RegisterActivity.userIdValue);
        documentId = getIntent().getStringExtra("docId");

//        mImageView = (ImageView) findViewById(R.id.imageView2);
//        mImageView.setImageResource(R.drawable.user);

    }

    public void onSubmit(View view) {
        TextInputLayout departmentId = findViewById(R.id.departmentId);
        TextInputLayout subjectId = findViewById(R.id.subjectId);

        String departmentInput = departmentId.getEditText().getText().toString();
        String subjectInput = subjectId.getEditText().getText().toString();

        Map<String, Object> userData = new HashMap<>();
        userData.put("department", departmentInput);
        userData.put("subject", subjectInput);

        db.collection("tutor").document(documentId).update(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void Void) {
//                        Log.d("MainActivity", "DocumentSnapshot addedwith ID: " + documentReference.getId());
                        Intent intent = new Intent(TutorRegisterActivity.this,TutorMainActivity.class);
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