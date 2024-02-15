package com.example.assignment;

import android.app.FragmentManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class StudentSearchFragment extends Fragment {
    View view;
    List<String> uidList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<InfoModel> infoList = new ArrayList<>();
    String data = "";
    InfoAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void getTutorList() {

    }

    private void onDetails(String uid, int layoutId) {
        Bundle mBundle = new Bundle();
        mBundle.putString("uid",uid);
        TutorProfileFragment tutorProfileFragment = new TutorProfileFragment();
        tutorProfileFragment.setArguments(mBundle);
        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(layoutId, tutorProfileFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (infoList.size()!=0) {
            adapter.clear();
        }
        db.collection("tutor").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Log.e("MainAct", "found");
//                        document.getData().get("bio");
                        uidList.add(document.getData().get("uId").toString());
                        Log.d("MainAct", "data: " + document.getData().get("name").toString());
                        infoList.add(new InfoModel(document.getData().get("name").toString(), document.getData().get("bio").toString(), document.getData().get("department").toString(), document.getData().get("profile_pic").toString()));
                    }
                }
                Log.d("MainAct", "infodata" + infoList.get(1).getProfileUrl());
//
                adapter.notifyDataSetChanged();
            }
        });
        adapter = new InfoAdapter(getActivity().getApplicationContext(), infoList);
        view = inflater.inflate(R.layout.fragment_student_search, container, false);

        ListView listview = view.findViewById(R.id.listId);
        listview.setAdapter(adapter);

        Bundle bundle = getArguments();
        int layoutId = bundle.getInt("layoutId");

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                bundle.putString("studentData", data);
                onDetails(uidList.get(position), layoutId);

            }
        });

        return view;
    }
}