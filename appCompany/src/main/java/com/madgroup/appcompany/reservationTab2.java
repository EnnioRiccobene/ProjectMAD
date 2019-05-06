package com.madgroup.appcompany;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link reservationTab2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link reservationTab2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class reservationTab2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView mRecyclerView;
    private SharedPreferences prefs;
    private String currentUser;

    private OnFragmentInteractionListener mListener;

    public reservationTab2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment reservationTab2.
     */
    // TODO: Rename and change types and number of parameters
    public static reservationTab2 newInstance(String param1, String param2) {
        reservationTab2 fragment = new reservationTab2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reservation_tab, container, false);
        // createReservationList();
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        currentUser = prefs.getString("currentUser", "noUser");
        buildRecyclerView(view);

        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    // The following function set up the RecyclerView
    public void buildRecyclerView(View view) {

        DatabaseReference acceptedRef = FirebaseDatabase.getInstance().getReference().child("Company").child("Reservation").child("Accepted").child(currentUser);
        FirebaseRecyclerOptions<Reservation> options = new FirebaseRecyclerOptions.Builder<Reservation>()
                .setQuery(acceptedRef, Reservation.class)
                .build();
        final FirebaseRecyclerAdapter<Reservation, ReservationViewHolder> adapter =
                new FirebaseRecyclerAdapter<Reservation, ReservationViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ReservationViewHolder holder, int i, @NonNull final Reservation currentItem) {
                        final int index = i;
                        switch (currentItem.getStatus()) {
                            case 1:
                                holder.mImageView.setImageResource(R.drawable.ic_call);
                                ImageViewCompat.setImageTintList(holder.mImageView, ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorPrimary)));
                                holder.mImageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        callRider(currentItem, index);
                                    }
                                });
                                break;
                            case 2:
                                holder.mImageView.setImageResource(R.drawable.ic_hourglass);
                                ImageViewCompat.setImageTintList(holder.mImageView, ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorPrimary)));
                                break;
                        }
                        holder.mTextView1.setText(currentItem.getAddress());
                        holder.mTextView2.setText(currentItem.getDeliveryTime());
                        holder.mTextView3.setText(currentItem.getPrice() + " €");
                    }

                    @NonNull
                    @Override
                    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_item, parent, false);
                        ReservationViewHolder evh = new ReservationViewHolder(v);
                        return evh;
                    }
                };

        mRecyclerView = view.findViewById(R.id.reservationRecyclerViewTab);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public void callRider(final Reservation currentItem, int index) {
        String orderID = currentItem.getOrderID();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference acceptedReservationRef = database.child("Company").child("Reservation").child("Accepted").child(currentUser).child(orderID);
        currentItem.setStatus(2);
        HashMap<String, Object> statusUpdate = new HashMap<>();
        statusUpdate.put("status", 2);
        acceptedReservationRef.updateChildren(statusUpdate);

        // Rider search part
        final DatabaseReference riderRef = database.child("Rider").child("Profile");
        final DatabaseReference deliveriesRef = database.child("Rider").child("Delivery");
        Query query = riderRef.orderByChild("active").equalTo(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if ((int) dataSnapshot.getChildrenCount() == 0)
                        return;
                    Random rand = new Random();
                    int nActiveProfiles = rand.nextInt((int) dataSnapshot.getChildrenCount());
                    Iterator itr = dataSnapshot.getChildren().iterator();
                    SmartLogger.d("Active Profiles: " + (int) dataSnapshot.getChildrenCount() + "\nRandomNumber: " + nActiveProfiles);
                    for (int i = 0; i < nActiveProfiles; i++)
                        itr.next();
                    DataSnapshot childSnapshot = (DataSnapshot) itr.next();
                    RiderProfile choosenRider = childSnapshot.getValue(RiderProfile.class);
                    // Creating Delivery Item
                    HashMap<String, String> Delivery = new HashMap<>();
                    Delivery.put("restaurantName", currentItem.getAddress());
                    Delivery.put("restaurantAddress", currentItem.getAddress());
                    Delivery.put("customerAddress", currentItem.getAddress());
                    Delivery.put("orderID", currentItem.getOrderID());
                    Delivery.put("deliveryTime", currentItem.getDeliveryTime());
                    deliveriesRef.child("Pending").child(choosenRider.getEmail()).child(currentItem.getOrderID()).setValue(Delivery);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static class ReservationViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;  // Address
        public TextView mTextView2;  // Lunch_time
        public TextView mTextView3;  // Price
        public RelativeLayout viewForeground;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.status_biker);
            mTextView1 = itemView.findViewById(R.id.text_address);
            mTextView2 = itemView.findViewById(R.id.lunch_time);
            mTextView3 = itemView.findViewById(R.id.order_price);
            viewForeground = itemView.findViewById(R.id.view_foreground);
        }
    }
}