package com.example.assignment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TutorProfileFragment extends Fragment {
    CommonClass commonClass = new CommonClass();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    View view;
    Dialog dialog;
    MaterialAutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> spinnerAdapter;
    List<String> timeList = new ArrayList<>();
    List<String> slotList = new ArrayList<>();
    String dateInput;
    Map<String, Object> timeMap;
    String tutorId;
    String studentId;
    TextInputLayout timeSelect;
    int layoutId;
    Activity activity;
    BottomNavigationView bottomNavigationView;
    Boolean slotRegistered;
    String studentName;
    String tutor_token;
    Loader loader;
    String studentProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    private void getAppoinmentDetails() {
        Button submitBtn = view.findViewById(R.id.submitId);
        Button cancelBtn = view.findViewById(R.id.cancelId);
        db.collection("appoinment").whereEqualTo("student", studentId).whereEqualTo("tutor", tutorId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getDocuments().size() != 0) {

                        submitBtn.setVisibility(View.GONE);
                        cancelBtn.setVisibility(View.VISIBLE);
                    } else {
                        submitBtn.setVisibility(View.VISIBLE);
                        cancelBtn.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void updateView(View view, DocumentSnapshot data) {
        ShapeableImageView profileView = view.findViewById(R.id.profileImage);
        TextView nameView = view.findViewById(R.id.nameId);
        TextView departmentView = view.findViewById(R.id.departmentId);
        TextView statusView = view.findViewById(R.id.statusId);
        TextView locationView = view.findViewById(R.id.officeId);
        TextView currentLocationView = view.findViewById(R.id.locationId);
        TextView currentLocationHeader = view.findViewById(R.id.locationTextId);
        TextView bioView = view.findViewById(R.id.bioId);
        TextView bView = view.findViewById(R.id.bioHeader);
        ImageView editIcon = view.findViewById(R.id.editIcon);


        if (!data.get("profile_pic").toString().equals("")) {
            commonClass.setImageView(getContext(), data.get("profile_pic").toString(), profileView);
        }
        if (data.get("check_in").equals(data.get("office_location"))) {
            statusView.setText("(In Office)");
            statusView.setTextColor(ContextCompat.getColor(getContext(), R.color.success));
            currentLocationView.setVisibility(View.GONE);
            currentLocationHeader.setVisibility(View.GONE);
        } else {
            statusView.setText("(Out of Office)");
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

        if (!data.get("bio").equals("")) {
            bioView.setText(data.get("bio").toString());
        } else {
            bioView.setVisibility(View.GONE);
            bView.setVisibility(View.GONE);
        }
        if (studentId != null) {
            getAppoinmentDetails();
            editIcon.setVisibility(View.GONE);
        } else {
            statusView.setVisibility(View.GONE);
            editIcon.setVisibility(View.VISIBLE);
        }
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

    private void homeRedirect() {
        dialog.hide();
        Bundle mBundle = new Bundle();
        mBundle.putString("user_id", studentId);
        mBundle.putInt("layoutId", layoutId);
        StudentHomeFragment studentHome = new StudentHomeFragment();
        studentHome.setArguments(mBundle);
        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(layoutId, studentHome);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void getProfileDetails(String id, View view) {
        loader.startLoading();
        db.collection("tutor").whereEqualTo("uId", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            if (document.getDocuments().size() != 0) {
                                updateView(view, document.getDocuments().get(0));
                                getAvailability(view, document.getDocuments().get(0));
                                tutor_token = document.getDocuments().get(0).get("token").toString();
                            } else {
                                Log.d("MainActivity", "No such document");

                            }
                        }
                        loader.stopLoading();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loader.stopLoading();
                        Log.w("MainActivity", "Error adding document", e);
                    }
                });
    }


    private Calendar addMinutes(Calendar cal, int minutes) {
        Calendar newCal = (Calendar) cal.clone();
        newCal.add(Calendar.MINUTE, minutes);
        return newCal;
    }

    private void showError(TextView tw, String value) {
        TextView errMsg = tw;
        errMsg.setText(value);
    }

    private void getData(String week_name) {
        if (timeMap.containsKey(week_name.toLowerCase())) {
            showError(dialog.findViewById(R.id.errMsg), "");
            timeSelect.setVisibility(View.VISIBLE);
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

                Calendar c_start = Calendar.getInstance();
                c_start.setTime(startTime);

                Calendar c_end = Calendar.getInstance();
                c_end.setTime(endTime);

                while (c_start.before(c_end)) {
                    String from_time = sdf.format(c_start.getTime());
                    String to_time = sdf.format(addMinutes(c_start, 30).getTime());
                    String time_slot = from_time + " - " + to_time;
                    if (!slotList.contains(time_slot)) {
                        timeList.add(time_slot);
                    }
                    System.out.println(sdf.format(c_start.getTime()) + " - " + sdf.format(addMinutes(c_start, 30).getTime()));
                    c_start.add(Calendar.MINUTE, 30);
                }
                spinnerAdapter.notifyDataSetChanged();


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            timeSelect.setVisibility(View.GONE);
            showError(dialog.findViewById(R.id.errMsg), "Tutor is not available on selected date.");
        }
    }

    private void senNotification(String date, String time) {
        if (!tutor_token.equals("")) {
            Log.d("MainAct", studentName);
            String title = studentName;
            String body = "Appointment registered at " + date + " (" + time + ")";
            String token = tutor_token;
            CommonClass commonClass = new CommonClass();
            commonClass.sendNotification(studentId, title, body, token, studentProfile);
        }
        homeRedirect();
    }

    private void onRegisterAppoinment() {
        if (timeSelect.getVisibility() != View.GONE) {
            String timeInput = autoCompleteTextView.getText().toString();

            if (!timeInput.equals("")) {
                Map<String, Object> appoinment_data = new HashMap<>();
                appoinment_data.put("date", dateInput);
                appoinment_data.put("time", timeInput);
                appoinment_data.put("tutor", tutorId);
                appoinment_data.put("student", studentId);

                String timeStamp = commonClass.toTimeStamp(dateInput, timeInput);
                appoinment_data.put("timeStamp", timeStamp);

                db.collection("appoinment").add(appoinment_data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getActivity(), "Appointment registered successfully", Toast.LENGTH_SHORT).show();
                                bottomNavigationView.setSelectedItemId(R.id.home);
                                senNotification(dateInput, timeInput);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("MainActivity", "Error adding document", e);
                            }
                        });
            } else {
                showError(dialog.findViewById(R.id.errMsg), "Please select time");
            }
        } else {
            showError(dialog.findViewById(R.id.errMsg), "Please select date");
        }
    }


    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();

        //disable till current date
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(new CalendarConstraints.DateValidator() {
            @Override
            public boolean isValid(long date) {
                return date >= calendar.getTimeInMillis();
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(@NonNull Parcel parcel, int i) {

            }
        });

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select Date");
        builder.setSelection(calendar.getTimeInMillis());
        builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<Long> materialDatePicker = builder.build();
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {

            Calendar selected_c = Calendar.getInstance();
            selected_c.setTimeInMillis(selection);

            SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
            String weekName = sdf.format(selected_c.getTime());

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dateInput = dateFormat.format(selected_c.getTime());

            getSlotList(dateInput, weekName);

            Button dateButton = dialog.findViewById(R.id.dateButton);
            dateButton.setText(dateInput);
        });

        materialDatePicker.show(getParentFragmentManager(), "MaterialDatePicker");
    }


    private void getSlotList(String date, String weekName) {
        db.collection("appoinment").whereEqualTo("tutor", tutorId).whereEqualTo("date", date).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getDocuments().size() != 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    slotList.add(document.getData().get("time").toString());
                                }
                                getData(weekName);
                            } else {
                                if (slotList.size() != 0) {
                                    slotList.clear();
                                }
                                getData(weekName);
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

    private void getDialogData() {
//        Custom alert popup
//        https://www.youtube.com/watch?v=0DH2tZjJtm0&list=LL&index=17

        autoCompleteTextView = dialog.findViewById(R.id.timeId);
        spinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.location_dropdown_item, timeList);
        autoCompleteTextView.setAdapter(spinnerAdapter);
        Button appoinmentSubmit = dialog.findViewById(R.id.appoinmentSubmit);
        Button dateButton = dialog.findViewById(R.id.dateButton);
        Button cancelButton = dialog.findViewById(R.id.cancel_button);
        timeSelect = dialog.findViewById(R.id.mainTimeInput);
        timeSelect.setVisibility(View.GONE);
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

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                dialog.hide();
            }
        });
    }

    public void onSubmit(View view) {
        dialog.setContentView(R.layout.appoinment_form);
        getDialogData();
        dialog.show();
    }

    private void onAppoinmentDelete() {
        db.collection("appoinment").whereEqualTo("student", studentId).whereEqualTo("tutor", tutorId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getDocuments().size() != 0) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getActivity(), "Appointment deleted successfully", Toast.LENGTH_SHORT).show();
                                            bottomNavigationView.setSelectedItemId(R.id.home);
                                            homeRedirect();
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
    }


    private void redirectEditProfile(int layoutId, String tutorId) {
        Bundle mBundle = new Bundle();
        mBundle.putString("tutorId", tutorId);
        mBundle.putInt("layoutId", layoutId);
        TutorEditProfile editProfile = new TutorEditProfile();
        editProfile.setArguments(mBundle);
        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(layoutId, editProfile);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activity = getActivity();
        bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);

        Bundle bundle = getArguments();
        tutorId = bundle.getString("user_id");
        studentId = bundle.getString("studentId");
        layoutId = bundle.getInt("layoutId");
        studentName = bundle.getString("studentName");
        studentProfile = bundle.getString("studentProfile");

        loader = new Loader(getActivity());

        view = inflater.inflate(R.layout.fragment_tutor_profile, container, false);
        TextView heading = view.findViewById(R.id.header_title);
        heading.setText(studentId != null ? "Tutor's profile" : "My Profile");
        getProfileDetails(tutorId, view);

        Button submitBtn = view.findViewById(R.id.submitId);
        Button cancelBtn = view.findViewById(R.id.cancelId);
        cancelBtn.setVisibility(View.GONE);
        dialog = new Dialog(getContext());
        if (studentId == null) {
            submitBtn.setVisibility(View.GONE);
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to cancel this appointment?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onAppoinmentDelete();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertBox = builder.create();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                onSubmit(view);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                alertBox.show();
            }
        });

        FloatingActionButton editId = view.findViewById(R.id.editIcon);

        editId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectEditProfile(layoutId, tutorId);
            }
        });
        return view;
    }
}