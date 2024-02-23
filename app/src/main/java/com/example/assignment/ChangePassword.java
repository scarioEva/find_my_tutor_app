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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ChangePassword extends Fragment {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    View view;
    int layoutId;
    TextInputLayout newPass;
    TextInputLayout confirmPass;
    TextInputLayout currentPass;
    TextView errMsg;
    String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void redirectPage() {
        Bundle mBundle = new Bundle();
        mBundle.putInt("layoutId", layoutId);
        mBundle.putString("user_id", id);
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
        String currentPassValue = currentPass.getEditText().getText().toString();

        if (!newPassValue.equals("") && !confirmPassValue.equals("") && !currentPassValue.equals("")) {

            if (newPassValue.equals(confirmPassValue)) {

                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassValue); // Replace "currentPassword123" with the user's current password
                user.reauthenticate(credential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // User has been reauthenticated, proceed with password update
                                String newPassword = "newPassword123"; // Replace with the new password entered by the user
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(updatePasswordTask -> {
                                            if (updatePasswordTask.isSuccessful()) {
                                                Toast.makeText(getActivity(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                                redirectPage();
                                            } else {
                                                // Failed to update password
                                                errMsg.setText("Failed to update password. Please try again");
                                            }
                                        });
                            } else {
                                errMsg.setText("Invalid current password");
//                                // Failed to reauthenticate user
//                                Log.e(TAG, "Error reauthenticating user.", task.getException());
//                                // You can add code here to handle the error, e.g., show an error message to the user
                            }
                        });

//                user.updatePassword(newPassValue)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(getActivity(), "Password updated successfully", Toast.LENGTH_SHORT).show();
//                                    redirectPage();
//                                } else {
//                                    errMsg.setText("Failed to update password: " + task.getException().getMessage());
//                                }
//                            }
//                        });

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
        id = bundle.getString("user_id");

        Button submit = view.findViewById(R.id.subBtn);
        currentPass = view.findViewById(R.id.currentPass);
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