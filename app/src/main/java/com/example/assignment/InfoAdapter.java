package com.example.assignment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class InfoAdapter extends ArrayAdapter<InfoModel> {
    CommonClass commonClass = new CommonClass();
    Context context;

    public InfoAdapter(Context context, List<InfoModel> infoList) {
        super(context, 0, infoList);
        this.context=context;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.profileImage);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        InfoModel info = getItem(position);
        TextView titleView = convertView.findViewById(R.id.cardName);
        TextView locationView = convertView.findViewById(R.id.cardLocation);
        TextView departmentView = convertView.findViewById(R.id.cardDepartment);
        TextView dateView = convertView.findViewById(R.id.cardDate);
        MaterialCardView cardView = convertView.findViewById(R.id.cardId);
        ImageView checkView=convertView.findViewById(R.id.check);



        if (info != null) {
            titleView.setText(info.getName());
            locationView.setText(info.getLocation());
            departmentView.setText(info.getDepartment());

            if (!info.getScheduled().equals("")) {
                dateView.setVisibility(View.VISIBLE);
                dateView.setText(info.getScheduled());
            } else {
                dateView.setVisibility(View.GONE);
            }


            if (!info.getProfileUrl().equals("")) {
                Glide.with(context)
                        .load(info.getProfileUrl())
                        .into(viewHolder.imageView);
            }
            else{
                viewHolder.imageView.setImageResource(R.drawable.user_new);
            }
            if(info.getInOffice()){
                checkView.setVisibility(View.VISIBLE);
            }
            else {
                checkView.setVisibility(View.GONE);
            }
        }

        return convertView;

    }
    static class ViewHolder {
        ShapeableImageView imageView;
    }

}
