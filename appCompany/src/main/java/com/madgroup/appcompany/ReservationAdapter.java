package com.madgroup.appcompany;


import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private ArrayList<Reservation> mExampleList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick (int position);
        void onDeleteClick (int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ReservationViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageView;
        public TextView mTextView1;  // Address
        public TextView mTextView2;  // Lunch_time
        public TextView mTextView3;  // Price
        public ImageView mDeleteImage;
        public RelativeLayout viewBackground, viewForeground;

        public ReservationViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.status_biker);
            mTextView1 = itemView.findViewById(R.id.text_address);
            mTextView2 = itemView.findViewById(R.id.lunch_time);
            mTextView3 = itemView.findViewById(R.id.order_price);
            //mDeleteImage = itemView.findViewById(R.id.delete_icon);
            //viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (listener!= null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }

                }

            });
           /* mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!= null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }

                }
            });*/
        }
    }

    public ReservationAdapter(ArrayList<Reservation> exampleList) {
        mExampleList = exampleList;

    }
    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_item, parent, false);
        ReservationViewHolder evh= new ReservationViewHolder(v, mListener );
        return evh;
    }
    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation currentItem= mExampleList.get(position);

        holder.mImageView.setImageResource(currentItem.getmImageResource());
        holder.mTextView1.setText(currentItem.getAddress());
        holder.mTextView2.setText(currentItem.getDelivery_time());
        holder.mTextView3.setText(currentItem.getPrice());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

    /*public void removeItem (int position){
        mExampleList.remove(position);
        notifyItemRemoved(position);
    }
    public void restoreItem (Reservation item, int position){
        mExampleList.add(position,item);
        notifyItemInserted(position);
    }*/
}

