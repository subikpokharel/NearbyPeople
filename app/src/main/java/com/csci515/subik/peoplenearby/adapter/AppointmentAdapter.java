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
    private String key;
    String job = null;
    DataTransferInterface dataTransferInterface;

    public AppointmentAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Appointment> objects, String key) {
        super(context, resource, objects);
        this.list = objects;
        this.resource = resource;
        this.mContext = context;
        mlayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.key = key;
        this.dataTransferInterface = (DataTransferInterface) context;
    }

    @Override
    public int getCount() {
        return list.size();
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Appointment appointment = getItem(position);
        final AppointmentAdapter.ViewHolder mViewHolder;

        if (convertView == null) {
            convertView= mlayoutInflater.inflate(resource, parent, false);
            mViewHolder = new AppointmentAdapter.ViewHolder(convertView);
            convertView.setTag(mViewHolder);

        } else {
            mViewHolder = (AppointmentAdapter.ViewHolder) convertView.getTag();
        }
        mViewHolder.tvId.setText(String.valueOf(appointment.getCus_id()));
        mViewHolder.tvTime.setText(String.format("Time: %s",appointment.getTime()));
        mViewHolder.tvResturant.setText(appointment.getResturant_name());
        mViewHolder.tvAddress.setText(appointment.getResturant_address());
        if (key.equals("sent")){
            mViewHolder.tvFName.setText(String.format("To: %s",appointment.getTo_name()));
            if (appointment.getStatus() == 0){
                mViewHolder.tvStatus.setText(mContext.getResources().getString(R.string.pending));
                job = "pending";
            }else{
                mViewHolder.tvStatus.setText(mContext.getResources().getString(R.string.trackFriend));
                mViewHolder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorAccepted));
                dataTransferInterface.makeHyperlink(mViewHolder.tvStatus);
                job = "track";
                //dataTransferInterface.clickHyperlink(appointment.getCus_id(), "track");

            }
        }else{
            mViewHolder.tvFName.setText(String.format("From: %s",appointment.getTo_name()));

            if (appointment.getStatus() == 0) {
                mViewHolder.tvStatus.setText(mContext.getResources().getString(R.string.acceptReject));
                mViewHolder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorAcceptReject));
                dataTransferInterface.makeHyperlink(mViewHolder.tvStatus);
                job = "view";
                //dataTransferInterface.clickHyperlink(appointment.getCus_id(), "view");
            }
            else {
                mViewHolder.tvStatus.setText(mContext.getResources().getString(R.string.trackFriend));
                mViewHolder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorAccepted));
                dataTransferInterface.makeHyperlink(mViewHolder.tvStatus);
                job = "track";
                //dataTransferInterface.clickHyperlink(appointment.getCus_id(), "track");
            }

        }


        mViewHolder.tvStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //int pos = list.get(position);

                dataTransferInterface.clickHyperlink(appointment.getCus_id(), job);
            }
        });

        return convertView;
    }

    private static class ViewHolder{
        private TextView tvId, tvFName, tvTime, tvResturant, tvAddress, tvStatus;

        private ViewHolder(View view){
            tvId = view.findViewById(R.id.tvCusId);
            tvFName = view.findViewById(R.id.tvName);
            tvTime = view.findViewById(R.id.tvTime);
            tvResturant = view.findViewById(R.id.tvResName);
            tvAddress = view.findViewById(R.id.tvResAddress);
            tvStatus = view.findViewById(R.id.tvStatus);

        }
    }

    public interface DataTransferInterface{
        void makeHyperlink(TextView textView);
        void clickHyperlink(int cus_id, String job);
    }
}
