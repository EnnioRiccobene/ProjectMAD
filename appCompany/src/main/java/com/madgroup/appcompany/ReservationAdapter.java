package com.madgroup.appcompany;


import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.madgroup.sdk.RiderProfile;
import com.madgroup.sdk.SmartLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;


public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private ArrayList<Reservation> reservationList;
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

    public ReservationAdapter(ArrayList<Reservation> reservationList) {
        this.reservationList = reservationList;
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
    public void onBindViewHolder(@NonNull final ReservationViewHolder holder, int position) {
        final Reservation currentItem = reservationList.get(position);
        final int index = position;
        switch (currentItem.getStatus()){
            case 0:
                holder.mImageView.setImageResource(R.drawable.ic_circled_confirm);
                ImageViewCompat.setImageTintList(holder.mImageView, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary)));
                holder.mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SmartLogger.d(String.valueOf(index));
                        acceptReservation(currentItem, index);
                    }
                });
                break;
            case 1:
                holder.mImageView.setImageResource(R.drawable.ic_call);
                ImageViewCompat.setImageTintList(holder.mImageView, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary)));
                holder.mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callRider(currentItem, index);
                    }
                });
                break;
            case 2:
                holder.mImageView.setImageResource(R.drawable.ic_hourglass);
                ImageViewCompat.setImageTintList(holder.mImageView, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary)));
                break;
            case 3:
                holder.mImageView.setImageResource(R.drawable.ic_circled_confirm);
                ImageViewCompat.setImageTintList(holder.mImageView, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary)));
                break;
            case 4:
                holder.mImageView.setImageResource(R.drawable.ic_circled_reject);
                ImageViewCompat.setImageTintList(holder.mImageView, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
                break;
        }

        holder.mTextView1.setText(currentItem.getAddress());
        holder.mTextView2.setText(currentItem.getDeliveryTime());
        holder.mTextView3.setText(currentItem.getPrice() + " €");
    }

    @Override
    public int getItemCount() {
        return reservationList.size();
    }

    public void removeItem (int position){
        reservationList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount() - position);
    }

    public void restoreItem (Reservation item, int position){
        reservationList.add(position,item);
        notifyItemInserted(position);
    }

    // Elimino dalle pending e inserisco nelle accepted. Prima dal database, poi nella lista
    public void acceptReservation(Reservation currentItem, int index){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference pendingReservationRef = database.child("Company").child("Reservation").child("Pending");
        DatabaseReference acceptedReservationRef = database.child("Company").child("Reservation").child("Accepted");
        String orderID = currentItem.getOrderID();
        currentItem.setStatus(1);
        // Iniziare qui transazione
        pendingReservationRef.child(orderID).removeValue();
        acceptedReservationRef.child(orderID).setValue(currentItem);
        // Finire qui transazione
        removeItem(index);
        reservationTab2.acceptedReservation.add(currentItem);
        reservationTab2.mAdapter.notifyItemInserted(reservationTab2.acceptedReservation.size());
    }

    // Modifico lo status della query accepted. Prima dal database, poi nella lista
    public void callRider(final Reservation currentItem, int index){
        String orderID = currentItem.getOrderID();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference acceptedReservationRef = database.child("Company").child("Reservation").child("Accepted").child(orderID);
        currentItem.setStatus(2);
        HashMap<String, Object> statusUpdate = new HashMap<>();
        statusUpdate.put("status", 2);
        acceptedReservationRef.updateChildren(statusUpdate);
        reservationTab2.mAdapter.notifyItemChanged(index);

        // Rider search part

        final DatabaseReference riderRef = database.child("Rider").child("Profile");
        Query query = riderRef.orderByChild("Active").equalTo(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Random rand = new Random();
                    int nActiveProfiles = rand.nextInt((int)dataSnapshot.getChildrenCount());
                    Iterator itr = dataSnapshot.getChildren().iterator();
                    for(int i = 0; i < nActiveProfiles; i++)
                        itr.next();
                    DataSnapshot childSnapshot = (DataSnapshot) itr.next();
                    RiderProfile choosenRider = childSnapshot.getValue(RiderProfile.class);
                    riderRef.child("Delivery").child("Pending").child(choosenRider.getEmail()).setValue(currentItem);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
