package com.example.assignment;

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
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class TutorHomeFragment extends Fragment {
    View view;
    String tutorId;
    int layoutId;
    RelativeLayout emptyMsg;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<AppoinmentObject> slotList = new ArrayList<>();
    InfoAdapter adapter;
    List<String> uidList = new ArrayList<>();
    List<InfoModel> infoList = new ArrayList<>();
    CommonClass commonClass = new CommonClass();
    List<AppoinmentObject> appointmentList = new ArrayList<>();
    List<String> deleteList = new ArrayList<>();
    ListView listview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void onDetails(String uid, int layoutId) {
        Bundle mBundle = new Bundle();
        mBundle.putString("tutorUid", tutorId);
        mBundle.putString("user_id", uid);
        mBundle.putInt("layoutId", layoutId);
        StudentProfileFragment studentProfileFragment = new StudentProfileFragment();
        studentProfileFragment.setArguments(mBundle);
        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(layoutId, studentProfileFragment);
//        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void getStudentData(View v) {
        try {
            if (infoList.size() != 0) {
                adapter.clear();
            }
            if (uidList.size() != 0) {
                uidList.clear();
            }

            for (int i = 0; i < slotList.size(); i++) {

                String date_time = commonClass.getDateTime(slotList.get(i).getDate().toString(), slotList.get(i).getTime().toString());

                db.collection("student").whereEqualTo("uId", slotList.get(i).getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getDocuments().size() != 0) {
                                HashSet<String> uniqueDocumentIds = new HashSet<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (uniqueDocumentIds.add(document.getId())) {
                                        uidList.add(document.getData().get("uId").toString());
                                        infoList.add(new InfoModel(document.getData().get("name").toString(),
                                                document.getData().get("accademic_year").toString(),
                                                document.getData().get("course").toString(),
                                                document.getData().get("profile_pic").toString(),
                                                date_time,
                                              false
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
        db.collection("appoinment").whereEqualTo("tutor", tutorId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getDocuments().size() != 0) {
                        listview.setVisibility(View.VISIBLE);
                        emptyMsg.setVisibility(View.GONE);
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            AppoinmentObject obj = new AppoinmentObject(document.getData().get("student").toString(), document.getData().get("date").toString(), document.getData().get("time").toString(), "");
                            slotList.add(obj);
                            Log.d("MainAct", "asdassd: "+document.getData().get("student").toString());
                        }
                    }
                    else{
                        listview.setVisibility(View.GONE);
                        emptyMsg.setVisibility(View.VISIBLE);
                    }
                }
                getStudentData(v);
            }
        });
    }

    private void deleteAppointments() {
        if (appointmentList.size() != 0) {
            appointmentList.clear();
        }

        WriteBatch batch = db.batch();
        if(deleteList.size()!=0) {
            for (String documentId : deleteList) {
                DocumentReference docRef = db.collection("appoinment").document(documentId);
                batch.delete(docRef);
            }

            batch.commit()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            deleteList.clear();
                            getRegisteredAppointments(view);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }
        else{
            getRegisteredAppointments(view);
        }
    }

    private void checkAppointmentDone() {
        for (int i = 0; i < appointmentList.size(); i++) {
            if (commonClass.checkDatePassed(appointmentList.get(i).getDate(), appointmentList.get(i).getTime())) {
                deleteList.add(appointmentList.get(i).getDocId());
            }
        }
        deleteAppointments();
    }

    public void getAppointmentList(String uid) {
        db.collection("appoinment").whereEqualTo("tutor", uid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getDocuments().size() != 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String docId = document.getId();
                                    Log.d("doad", "doc get: "+ docId);
                                    String date = document.getData().get("date").toString();
                                    String time = document.getData().get("time").toString();
                                    AppoinmentObject obj = new AppoinmentObject("", date, time, docId);
                                    appointmentList.add(obj);
                                }
                                checkAppointmentDone();
                            }
                            else {
                                getRegisteredAppointments(view);
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
        view = inflater.inflate(R.layout.fragment_tutor_home, container, false);

        Bundle bundle = getArguments();
        tutorId = bundle.getString("user_id");
        layoutId = bundle.getInt("layoutId");
        listview = view.findViewById(R.id.listId);
        emptyMsg=view.findViewById(R.id.emptyMsg);
        emptyMsg.setVisibility(View.GONE);
        getAppointmentList(tutorId);
        return view;
    }
}