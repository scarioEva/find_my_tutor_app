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


public class ChangePassword extends Fragment {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    View view;
    int layoutId;
    TextInputLayout newPass;
    TextInputLayout confirmPass;
    TextView errMsg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void redirectPage(){
        Bundle mBundle = new Bundle();
        mBundle.putInt("layoutId", layoutId);
        AccountSetting accountSetting = new AccountSetting();
        accountSetting.setArguments(mBundle);
        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(layoutId, accountSetting);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void updatePass() {
        String newPassValue = newPass.getEditText().getText().toString();
        String confirmPassValue = confirmPass.getEditText().getText().toString();

        if (!newPassValue.equals("") && !confirmPassValue.equals("")) {

            if (newPassValue.equals(confirmPassValue)) {
                user.updatePassword(newPassValue)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                    redirectPage();
                                } else {
                                    errMsg.setText("Failed to update password: " + task.getException().getMessage());
                                }
                            }
                        });

            } else {
                errMsg.setText("New password and conform new password did not match.");
            }
        } else {
            errMsg.setText("Enter all the required fields.");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_change_password, container, false);

        Bundle bundle = getArguments();
        layoutId = bundle.getInt("layoutId");


        Button submit = view.findViewById(R.id.subBtn);
        newPass = view.findViewById(R.id.newPass);
        confirmPass = view.findViewById(R.id.confirmPass);
        errMsg = view.findViewById(R.id.errMsg);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePass();
            }
        });
        return view;
    }
}