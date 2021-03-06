package com.madgroup.madproject;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madgroup.sdk.SmartLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchRestaurantTab2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchRestaurantTab2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchRestaurantTab2 extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private String currentUser;
    private SharedPreferences prefs;
    private SearchRestaurantTab2Adapter adapter;
    static public ArrayList<Restaurant> topRestaurant;
    static public RecyclerView recyclerView;

    public SearchRestaurantTab2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchRestaurantTab2.
     */

    public static SearchRestaurantTab2 newInstance(String param1, String param2) {
        SearchRestaurantTab2 fragment = new SearchRestaurantTab2();
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

    private void buildRecyclerView(final View view) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference restaurantRef = database.child("Company").child("Profile");
        topRestaurant = new ArrayList<>();
        restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                    return;
                for (DataSnapshot restaurant : dataSnapshot.getChildren()){
                    if(restaurant.getValue(Restaurant.class).getRatingCounter() != null && !restaurant.getValue(Restaurant.class).getRatingCounter().equals("0"))
                        topRestaurant.add(restaurant.getValue(Restaurant.class));
                }
                // ORDINARE LISTA
                Collections.sort(topRestaurant, new Comparator<Restaurant>() {
                    @Override
                    public int compare(Restaurant o1, Restaurant o2) {
                        float score1 = (0.75f * Float.parseFloat(o1.getRatingAvg())) * (0.25f * Float.parseFloat(o1.getRatingCounter()));
                        float score2 = (0.75f * Float.parseFloat(o2.getRatingAvg())) * (0.25f * Float.parseFloat(o2.getRatingCounter()));
                        if (score1 > score2)
                            return -1;
                        else if (score1 < score2)
                            return 1;
                        else
                            return 0;
                    }
                });
                for (Restaurant element : topRestaurant) {
                    float score1 = (0.75f * Float.parseFloat(element.getRatingAvg())) * (0.25f * Float.parseFloat(element.getRatingCounter()));
                    SmartLogger.d("Score: " + String.valueOf(score1));
                }
                recyclerView = (RecyclerView) view.findViewById(R.id.favoriteRecyclerViewTab);
                adapter = new SearchRestaurantTab2Adapter(getContext(), topRestaurant);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
