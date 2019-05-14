package com.madgroup.appcompany;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.madgroup.sdk.Reservation;
import com.madgroup.sdk.RiderProfile;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChooseRiderAdapter extends
        RecyclerView.Adapter<ChooseRiderAdapter.ViewHolder> {

    private String restaurantAddress;
    private ArrayList<RiderProfile> riderList;
    private Reservation reservation;
    private String currentUser;
    private SharedPreferences prefs;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView riderName;
        CircleImageView riderPhoto;
        TextView riderDistance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            riderName = itemView.findViewById(R.id.rider_name);
            riderDistance = itemView.findViewById(R.id.rider_distance);
            riderPhoto = itemView.findViewById(R.id.rider_photo);
        }
    }

    public ChooseRiderAdapter(Context context, ArrayList<RiderProfile> riderList, Reservation reservation) {
        this.riderList = riderList;
        this.reservation = reservation;
        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        currentUser = prefs.getString("currentUser", "noUser");
        restaurantAddress = prefs.getString("Address", "noAddress");
    }

    @Override
    public int getItemCount() {
        return riderList.size();
    }

    @NonNull
    @Override
    public ChooseRiderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_rider_profile_item, parent, false);
        ChooseRiderAdapter.ViewHolder holder = new ChooseRiderAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ChooseRiderAdapter.ViewHolder holder, int position) {
        final RiderProfile rider = riderList.get(position);
        holder.riderName.setText(rider.getName());
        loadPhoto(holder, rider);
        // TODO: calcolo distanza
        // getDistance(restaurantAddress, rider.getPosition);
        holder.riderDistance.setText("300 mt");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callRider(rider);
            }
        });
    }

    private void callRider(RiderProfile rider) {
        String orderID = reservation.getOrderID();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference deliveriesRef = database.child("Rider").child("Delivery");
        DatabaseReference acceptedReservationRef = database.child("Company").child("Reservation").child("Accepted").child(currentUser).child(orderID);
        reservation.setStatus(2);
        HashMap<String, Object> statusUpdate = new HashMap<>();
        statusUpdate.put("status", 2);
        acceptedReservationRef.updateChildren(statusUpdate);

        // Creating Delivery Item
        HashMap<String, String> Delivery = new HashMap<>();
        Delivery.put("restaurantID", currentUser);
        Delivery.put("customerID", reservation.getCustomerID());
        Delivery.put("restaurantName", prefs.getString("Name", ""));
        Delivery.put("restaurantAddress", prefs.getString("Address", ""));
        Delivery.put("customerAddress", reservation.getAddress());
        Delivery.put("orderID", reservation.getOrderID());
        Delivery.put("deliveryTime", reservation.getDeliveryTime());
        //Delivery.put("seen", false);
        deliveriesRef.child("Pending").child(rider.getId()).child(reservation.getOrderID()).setValue(Delivery);
        final DatabaseReference notifyFlagRef = database.child("Rider").child("Delivery").child("Pending").child("NotifyFlag").child(rider.getId()).child(reservation.getOrderID()).child("seen");
        notifyFlagRef.setValue(false);
        ((Activity)context).finish();
    }

    private void loadPhoto(ChooseRiderAdapter.ViewHolder holder, RiderProfile rider) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pics")
                .child("bikers").child(rider.getId());

        GlideApp.with(context)
                .load(storageReference)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(GlideApp.with(context).load(R.drawable.personicon))
                .into(holder.riderPhoto);
    }
}
