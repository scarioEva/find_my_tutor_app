package com.example.assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.Map;

public class TutorMainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String userId;
    BottomNavigationView bottomNavigationView;
    String checkIn;
    String docId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        userId = getIntent().getStringExtra("uId");

        getTutorDatabase();
    }

    public boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            Bundle mBundle = new Bundle();
//            mBundle.putString("studentData",data);
            fragment.setArguments(mBundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
        }

        return true;
    }

    private void getTutorDatabase() {
        db.collection("tutor").whereEqualTo("uId", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            if (document.getDocuments().size() != 0) {
                                Log.d("MainActivity", " data: " + document.getDocuments().get(0).getId());
                                docId = document.getDocuments().get(0).getId();
                                Log.d("MainActivity", "DocumentSnapshot data: " + document.getDocuments().get(0));
                                String name = "Welcome " + document.getDocuments().get(0).get("name");
                                if (document.getDocuments().get(0).get("check_in") != null) {
                                    checkIn = document.getDocuments().get(0).get("check_in").toString();
                                } else {
                                    checkIn = "";
                                }
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

    private void updateTutorCheckIn(String val) {
        Log.d("MainAct", "valye:"+ val);
        Log.d("MainAct", "checking:"+ checkIn);
        Map<String, Object> tutorData = new HashMap<>();
        tutorData.put("check_in", val.equals(checkIn) ? "" : val);
        db.collection("tutor").document(docId).update(tutorData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void Void) {
                   getTutorDatabase();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("MainActivity", "Error adding document", e);
                    }
                });
    }

    private void onScanQr(String value) {
        db.collection("campus_buildings").document("building_list").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
//                        if (document.getData().get(value) != null) {
//                            Log.d("MainActivity", "DocumentSnapshot data: " +
//                                    document.getData().get(value));
//                            updateTutorCheckIn(document.getData().get(value).toString());
//                        }

                        if (document.getData().get("data") != null) {

                            updateTutorCheckIn(document.getData().get(value).toString());
                        }

                    } else {
                        Log.d("MainActivity", "No such document");
                    }
                } else {
                    Log.d("MainActivity", "get failed with ",
                            task.getException());
                }

            }
        });

    }


    private void initQRCodeScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setOrientationLocked(true);
        integrator.setPrompt("Scan a QR code");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_LONG).show();
            } else {
                onScanQr(result.getContents());
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int id = item.getItemId();

        if (id == R.id.home) {

        } else if (id == R.id.qrIcon) {
            initQRCodeScanner();
        } else if (id == R.id.profileIcon) {

        } else if (id == R.id.settingIcon) {

        }
        return loadFragment(fragment);
    }
}