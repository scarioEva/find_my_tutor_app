package com.example.assignment;

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
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class StudentEditProfile extends Fragment {
    CommonClass commonClass = new CommonClass();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    View view;
    String studentId;
    int layoutId;
    ShapeableImageView imageView;
    TextInputLayout nameView;
    TextInputLayout courseView;
    TextInputLayout accYrView;
    TextInputLayout phoneView;
    TextInputLayout bioView;
    TextView errMsg;
    Button submitId;
    public final int CAMERA_REQ_CODE = 100;
    public final int GALLERY_REQ_CODE = 200;
    final int CAMERA_PERMISSION_CODE = 1001;
    private Uri ImageUri;
    private Bitmap bitmap;
    String profileUrl = "";
    String checkProfileUrl = "";
    Boolean removeImage = false;
    LinearLayout removeLayout;
    Loader loader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void updateView(DocumentSnapshot data) {
        profileUrl = data.get("profile_pic").toString();
        checkProfileUrl = data.get("profile_pic").toString();

        if (!data.get("profile_pic").toString().equals("")) {
            commonClass.setImageView(getContext(),data.get("profile_pic").toString(),imageView );
        }

        nameView.getEditText().setText(data.get("name").toString());
        courseView.getEditText().setText(data.get("course").toString());
        accYrView.getEditText().setText(data.get("accademic_year").toString());
        phoneView.getEditText().setText(data.get("phone").toString());
        bioView.getEditText().setText(data.get("bio").toString());

    }

    private void getProfileDetails(String id) {
        loader.startLoading();
        db.collection("student").whereEqualTo("uId", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            loader.stopLoading();
                            QuerySnapshot document = task.getResult();
                            if (document.getDocuments().size() != 0) {
                                updateView(document.getDocuments().get(0));
                            } else {
                                Log.d("MainActivity", "No such document");

                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loader.stopLoading();
                        Log.w("MainActivity", "Error adding document", e);
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

    private void onRedirectProfile() {
        Bundle mBundle = new Bundle();
        mBundle.putString("user_id", studentId);
        mBundle.putInt("layoutId", layoutId);
        StudentProfileFragment profileFragment = new StudentProfileFragment();
        profileFragment.setArguments(mBundle);
        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(layoutId, profileFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private void onUpdateData(String profileUrl) {
        String nameInput = nameView.getEditText().getText().toString();
        String courseInput = courseView.getEditText().getText().toString();
        String phoneInput = phoneView.getEditText().getText().toString();
        String accademicInput = accYrView.getEditText().getText().toString();
        String bioInput = bioView.getEditText().getText().toString();


        if (nameInput.equals("")) {
            setBorderRed(nameView);
        }

        if (courseInput.equals("")) {
            setBorderRed(courseView);
        }

        if (accademicInput.equals("")) {
            setBorderRed(accYrView);
        }

        if (!courseInput.equals("") && !accademicInput.equals("") && !nameInput.equals("")) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", nameInput);
            userData.put("course", courseInput);
            userData.put("accademic_year", accademicInput);
            userData.put("phone", phoneInput);
            userData.put("bio", bioInput);
            userData.put("profile_pic", profileUrl);

            db.collection("student").whereEqualTo("uId", studentId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().getDocuments().size() != 0) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().update(userData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                loader.stopLoading();
                                                Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                                onRedirectProfile();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                loader.stopLoading();
                                                errMsg.setText("Something went wrong. Please try again.");
                                            }
                                        });
                            }
                        }
                    }
                }
            });
        } else {
            loader.stopLoading();
            errMsg.setText("Enter all the required fields.");
        }

    }

    private void openCamera() {
        removeImage = false;
        Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(iCamera, CAMERA_REQ_CODE);

    }

    private void reqCameraPermission(){
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();

        } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.CAMERA)) {
            AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
            builder.setMessage("This app requires CAMERA permission for this feature to use.")
                    .setTitle("PermissionRequired")
                    .setCancelable(false)
                    .setPositiveButton("OK", ((dialogInterface, i) -> {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                        dialogInterface.dismiss();
                    }))
                    .setNegativeButton("Cancel", (((dialogInterface, i) -> dialogInterface.dismiss())));

        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                    builder.setMessage("This app requires CAMERA permission for this feature to use.")
                            .setTitle("PermissionRequired")
                            .setCancelable(false)
                            .setPositiveButton("Settings", ((dialogInterface, i) -> {
                                Intent intent_i=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri u=Uri.fromParts("package", requireContext().getPackageName(), null);
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
    public void openDrawer() {
        Log.d("MainAct", "opened");
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_drawable_layout);
        LinearLayout captureLayout = dialog.findViewById(R.id.drawableItm1ID);
        LinearLayout fileLayout = dialog.findViewById(R.id.drawableItm2ID);
        removeLayout = dialog.findViewById(R.id.drawableItm3ID);

        if (!checkProfileUrl.equals("") || ImageUri != null) {
            removeLayout.setVisibility(View.VISIBLE);
            removeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeImage = true;
                    ImageUri = null;
                    checkProfileUrl = "";
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable._184159_3094350));
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
                removeImage = false;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQ_CODE) {
                Bitmap img = (Bitmap) (data.getExtras().get("data"));
                imageView.setImageBitmap(img);
                ImageUri = commonClass.saveImage(img, getContext());
                Log.d("MainAct", "image: " + ImageUri.toString());
            } else if (requestCode == GALLERY_REQ_CODE) {
                if (data != null & data.getData() != null) {
                    ImageUri = data.getData();
                    int orientation = commonClass.getImageOrientation(ImageUri, requireActivity());
                    bitmap =commonClass.getRotatedBitmap(ImageUri, orientation, requireActivity());
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }



    private void getFileName() {

        if (profileUrl.equals("")) {
            uploadImageStorage(commonClass.generateRandomString(10));
        } else {
            StorageReference storageRef = storage.getReferenceFromUrl(profileUrl);
            String fileName = storageRef.getName();
            uploadImageStorage(fileName);

        }
    }

    private void onDeletefileImage() {
        StorageReference storageRef = storage.getReferenceFromUrl(profileUrl);
        storageRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        onUpdateData("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loader.stopLoading();
                        errMsg.setText("Something went wrong. Please try again.");
                    }
                });
    }

    private void uploadImageStorage(String fileName) {

        StorageReference profileIngRef;

        profileIngRef = storageRef.child("profile_image/" + fileName);

        profileIngRef.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profileIngRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                if (uri != null) {
                                    onUpdateData(uri.toString());
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("MainAct", e.getMessage());
                                errMsg.setText("Image unable to upload. Please Try differnt image");
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loader.stopLoading();
                errMsg.setText("Image unable to upload. Please Try differnt image");
                Log.d("MainAct2", e.getMessage());
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_student_edit_profile, container, false);
        Bundle bundle = getArguments();
        loader = new Loader(getActivity());
        studentId = bundle.getString("studentData");
        layoutId = bundle.getInt("layoutId");
        nameView = view.findViewById(R.id.nameId);
        courseView = view.findViewById(R.id.courseId);
        accYrView = view.findViewById(R.id.accademicYearId);
        phoneView = view.findViewById(R.id.phoneId);
        bioView = view.findViewById(R.id.bioId);
        imageView = view.findViewById(R.id.profileImage);
        errMsg = view.findViewById(R.id.errMsg);
        submitId = view.findViewById(R.id.submitId);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer();
            }
        });
        submitId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loader.startLoading();
                if (removeImage && !profileUrl.equals("")) {
                    onDeletefileImage();
                } else if (ImageUri != null) {
                    getFileName();
                } else if (ImageUri == null && !profileUrl.isEmpty()) {
                    onUpdateData(profileUrl);
                } else {
                    onUpdateData("");
                }

            }
        });
        getProfileDetails(studentId);
        return view;
    }
}