package com.example.assignment;

import android.app.FragmentManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.C;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class StudentSearchFragment extends Fragment {
    View view;
    List<String> uidList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<InfoModel> infoList = new ArrayList<>();
    String data = "";
    InfoAdapter adapter;
    ListView listview;
    String studentID;
    int layoutId;
    List<AppoinmentObject> slotList = new ArrayList<>();
    CommonClass commonClass = new CommonClass();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void onDetails(String uid, int layoutId) {
        Bundle mBundle = new Bundle();
        mBundle.putString("user_id", uid);
        mBundle.putString("studentId", studentID);
        mBundle.putInt("layoutId", layoutId);
        TutorProfileFragment tutorProfileFragment = new TutorProfileFragment();
        tutorProfileFragment.setArguments(mBundle);
        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(layoutId, tutorProfileFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private void onTextChanges(String val) {

        if (infoList.size() != 0) {
            adapter.clear();
        }
        if (uidList.size() != 0) {
            uidList.clear();
        }

        db.collection("tutor").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    HashSet<String> uniqueDocumentIds = new HashSet<>();
                    HashSet<String> nextUniqueDocumentIds = new HashSet<>();
                    if (task.getResult().getDocuments().size() != 0) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.contains("department")) {
                                if (document.getData().get("name").toString().toLowerCase().startsWith(val.toLowerCase()) || document.getData().get("department").toString().toLowerCase().startsWith(val.toLowerCase())) {
                                    if (uniqueDocumentIds.add(document.getId())) {
                                        uidList.add(document.getData().get("uId").toString());
                                        infoList.add(new InfoModel(document.getData().get("name").toString(),
                                                document.getData().get("office_location").toString(),
                                                document.getData().get("department").toString(),
                                                document.getData().get("profile_pic").toString(),
                                                "",
                                                false
                                        ));
//                                    for (AppoinmentObject obj : slotList) {
//
//                                        if (obj.getTutorId().equals(document.getData().get("uId"))) {
////                                            try {
////                                                infoList.add(new InfoModel(document.getData().get("name").toString(),
////                                                        document.getData().get("office_location").toString(),
////                                                        document.getData().get("department").toString(),
////                                                        document.getData().get("profile_pic").toString(),
////                                                        commonClass.getDateTime(obj.getDate(), obj.getTime()),
////                                                        false
////                                                ));
////
////                                            } catch (ParseException e) {
////                                                throw new RuntimeException(e);
////                                            }
//                                        } else {
//                                            if (nextUniqueDocumentIds.add(document.getId())) {
//                                                Log.d("MainAct", document.getData().get("name").toString());
////                                                break;
//                                                infoList.add(new InfoModel(document.getData().get("name").toString(),
//                                                        document.getData().get("office_location").toString(),
//                                                        document.getData().get("department").toString(),
//                                                        document.getData().get("profile_pic").toString(),
//                                                        "",
//                                                        false
//                                                ));
//                                            }
//                                        }
//                                    }

                                    }
                                }
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        adapter = new InfoAdapter(getActivity().getApplicationContext(), infoList);


        listview = view.findViewById(R.id.listId);
        listview.setAdapter(adapter);

        Bundle bundle = getArguments();
        layoutId = bundle.getInt("layoutId");
        studentID = bundle.getString("user_id");
        Log.d("MainAct", "stu:" + studentID);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//temp
                onDetails(uidList.get(position), layoutId);

            }
        });
    }

//    private void getAppoinmentList() {
//        db.collection("appoinment").whereEqualTo("student", "UmAA5gUrzdecZc8VUt2k6PCtczj1").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    if (task.getResult().getDocuments().size() != 0) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            AppoinmentObject obj = new AppoinmentObject(document.getData().get("tutor").toString(), document.getData().get("date").toString(), document.getData().get("time").toString());
//                            slotList.add(obj);
//                            Log.d("MainAct", document.getData().toString());
//
//                        }
//                    }
//                }
//                onTextChanges("");
//
//            }
//        });
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_student_search, container, false);

        TextInputLayout textInputLayout = view.findViewById(R.id.searchInput);
        EditText editText = textInputLayout.getEditText();

        if (editText != null) {
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_DONE ||
                            actionId == EditorInfo.IME_ACTION_NEXT ||
                            actionId == EditorInfo.IME_ACTION_SEND ||
                            (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                                    keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                        onTextChanges(editText.getText().toString());
                        Log.d("MainAct", editText.getText().toString());
                        return true;
                    }
                    return false;
                }
            });
        }
//        getAppoinmentList();
        onTextChanges("");


        return view;
    }
}