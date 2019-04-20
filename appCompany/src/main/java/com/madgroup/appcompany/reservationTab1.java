package com.madgroup.appcompany;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madgroup.sdk.SmartLogger;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link reservationTab1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link reservationTab1#newInstance} factory method to
 * create an instance of this fragment.
 */


public class reservationTab1 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    static public ArrayList<Reservation> pendingReservation;
    private ReservationAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private int lastPositionClicked;
    private static final int CONFIRM_OR_REJECT_CODE = 1;

    // TODO: Rename and change types of parameters
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
    // TODO: Rename and change types and number of parameters
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

        View view = inflater.inflate(R.layout.fragment_reservation_tab1, container, false);
//        createPendingReservationList();
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

        mRecyclerView = view.findViewById(R.id.reservationRecyclerViewTab1);
        //rootLayout = (CoordinatorLayout)findViewById(R.id.rootLayout);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new ReservationAdapter(pendingReservation);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mAdapter.notifyDataSetChanged();
        mAdapter.setOnItemClickListener(new ReservationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                // Scarico dal DB orderedFood
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                DatabaseReference pendingReservationRef = database.child("Company").child("Reservation").child("OrderedFood");
                String orderID = pendingReservation.get(position).getOrderID();
                pendingReservationRef.child(orderID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<OrderedDish> orderedFood = new ArrayList<>();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            OrderedDish post = postSnapshot.getValue(OrderedDish.class);
                            orderedFood.add(post);
                        }
                        lastPositionClicked = position;
                        Intent openPage = new Intent(getActivity(), DetailedReservation.class);
                        openPage.putExtra("Reservation", pendingReservation.get(position));
                        openPage.putExtra("OrderedFood", orderedFood);
                        startActivityForResult(openPage, CONFIRM_OR_REJECT_CODE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            //This Function is useful if we want to delete an item in the list
            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });

        /*ItemTouchHelper.SimpleCallback itemTouchHelperCallBack
                = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT ,this );
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(mRecyclerView);

        */
    }

    public void removeItem(int position) {
        pendingReservation.remove(position);
        mAdapter.notifyDataSetChanged();
//        mAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONFIRM_OR_REJECT_CODE && data != null){
            if(resultCode == RESULT_OK){
                String result = data.getStringExtra("Result");
                if(result.equals("Confirmed")){
                    acceptReservation(lastPositionClicked);
                } else if(result.equals("Rejected")){
                    rejectReservation(lastPositionClicked);
                }
            }
        }
    }

    // Elimino dalle pending e inserisco nelle accepted. Prima dal database, poi nella lista
    public void acceptReservation(int index){
        Reservation currentItem = pendingReservation.get(index);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference pendingReservationRef = database.child("Company").child("Reservation").child("Pending");
        DatabaseReference acceptedReservationRef = database.child("Company").child("Reservation").child("Accepted");
        String orderID = currentItem.getOrderID();
        currentItem.setStatus(ReservationActivity.ACCEPTED_RESERVATION_CODE);
        pendingReservationRef.child(orderID).removeValue();
        acceptedReservationRef.child(orderID).setValue(currentItem);
        removeItem(index);
        if(reservationTab2.acceptedReservation != null && reservationTab2.mAdapter != null){
            reservationTab2.acceptedReservation.add(currentItem);
            reservationTab2.mAdapter.notifyItemInserted(reservationTab2.acceptedReservation.size());
        }
    }

    //TODO
    public void rejectReservation(int index){
        Reservation currentItem = pendingReservation.get(index);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference pendingReservationRef = database.child("Company").child("Reservation").child("Pending");
        DatabaseReference historyReservationRef = database.child("Company").child("Reservation").child("History");
        String orderID = currentItem.getOrderID();
        currentItem.setStatus(ReservationActivity.HISTORY_REJECT_RESERVATION_CODE);
        pendingReservationRef.child(orderID).removeValue();
        historyReservationRef.child(orderID).setValue(currentItem);
        removeItem(index);
        if(reservationTab3.historyReservation != null && reservationTab3.mAdapter != null) {
            reservationTab3.historyReservation.add(currentItem);
            reservationTab3.mAdapter.notifyItemInserted(reservationTab3.historyReservation.size());
        }
    }

}
