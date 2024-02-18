package com.example.assignment;

import android.app.Activity;
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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class StudentHomeFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //    Map<String, Object> slotList = new HashMap<>();
    List<AppoinmentObject> slotList = new ArrayList<>();

    InfoAdapter adapter;
    ListView listview;
    View view;
    String studentId;

    List<String> uidList = new ArrayList<>();
    List<InfoModel> infoList = new ArrayList<>();
    CommonClass commonClass = new CommonClass();
    int layoutId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void onDetails(String uid, int layoutId) {
        Bundle mBundle = new Bundle();
        mBundle.putString("tutorUid", uid);
        mBundle.putString("studentId", studentId);
        mBundle.putInt("layoutId", layoutId);
        TutorProfileFragment tutorProfileFragment = new TutorProfileFragment();
        tutorProfileFragment.setArguments(mBundle);
        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(layoutId, tutorProfileFragment);
//        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private void getTutorData(View v) {
        try {
            if (infoList.size() != 0) {
                adapter.clear();
            }
            if (uidList.size() != 0) {
                uidList.clear();
            }

            for (int i = 0; i < slotList.size(); i++) {

                String date_time = commonClass.getDateTime(slotList.get(i).getDate().toString(), slotList.get(i).getTime().toString());

                db.collection("tutor").whereEqualTo("uId", slotList.get(i).tutorId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getDocuments().size() != 0) {
                                HashSet<String> uniqueDocumentIds = new HashSet<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (uniqueDocumentIds.add(document.getId())) {
                                        uidList.add(document.getData().get("uId").toString());
                                        infoList.add(new InfoModel(document.getData().get("name").toString(),
                                                document.getData().get("office_location").toString(),
                                                document.getData().get("department").toString(),
                                                document.getData().get("profile_pic").toString(),
                                                date_time,
                                                document.getData().get("check_in").toString().equals(document.getData().get("office_location").toString())
                                        ));
                                    }
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            adapter = new InfoAdapter(getActivity().getApplicationContext(), infoList);


            listview = v.findViewById(R.id.listId);
            listview.setAdapter(adapter);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    onDetails(uidList.get(position), layoutId);

                }
            });
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void getRegisteredAppointments(View v) {
        db.collection("appoinment").whereEqualTo("student", studentId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getDocuments().size() != 0) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            AppoinmentObject obj = new AppoinmentObject(document.getData().get("tutor").toString(), document.getData().get("date").toString(), document.getData().get("time").toString());
                            slotList.add(obj);
                            Log.d("MainAct", document.getData().toString());

//                            slotList.put("data", slotListData);
                        }
                    }
                }
                getTutorData(v);
            }
        });


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_student_home, container, false);
        Bundle bundle = getArguments();
        studentId = bundle.getString("user_id");
        layoutId = bundle.getInt("layoutId");
        getRegisteredAppointments(view);
        return view;
    }
}