package com.madgroup.appbikers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
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
import com.madgroup.sdk.Delivery;
import com.madgroup.sdk.SmartLogger;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DeliveryHistoryTab2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DeliveryHistoryTab2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeliveryHistoryTab2 extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    static public ArrayList<Delivery> deliveriesList;
    private RecyclerView recyclerView;
    private SharedPreferences prefs;
    private String currentUser;

    private OnFragmentInteractionListener mListener;

    public DeliveryHistoryTab2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeliveryHistoryTab2.
     */

    public static DeliveryHistoryTab2 newInstance(String param1, String param2) {
        DeliveryHistoryTab2 fragment = new DeliveryHistoryTab2();
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
        View view =  inflater.inflate(R.layout.fragment_order_tab, container, false);
        // createHistoryDeliveryList();
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        currentUser = prefs.getString("currentUser", "noUser");
        if(currentUser.equals("noUser"))
            SmartLogger.e("Error noUser");
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

        void onFragmentInteraction(Uri uri);
    }

    public void buildRecyclerView(View view){

        DatabaseReference pendingRef = FirebaseDatabase.getInstance().getReference().child("Rider").child("Delivery").child("History").child(currentUser);
        FirebaseRecyclerOptions<Delivery> options = new FirebaseRecyclerOptions.Builder<Delivery>()
                .setQuery(pendingRef, Delivery.class)
                .build();
        final FirebaseRecyclerAdapter<Delivery, ViewHolder> adapter =
                new FirebaseRecyclerAdapter<Delivery, ViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolder holder, int i, @NonNull final Delivery currentItem) {
                        holder.startNavigationButton.setVisibility(View.GONE);
                        holder.restaurantName.setText(currentItem.getRestaurantName());
                        holder.restaurantAddress.setText(currentItem.getRestaurantAddress());
                        // holder.distance.setText("");
                        holder.customerName.setText(currentItem.getCustomerName());
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
        // TextView distance;
        TextView customerAddress;
        TextView customerName;
        AppCompatButton startNavigationButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deliveryItemCardView = itemView.findViewById(R.id.deliveryItemCardView);
            relativeLayout = itemView.findViewById(R.id.deliveryItemLayout);
            restaurantName = itemView.findViewById(R.id.restaurantName);
            restaurantAddress = itemView.findViewById(R.id.restaurantAddress);
            // distance = itemView.findViewById(R.id.distance);
            customerAddress = itemView.findViewById(R.id.customerAddress);
            customerName = itemView.findViewById(R.id.customerName);
            startNavigationButton = itemView.findViewById(R.id.startNavigationButton);
        }

    }
}
