package com.example.assignment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class Setting extends Fragment {
    View view;
String id;
int layoutId;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void redirectPage(){
        Bundle mBundle = new Bundle();
        mBundle.putString("user_id", id);
        mBundle.putInt("layoutId", layoutId);
        AccountSetting accountSetting = new AccountSetting();
        accountSetting.setArguments(mBundle);
        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(layoutId, accountSetting);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void onSignOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        Bundle bundle = getArguments();
        id = bundle.getString("user_id");
        layoutId = bundle.getInt("layoutId");

        Button accSetting=view.findViewById(R.id.accSetting);
        Button logout=view.findViewById(R.id.logout);

        accSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectPage();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSignOut();
            }
        });
        return view;
    }
}