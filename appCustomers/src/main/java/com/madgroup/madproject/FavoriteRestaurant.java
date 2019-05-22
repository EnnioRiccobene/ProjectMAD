package com.madgroup.madproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.madgroup.sdk.OrderedDish;
import com.madgroup.sdk.SmartLogger;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavoriteRestaurant.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FavoriteRestaurant#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoriteRestaurant extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SharedPreferences prefs;
    private String currentUser;

    private OnFragmentInteractionListener mListener;

    public FavoriteRestaurant() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoriteRestaurant.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoriteRestaurant newInstance(String param1, String param2) {
        FavoriteRestaurant fragment = new FavoriteRestaurant();
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
        View view = inflater.inflate(R.layout.fragment_favorite_restaurant, container, false);
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
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference().child("Customer").child("Favorite").child(currentUser);

        // Aggiungere dummy data

        // Aggiungere dummy data (end)

        FirebaseRecyclerOptions<Restaurant> options = new FirebaseRecyclerOptions.Builder<Restaurant>()
                .setQuery(favoritesRef, Restaurant.class)
                .build();

        final FirebaseRecyclerAdapter<Restaurant, FavoriteViewHolder> adapter =
                new FirebaseRecyclerAdapter<Restaurant, FavoriteViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final FavoriteRestaurant.FavoriteViewHolder holder, final int position, @NonNull final Restaurant model) {
                        holder.restaurant_name.setText(model.getName());
                        holder.food_category.setText(model.getFoodCategory());
                        holder.minimum_order_amount.setText(model.getMinOrder());
                        holder.delivery_cost_amount.setText(model.getDeliveryCost());
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pics")
                                .child("restaurants").child(model.getId());

                        GlideApp.with(FavoriteRestaurant.this)
                                .load(storageReference)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .error(GlideApp.with(FavoriteRestaurant.this).load(R.drawable.personicon))
                                .into(holder.restaurant_photo);


                        holder.cardLayout.setOnClickListener(new View.OnClickListener() {
                            @SuppressLint("ShowToast")
                            @Override
                            public void onClick(View v) {
                                if (    prefs.getString("Name", "").isEmpty() ||
                                        prefs.getString("Email", "").isEmpty() ||
                                        prefs.getString("Phone", "").isEmpty() ||
                                        prefs.getString("Address", "").isEmpty()) {

                                    //Il profilo è da riempire
//                                    Toast.makeText(SearchRestaurantActivity.this, "Your profile is not complete", Toast.LENGTH_LONG);
                                    Intent homepage = new Intent(getContext(), ProfileActivity.class);
                                    startActivity(homepage);
                                }
                                    //il profilo è pieno e c'è in save preference
                                    //Avvio la seguente Activity
                                    RestaurantMenuActivity.start(getContext(), model.getId());
                            }
                        });

                        refreshFavoriteList(holder, model);
                        holder.favoriteCheckBox.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                manageFavorites(holder, model);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_item, parent, false);
                        FavoriteViewHolder evh = new FavoriteViewHolder(v);
                        return evh;
                    }
                };

        RecyclerView mRecyclerView = view.findViewById(R.id.favoriteRecyclerViewTab);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
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

        public FavoriteViewHolder(@NonNull View itemView) {
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
        }
    }

    private void downloadProfilePic(String restaurantId, final CircleImageView mImageView) {
        final long ONE_MEGABYTE = 1024 * 1024;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pics").child("restaurants").child(restaurantId);
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Scarico l'immagine e la setto
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                mImageView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                int errorCode = ((StorageException) exception).getErrorCode();
                if (errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                    // La foto non è presente: carico immagine di default
                    Drawable defaultImg = getResources().getDrawable(R.drawable.personicon);
                    mImageView.setImageDrawable(defaultImg);
                }
            }
        });
    }

    public void manageFavorites(FavoriteRestaurant.FavoriteViewHolder holder, final Restaurant model) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference favoriteListRef = database.child("Customer").child("Favorite").child(currentUser).child(model.getId());
        if (holder.favoriteCheckBox.isChecked()) {
            // Add to favorite
            DatabaseReference restaurantRef = database.child("Company").child("Profile").child(model.getId());
            restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
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

    public void refreshFavoriteList(final FavoriteRestaurant.FavoriteViewHolder holder, Restaurant model) {
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
}
