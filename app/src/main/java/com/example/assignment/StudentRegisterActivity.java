package com.example.assignment;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
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
    final int CAMERA_PERMISSION_CODE = 1001;
    CommonClass commonClass = new CommonClass();
    Loader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);

        Intent intent = getIntent();
        userId = intent.getStringExtra("uId");
        Log.d("Cll", "login, "+userId);
        documentId = getIntent().getStringExtra("docId");

        imageView = findViewById(R.id.imageView);
        loader = new Loader(StudentRegisterActivity.this);

        Button loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent loginIntent = new Intent(StudentRegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }

    private void openCamera() {
        Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(iCamera, CAMERA_REQ_CODE);

    }

    private void reqCameraPermission() {
        if (ActivityCompat.checkSelfPermission(StudentRegisterActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();

        } else if (ActivityCompat.shouldShowRequestPermissionRationale(StudentRegisterActivity.this, android.Manifest.permission.CAMERA)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(StudentRegisterActivity.this);
            builder.setMessage("This app requires CAMERA permission for this feature to use.")
                    .setTitle("PermissionRequired")
                    .setCancelable(false)
                    .setPositiveButton("OK", ((dialogInterface, i) -> {
                        ActivityCompat.requestPermissions(StudentRegisterActivity.this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                        dialogInterface.dismiss();
                    }))
                    .setNegativeButton("Cancel", (((dialogInterface, i) -> dialogInterface.dismiss())));

        } else {
            ActivityCompat.requestPermissions(StudentRegisterActivity.this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(StudentRegisterActivity.this, Manifest.permission.CAMERA)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(StudentRegisterActivity.this);
                    builder.setMessage("This app requires CAMERA permission for this feature to use.")
                            .setTitle("PermissionRequired")
                            .setCancelable(false)
                            .setPositiveButton("Settings", ((dialogInterface, i) -> {
                                Intent intent_i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri u = Uri.fromParts("package", StudentRegisterActivity.this.getPackageName(), null);
                                intent_i.setData(u);
                                startActivity(intent_i);
                                dialogInterface.dismiss();
                            }))
                            .setNegativeButton("Cancel", (((dialogInterface, i) -> dialogInterface.dismiss())));
                    // User has denied permission and selected "Never ask again"
                    // Show a dialog explaining why the permission is needed
//                    showPermissionDeniedDialog();
                } else {
                    reqCameraPermission();
                }
            }
        }
    }

    public void openDrawer(View view) {
        Log.d("MainAct", "opened");
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_drawable_layout);
        LinearLayout captureLayout = dialog.findViewById(R.id.drawableItm1ID);
        LinearLayout fileLayout = dialog.findViewById(R.id.drawableItm2ID);
        LinearLayout removeLayout = dialog.findViewById(R.id.drawableItm3ID);
        removeLayout.setVisibility(View.GONE);
        if (ImageUri != null) {
            removeLayout.setVisibility(View.VISIBLE);
            removeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageUri = null;
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.user_new));
                    dialog.hide();
                }
            });
        } else {
            removeLayout.setVisibility(View.GONE);
        }

        captureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reqCameraPermission();
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
        loader.startLoading();
        Log.d("MainAct", "uri:" + ImageUri);
        if (ImageUri != null) {
            uploadImageStorage();
        } else {
            updateDatabase("");
        }
    }

    private void setBorderRed(TextInputLayout textInput) {
        GradientDrawable borderDrawable = new GradientDrawable();
        borderDrawable.setStroke(1, Color.RED);
        borderDrawable.setCornerRadius(8);
        EditText editText = textInput.getEditText();
        editText.setBackground(borderDrawable);
    }

    private void updateDatabase(String profileUrl) {
        loader.stopLoading();
        TextInputLayout courseId = findViewById(R.id.studentCourseId);
        TextInputLayout phoneId = findViewById(R.id.contactId);
        TextInputLayout accYrId = findViewById(R.id.accYrId);
        TextInputLayout bioId = findViewById(R.id.bioId);
        TextInputLayout studentId = findViewById(R.id.student);

        String courseInput = courseId.getEditText().getText().toString();
        String phoneInput = phoneId.getEditText().getText().toString();
        String accademicInput = accYrId.getEditText().getText().toString();
        String bioInput = bioId.getEditText().getText().toString();
        String studentIdInput = studentId.getEditText().getText().toString();


        Log.d("MainAct", "uploadSuccess");

        if (courseInput.equals("")) {
            setBorderRed(courseId);
        }

        if (accademicInput.equals("")) {
            setBorderRed(accYrId);
        }

        if (studentIdInput.equals("")) {
            setBorderRed(studentId);
        }

        TextView errMsg = findViewById(R.id.errMsg);

        if (!courseInput.equals("") && !accademicInput.equals("") && !studentIdInput.equals("")) {

            Map<String, Object> userData = new HashMap<>();
            userData.put("course", courseInput);
            userData.put("student_id", studentIdInput);
            userData.put("accademic_year", accademicInput);
            userData.put("phone", phoneInput);
            userData.put("bio", bioInput);
            userData.put("token", "");
            userData.put("profile_pic", profileUrl);

            db.collection("student").document(documentId).update(userData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void Void) {
                            Toast.makeText(StudentRegisterActivity.this, "Registered", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(StudentRegisterActivity.this, StudentMainActivity.class);
                            intent.putExtra("uId", userId);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            errMsg.setText("Something went wrong. Please try again");
                            Log.w("MainActivity", "Error adding document", e);
                        }
                    });
        } else {
            errMsg.setText("Enter all the required fields.");
        }
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
                                loader.stopLoading();
                                Log.d("MainAct", e.getMessage());
                                Toast.makeText(StudentRegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("MainAct2", e.getMessage());
                loader.stopLoading();

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
                ImageUri = commonClass.saveImage(img, StudentRegisterActivity.this);

            } else if (requestCode == GALLERY_REQ_CODE) {
                if (data != null & data.getData() != null) {
                    ImageUri = data.getData();
                    int orientation = commonClass.getImageOrientation(ImageUri, StudentRegisterActivity.this);
                    bitmap = commonClass.getRotatedBitmap(ImageUri, orientation, StudentRegisterActivity.this);
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }


}