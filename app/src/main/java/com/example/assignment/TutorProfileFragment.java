package com.example.assignment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TutorProfileFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    View view;
    Dialog dialog;
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> spinnerAdapter;
    List<String> timeList = new ArrayList<>();
    String dateInput;
    Map<String, Object> timeMap;
    String tutorId;
    String studentId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void updateView(View view, DocumentSnapshot data) {
        ShapeableImageView profileView = view.findViewById(R.id.profileImage);
        TextView nameView = view.findViewById(R.id.nameId);
        TextView departmentView = view.findViewById(R.id.departmentId);
        TextView statusView = view.findViewById(R.id.statusId);
        TextView locationView = view.findViewById(R.id.officeId);
        TextView currentLocationView = view.findViewById(R.id.locationId);
        TextView currentLocationHeader = view.findViewById(R.id.locationTextId);


        if (!data.get("profile_pic").toString().equals("")) {
            UrlImage obj = new UrlImage(data.get("profile_pic").toString(), profileView);
            obj.execute();
        }
        if (data.get("check_in").equals(data.get("office_location"))) {
            statusView.setText("In Office");
            statusView.setTextColor(ContextCompat.getColor(getContext(), R.color.success));
            currentLocationView.setVisibility(View.GONE);
            currentLocationHeader.setVisibility(View.GONE);
        } else {
            statusView.setText("Out of Office");
            statusView.setTextColor(ContextCompat.getColor(getContext(), R.color.danger));
        }

        if (!data.get("check_in").equals("")) {
            currentLocationView.setText(data.get("check_in").toString());
        } else {
            currentLocationView.setVisibility(View.GONE);
            currentLocationHeader.setVisibility(View.GONE);
        }

        locationView.setText(data.get("office_location").toString());
        nameView.setText(data.get("name").toString());
        departmentView.setText(data.get("department").toString());
    }

    private void getAvailability(View view, DocumentSnapshot data) {
        TextView monFrom = view.findViewById(R.id.monFromId);
        TextView monTo = view.findViewById(R.id.monToId);
        TextView tueFrom = view.findViewById(R.id.tueFromId);
        TextView tueTo = view.findViewById(R.id.tueToId);
        TextView wedFrom = view.findViewById(R.id.wedFromId);
        TextView wedTo = view.findViewById(R.id.wedToId);
        TextView thuFrom = view.findViewById(R.id.thuFromId);
        TextView thuTo = view.findViewById(R.id.thuToId);
        TextView friFrom = view.findViewById(R.id.friFromId);
        TextView friTo = view.findViewById(R.id.friToId);

        timeMap = (Map<String, Object>) data.get("availability");

        Map<String, Object> monday = (Map<String, Object>) timeMap.get("monday");
        assert monday != null;
        monFrom.setText(monday.get("from").toString());
        monTo.setText(monday.get("to").toString());

        Map<String, Object> tuesday = (Map<String, Object>) timeMap.get("tuesday");
        assert tuesday != null;
        tueFrom.setText(tuesday.get("from").toString());
        tueTo.setText(tuesday.get("to").toString());

        Map<String, Object> wednesday = (Map<String, Object>) timeMap.get("wednesday");
        assert wednesday != null;
        wedFrom.setText(wednesday.get("from").toString());
        wedTo.setText(wednesday.get("to").toString());

        Map<String, Object> thursday = (Map<String, Object>) timeMap.get("thursday");
        assert thursday != null;
        thuFrom.setText(thursday.get("from").toString());
        thuTo.setText(thursday.get("to").toString());

        Map<String, Object> friday = (Map<String, Object>) timeMap.get("friday");
        assert friday != null;
        friFrom.setText(friday.get("from").toString());
        friTo.setText(friday.get("to").toString());
    }

    private void getProfileDetails(String id, View view) {
        db.collection("tutor").whereEqualTo("uId", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            if (document.getDocuments().size() != 0) {
                                updateView(view, document.getDocuments().get(0));
                                getAvailability(view, document.getDocuments().get(0));
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


    private Calendar addMinutes(Calendar cal, int minutes) {
        Calendar newCal = (Calendar) cal.clone();
        newCal.add(Calendar.MINUTE, minutes);
        return newCal;
    }

    private void getData(String week_name) {
        Map<String, Object> new_time = (Map<String, Object>) timeMap.get(week_name.toLowerCase());
        if (timeList.size() != 0) {
            timeList.clear();
        }
        String startTimeStr = new_time.get("from").toString();
        String endTimeStr = new_time.get("to").toString();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        try {
            Date startTime = sdf.parse(startTimeStr);
            Date endTime = sdf.parse(endTimeStr);

            Calendar calStart = Calendar.getInstance();
            calStart.setTime(startTime);

            Calendar calEnd = Calendar.getInstance();
            calEnd.setTime(endTime);

            while (calStart.before(calEnd)) {
                String from_time = sdf.format(calStart.getTime());
                String to_time = sdf.format(addMinutes(calStart, 30).getTime());
                timeList.add(from_time + " - " + to_time);
                System.out.println(sdf.format(calStart.getTime()) + " - " + sdf.format(addMinutes(calStart, 30).getTime()));
                calStart.add(Calendar.MINUTE, 30);
            }
            spinnerAdapter.notifyDataSetChanged();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void onRegisterAppoinment() {
        String timeInput = autoCompleteTextView.getText().toString();

        Log.d("MainAct", dateInput);
        Log.d("MainAct", timeInput);

        Map<String, Object> appoinment_data = new HashMap<>();
        appoinment_data.put("date", dateInput);
        appoinment_data.put("time", timeInput);
        appoinment_data.put("tutor", tutorId);
        appoinment_data.put("student", studentId);

        db.collection("appoinment").add(appoinment_data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("MainActivity", "Error adding document", e);
                    }
                });
    }


    private void showDatePickerDialog() {
        // Get current date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog and show it
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
                        String weekName = sdf.format(selectedCalendar.getTime());
                        getData(weekName);

                        Log.d("MainAct", weekName);

                        dateInput = dayOfMonth + "/" + (month + 1) + "/" + year;
                    }
                },
                year, month, dayOfMonth
        );
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void getDialogData() {
        autoCompleteTextView = dialog.findViewById(R.id.timeId);
        spinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.location_dropdown_item, timeList);
        autoCompleteTextView.setAdapter(spinnerAdapter);
        Button appoinmentSubmit = dialog.findViewById(R.id.appoinmentSubmit);
        Button dateButton = dialog.findViewById(R.id.dateButton);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                showDatePickerDialog();
            }
        });
        appoinmentSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                onRegisterAppoinment();
            }
        });
    }

    public void onSubmit(View view) {
        dialog.setContentView(R.layout.appoinment_form);
        getDialogData();
        dialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        tutorId = bundle.getString("tutorUid");
        studentId = bundle.getString("studentId");

        Log.d("MainActivity", "uuid data:" + tutorId);
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_tutor_profile, container, false);
        getProfileDetails(tutorId, view);

        Button submitBtn = view.findViewById(R.id.submitId);
        dialog = new Dialog(getContext());
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                onSubmit(view);
            }
        });


        return view;
    }
}