package com.example.assignment;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StudentRegisterActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    String userId = "";
    String documentId = "";
    private Uri ImageUri;
    private Bitmap bitmap;
    ImageView imageView;

    StorageReference storageRef = storage.getReference();

    String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);

        Intent intent = getIntent();
        this.userId = intent.getStringExtra(RegisterActivity.userIdValue);
        documentId = getIntent().getStringExtra("docId");

        imageView = findViewById(R.id.profileImage);
    }

    public void onSubmit(View view) {
        uploadImageStorage();
    }

    private void addToDatabase() {
        TextInputLayout courseId = findViewById(R.id.studentCourseId);
        String courseInput = courseId.getEditText().getText().toString();
        Map<String, Object> userData = new HashMap<>();
        userData.put("course", courseInput);
        userData.put("profileImage", imageUrl);
        Log.d("MainAct", "uploadSuccess");
        db.collection("student").document(documentId).update(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void Void) {
                        Intent intent = new Intent(StudentRegisterActivity.this, StudentMainActivity.class);
                        intent.putExtra("uId", userId);
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

    public void imageUpload(View view) {
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
//            if(ContextCompat.checkSelfPermission(getAct))
//        }
        onSelectImage();
    }

    private void uploadImageStorage() {

        if (ImageUri != null) {
            StorageReference myRef = storageRef.child("profileImage/" + ImageUri.getLastPathSegment());
            myRef.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    myRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if (uri != null) {
                                        imageUrl = uri.toString();
                                        addToDatabase();
                                        Log.d("MainActivity", "url:" + uri.toString());
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(StudentRegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(StudentRegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void onSelectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult( Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        launcher.launch(intent);
    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null & data.getData() != null) {
                ImageUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), ImageUri
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                    ;
                }
            }
            if (ImageUri != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    });
}