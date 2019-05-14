package com.madgroup.appbikers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.provider.ContactsContract;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.madgroup.sdk.Reservation;
import com.madgroup.sdk.SmartLogger;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DeliveryPendingTab1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DeliveryPendingTab1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeliveryPendingTab1 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    static public ArrayList<Delivery> deliveriesList;

    private RecyclerView recyclerView;
    private SharedPreferences prefs;
    private String currentUser;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DeliveryPendingTab1() {
        // Required empty public constructor
    }

    public static DeliveryPendingTab1 newInstance(String param1, String param2) {
        DeliveryPendingTab1 fragment = new DeliveryPendingTab1();
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
        View view = inflater.inflate(R.layout.fragment_order_tab, container, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        currentUser = prefs.getString("currentUser", "noUser");
        buildRecyclerView(view);
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void buildRecyclerView(View view) {

        DatabaseReference pendingRef = FirebaseDatabase.getInstance().getReference().child("Rider").child("Delivery").child("Pending").child(currentUser);
        FirebaseRecyclerOptions<Delivery> options = new FirebaseRecyclerOptions.Builder<Delivery>()
                .setQuery(pendingRef, Delivery.class)
                .build();
        final FirebaseRecyclerAdapter<Delivery, ViewHolder> adapter =
                new FirebaseRecyclerAdapter<Delivery, ViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolder holder, int i, @NonNull final Delivery currentItem) {
                        holder.restaurantName.setText(currentItem.getRestaurantName());
                        holder.restaurantAddress.setText(currentItem.getRestaurantAddress());
                        holder.distance.setText(currentItem.calculateDistance("123", "123") + " mt");
                        holder.customerAddress.setText(currentItem.getCustomerAddress());

                        holder.bikerArrived.setImageResource(R.drawable.ic_circled_confirm);
                        ImageViewCompat.setImageTintList(holder.bikerArrived, ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorPrimary)));
                        holder.bikerArrived.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Ordine Arrivato:
                                // Company: passare da accepted a history
                                // Customer: passare da pending a history
                                // Rider: passare da pending a history
                                final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                // Company: prendo reservation, pongo status = 3, metto su history, rimuovo da pending
                                DatabaseReference companyReservationRef = database.child("Company").child("Reservation").child("Accepted").child(currentItem.getRestaurantID()).child(currentItem.getOrderID());
                                DatabaseReference customerReservationRef = database.child("Customer").child("Order").child("Pending").child(currentItem.getCustomerID()).child(currentItem.getOrderID());
                                companyReservationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Reservation moveReservation = (Reservation) dataSnapshot.getValue(Reservation.class);
                                        moveReservation.setStatus(3);
                                        database.child("Company").child("Reservation").child("History").child(currentItem.getRestaurantID()).child(currentItem.getOrderID()).setValue(moveReservation);
                                        database.child("Company").child("Reservation").child("Accepted").child(currentItem.getRestaurantID()).child(currentItem.getOrderID()).setValue(null);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                customerReservationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // Customer: prendo reservation, pongo status = 1, metto su history, rimuovo da pending
                                        Reservation moveReservation = (Reservation) dataSnapshot.getValue(Reservation.class);
                                        moveReservation.setStatus(1);
                                        database.child("Customer").child("Order").child("History").child(currentItem.getCustomerID()).child(currentItem.getOrderID()).setValue(moveReservation);
                                        database.child("Customer").child("Order").child("Pending").child(currentItem.getCustomerID()).child(currentItem.getOrderID()).setValue(null);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                // Rider: rimuovo da pending e pongo su history
                                database.child("Rider").child("Delivery").child("Pending").child(currentUser).child(currentItem.getOrderID()).setValue(null);
                                database.child("Rider").child("Delivery").child("History").child(currentUser).child(currentItem.getOrderID()).setValue(currentItem);

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.delivery_item, parent, false);
                        ViewHolder holder = new ViewHolder(view);
                        return holder;
                    }
                };

        recyclerView = view.findViewById(R.id.orderRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView deliveryItemCardView;
        RelativeLayout relativeLayout;
        TextView restaurantName;
        TextView restaurantAddress;
        TextView distance;
        TextView customerAddress;
        ImageView bikerArrived;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deliveryItemCardView = itemView.findViewById(R.id.deliveryItemCardView);
            relativeLayout = itemView.findViewById(R.id.deliveryItemLayout);
            restaurantName = itemView.findViewById(R.id.restaurantName);
            restaurantAddress = itemView.findViewById(R.id.restaurantAddress);
            distance = itemView.findViewById(R.id.distance);
            customerAddress = itemView.findViewById(R.id.customerAddress);
            bikerArrived = itemView.findViewById(R.id.biker_arrived);
        }

    }
}
