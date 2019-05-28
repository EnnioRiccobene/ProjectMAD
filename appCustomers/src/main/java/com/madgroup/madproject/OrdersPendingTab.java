package com.madgroup.madproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.madgroup.sdk.Delivery;
import com.madgroup.sdk.OrderedDish;
import com.madgroup.sdk.Reservation;
import com.madgroup.sdk.SmartLogger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrdersPendingTab.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrdersPendingTab#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrdersPendingTab extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private SharedPreferences prefs;
    private String currentUser;

    public OrdersPendingTab() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrdersPendingTab.
     */
    // TODO: Rename and change types and number of parameters
    public static OrdersPendingTab newInstance(String param1, String param2) {
        OrdersPendingTab fragment = new OrdersPendingTab();
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
        prefs = this.getActivity().getSharedPreferences("MyData", MODE_PRIVATE);
        currentUser = prefs.getString("currentUser", "noUser");
        View view = inflater.inflate(R.layout.fragment_orders_pending_tab, container, false);
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

    private void buildRecyclerView(View view) {
        DatabaseReference pendingRef = FirebaseDatabase.getInstance().getReference().child("Customer").child("Order").child("Pending").child(currentUser);
        FirebaseRecyclerOptions<Reservation> options = new FirebaseRecyclerOptions.Builder<Reservation>()
                .setQuery(pendingRef, Reservation.class)
                .build();
        final FirebaseRecyclerAdapter<Reservation, OrderViewHolder> adapter =
                new FirebaseRecyclerAdapter<Reservation, OrderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull OrderViewHolder holder, int i, @NonNull final Reservation currentItem) {
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pics")
                                .child("restaurants").child(currentItem.getRestaurantID());
                        GlideApp.with(OrdersPendingTab.this)
                                .load(storageReference)
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .skipMemoryCache(false)
                                .error(GlideApp.with(OrdersPendingTab.this).load(R.drawable.personicon))
                                .into(holder.mImageView);

                        holder.mTextView1.setText(currentItem.getRestaurantName());
                        holder.mTextView2.setText(currentItem.getDeliveryTime());
                        holder.mTextView3.setText(currentItem.getPrice());
                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Scarico dal DB orderedFood
                                String orderID = currentItem.getOrderID();
                                String restaurantID = currentItem.getRestaurantID();
                                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                DatabaseReference orderedFoodRed = database.child("Company").child("Reservation").child("OrderedFood").child(restaurantID).child(orderID);
                                orderedFoodRed.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        ArrayList<OrderedDish> orderedFood = new ArrayList<>();
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

                        holder.confirmOrder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Ordine Arrivato:
                                // Company: passare da accepted a history
                                // Customer: passare da pending a history
                                // Rider: passare da pending a history
                                final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

                                // I want atomic queries -> create a map and do a single updateChilden on that map
                                final HashMap<String, Object> multipleAtomicQuery = new HashMap<>();

                                // 1. Company: prendo reservation, pongo status = 3, metto su history, rimuovo da pending
                                multipleAtomicQuery.put("Company/Reservation/Accepted/" + currentItem.getRestaurantID() + "/" + currentItem.getOrderID(), null);
                                currentItem.setStatus(3);
                                multipleAtomicQuery.put("Company/Reservation/History/" + currentItem.getRestaurantID() + "/" + currentItem.getOrderID(), currentItem);

                                // 2. Customer: prendo reservation, pongo status = 1, metto su history, rimuovo da pending
                                multipleAtomicQuery.put("Customer/Order/Pending/" + currentItem.getCustomerID() + "/" + currentItem.getOrderID(), null);
                                currentItem.setStatus(2);
                                multipleAtomicQuery.put("Customer/Order/History/" + currentItem.getCustomerID() + "/" + currentItem.getOrderID(), currentItem);

                                // 3. Rider: rimuovo da pending e pongo su history
                                DatabaseReference bikerDeliveryRef = database.child("Rider").child("Delivery").child("Pending");
                                bikerDeliveryRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(!dataSnapshot.exists())
                                            return;
                                        Delivery currentDelivery = dataSnapshot.getValue(Delivery.class);
                                        multipleAtomicQuery.put("Rider/Delivery/Pending/" + currentItem.getBikerID() + "/" + currentItem.getOrderID(), null);
                                        multipleAtomicQuery.put("Rider/Delivery/History/" + currentItem.getBikerID() + "/" + currentItem.getOrderID(), currentDelivery);
                                        database.updateChildren(multipleAtomicQuery);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                // Raf: Inizializzo o incremento il contatore nel nodo TimingOrder per le statistiche. Il contatore equivale al numero di ordini effettuati dal ristorante in una certa fascia ORARIA.
                                Calendar calendar = Calendar.getInstance();
                                String year = Integer.toString(calendar.get(Calendar.YEAR));
                                String month = Integer.toString(calendar.get(Calendar.MONTH)+1);
                                String weekOfMonth = Integer.toString(calendar.get(Calendar.WEEK_OF_MONTH));
                                final String node = year+"_"+month+"_"+weekOfMonth;
                                String dayOfMonth = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
                                String dayOfWeek = Integer.toString(calendar.get(Calendar.DAY_OF_WEEK)-1);
                                String hourOfDay = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)); // Fascia oraria
                                final String key = dayOfMonth+"_"+dayOfWeek+"_"+hourOfDay;

                                DatabaseReference timingOrderRef = database.child("Company").child("Reservation").child("TimingOrder")
                                        .child(currentItem.getRestaurantID()).child(node).child(key);
                                timingOrderRef.runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        Integer amountOfOrders = mutableData.getValue(Integer.class);
                                        if (amountOfOrders == null)
                                            mutableData.setValue(1);
                                        else
                                            mutableData.setValue(amountOfOrders + 1);
                                        return Transaction.success(mutableData);
                                    }
                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                                        //System.out.println("Transaction completed");
                                    }
                                });

                                // Aggiorno la tabella TopMeals
                                final HashMap<String, Integer> dishesIDQuantity = new HashMap<>();
                                DatabaseReference orderedFoodRef = database.child("Company").child("Reservation").child("OrderedFood").child(currentItem.getRestaurantID()).child(currentItem.getOrderID());
                                orderedFoodRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(!dataSnapshot.exists())
                                            return;
                                        for(DataSnapshot currentDish : dataSnapshot.getChildren()) {
                                            //dishesID.add(currentDish.getValue(Dish.class).getId());
                                            String dishID = currentDish.getValue(OrderedDish.class).getId();
                                            Integer dishQuantity = Integer.parseInt(currentDish.getValue(OrderedDish.class).getQuantity());
                                            dishesIDQuantity.put(dishID, dishQuantity);
                                        }
                                        if(dishesIDQuantity.size() == 0)
                                            return;

                                        DatabaseReference topMealsRef = database.child("Company").child("Reservation").child("TopMeals")
                                                .child(currentItem.getRestaurantID()).child(node).child(key);
                                        for (HashMap.Entry<String, Integer> entry : dishesIDQuantity.entrySet()) {
                                            final String dishID = entry.getKey();
                                            Integer dishQuantity = entry.getValue();
                                            topMealsRef.child(dishID).runTransaction(new Transaction.Handler() {
                                                @Override
                                                public Transaction.Result doTransaction(MutableData mutableData) {
                                                    Integer amountOfOrders = mutableData.getValue(Integer.class);
                                                    Integer dishQuantity = dishesIDQuantity.get(dishID);
                                                    if (amountOfOrders == null)
                                                        mutableData.setValue(dishQuantity);
                                                    else
                                                        mutableData.setValue(amountOfOrders + dishQuantity);
                                                    return Transaction.success(mutableData);
                                                }
                                                @Override
                                                public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                                                    //System.out.println("Transaction completed");
                                                }
                                            });
                                        }
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
                        return new OrderViewHolder(v);
                    }
                };

        RecyclerView mRecyclerView = view.findViewById(R.id.reservationRecyclerViewTab);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView mImageView;
        private TextView mTextView1;  // Address
        private TextView mTextView2;  // Lunch_time
        private TextView mTextView3;  // Price
        private RelativeLayout viewForeground;
        private ImageView confirmOrder;
        View mView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mImageView = itemView.findViewById(R.id.restaurantPhoto);
            mTextView1 = itemView.findViewById(R.id.textRestaurantName);
            mTextView2 = itemView.findViewById(R.id.lunch_time);
            mTextView3 = itemView.findViewById(R.id.order_price);
            viewForeground = itemView.findViewById(R.id.view_foreground);
            confirmOrder = itemView.findViewById(R.id.confirmOrder);
        }
    }
}
