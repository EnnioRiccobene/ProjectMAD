package com.madgroup.appcompany;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.madgroup.sdk.Haversine;
import com.madgroup.sdk.Position;
import com.madgroup.sdk.Reservation;
import com.madgroup.sdk.RiderProfile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChooseRiderAdapter extends
        RecyclerView.Adapter<ChooseRiderAdapter.ViewHolder> {

    private String restaurantAddress;
    private ArrayList<RiderProfile> riderList;
    private Reservation reservation;
    private String currentUser;
    private SharedPreferences prefs;
    private Context context;
    private Activity activity;

    public class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView riderName;
        CircleImageView riderPhoto;
        TextView riderDistance;
        AppCompatRatingBar rating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            riderName = itemView.findViewById(R.id.rider_name);
            riderDistance = itemView.findViewById(R.id.rider_distance);
            riderPhoto = itemView.findViewById(R.id.rider_photo);
            rating = itemView.findViewById(R.id.ratingBar);
        }
    }

    public ChooseRiderAdapter(Context context, ArrayList<RiderProfile> riderList, Reservation reservation, Activity activity) {
        this.riderList = riderList;
        this.reservation = reservation;
        this.context = context;
        this.activity = activity;
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
        holder.riderDistance.setText(getRiderDistance(restaurantAddress, rider.getPosition()));
        if(rider.getRatingAvg() != null && !rider.getRatingAvg().equals("0")) {
            holder.rating.setRating(Float.parseFloat(rider.getRatingAvg()));
        }
        else{
            holder.rating.setVisibility(View.GONE);
            // when there isn't rating  -> "centerInParent = true" for the riderName
            RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams)holder.riderName.getLayoutParams();
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            holder.riderName.setLayoutParams(layoutParams);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                callRider(rider);
                confirmCallBikerDialog(activity, "MADelivery", context.getString(R.string.dialog_callbiker_msg), rider);
            }
        });
    }

    public void confirmCallBikerDialog(Activity activity, String title, CharSequence message, final RiderProfile rider) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                callRider(rider);

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void callRider(RiderProfile rider) {
        HashMap<String, Object> multipleAtomicQuery = new HashMap<>();
        String orderID = reservation.getOrderID();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        // DatabaseReference deliveriesRef = database.child("Rider").child("Delivery");
        // DatabaseReference acceptedReservationRef = database.child("Company").child("Reservation").child("Accepted").child(currentUser).child(orderID);
        reservation.setStatus(2);
        // HashMap<String, Object> statusUpdate = new HashMap<>();
        // statusUpdate.put("status", 2);
        // acceptedReservationRef.updateChildren(statusUpdate);
        multipleAtomicQuery.put("Company/Reservation/Accepted/" + currentUser + "/" + orderID + "/status", 2);
        multipleAtomicQuery.put("Company/Reservation/Accepted/" + currentUser + "/" + orderID + "/bikerID", rider.getId());
        multipleAtomicQuery.put("Customer/Order/Pending/" + reservation.getCustomerID() + "/" + orderID + "/bikerID", rider.getId());
        multipleAtomicQuery.put("Customer/Order/Pending/" + reservation.getCustomerID() + "/" + orderID + "/status", 2);

        // Creating Delivery Item
        HashMap<String, String> Delivery = new HashMap<>();
        Delivery.put("restaurantID", currentUser);
        Delivery.put("customerId", reservation.getCustomerID());
        Delivery.put("restaurantName", prefs.getString("Name", ""));
        Delivery.put("restaurantAddress", prefs.getString("Address", ""));
        Delivery.put("customerAddress", reservation.getAddress());
        Delivery.put("orderID", reservation.getOrderID());
        Delivery.put("deliveryTime", reservation.getDeliveryTime());
        Delivery.put("restaurantCustomerDistance", getCustomerDistance());
        Delivery.put("customerName", reservation.getCustomerName());

        //Delivery.put("seen", false);
        // deliveriesRef.child("Pending").child(rider.getId()).child(reservation.getOrderID()).setValue(Delivery);
        // final DatabaseReference notifyFlagRef = database.child("Rider").child("Delivery").child("Pending").child("NotifyFlag").child(rider.getId()).child(reservation.getOrderID()).child("seen");
        // notifyFlagRef.setValue(false);
        multipleAtomicQuery.put("Rider/Delivery/Pending/" + rider.getId() + "/" + reservation.getOrderID(), Delivery);
        multipleAtomicQuery.put("Rider/Delivery/Pending/NotifyFlag/" + rider.getId() + "/" + reservation.getOrderID() + "/seen", false);
        multipleAtomicQuery.put("Rider/Profile/" + rider.getId() + "/status", false);

        database.updateChildren(multipleAtomicQuery);
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

    String getRiderDistance(String restaurantAddress, Position riderPosition){
        if(riderPosition == null || (riderPosition.getLon() == 0 && riderPosition.getLat() == 0))
            return "N.A.";
        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(restaurantAddress, 1);
            if (addresses.size() > 0) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();
                final Position restaurantPosition = new Position(latitude, longitude);
                double distance = Haversine.distance(restaurantPosition, riderPosition);
                DecimalFormat df = new DecimalFormat("#.#");
                df.setMaximumIntegerDigits(2);
                return df.format(distance) + " km";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "N.A.";
        }
        return "N.A.";
    }

    private String getCustomerDistance() {
        Geocoder geocoder = new Geocoder(this.context);
        List<Address> restaurantGeocoder;
        List<Address> customerGeocoder;
        try {
            restaurantGeocoder = geocoder.getFromLocationName(restaurantAddress, 1);
            customerGeocoder = geocoder.getFromLocationName(reservation.getAddress(), 1);
            if (restaurantGeocoder.size() > 0 && customerGeocoder.size() > 0) {
                double latitude = restaurantGeocoder.get(0).getLatitude();
                double longitude = restaurantGeocoder.get(0).getLongitude();
                Position restaurantPosition = new Position(latitude, longitude);
                latitude = customerGeocoder.get(0).getLatitude();
                longitude = customerGeocoder.get(0).getLongitude();
                Position customerPosition = new Position(latitude, longitude);
                double distance = Haversine.distance(restaurantPosition, customerPosition);
                DecimalFormat df = new DecimalFormat("#.##");
                df.setMinimumFractionDigits(2);
                return df.format(distance);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "0";
        }
        return "0";
    }
}
