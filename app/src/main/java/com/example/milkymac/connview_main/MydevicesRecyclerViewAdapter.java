package com.example.milkymac.connview_main;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;


import com.example.milkymac.connview_main.devicesFragment.OnListFragmentInteractionListener;
import com.example.milkymac.connview_main.models.Devices;

import java.util.List;


public class MydevicesRecyclerViewAdapter extends RecyclerView.Adapter<MydevicesRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private final List<Devices> mValues;
    private final OnListFragmentInteractionListener mListener;

    private List<Devices> mDevices;

    public MydevicesRecyclerViewAdapter(List<Devices> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_devices, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        if (holder.mItem.getType().equals("DESKTOP")) { holder.mIdView.setImageResource(R.drawable.ic_monitor); }
        else { holder.mIdView.setImageResource(R.drawable.ic_phone); }

        holder.mContentView.setText(mValues.get(position).getIp());


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String isup = (holder.mItem.getState()) ? "UP" : "DOWN";

                final AlertDialog TypeDialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Device Details");

                builder.setMessage("("+isup+") "+" IP: "+ holder.mItem.getIp()
                    +"\nMAC: " + holder.mItem.getMac()
                    +"\nTYPE: " + holder.mItem.getType());

                TypeDialog = builder.create();
                TypeDialog.show();
            }
        });
    }




    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mIdView;
        public final TextView mContentView;
        public Devices mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (ImageView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
