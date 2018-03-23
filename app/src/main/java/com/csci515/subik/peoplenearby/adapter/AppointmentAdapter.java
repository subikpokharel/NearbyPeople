package com.csci515.subik.peoplenearby.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.csci515.subik.peoplenearby.R;
import com.csci515.subik.peoplenearby.parsing.Appointment;

import java.util.ArrayList;

/**
 * Created by subik on 3/23/18.
 */

public class AppointmentAdapter extends ArrayAdapter<Appointment> {

    private Context mContext;
    private ArrayList<Appointment> list;
    private LayoutInflater mlayoutInflater;
    private int resource;

    public AppointmentAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Appointment> objects) {
        super(context, resource, objects);
        this.list = objects;
        this.resource = resource;
        this.mContext = context;
        mlayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Appointment appointment = getItem(position);
        final AppointmentAdapter.ViewHolder mViewHolder;
        int i = 0;

        if (convertView == null) {
            convertView= mlayoutInflater.inflate(resource, parent, false);
            mViewHolder = new AppointmentAdapter.ViewHolder(convertView);
            convertView.setTag(mViewHolder);

        } else {
            mViewHolder = (AppointmentAdapter.ViewHolder) convertView.getTag();
        }
        i++;
        //mViewHolder.tvSlNo.setText(i);
        mViewHolder.tvFName.setText(appointment.getTo_name());
        mViewHolder.tvTime.setText(appointment.getTime());
        mViewHolder.tvResturant.setText(appointment.getResturant_address());
        mViewHolder.tvAddress.setText(appointment.getResturant_address());
        if (appointment.getStatus() == 0){
            mViewHolder.tvStatus.setText("Pending");
        }else{
            mViewHolder.tvStatus.setText("Accepted");
        }
        return convertView;
    }

    private static class ViewHolder{
        public TextView tvSlNo, tvFName, tvTime, tvResturant, tvAddress, tvStatus;

        public ViewHolder(View view){
            tvSlNo = view.findViewById(R.id.tvSlno);
            tvFName = view.findViewById(R.id.tvName);
            tvTime = view.findViewById(R.id.tvTime);
            tvResturant = view.findViewById(R.id.tvResName);
            tvAddress = view.findViewById(R.id.tvResAddress);
            tvStatus = view.findViewById(R.id.tvStatus);

        }
    }
}
