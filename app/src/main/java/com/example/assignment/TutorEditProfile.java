package com.example.assignment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.ExifInterface;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TutorEditProfile extends Fragment {
    CommonClass commonClass = new CommonClass();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    View view;
    String tutorId;
    int layoutId;
    ShapeableImageView imageView;
    TextView errMsg;
    Button submitId;
    Button monFromId;
    Button monToId;
    Button tueFromId;
    Button tueToId;
    Button wedFromId;
    Button wedToId;
    Button thuFromId;
    Button thuToId;
    Button friFromId;
    Button friToId;
    int hour, minute;
    TextInputLayout nameId;
    TextInputLayout departmentId;
    TextInputLayout bioId;
    TextInputLayout mainLocationId;
    List<String> locationList = new ArrayList<>();
    ArrayAdapter<String> spinnerAdapter;
    AutoCompleteTextView autoCompleteTextView;
    public final int CAMERA_REQ_CODE = 100;
    public final int GALLERY_REQ_CODE = 200;
    final int CAMERA_PERMISSION_CODE = 1001;
    private Uri ImageUri;
    private Bitmap bitmap;
    String profileUrl = "";
    String checkProfileUrl = "";
    Boolean removeImage = false;
    LinearLayout removeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        mBundle.putString("user_id", tutorId);
        mBundle.putInt("layoutId", layoutId);
        TutorProfileFragment profileFragment = new TutorProfileFragment();
        profileFragment.setArguments(mBundle);
        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(layoutId, profileFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void onUpdateData(String fileUrl) {


        String departmentInput = departmentId.getEditText().getText().toString();
        String bioInput = bioId.getEditText().getText().toString();
        String nameInput = nameId.getEditText().getText().toString();


        Map<String, Object> mondayData = new HashMap<>();
        mondayData.put("from", monFromId.getText());
        mondayData.put("to", monToId.getText());

        Map<String, Object> tuesdayData = new HashMap<>();
        tuesdayData.put("from", tueFromId.getText());
        tuesdayData.put("to", tueToId.getText());

        Map<String, Object> wednesdayData = new HashMap<>();
        wednesdayData.put("from", wedFromId.getText());
        wednesdayData.put("to", wedToId.getText());

        Map<String, Object> thursdayData = new HashMap<>();
        thursdayData.put("from", thuFromId.getText());
        thursdayData.put("to", thuToId.getText());

        Map<String, Object> fridayData = new HashMap<>();
        fridayData.put("from", friFromId.getText());
        fridayData.put("to", friToId.getText());

        Map<Object, Object> availability = new HashMap<>();
        availability.put("monday", mondayData);
        availability.put("tuesday", tuesdayData);
        availability.put("wednesday", wednesdayData);
        availability.put("thursday", thursdayData);
        availability.put("friday", fridayData);

        Map<String, Object> userData = new HashMap<>();

//        String locationInput = autoCompleteTextView.getText().toString();
        userData.put("department", departmentInput);
        userData.put("bio", bioInput);
        userData.put("availability", availability);
        userData.put("profile_pic", fileUrl);
        userData.put("name", nameInput);

        Log.d("MainAct", userData.toString());
        if (departmentInput.equals("")) {
            setBorderRed(departmentId);
        }
//        if (locationInput.equals("")) {
//            setBorderRed(mainLocationId);
//        }

        if (!departmentInput.equals("")) {
            db.collection("tutor").whereEqualTo("uId", tutorId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().getDocuments().size() != 0) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().update(userData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                                onRedirectProfile();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                            }
                                        });
                            }
                        }
                    }
                }
            });
        } else {
            errMsg.setText("Enter all the required fields");
        }

    }

    private void getAvailability(DocumentSnapshot data) {

        Map<String, Object> timeMap = (Map<String, Object>) data.get("availability");

        Map<String, Object> monday = (Map<String, Object>) timeMap.get("monday");
        assert monday != null;
        monFromId.setText(monday.get("from").toString());
        monToId.setText(monday.get("to").toString());

        Map<String, Object> tuesday = (Map<String, Object>) timeMap.get("tuesday");
        assert tuesday != null;
        tueFromId.setText(tuesday.get("from").toString());
        tueToId.setText(tuesday.get("to").toString());

        Map<String, Object> wednesday = (Map<String, Object>) timeMap.get("wednesday");
        assert wednesday != null;
        wedFromId.setText(wednesday.get("from").toString());
        wedToId.setText(wednesday.get("to").toString());

        Map<String, Object> thursday = (Map<String, Object>) timeMap.get("thursday");
        assert thursday != null;
        thuFromId.setText(thursday.get("from").toString());
        thuToId.setText(thursday.get("to").toString());

        Map<String, Object> friday = (Map<String, Object>) timeMap.get("friday");
        assert friday != null;
        friFromId.setText(friday.get("from").toString());
        friToId.setText(friday.get("to").toString());
    }

    private void updateView(DocumentSnapshot data) {
        profileUrl = data.get("profile_pic").toString();
        checkProfileUrl = data.get("profile_pic").toString();
        if (!data.get("profile_pic").toString().equals("")) {
            commonClass.setImageView(getContext(),data.get("profile_pic").toString(),imageView );
        }
        nameId.getEditText().setText(data.get("name").toString());
        departmentId.getEditText().setText(data.get("department").toString());
//        mainLocationId.getEditText().setText(data.get("office_location").toString());
        bioId.getEditText().setText(data.get("bio").toString());

    }

    private void getProfileDetails() {
        db.collection("tutor").whereEqualTo("uId", tutorId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            if (document.getDocuments().size() != 0) {
                                updateView(document.getDocuments().get(0));
                                getAvailability(document.getDocuments().get(0));
                                Log.d("MainActivity", "DocumentSnapshot data: " + document.getDocuments().get(0));
                            } else {
                                Log.d("MainActivity", "No such document");

                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("MainActivity", "Error adding document", e);
                    }
                });
    }


    private void showTimePickerDialog(Button btn) {
        Calendar calendar = Calendar.getInstance();

        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfDay) {
                        hour = hourOfDay;
                        minute = minuteOfDay;

                        String time = String.format("%02d:%02d", hour, minute);
                        btn.setText(time);
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }

