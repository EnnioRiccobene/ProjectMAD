package com.madgroup.madproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.madgroup.sdk.OrderedDish;
import com.madgroup.sdk.Reservation;
import com.madgroup.sdk.SmartLogger;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrdersHistoryTab.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrdersHistoryTab#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrdersHistoryTab extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private SharedPreferences prefs;
    private String currentUser;

    public OrdersHistoryTab() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrdersHistoryTab.
     */

    public static OrdersHistoryTab newInstance(String param1, String param2) {
        OrdersHistoryTab fragment = new OrdersHistoryTab();
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
        prefs = this.getActivity().getSharedPreferences("MyData", MODE_PRIVATE);
        currentUser = prefs.getString("currentUser", "noUser");
        View view = inflater.inflate(R.layout.fragment_orders_pending_tab, container, false);
        buildRecyclerView(view);

        // Inflate the layout for this fragment
        return view;    }

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

    public void showEvaluationDialog(Activity activity, String title, CharSequence message, final Reservation currentItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(currentItem.getBikerID().equals("")){
                    Toast.makeText(getActivity(),getString(R.string.error_evaluate_toast),Toast.LENGTH_SHORT).show();
                } else {
                    EvaluationActivity.start(getContext(), currentItem.getOrderID(), currentItem.getRestaurantID(), currentItem.getCustomerID(), currentItem.getBikerID());
                }
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

    private void buildRecyclerView(View view) {
        DatabaseReference pendingRef = FirebaseDatabase.getInstance().getReference().child("Customer").child("Order").child("History").child(currentUser);
        FirebaseRecyclerOptions<Reservation> options = new FirebaseRecyclerOptions.Builder<Reservation>()
                .setQuery(pendingRef, Reservation.class)
                .build();
        final FirebaseRecyclerAdapter<Reservation, OrderViewHolder> adapter =
                new FirebaseRecyclerAdapter<Reservation, OrderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull OrderViewHolder holder, int i, @NonNull final Reservation currentItem) {
                        final int index = i;
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pics")
                                .child("restaurants").child(currentItem.getRestaurantID());
                        GlideApp.with(OrdersHistoryTab.this)
                                .load(storageReference)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .error(GlideApp.with(OrdersHistoryTab.this).load(R.drawable.personicon))
                                .into(holder.mImageView);
                        holder.mTextView1.setText(currentItem.getRestaurantName());
                        holder.mTextView2.setText(currentItem.getDeliveryTime());
                        holder.mTextView3.setText(currentItem.getPrice());
                        holder.evaluateOrder.setText(R.string.leave_review);
                        holder.evaluateOrder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showEvaluationDialog(getActivity(), "MADelivery", getString(R.string.evaluate_dialog_message), currentItem);
                            }
                        });

                        holder.viewDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Scarico dal DB orderedFood
                                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                String orderID = currentItem.getOrderID();
                                String restaurantID = currentItem.getRestaurantID();
                                DatabaseReference orderedFoodRed = database.child("Company").child("Reservation").child("OrderedFood").child(restaurantID).child(orderID);
                                orderedFoodRed.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        ArrayList<OrderedDish> orderedFood = new ArrayList<>();
                                        SmartLogger.d("SIZE: " + dataSnapshot.getChildrenCount());
                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            OrderedDish post = postSnapshot.getValue(OrderedDish.class);
                                            orderedFood.add(post);
                                        }
                                        DetailedOrder.start(getActivity(), currentItem, orderedFood);
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
                    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
                        OrderViewHolder evh = new OrderViewHolder(v);
                        return evh;
                    }
                };

        RecyclerView mRecyclerView = view.findViewById(R.id.reservationRecyclerViewTab);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView mImageView;
        public TextView mTextView1;  // Address
        public TextView mTextView2;  // Lunch_time
        public TextView mTextView3;  // Price
        public RelativeLayout viewForeground;
        private AppCompatButton evaluateOrder;
        private AppCompatButton viewDetails;
        View mView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mImageView = itemView.findViewById(R.id.restaurantPhoto);
            mTextView1 = itemView.findViewById(R.id.textRestaurantName);
            mTextView2 = itemView.findViewById(R.id.lunch_time);
            mTextView3 = itemView.findViewById(R.id.order_price);
            viewForeground = itemView.findViewById(R.id.view_foreground);
            evaluateOrder = itemView.findViewById(R.id.confirmButton);
            viewDetails = itemView.findViewById(R.id.viewDetails);
        }
    }
}
