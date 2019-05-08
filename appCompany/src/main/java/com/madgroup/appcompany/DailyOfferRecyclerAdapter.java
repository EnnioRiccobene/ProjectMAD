package com.madgroup.appcompany;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.madgroup.sdk.Dish;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Locale;

public class DailyOfferRecyclerAdapter extends RecyclerView.Adapter<FindDishViewHolder> implements PopupMenu.OnMenuItemClickListener {


    private ArrayList<Dish> dataset = new ArrayList<>();
    private Context mContext;
    private DailyOfferRecyclerListener listener;
    private int currentIndex = -1;
    private String restaurantUID;

    public DailyOfferRecyclerAdapter(Context mContext, DailyOfferRecyclerListener listener, String restaurantUID) {
        this.mContext = mContext;
        this.listener = listener;
        this.restaurantUID=restaurantUID;
    }

    @NonNull
    @Override
    public FindDishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_offer_item, parent, false);
        FindDishViewHolder holder = new FindDishViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final FindDishViewHolder viewHolder, final int position) {
        Dish dish = dataset.get(position);

        viewHolder.dishName.setText(dish.getName());
        // Rappresentazione con due cifre decimali
        float dishFprice = Float.valueOf(dish.getPrice().replace("€", "").replace("$", "")
                .replace("£", "").replace(",", ".").replaceAll("\\s", ""));
        BigDecimal dishPrice = new BigDecimal(dishFprice).setScale(2, RoundingMode.HALF_UP);

//        Locale current = getResources().getConfiguration().locale;
        String current = Locale.getDefault().toString();
        String currency;
        if (current.equals("en_US")) {
            currency = " $";
        } else if (current.equals("en_GB")) {
            currency = " £";
        } else {
            currency = " €";
        }

        viewHolder.dishPrice.setText("" + dishPrice + currency);
        viewHolder.dishQuantity.setText(mContext.getResources().getString(R.string.availableQuantity) + " " + dish.getAvailableQuantity());
        viewHolder.dishDescription.setText(dish.getDescription());
        viewHolder.dishPhoto.setImageBitmap(dish.getPhoto());

        //todo: togliere questa prova
        //String url = "https://vignette.wikia.nocookie.net/simpsons/images/4/40/Picture0003.jpg/revision/latest/scale-to-width-down/200?cb=20110623042517";
        //Glide.with(mContext).load(url).into(viewHolder.dishPhoto);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("dish_pics")
                .child(restaurantUID).child(dish.getId());
        GlideApp.with(mContext)
                .load(storageReference)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(GlideApp.with(mContext).load(R.drawable.ic_dish))
                .into(viewHolder.dishPhoto);

        viewHolder.popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditPopup(viewHolder.popupButton, position);
            }
        });
    }


    public void showEditPopup(View v, int index) {
        currentIndex = index;
        PopupMenu popup = new PopupMenu(mContext, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.daily_offer_item_menu);
        popup.show();
    }

    public void setDataset(@NonNull ArrayList<Dish> dailyOfferList) {
        this.dataset = dailyOfferList != null ? dailyOfferList : new ArrayList<Dish>();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.itemEdit:
                listener.updateItem(dataset.get(currentIndex));
                return true;

            case R.id.itemRemove:
                listener.removeItem(dataset.get(currentIndex).getId());
                return true;

            default:
                return false;
        }
    }

    interface DailyOfferRecyclerListener {

        void removeItem(String id);

        void updateItem(Dish item);

    }
}