//    private void getLocationList(View view) {
//        db.collection("campus_buildings").document("building_list").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.exists()) {
//                    Map<String, Object> campusLocations = documentSnapshot.getData();
//                    for (Map.Entry<String, Object> entry : campusLocations.entrySet()) {
////                        String key = entry.getKey();
//                        Object value = entry.getValue();
//                        locationList.add(value.toString());
//                    }
//
//                    spinnerAdapter.notifyDataSetChanged();
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//            }
//        });
//        Log.d("MainAct", "location:" + locationList.toString());
//        Context context=getActivity();
//        autoCompleteTextView = view.findViewById(R.id.officeLocationId1);
//        spinnerAdapter = new ArrayAdapter<>(context, R.layout.location_dropdown_item, locationList);
//        autoCompleteTextView.setAdapter(spinnerAdapter);
//    }

    private void openCamera() {
        removeImage = false;
        Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(iCamera, CAMERA_REQ_CODE);

    }

    private void reqCameraPermission(){
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();

        } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
            AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
            builder.setMessage("This app requires CAMERA permission for this feature to use.")
                    .setTitle("PermissionRequired")
                    .setCancelable(false)
                    .setPositiveButton("OK", ((dialogInterface, i) -> {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                        dialogInterface.dismiss();
                    }))
                    .setNegativeButton("Cancel", (((dialogInterface, i) -> dialogInterface.dismiss())));

        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
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

//    private int getImageOrientation(Uri imageUri) {
//        try {
//            InputStream inputStream = requireActivity().getContentResolver().openInputStream(imageUri);
//            if (inputStream != null) {
//                ExifInterface exifInterface = null;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                    exifInterface = new ExifInterface(inputStream);
//                }
//                return exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return ExifInterface.ORIENTATION_UNDEFINED;
//    }
//
//    private Bitmap getRotatedBitmap(Uri imageUri, int orientation) {
//        Bitmap bitmap;
//        try {
//            bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
//            if (orientation != ExifInterface.ORIENTATION_UNDEFINED) {
//                Matrix matrix = new Matrix();
//                switch (orientation) {
//                    case ExifInterface.ORIENTATION_ROTATE_90:
//                        matrix.postRotate(90);
//                        break;
//                    case ExifInterface.ORIENTATION_ROTATE_180:
//                        matrix.postRotate(180);
//                        break;
//                    case ExifInterface.ORIENTATION_ROTATE_270:
//                        matrix.postRotate(270);
//                        break;
//                    default:
//                        return bitmap;
//                }
//                return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }



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
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("MainAct2", e.getMessage());
            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_tutor_edit_profile, container, false);
        Bundle bundle = getArguments();
        tutorId = bundle.getString("tutorId");
        layoutId = bundle.getInt("layoutId");
        nameId = view.findViewById(R.id.nameId);
        departmentId = view.findViewById(R.id.departmentId);
        bioId = view.findViewById(R.id.bioId);
        imageView = view.findViewById(R.id.profileImage);
        errMsg = view.findViewById(R.id.errMsg);
        submitId = view.findViewById(R.id.submitId);
//        mainLocationId = view.findViewById(R.id.mainLocationInput);
//        getLocationList(view);

        getProfileDetails();


        monFromId = view.findViewById(R.id.monFromId);
        monFromId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(monFromId);
            }
        });

        monToId = view.findViewById(R.id.monToId);
        monToId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(monToId);
            }
        });

        tueFromId = view.findViewById(R.id.tueFromId);
        tueFromId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(tueFromId);
            }
        });

        tueToId = view.findViewById(R.id.tueToId);
        tueToId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(tueToId);
            }
        });

        wedFromId = view.findViewById(R.id.wedFromId);
        wedFromId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(wedFromId);
            }
        });

        wedToId = view.findViewById(R.id.wedToId);
        wedToId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(wedToId);
            }
        });

        thuFromId = view.findViewById(R.id.thuFromId);
        thuFromId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(thuFromId);
            }
        });

        thuToId = view.findViewById(R.id.thuToId);
        thuToId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(thuToId);
            }
        });

        friFromId = view.findViewById(R.id.friFromId);
        friFromId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(friFromId);
            }
        });

        friToId = view.findViewById(R.id.friToId);
        friToId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(friToId);
            }
        });

        submitId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (removeImage && !profileUrl.equals("")) {
                    Log.d("MainAct", "call1");
                    onDeletefileImage();
                } else if (ImageUri != null) {
                    Log.d("MainAct", "call2");
                    getFileName();
                } else if (ImageUri == null && !profileUrl.isEmpty()) {
                    Log.d("MainAct", "call3");
                    onUpdateData(profileUrl);
                } else {
                    Log.d("MainAct", "call4");
                    onUpdateData("");
                }

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer();
            }
        });

        return view;
    }
}