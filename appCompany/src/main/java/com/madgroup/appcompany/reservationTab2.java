package com.madgroup.appcompany;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.madgroup.sdk.Haversine;
import com.madgroup.sdk.OrderedDish;
import com.madgroup.sdk.Position;
import com.madgroup.sdk.Reservation;
import com.madgroup.sdk.RiderProfile;
import com.madgroup.sdk.SmartLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link reservationTab2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link reservationTab2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class reservationTab2 extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
    private RecyclerView mRecyclerView;
    private SharedPreferences prefs;
    private String currentUser;

    private OnFragmentInteractionListener mListener;
    private String restaurantAddress;

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
        restaurantAddress = prefs.getString("Address", "noAddress");
        if (currentUser.equals("noUser") || restaurantAddress.equals("noAddress"))
            getActivity().finish();
        buildRecyclerView(view);

        // Inflate the layout for this fragment
        return view;
    }


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

        void onFragmentInteraction(Uri uri);
    }

    public void confirmCallBikerDialog(Activity activity, String title, CharSequence message, final Reservation currentItem, final int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

//        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                openRiderPage(currentItem, index);

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
//                                        openRiderPage(currentItem, index);
                                        confirmCallBikerDialog(getActivity(), "", getString(R.string.dialog_callbiker_msg), currentItem, index);
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
                        holder.mTextView3.setText(currentItem.getPrice());

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Scarico dal DB orderedFood
                                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                String orderID = currentItem.getOrderID();
                                DatabaseReference orderedFoodRef = database.child("Company").child("Reservation").child("OrderedFood").child(currentUser).child(orderID);
                                orderedFoodRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        ArrayList<OrderedDish> orderedFood = new ArrayList<>();
                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            OrderedDish post = postSnapshot.getValue(OrderedDish.class);
                                            orderedFood.add(post);
                                        }
                                        Intent openPage = new Intent(getActivity(), DetailedReservation.class);
                                        openPage.putExtra("Reservation", currentItem);
                                        openPage.putExtra("OrderedFood", orderedFood);
                                        getActivity().startActivity(openPage);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
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

    public void openRiderPage(final Reservation currentItem, int index) {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference riderRef = database.child("Rider").child("Profile");
        Query query = riderRef.orderByChild("status").equalTo(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists() || dataSnapshot.getChildrenCount() == 0) {
                    Toast.makeText(getActivity(), getString(R.string.no_rider), Toast.LENGTH_LONG).show();
                    return;
                }
                ArrayList<RiderProfile> riderList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                    riderList.add(postSnapshot.getValue(RiderProfile.class));

                if (riderList.size() == 0) {
                    Toast.makeText(getActivity(), getString(R.string.no_rider), Toast.LENGTH_LONG).show();
                    return;
                }

                // Sort Array based on position
                sortRiderList(riderList);

                // Change Activity
                Intent openRiderListActivity = new Intent(getContext(), ChooseRiderActivity.class);
                openRiderListActivity.putExtra("riderList", riderList);
                openRiderListActivity.putExtra("reservation", currentItem);
                getActivity().startActivity(openRiderListActivity);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sortRiderList(ArrayList<RiderProfile> riderList) {
        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(restaurantAddress, 1);
            if (addresses.size() > 0) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();
                final Position restaurantPosition = new Position(latitude, longitude);
                ArrayList<RiderProfile> notSortableRider = new ArrayList<>();
                Iterator<RiderProfile> iterator = riderList.iterator();
                while (iterator.hasNext()) {
                    RiderProfile element = iterator.next();
                    if (element.getPosition() == null || (element.getPosition().getLat() == 0 && element.getPosition().getLon() == 0)) {
                        iterator.remove();
                        // riderList.remove(element);
                        notSortableRider.add(element);
                    }
                }
                Collections.sort(riderList, new Comparator<RiderProfile>() {
                    @Override
                    public int compare(RiderProfile o1, RiderProfile o2) {
                        double distance1 = Haversine.distance(restaurantPosition, o1.getPosition());
                        double distance2 = Haversine.distance(restaurantPosition, o2.getPosition());
                        if (distance1 > distance2)
                            return 1;
                        else if (distance1 < distance2)
                            return -1;
                        else
                            return 0;
                    }
                });
                for (RiderProfile element : notSortableRider)
                    riderList.add(element);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ReservationViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;  // Address
        public TextView mTextView2;  // Lunch_time
        public TextView mTextView3;  // Price
        public RelativeLayout viewForeground;
        View mView;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.status_biker);
            mTextView1 = itemView.findViewById(R.id.text_address);
            mTextView2 = itemView.findViewById(R.id.lunch_time);
            mTextView3 = itemView.findViewById(R.id.order_price);
            viewForeground = itemView.findViewById(R.id.view_foreground);
            mView = itemView;
        }
    }
}
