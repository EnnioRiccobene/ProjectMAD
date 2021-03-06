package com.madgroup.madproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.madgroup.sdk.Delivery;
import com.madgroup.sdk.OrderedDish;
import com.madgroup.sdk.Reservation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

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

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


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
                if (currentItem.getBikerID().equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.error_evaluate_toast), Toast.LENGTH_SHORT).show();
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

    private void confirmOrderReceived(final Reservation currentItem) {
        // Ordine Arrivato:
        // Company: passare da accepted a history + Analytics
        // Customer: passare da pending a history
        // Rider: passare da pending a history, + Analytics
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        // I want atomic queries -> create a map and do a single updateChilden on that map
        final HashMap<String, Object> multipleAtomicQuery = new HashMap<>();

        // 1.1 Company: prendo reservation, pongo status = 3, metto su history, rimuovo da pending
        multipleAtomicQuery.put("Company/Reservation/Accepted/" + currentItem.getRestaurantID() + "/" + currentItem.getOrderID(), null);
        currentItem.setStatus(3);
        multipleAtomicQuery.put("Company/Reservation/History/" + currentItem.getRestaurantID() + "/" + currentItem.getOrderID(), currentItem);

        // 1.2 Company: Analytics
        // Inizializzo o incremento il contatore nel nodo TimingOrder per le statistiche.
        // Il contatore equivale al numero di ordini effettuati dal ristorante in una certa fascia ORARIA.
        Calendar calendar = Calendar.getInstance();
        String year = Integer.toString(calendar.get(Calendar.YEAR));
        String month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
        GregorianCalendar cal = new GregorianCalendar();
        cal.setMinimalDaysInFirstWeek(7);
        String weekOfMonth = Integer.toString(getWeekOfMonth(cal));
        final String node = year + "_" + month + "_" + weekOfMonth;
        String dayOfMonth = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        String dayOfWeek = Integer.toString(calendar.get(Calendar.DAY_OF_WEEK));
        String hourOfDay = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)); // Fascia oraria
        final String key = dayOfMonth + "_" + dayOfWeek + "_" + hourOfDay;

        DatabaseReference timingOrderRef = database.child("Analytics").child("TimingOrder")
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
                if (!dataSnapshot.exists())
                    return;
                for (DataSnapshot currentDish : dataSnapshot.getChildren()) {
                    //dishesID.add(currentDish.getValue(Dish.class).getId());
                    String dishID = currentDish.getValue(OrderedDish.class).getId();
                    Integer dishQuantity = Integer.parseInt(currentDish.getValue(OrderedDish.class).getQuantity());
                    dishesIDQuantity.put(dishID, dishQuantity);
                }
                if (dishesIDQuantity.size() == 0)
                    return;

                DatabaseReference topMealsRef = database.child("Analytics").child("TopMeals")
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


        // 2. Customer: prendo reservation, pongo status = 3, metto su history, rimuovo da pending
        multipleAtomicQuery.put("Customer/Order/Pending/" + currentItem.getCustomerID() + "/" + currentItem.getOrderID(), null);
        currentItem.setStatus(3);
        multipleAtomicQuery.put("Customer/Order/History/" + currentItem.getCustomerID() + "/" + currentItem.getOrderID(), currentItem);
        database.updateChildren(multipleAtomicQuery);


        // 3.1 Rider: rimuovo da pending e pongo su history
        // 3.2 Rider: incrementare deliveryNumber e totDistance
        DatabaseReference bikerDeliveryRef = database.child("Rider").child("Delivery").child("Pending").child(currentItem.getBikerID()).child(currentItem.getOrderID());
        final DatabaseReference riderProfileRef = database.child("Rider").child("Profile").child(currentItem.getBikerID());
        bikerDeliveryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    return;
                Delivery currentDelivery = dataSnapshot.getValue(Delivery.class);
                database.child("Rider").child("Delivery").child("Pending").child(currentItem.getBikerID()).child(currentItem.getOrderID()).setValue(null);
                database.child("Rider").child("Delivery").child("History").child(currentItem.getBikerID()).child(currentItem.getOrderID()).setValue(currentDelivery);
                final String distance = currentDelivery.getRestaurantCustomerDistance();
                riderProfileRef.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        String totDistance = "0";
                        String deliveryNumber = "0";
                        if (mutableData.child("totDistance").getValue() != null &&
                                mutableData.child("deliveryNumber").getValue() != null) {
                            totDistance = mutableData.child("totDistance").getValue(String.class);
                            deliveryNumber = mutableData.child("deliveryNumber").getValue(String.class);
                        }
                        totDistance = String.valueOf(Float.valueOf(totDistance) + Float.valueOf(distance));
                        deliveryNumber = String.valueOf(Integer.valueOf(deliveryNumber) + 1);
                        mutableData.child("totDistance").setValue(totDistance);
                        mutableData.child("deliveryNumber").setValue(deliveryNumber);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void confirmOrderReceivedDialog(Activity activity, String title, CharSequence message, final Reservation currentItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (currentItem.getBikerID().equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.error_evaluate_toast), Toast.LENGTH_SHORT).show();
                } else {
                    confirmOrderReceived(currentItem);
                }

                dialog.dismiss();

                showEvaluationDialog(getActivity(), "MADelivery", getString(R.string.evaluate_dialog_message), currentItem);
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
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .error(GlideApp.with(OrdersPendingTab.this).load(R.drawable.personicon))
                                .into(holder.mImageView);

                        holder.mTextView1.setText(currentItem.getRestaurantName());
                        holder.mTextView2.setText(currentItem.getDeliveryTime());
                        holder.mTextView3.setText(currentItem.getPrice());
                        holder.viewDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                confirmOrderReceivedDialog(getActivity(), "MADelivery", getString(R.string.dialog_order_received_msg), currentItem);
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
                        if (currentItem.getStatus() == 2)
                            holder.confirmOrder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    confirmOrderReceivedDialog(getActivity(), "MADelivery", getString(R.string.dialog_order_received_msg), currentItem);
                                }
                            });
                        else
                            holder.confirmOrder.getBackground().setColorFilter(ContextCompat.getColor(getContext(), android.R.color.darker_gray), PorterDuff.Mode.MULTIPLY);
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
        private AppCompatButton confirmOrder;
        private AppCompatButton viewDetails;
        View mView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mImageView = itemView.findViewById(R.id.restaurantPhoto);
            mTextView1 = itemView.findViewById(R.id.textRestaurantName);
            mTextView2 = itemView.findViewById(R.id.lunch_time);
            mTextView3 = itemView.findViewById(R.id.order_price);
            confirmOrder = itemView.findViewById(R.id.confirmButton);
            viewDetails = itemView.findViewById(R.id.viewDetails);
        }
    }

    /* Ritorna l'indice corrispondente alla settimana del mese relativa alla data passata per argomento. L'indice parte da 1. */
    private Integer getWeekOfMonth(GregorianCalendar cal) {
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        GregorianCalendar firstDay = new GregorianCalendar(year, month, 1);
        firstDay.setMinimalDaysInFirstWeek(7);

        int firstDayValue = firstDay.get(Calendar.DAY_OF_WEEK);
        if (firstDayValue == Calendar.MONDAY)
            return (cal.get(Calendar.WEEK_OF_MONTH));
        else
            return (cal.get(Calendar.WEEK_OF_MONTH) + 1);
    }
}
