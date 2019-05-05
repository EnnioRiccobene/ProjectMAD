package com.madgroup.appbikers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeliveryPendingTab1.
     */
    // TODO: Rename and change types and number of parameters
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
        View view = inflater.inflate(R.layout.fragment_pending_tab1, container, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        currentUser = prefs.getString("currentUser", "noUser");
        buildRecyclerView(view);
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

                        holder.deliveryItemCardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Set action on click

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

        recyclerView = view.findViewById(R.id.pendingDeliveryRecycleView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();


//        recyclerView = view.findViewById(R.id.pendingDeliveryRecycleView);
//        //rootLayout = (CoordinatorLayout)findViewById(R.id.rootLayout);
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
//        adapter = new DeliveryAdapter(deliveriesList);
//
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        // mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
//        adapter.notifyDataSetChanged();


//
//        adapter.setOnItemClickListener(new DeliveryAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(final int position) {
//                // Scarico dal DB orderedFood
//                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
//                DatabaseReference pendingReservationRef = database.child("Company").child("Reservation").child("OrderedFood");
//                String orderID = pendingReservation.get(position).getOrderID();
//                pendingReservationRef.child(orderID).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        ArrayList<OrderedDish> orderedFood = new ArrayList<>();
//                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                            OrderedDish post = postSnapshot.getValue(OrderedDish.class);
//                            orderedFood.add(post);
//                        }
//                        lastPositionClicked = position;
//                        Intent openPage = new Intent(getActivity(), DetailedReservation.class);
//                        openPage.putExtra("Reservation", pendingReservation.get(position));
//                        openPage.putExtra("OrderedFood", orderedFood);
//                        startActivityForResult(openPage, CONFIRM_OR_REJECT_CODE);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//            }
//
//            //This Function is useful if we want to delete an item in the list
//            @Override
//            public void onDeleteClick(int position) {
//                removeItem(position);
//            }
//        });
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView deliveryItemCardView;
        RelativeLayout relativeLayout;
        TextView restaurantName;
        TextView restaurantAddress;
        TextView distance;
        TextView customerAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deliveryItemCardView = itemView.findViewById(R.id.deliveryItemCardView);
            relativeLayout = itemView.findViewById(R.id.deliveryItemLayout);
            restaurantName = itemView.findViewById(R.id.restaurantName);
            restaurantAddress = itemView.findViewById(R.id.restaurantAddress);
            distance = itemView.findViewById(R.id.distance);
            customerAddress = itemView.findViewById(R.id.customerAddress);
        }

    }
}
