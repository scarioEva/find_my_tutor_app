package com.example.assignment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TutorProfileFragment extends Fragment {
    View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        String uuid = bundle.getString("uid");

        Log.d("MainActivity","uuid data:"+uuid);
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_tutor_profile, container, false);
        TextView tw=view.findViewById(R.id.uuid);
        tw.setText(uuid);
        return  view;
    }
}