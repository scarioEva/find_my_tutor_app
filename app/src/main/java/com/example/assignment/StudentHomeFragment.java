package com.example.assignment;

import android.app.Activity;
import android.content.Context;
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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class StudentHomeFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //    Map<String, Object> slotList = new HashMap<>();
    List<AppoinmentObject> slotList = new ArrayList<>();
    RelativeLayout emptyMsg;
    InfoAdapter adapter;
    ListView listview;
    View view;
    String studentId;

    List<String> uidList = new ArrayList<>();
    List<InfoModel> infoList = new ArrayList<>();
    CommonClass commonClass = new CommonClass();
    int layoutId;
    List<AppoinmentObject> appointmentList = new ArrayList<>();
    List<String> deleteList = new ArrayList<>();
    String studentName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void onDetails(String uid, int layoutId) {
        Bundle mBundle = new Bundle();
        mBundle.putString("user_id", uid);
        mBundle.putString("studentId", studentId);
        mBundle.putString("studentName", studentName);
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

                db.collection("tutor").whereEqualTo("uId", slotList.get(i).getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        listview.setVisibility(View.VISIBLE);
                        emptyMsg.setVisibility(View.GONE);

                        List<QueryDocumentSnapshot> documents = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            documents.add(document);
                        }

                        Collections.sort(documents, new Comparator<QueryDocumentSnapshot>() {
                            @Override
                            public int compare(QueryDocumentSnapshot doc1, QueryDocumentSnapshot doc2) {
                                String dateString1 = doc1.getString("date");
                                String dateString2 = doc2.getString("date");

                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                Date date1 = null, date2 = null;
                                try {
                                    date1 = dateFormat.parse(dateString1);
                                    date2 = dateFormat.parse(dateString2);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }


                                if (date1 != null && date2 != null) {
//                                  Compare dates: https://stackoverflow.com/questions/2592501/how-to-compare-dates-in-java
                                    return date1.compareTo(date2);
                                }
                                return 0;
                            }
                        });

                        for (QueryDocumentSnapshot document : documents) {
                            AppoinmentObject obj = new AppoinmentObject(document.getData().get("tutor").toString(), document.getData().get("date").toString(), document.getData().get("time").toString(), "");
                            slotList.add(obj);
                        }
                    } else {
                        listview.setVisibility(View.GONE);
                        emptyMsg.setVisibility(View.VISIBLE);
                    }
                }
                getTutorData(v);
            }
        });


    }


    private void deleteAppointments() {
        if (appointmentList.size() != 0) {
            appointmentList.clear();
        }
//        Firebase WriteBatch: https://www.youtube.com/watch?v=BTYsml-OlnY
        WriteBatch batch = db.batch();
        if (deleteList.size() != 0) {
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
        } else {
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

    //for checking if current date exceeds then delete appointment
    public void getAppointmentList(String uid) {
        db.collection("appoinment").whereEqualTo("student", uid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getDocuments().size() != 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String docId = document.getId();
                                    String date = document.getData().get("date").toString();
                                    String time = document.getData().get("time").toString();
                                    AppoinmentObject obj = new AppoinmentObject("", date, time, docId);
                                    appointmentList.add(obj);
                                }
                                checkAppointmentDone();
                            } else {
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

        view = inflater.inflate(R.layout.fragment_student_home, container, false);
        Bundle bundle = getArguments();
        studentId = bundle.getString("user_id");
        layoutId = bundle.getInt("layoutId");
        studentName = bundle.getString("studentName");
        listview = view.findViewById(R.id.listId);
        emptyMsg = view.findViewById(R.id.emptyMsg);
        emptyMsg.setVisibility(View.GONE);
        getAppointmentList(studentId);
        return view;
    }
}