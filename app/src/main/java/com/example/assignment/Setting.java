package com.example.assignment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Setting extends Fragment {
    View view;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String id;
    int layoutId;
    String studentName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void redirectPage() {
        Bundle mBundle = new Bundle();
        mBundle.putString("user_id", id);
        mBundle.putInt("layoutId", layoutId);
        AccountSetting accountSetting = new AccountSetting();
        accountSetting.setArguments(mBundle);
        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(layoutId, accountSetting);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void onSignOut() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("token", "");
        db.collection(studentName.equals("")?"tutor":"student").whereEqualTo("uId", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getDocuments().size() != 0) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.contains("token")) {
                                document.getReference().update(userData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

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
            }
        });

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        Bundle bundle = getArguments();
        id = bundle.getString("user_id");
        layoutId = bundle.getInt("layoutId");
        studentName=bundle.getString("studentName");

        Button accSetting = view.findViewById(R.id.accSetting);
        Button logout = view.findViewById(R.id.logout);

        accSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectPage();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSignOut();
            }
        });
        return view;
    }
}