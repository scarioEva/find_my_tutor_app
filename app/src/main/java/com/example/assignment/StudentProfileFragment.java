package com.example.assignment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class StudentProfileFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    View view;
    String studentId;
    String tutorId;
    int layoutId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void updateView(View view, DocumentSnapshot data) {
        TextView nameView = view.findViewById(R.id.nameId);
        TextView courseView=view.findViewById(R.id.courseId);
        TextView acView=view.findViewById(R.id.accademicYearId);
        TextView phoneView=view.findViewById(R.id.phoneId);
        TextView bioView=view.findViewById(R.id.bioId);
        TextView bView=view.findViewById(R.id.b);
        TextView pView=view.findViewById(R.id.p);


        ImageView editIcom=view.findViewById(R.id.editIcon);
        ShapeableImageView profileView = view.findViewById(R.id.profileImage);
        if (!data.get("profile_pic").toString().equals("")) {
            UrlImage obj = new UrlImage(data.get("profile_pic").toString(), profileView);
            obj.execute();
        }

        nameView.setText(data.get("name").toString());
        courseView.setText(data.get("course").toString());
        acView.setText(data.get("accademic_year").toString());

        if (!data.get("bio").equals("")) {
            bioView.setText(data.get("bio").toString());
        } else {
            bioView.setVisibility(View.GONE);
            bView.setVisibility(View.GONE);
        }

        if (!data.get("phone").equals("")) {
            phoneView.setText(data.get("phone").toString());
        } else {
            phoneView.setVisibility(View.GONE);
            pView.setVisibility(View.GONE);
        }
        if (tutorId != null) {
            editIcom.setVisibility(View.GONE);
        }
        else {
            editIcom.setVisibility(View.VISIBLE);
        }
    }
    private void getProfileDetails(String id, View view) {
        db.collection("student").whereEqualTo("uId", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            if (document.getDocuments().size() != 0) {
                                updateView(view, document.getDocuments().get(0));
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_student_profile, container, false);
        Bundle bundle = getArguments();
        studentId = bundle.getString("studentData");
        Log.d("MainAct", "tutor main student id: "+ studentId);
        layoutId = bundle.getInt("layoutId");
        tutorId=bundle.getString("tutorUid");

        getProfileDetails(studentId, view);

        return view;
    }
}