package com.madgroup.madproject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchRestaurantTab1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchRestaurantTab1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchRestaurantTab1 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<ratedDish> topMeal;

    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private String currentUser;
    private SharedPreferences prefs;
    private DatabaseReference restaurantRef;
    private FirebaseRecyclerOptions<Restaurant> options;
    private Context mContext;
    private String restaurantCategory = null;
    private SearchView searchRestaurant;

    public SearchRestaurantTab1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchRestaurantTab1.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchRestaurantTab1 newInstance(String param1, String param2) {
        SearchRestaurantTab1 fragment = new SearchRestaurantTab1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_searchrestaurant, container, false);
        prefs = this.getActivity().getSharedPreferences("MyData", MODE_PRIVATE);
        currentUser = prefs.getString("currentUser", "noUser");
        buildRecyclerView(view);

        // Inflate the layout for this fragment
        return view;    }

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

    private void buildRecyclerView(final View view) {
        // Getting the instance of Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        restaurantRef = database.getReference().child("Company").child("Profile");

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        mContext = this.getContext();
        CircleImageView photo = view.findViewById(R.id.restaurant_photo);
        searchRestaurant = view.findViewById(R.id.searchWidget);
        searchRestaurant.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Query applyQuery = restaurantRef.orderByChild("name").startAt(query).endAt(query + "\uf8ff");
                options = new FirebaseRecyclerOptions.Builder<Restaurant>()
                        .setQuery(applyQuery, Restaurant.class)
                        .build();
                onStart();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Query applyQuery = restaurantRef.orderByChild("name").startAt(newText).endAt(newText + "\uf8ff");
                options = new FirebaseRecyclerOptions.Builder<Restaurant>()
                        .setQuery(applyQuery, Restaurant.class)
                        .build();
                onStart();
                return true;
            }
        });

        recyclerView = view.findViewById(R.id.restaurantsrecycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        options = new FirebaseRecyclerOptions.Builder<Restaurant>()
                .setQuery(restaurantRef, Restaurant.class)
                .build();










//        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference restaurantRef = database.child("Company").child("Profile");
//        DatabaseReference companyRef = database.child("Company");
//        topMeal = new ArrayList<>();
//
//        companyRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(!dataSnapshot.exists())
//                    return;
//                for(DataSnapshot restaurantProfile : dataSnapshot.child("Profile").getChildren()){
//                    if(restaurantProfile.child("foodRatingAvg").getValue(String.class) == null
//                            || restaurantProfile.child("foodRatingAvg").getValue(String.class).equals("0"))
//                        continue;
//                    String restaurantName = restaurantProfile.child("name").getValue(String.class);
//                    String restaurantID = restaurantProfile.child("id").getValue(String.class);
//                    Float foodRating = Float.parseFloat(restaurantProfile.child("foodRating").getValue(String.class));
//                    for (DataSnapshot dish : dataSnapshot.child("Menu").child(restaurantID).getChildren()){
//                        Dish currentDish = dish.getValue(Dish.class);
//                        ratedDish newTopDish = new ratedDish(currentDish.getId(), currentDish.getName(), restaurantName, restaurantID, foodRating, Integer.parseInt(currentDish.getAvailableQuantity()), currentDish.getDescription(), currentDish.getPrice());
//                        topMeal.add(newTopDish);
//                    }
//                }
//                // ORDINARE LISTA
//                Collections.sort(topMeal, new Comparator<ratedDish>() {
//                    @Override
//                    public int compare(ratedDish o1, ratedDish o2) {
//                        float score1 = o1.getFoodRating() * o1.getOrderCount();
//                        float score2 = o2.getFoodRating() * o2.getOrderCount();
//                        if (score1 > score2)
//                            return 1;
//                        else if (score1 < score2)
//                            return -1;
//                        else
//                            return 0;
//                    }
//                });
//
////                recyclerView = (RecyclerView) view.findViewById(R.id.favoriteRecyclerViewTab);
////                SearchRestaurantTab2Adapter adapter = new SearchRestaurantTab2Adapter(topMeal);
////                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
////                recyclerView.setLayoutManager(mLayoutManager);
////                recyclerView.setItemAnimator(new DefaultItemAnimator());
////                recyclerView.setAdapter(adapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Restaurant, SearchRestaurantTab1.FindRestaurantViewHolder> adapter =
                new FirebaseRecyclerAdapter<Restaurant, SearchRestaurantTab1.FindRestaurantViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final SearchRestaurantTab1.FindRestaurantViewHolder holder, final int position, @NonNull final Restaurant model) {
                        holder.restaurant_name.setText(model.getName());
                        holder.food_category.setText(model.getFoodCategory());
                        holder.minimum_order_amount.setText(model.getMinOrder());
                        holder.delivery_cost_amount.setText(model.getDeliveryCost());
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pics")
                                .child("restaurants").child(model.getId());

                        GlideApp.with(mContext)
                                .load(storageReference)
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .skipMemoryCache(false)
                                .error(GlideApp.with(mContext).load(R.drawable.personicon))
                                .into(holder.restaurant_photo);

                        holder.cardLayout.setOnClickListener(new View.OnClickListener() {
                            @SuppressLint("ShowToast")
                            @Override
                            public void onClick(View v) {
                                prefs = mContext.getSharedPreferences("MyData", MODE_PRIVATE);
                                if (prefs.getString("Name", "").isEmpty() ||
                                        prefs.getString("Email", "").isEmpty() ||
                                        prefs.getString("Phone", "").isEmpty() ||
                                        prefs.getString("Address", "").isEmpty()) {

                                    //Il profilo è da riempire
                                    Intent homepage = new Intent(getActivity(), ProfileActivity.class);
                                    startActivity(homepage);
                                } else {
                                    //il profilo è pieno e c'è in save preference
                                    //Avvio la seguente Activity
                                    RestaurantMenuActivity.start(mContext, model.getId());
                                }
                            }
                        });
                        refreshFavoriteList(holder, model);
                        holder.favoriteCheckBox.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                manageFavorites(holder, model);
                            }
                        });

                        if(model.getRatingAvg() != null && !model.getRatingAvg().equals("0"))
                            holder.ratingBar.setRating(Float.parseFloat(model.getRatingAvg()));
                        else
                            holder.ratingBar.setVisibility(View.GONE);
                    }

                    @NonNull
                    @Override
                    public SearchRestaurantTab1.FindRestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_item, parent, false);
                        SearchRestaurantTab1.FindRestaurantViewHolder viewHolder = new SearchRestaurantTab1.FindRestaurantViewHolder(view);
                        return viewHolder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    public static class FindRestaurantViewHolder extends RecyclerView.ViewHolder {

        CardView cardLayout;
        RelativeLayout restaurant_item_layout;
        CircleImageView restaurant_photo;
        RelativeLayout name_button_layout;
        TextView restaurant_name;
        TextView food_category;
        TextView minimum_order;
        TextView minimum_order_amount;
        TextView delivery_cost;
        TextView delivery_cost_amount;
        CheckBox favoriteCheckBox;
        AppCompatRatingBar ratingBar;

        public FindRestaurantViewHolder(@NonNull View itemView) {
            super(itemView);

            cardLayout = itemView.findViewById(R.id.cardLayout);
            restaurant_item_layout = itemView.findViewById(R.id.restaurant_item_layout);
            restaurant_photo = itemView.findViewById(R.id.restaurant_photo);
            name_button_layout = itemView.findViewById(R.id.name_button_layout);
            restaurant_name = itemView.findViewById(R.id.restaurant_name);
            food_category = itemView.findViewById(R.id.food_category);
            minimum_order = itemView.findViewById(R.id.minimum_order);
            minimum_order_amount = itemView.findViewById(R.id.minimum_order_amount);
            delivery_cost = itemView.findViewById(R.id.delivery_cost);
            delivery_cost_amount = itemView.findViewById(R.id.delivery_cost_amount);
            favoriteCheckBox = itemView.findViewById(R.id.favoriteCheckBox);
            ratingBar = itemView.findViewById(R.id.restaurantRating);
        }
    }

    private void showFilterDialog() {
        //custom Dialog
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle(getString(R.string.filter_restaurants));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.restaurant_filter_dialog);

        TextView dialogDismiss = dialog.findViewById(R.id.dialogCancel);
        TextView dialogConfirm = dialog.findViewById(R.id.dialogConfirm);
        final TextView food_category = dialog.findViewById(R.id.food_category);
        RadioGroup radioGroupFoodCategory = dialog.findViewById(R.id.radio_group_food_category);
        RadioButton radioAll = dialog.findViewById(R.id.radio_all);
        RadioButton radioPizza = dialog.findViewById(R.id.radio_pizza);
        RadioButton radioSandwiches = dialog.findViewById(R.id.radio_sandwiches);
        RadioButton radioKebab = dialog.findViewById(R.id.radio_kebab);
        RadioButton radioItalian = dialog.findViewById(R.id.radio_italian);
        RadioButton radioAmerican = dialog.findViewById(R.id.radio_american);
        RadioButton radioDessert = dialog.findViewById(R.id.radio_desserts);
        RadioButton radioFry = dialog.findViewById(R.id.radio_fry);
        RadioButton radioVegetarian = dialog.findViewById(R.id.radio_vegetarian);
        RadioButton radioAsian = dialog.findViewById(R.id.radio_asian);
        RadioButton radioMediterranean = dialog.findViewById(R.id.radio_mediterranean);
        RadioButton radioSouthAmerican = dialog.findViewById(R.id.radio_south_american);
        final CheckBox freeDeliveryCheckbox = dialog.findViewById(R.id.freeDeliveryCheckBox);

        radioGroupFoodCategory.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.radio_all) {
                    restaurantCategory = getResources().getString(R.string.All);
                } else if (checkedId == R.id.radio_pizza) {
                    restaurantCategory = getResources().getString(R.string.Pizza);
                } else if (checkedId == R.id.radio_sandwiches) {
                    restaurantCategory = getResources().getString(R.string.Sandwiches);
                } else if (checkedId == R.id.radio_kebab) {
                    restaurantCategory = getResources().getString(R.string.Kebab);
                } else if (checkedId == R.id.radio_italian) {
                    restaurantCategory = getResources().getString(R.string.Italian);
                } else if (checkedId == R.id.radio_american) {
                    restaurantCategory = getResources().getString(R.string.American);
                } else if (checkedId == R.id.radio_desserts) {
                    restaurantCategory = getResources().getString(R.string.Desserts);
                } else if (checkedId == R.id.radio_fry) {
                    restaurantCategory = getResources().getString(R.string.Fry);
                } else if (checkedId == R.id.radio_vegetarian) {
                    restaurantCategory = getResources().getString(R.string.Vegetarian);
                } else if (checkedId == R.id.radio_asian) {
                    restaurantCategory = getResources().getString(R.string.Asian);
                } else if (checkedId == R.id.radio_mediterranean) {
                    restaurantCategory = getResources().getString(R.string.Mediterranean);
                } else if (checkedId == R.id.radio_south_american) {
                    restaurantCategory = getResources().getString(R.string.South_American);
                }
            }
        });

        dialogDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restaurantCategory = null;
                dialog.dismiss();
            }
        });

        dialogConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query applyQuery;
                if (restaurantCategory != null) {
                    String foodCatChildKey = "foodCategory";
                    if (restaurantCategory.equals("Pizza"))
                        foodCatChildKey = "catPizza";
                    else if (restaurantCategory.equals("Sandwiches") || restaurantCategory.equals("Panini"))
                        foodCatChildKey = "catSandwiches";
                    else if (restaurantCategory.equals("Kebab"))
                        foodCatChildKey = "catKebab";
                    else if (restaurantCategory.equals("Italian") || restaurantCategory.equals("Italiano"))
                        foodCatChildKey = "catItalian";
                    else if (restaurantCategory.equals("American") || restaurantCategory.equals("Americano"))
                        foodCatChildKey = "catAmerican";
                    else if (restaurantCategory.equals("Desserts") || restaurantCategory.equals("Dolci"))
                        foodCatChildKey = "catDesserts";
                    else if (restaurantCategory.equals("Fry") || restaurantCategory.equals("Fritti"))
                        foodCatChildKey = "catFry";
                    else if (restaurantCategory.equals("Vegetarian") || restaurantCategory.equals("Vegetariano"))
                        foodCatChildKey = "catVegetarian";
                    else if (restaurantCategory.equals("Asian") || restaurantCategory.equals("Asiatico"))
                        foodCatChildKey = "catAsian";
                    else if (restaurantCategory.equals("Mediterranean") || restaurantCategory.equals("Mediterraneo"))
                        foodCatChildKey = "catMediterranean";
                    else if (restaurantCategory.equals("South American") || restaurantCategory.equals("Sud Americano"))
                        foodCatChildKey = "catSouthAmerican";


                    if ((!restaurantCategory.equals("All")) && (!restaurantCategory.equals("Qualsiasi"))) {
                        //restaurantRef arriva fino a profile
                        String queryText = restaurantCategory;
//                        applyQuery = restaurantRef.orderByChild("foodCategory").startAt(queryText).endAt(queryText + "\uf8ff");
                        applyQuery = restaurantRef.orderByChild(foodCatChildKey).equalTo("true");
                        if (freeDeliveryCheckbox.isChecked())
                            applyQuery = restaurantRef.orderByChild(foodCatChildKey + "Del").startAt("true_000").endAt("true_000\uf8ff");

                    } else {
                        applyQuery = restaurantRef;
                        if (freeDeliveryCheckbox.isChecked())
                            applyQuery = restaurantRef.orderByChild("deliveryCost").startAt("0").endAt("00" + "\uf8ff");
                    }
                    options = new FirebaseRecyclerOptions.Builder<Restaurant>()
                            .setQuery(applyQuery, Restaurant.class)
                            .build();
                } else {
                    applyQuery = restaurantRef;
                    if (freeDeliveryCheckbox.isChecked())
                        applyQuery = restaurantRef.orderByChild("deliveryCost").startAt("0").endAt("00" + "\uf8ff");

                    options = new FirebaseRecyclerOptions.Builder<Restaurant>()
                            .setQuery(applyQuery, Restaurant.class)
                            .build();
                }
                restaurantCategory = null;
                dialog.dismiss();
                onStart();
            }
        });
        dialog.show();
    }

    private void manageFavorites(SearchRestaurantTab1.FindRestaurantViewHolder holder, final Restaurant model) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference favoriteListRef = database.child("Customer").child("Favorite").child(currentUser).child(model.getId());
        if (holder.favoriteCheckBox.isChecked()) {
            // Add to favorite
            DatabaseReference restaurantRef = database.child("Company").child("Profile").child(model.getId());
            restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Restaurant newFavorite = dataSnapshot.getValue(Restaurant.class);
                        favoriteListRef.setValue(newFavorite);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            // Remove from favorite
            favoriteListRef.removeValue();
        }
    }

    private void refreshFavoriteList(final SearchRestaurantTab1.FindRestaurantViewHolder holder, Restaurant model) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference favoritesRef = database.child("Customer").child("Favorite").child(currentUser).child(model.getId());
        favoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    holder.favoriteCheckBox.setChecked(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.searchItem:
                if (searchRestaurant.getVisibility() == View.GONE) {
                    searchRestaurant.setVisibility(View.VISIBLE);
                    searchRestaurant.onActionViewExpanded();
                    searchRestaurant.requestFocus();
                } else
                    searchRestaurant.setVisibility(View.GONE);
                return true;
            case R.id.filterItem:
                showFilterDialog();
                return true;
        }
        return true;
    }
}
