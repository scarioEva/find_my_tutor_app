package com.example.assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class InfoAdapter extends ArrayAdapter<InfoModel> {

    public InfoAdapter(Context context, List<InfoModel> infoList) {
        super(context, 0, infoList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card, parent, false);
        }

        InfoModel info = getItem(position);
        TextView titleView = convertView.findViewById(R.id.cardName);
        TextView locationView = convertView.findViewById(R.id.cardLocation);
        TextView departmentView = convertView.findViewById(R.id.cardDepartment);
        ShapeableImageView profileView = convertView.findViewById(R.id.profileImage);

        if (info != null) {
            titleView.setText(info.getName());
            locationView.setText(info.getLocation());
            departmentView.setText(info.getDepartment());

            if(!info.getProfileUrl().equals("")) {
                UrlImage obj = new UrlImage(info.getProfileUrl(), profileView);
                obj.execute();
            }
        }

        return convertView;

    }

}
