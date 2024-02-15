package com.example.assignment;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StudentRegisterActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    String userId = "";
    String documentId = "";
    private Uri ImageUri;
    private Bitmap bitmap;
    ImageView imageView;
    StorageReference storageRef = storage.getReference();
    public final int CAMERA_REQ_CODE = 100;
    public final int GALLERY_REQ_CODE = 200;
    CommonClass commonClass = new CommonClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);

        Intent intent = getIntent();
        this.userId = intent.getStringExtra(RegisterActivity.userIdValue);
        documentId = getIntent().getStringExtra("docId");

        imageView = findViewById(R.id.imageView);
    }

    public void openDrawer(View view) {
        Log.d("MainAct", "opened");
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_drawable_layout);
        LinearLayout captureLayout = dialog.findViewById(R.id.drawableItm1ID);
        LinearLayout fileLayout = dialog.findViewById(R.id.drawableItm2ID);

        captureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(iCamera, CAMERA_REQ_CODE);
                dialog.hide();
            }
        });

        fileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GALLERY_REQ_CODE);

                dialog.hide();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void onSubmit(View view) {
        Log.d("MainAct", "uri:" + ImageUri);
        if (ImageUri != null) {
            uploadImageStorage();
        } else {
            updateDatabase("");
        }
    }

    private void updateDatabase(String profileUrl) {
        TextInputLayout courseId = findViewById(R.id.studentCourseId);
        TextInputLayout phoneId = findViewById(R.id.contactId);
        TextInputLayout accYrId = findViewById(R.id.accYrId);
        TextInputLayout bioId = findViewById(R.id.bioId);

        String courseInput = courseId.getEditText().getText().toString();
        String phoneInput = phoneId.getEditText().getText().toString();
        String accademicInout = accYrId.getEditText().getText().toString();
        String bioInput = bioId.getEditText().getText().toString();

        Map<String, Object> userData = new HashMap<>();
        userData.put("course", courseInput);
        userData.put("accademic_year", accademicInout);
        userData.put("phone", phoneInput);
        userData.put("bio", bioInput);

        userData.put("profile_pic", profileUrl);

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


    private void uploadImageStorage() {

        StorageReference profileIngRef = storageRef.child("profile_image/" + commonClass.generateRandomString(10));
        profileIngRef.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profileIngRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                if (uri != null) {
                                    updateDatabase(uri.toString());
                                }
                                Toast.makeText(StudentRegisterActivity.this, "UPLOADED", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("MainAct", e.getMessage());
                                Toast.makeText(StudentRegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("MainAct2", e.getMessage());

                Toast.makeText(StudentRegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQ_CODE) {
                Bitmap img = (Bitmap) (data.getExtras().get("data"));
                imageView.setImageBitmap(img);
                ImageUri = saveImage(img, StudentRegisterActivity.this);

            } else if (requestCode == GALLERY_REQ_CODE) {
                if (data != null & data.getData() != null) {
                    ImageUri = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), ImageUri
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (ImageUri != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    private Uri saveImage(Bitmap image, Context context) {
        File imageFolder = new File(context.getCacheDir(), "images");
        Uri uri = null;

        try {
            imageFolder.mkdirs();
            File file = new File(imageFolder, "capture_images.jpg");
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(context.getApplicationContext(), "com.example.assignment" + ".provider", file);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return uri;
    }


}