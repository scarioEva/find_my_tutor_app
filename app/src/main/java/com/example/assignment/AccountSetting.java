package com.example.assignment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class AccountSetting extends Fragment {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    View view;
    String id;
    int layoutId;
    TextInputLayout emailId;
    TextView errMsg;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void getEmail() {
        if (user != null) {
            String email = user.getEmail();
            if (email != null) {
                emailId.getEditText().setText(email);
            }
        }
    }

    private void updateEmailId() {
        String newEmail = emailId.getEditText().getText().toString();
        if (!newEmail.equals((""))) {
            if (user != null) {
                user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Email changed successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            errMsg.setText("Failed to update email:"+task.getException().getMessage());
//                            Log.d("MainActivity", "Failed to update email: " + task.getException().getMessage());
                        }
                    }
                });
            } else {
                // User is not signed in
                Log.d("MainActivity", "User is not signed in");
            }
        } else {
            errMsg.setText("Please enter Email id");
        }

    }

    private  void redireChangePassPage(){
        Bundle mBundle = new Bundle();
        mBundle.putInt("layoutId", layoutId);
        ChangePassword changePassword = new ChangePassword();
        changePassword.setArguments(mBundle);
        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(layoutId, changePassword);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_account_setting, container, false);
        Bundle bundle = getArguments();
        id = bundle.getString("user_id");
        layoutId = bundle.getInt("layoutId");
        Button saveBtn = view.findViewById(R.id.saveBtn);
        Button changePass=view.findViewById(R.id.changePass);

        emailId = view.findViewById(R.id.emailId);
        errMsg = view.findViewById(R.id.errMsg);

        getEmail();

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redireChangePassPage();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEmailId();
            }
        });
        return view;
    }
}