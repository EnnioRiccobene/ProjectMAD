package com.madgroup.appcompany;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.madgroup.sdk.OrderedDish;
import com.madgroup.sdk.Reservation;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link reservationTab1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link reservationTab1#newInstance} factory method to
 * create an instance of this fragment.
 */


public class reservationTab1 extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    static public ArrayList<Reservation> pendingReservation;
    private RecyclerView mRecyclerView;
    private static final int CONFIRM_OR_REJECT_CODE = 1;
    private SharedPreferences prefs;
    private String currentUser;


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public reservationTab1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment reservationTab1.
     */

    public static reservationTab1 newInstance(String param1, String param2) {
        reservationTab1 fragment = new reservationTab1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM1, param2);
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
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        currentUser = prefs.getString("currentUser", "noUser");
        View view = inflater.inflate(R.layout.fragment_reservation_tab, container, false);
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

    public void confirmAcceptanceDialog(Activity activity, String title, CharSequence message, final Reservation currentItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                DatabaseReference database = FirebaseDatabase.getInstance().getReference();

                String orderID = currentItem.getOrderID();
                currentItem.setStatus(ReservationActivity.ACCEPTED_RESERVATION_CODE);

                HashMap<String, Object> multipleAtomicQuery = new HashMap<>();
                multipleAtomicQuery.put("Company/Reservation/Pending/" + currentUser + "/" + orderID, null);
                multipleAtomicQuery.put("Company/Reservation/Accepted/" + currentUser + "/" + orderID, currentItem);
                multipleAtomicQuery.put("Customer/Order/Pending/" + currentItem.getCustomerID() + "/" + orderID + "/status", 1);
                database.updateChildren(multipleAtomicQuery);

                //transazione per aggiornare il campo del db orderedQuantityTot dei piatti ordinati
                updateDishesOrderedQuantityTot(currentItem);

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

    private void updateDishesOrderedQuantityTot(final Reservation currentItem){

        DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference().child("Company").child("Menu").child(currentUser);

        menuRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                if(mutableData.getValue() != null){

                    for (int i = 0; i < currentItem.getOrderedDishList().size(); i++) {
                        int a = Integer.valueOf((String) mutableData.child(currentItem.getOrderedDishList().get(i).getId()).child("orderedQuantityTot").getValue())
                                + Integer.valueOf(currentItem.getOrderedDishList().get(i).getQuantity());
                        mutableData.child(currentItem.getOrderedDishList().get(i).getId()).child("orderedQuantityTot").setValue(String.valueOf(a));
                    }

                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });
    }

    // The following function set up the RecyclerView
    public void buildRecyclerView(View view) {

        DatabaseReference pendingRef = FirebaseDatabase.getInstance().getReference().child("Company").child("Reservation").child("Pending").child(currentUser);
        FirebaseRecyclerOptions<Reservation> options = new FirebaseRecyclerOptions.Builder<Reservation>()
                .setQuery(pendingRef, Reservation.class)
                .build();
        final FirebaseRecyclerAdapter<Reservation, ReservationViewHolder> adapter =
                new FirebaseRecyclerAdapter<Reservation, ReservationViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ReservationViewHolder holder, int i, @NonNull final Reservation currentItem) {
                        final int index = i;
                        // holder.mImageView.setImageResource(R.drawable.ic_confirm_5);
                        // ImageViewCompat.setImageTintList(holder.mImageView, ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorAccent)));
//                        holder.mImageView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                // Scarico dal DB orderedFood
//                                DatabaseReference fooddatabase = FirebaseDatabase.getInstance().getReference();
//                                String orderID = currentItem.getOrderID();
//                                DatabaseReference orderedFoodRef = fooddatabase.child("Company").child("Reservation").child("OrderedFood").child(currentUser).child(orderID);
//                                orderedFoodRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        ArrayList<OrderedDish> orderedFood = new ArrayList<>();
//                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                                            OrderedDish post = postSnapshot.getValue(OrderedDish.class);
//                                            orderedFood.add(post);
//                                        }
//                                        currentItem.setOrderedDishList(orderedFood);
//
//                                        confirmAcceptanceDialog(getActivity(), "MADelivery", getString(R.string.msg_accept_dialog), currentItem);
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//
//                            }
//                        });
                        holder.mTextView1.setText(currentItem.getAddress());
                        holder.mTextView2.setText(currentItem.getDeliveryTime());
                        holder.mTextView3.setText(currentItem.getPrice());
                        holder.confirmButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmOrder(currentItem);
                            }
                        });

                        holder.refuseButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                rejectOrder(currentItem);
                            }
                        });

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
                        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_item_tab1, parent, false);
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

    public static class ReservationViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView mImageView;
        public TextView mTextView1;  // Address
        public TextView mTextView2;  // Lunch_time
        public TextView mTextView3;  // Price
        public RelativeLayout viewForeground;
        private AppCompatButton refuseButton;
        private AppCompatButton confirmButton;
        View mView;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            // mImageView = itemView.findViewById(R.id.status_biker);
            mTextView1 = itemView.findViewById(R.id.text_address);
            mTextView2 = itemView.findViewById(R.id.lunch_time);
            mTextView3 = itemView.findViewById(R.id.order_price);
            viewForeground = itemView.findViewById(R.id.view_foreground);
            refuseButton = itemView.findViewById(R.id.refuseButton);
            confirmButton = itemView.findViewById(R.id.confirmButton);
        }
    }

    private void confirmOrder(final Reservation currentItem) {
//      Scarico dal DB orderedFood
        DatabaseReference fooddatabase = FirebaseDatabase.getInstance().getReference();
        String orderID = currentItem.getOrderID();
        DatabaseReference orderedFoodRef = fooddatabase.child("Company").child("Reservation").child("OrderedFood").child(currentUser).child(orderID);
        orderedFoodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<OrderedDish> orderedFood = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    OrderedDish post = postSnapshot.getValue(OrderedDish.class);
                    orderedFood.add(post);
                }
                currentItem.setOrderedDishList(orderedFood);

                confirmAcceptanceDialog(getActivity(), "MADelivery", getString(R.string.msg_accept_dialog), currentItem);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void rejectOrder(Reservation reservation) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> multipleAtomicQueries = new HashMap<>();
        String orderID = reservation.getOrderID();
        multipleAtomicQueries.put("Company/Reservation/Pending/" + currentUser + "/" + orderID, null);
        reservation.setStatus(ReservationActivity.HISTORY_REJECT_RESERVATION_CODE);
        multipleAtomicQueries.put("Company/Reservation/History/" + currentUser + "/" + orderID, reservation);
        multipleAtomicQueries.put("Customer/Order/Pending/" + reservation.getCustomerID() + "/" + orderID, null);
        reservation.setStatus(4);
        multipleAtomicQueries.put("Customer/Order/History/" + reservation.getCustomerID()  + "/" + orderID, reservation);
        database.updateChildren(multipleAtomicQueries);
    }
}