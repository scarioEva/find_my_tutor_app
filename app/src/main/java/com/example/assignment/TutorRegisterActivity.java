package com.example.assignment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TutorRegisterActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    public final int CAMERA_REQ_CODE = 100;
    public final int GALLERY_REQ_CODE = 200;
    String userId = "";
    String documentId = "";
    Uri ImageUri;
    Bitmap bitmap;
    ShapeableImageView imageView;

    CommonClass commonClass = new CommonClass();
    List<String> locationList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_register);
        Intent intent = getIntent();
        this.userId = intent.getStringExtra(RegisterActivity.userIdValue);
        documentId = getIntent().getStringExtra("docId");

        imageView = findViewById(R.id.imageView);
        getLocationList();

    }

    private void getLocationList() {
        db.collection("campus_buildings").document("building_list").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> campusLocations = documentSnapshot.getData();
                    for (Map.Entry<String, Object> entry : campusLocations.entrySet()) {
//                        String key = entry.getKey();
                        Object value = entry.getValue();
                        locationList.add(value.toString());
                    }
                    Log.d("MainAct","location:"+ locationList.toString());

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Log.w(TAG, "Error getting document", e);
            }
        });
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

    private void updateDatabase(String fileUrl) {
        TextInputLayout departmentId = findViewById(R.id.departmentId);
        TextInputLayout bioId = findViewById(R.id.bioId);


        String departmentInput = departmentId.getEditText().getText().toString();
        String bioInput = bioId.getEditText().getText().toString();

        EditText monFromId = findViewById(R.id.monFromId);
        EditText monToId = findViewById(R.id.monToId);
        EditText tueFromId = findViewById(R.id.tueFromId);
        EditText tueToId = findViewById(R.id.tueToId);
        EditText wedFromId = findViewById(R.id.wedFromId);
        EditText wedToId = findViewById(R.id.wedToId);
        EditText thuFromId = findViewById(R.id.thuFromId);
        EditText thuToId = findViewById(R.id.thuToId);
        EditText friFromId = findViewById(R.id.friFromId);
        EditText friToId = findViewById(R.id.friToId);

        String monFromValue = monFromId.getText().toString();
        String monToValue = monToId.getText().toString();
        String tueFromValue = tueFromId.getText().toString();
        String tueToValue = tueToId.getText().toString();
        String wedFromValue = wedFromId.getText().toString();
        String wedToValue = wedToId.getText().toString();
        String thuFromValue = thuFromId.getText().toString();
        String thuToValue = thuToId.getText().toString();
        String friFromValue = friFromId.getText().toString();
        String friToValue = friToId.getText().toString();

        Map<String, Object> mondayData = new HashMap<>();
        mondayData.put("from", monFromValue);
        mondayData.put("to", monToValue);

        Map<String, Object> tuesdayData = new HashMap<>();
        tuesdayData.put("from", tueFromValue);
        tuesdayData.put("to", tueToValue);

        Map<String, Object> wednesdayData = new HashMap<>();
        wednesdayData.put("from", wedFromValue);
        wednesdayData.put("to", wedToValue);

        Map<String, Object> thursdayData = new HashMap<>();
        thursdayData.put("from", thuFromValue);
        thursdayData.put("to", thuToValue);

        Map<String, Object> fridayData = new HashMap<>();
        fridayData.put("from", friFromValue);
        fridayData.put("to", friToValue);

        Map<Object, Object> availability = new HashMap<>();
        availability.put("monday", mondayData);
        availability.put("tuesday", tuesdayData);
        availability.put("wednesday", wednesdayData);
        availability.put("thursday", thursdayData);
        availability.put("friday", fridayData);

        Map<String, Object> userData = new HashMap<>();
        userData.put("department", departmentInput);
        userData.put("bio", bioInput);
        userData.put("availability", availability);
        userData.put("profile_pic", fileUrl);

        Log.d("MainAct", userData.toString());

        db.collection("tutor").document(documentId).update(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void Void) {
                        Intent intent = new Intent(TutorRegisterActivity.this, TutorMainActivity.class);
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
                                Toast.makeText(TutorRegisterActivity.this, "UPLOADED", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("MainAct", e.getMessage());
                                Toast.makeText(TutorRegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("MainAct2", e.getMessage());

                Toast.makeText(TutorRegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                ImageUri = saveImage(img, TutorRegisterActivity.this);

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