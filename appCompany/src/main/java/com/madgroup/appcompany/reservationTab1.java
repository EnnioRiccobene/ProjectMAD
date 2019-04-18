package com.madgroup.appcompany;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.madgroup.sdk.SmartLogger;

import java.util.ArrayList;


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
        SmartLogger.d("View Built");

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
        SmartLogger.d("Size = " + pendingReservation.size());
        mAdapter.setOnItemClickListener(new ReservationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent openPage = new Intent(getActivity(), DetailedReservation.class);
                openPage.putExtra("Reservation", pendingReservation.get(position));
                startActivity(openPage);
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
        mRecyclerView.removeViewAt(position);
        mAdapter.notifyItemRemoved(position);
        mAdapter.notifyItemRangeChanged(position, pendingReservation.size());

//        reservationList.remove(position);
//        mAdapter.notifyItemRemoved(position);
    }


}
