package com.madgroup.appcompany;


import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;


public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private ArrayList<Reservation> mExampleList;
    private OnItemClickListener mListener;
    private Context context;

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
        context = parent.getContext();
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation currentItem = mExampleList.get(position);
        final int index = position;
        switch (currentItem.getStatus()){
            case 0:
                holder.mImageView.setImageResource(R.drawable.ic_circled_confirm);
                ImageViewCompat.setImageTintList(holder.mImageView, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary)));
                holder.mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), String.valueOf(index), Toast.LENGTH_SHORT).show();
                        removeItem(index);
                    }
                });
                break;
            case 1:
                holder.mImageView.setImageResource(R.drawable.ic_call);
                ImageViewCompat.setImageTintList(holder.mImageView, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary)));
                holder.mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "Rider Called", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case 2:
                holder.mImageView.setImageResource(R.drawable.ic_circled_confirm);
                ImageViewCompat.setImageTintList(holder.mImageView, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary)));

                break;
            case 3:
                holder.mImageView.setImageResource(R.drawable.ic_circled_reject);
                ImageViewCompat.setImageTintList(holder.mImageView, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
                break;
        }

        holder.mTextView1.setText(currentItem.getAddress());
        holder.mTextView2.setText(currentItem.getDeliveryTime());
        holder.mTextView3.setText(currentItem.getPrice());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

    public void removeItem (int position){
        mExampleList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount() - position);
    }

    public void restoreItem (Reservation item, int position){
        mExampleList.add(position,item);
        notifyItemInserted(position);
    }
}

