package com.madgroup.appcompany;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

// Creo un ViewHolder (che contiene il layout della cella) e l'Adapter lo setto di tipo ViewHolder

public class DailyOfferRecyclerViewAdapter extends RecyclerView.Adapter<DailyOfferRecyclerViewAdapter.ViewHolder>  implements PopupMenu.OnMenuItemClickListener{

    private ArrayList<Dish> dailyOfferList = new ArrayList<>();
    private Context mContext;
    private static String POPUP_CONSTANT = "mPopup";
    private static String POPUP_FORCE_SHOW_ICON = "setForceShowIcon";
    private int currentIndex = -1;

    public DailyOfferRecyclerViewAdapter(Context mContext, ArrayList<Dish> dailyOfferList) {
        this.dailyOfferList = dailyOfferList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dish_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        viewHolder.dishName.setText(dailyOfferList.get(i).getName());
        viewHolder.dishPrice.setText(""+dailyOfferList.get(i).getPrice()+"€");
        viewHolder.dishQuantity.setText("Quantità disponibile: "+dailyOfferList.get(i).getAvailableQuantity());
        viewHolder.dishDescription.setText(dailyOfferList.get(i).getDescription());
        viewHolder.dishPhoto.setImageBitmap(dailyOfferList.get(i).getPhoto());

//        viewHolder.layout.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mContext, ""+i, Toast.LENGTH_SHORT).show();
//            }
//        });

        viewHolder.popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, "Selected menu with index: "+i, Toast.LENGTH_SHORT).show();
                //viewHolder.popupButton.setBackgroundColor(Color.TRANSPARENT);
                showEditPopup(viewHolder.popupButton, i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dailyOfferList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout layout;
        TextView dishName;
        TextView dishDescription;
        TextView dishPrice;
        CircleImageView dishPhoto;
        TextView dishQuantity;
        ImageView popupButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.dish_item_layout);
            dishName = itemView.findViewById(R.id.dish_name);
            dishDescription = itemView.findViewById(R.id.dish_description);
            dishPhoto = itemView.findViewById(R.id.dish_photo);
            dishPrice = itemView.findViewById(R.id.dish_price);
            dishQuantity = itemView.findViewById(R.id.dish_quantity);
            popupButton = itemView.findViewById(R.id.popupButton);
        }


    }

    public void showEditPopup(View v, int index) {
        currentIndex = index;
        PopupMenu popup = new PopupMenu(mContext, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.actions2);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.itemEdit:
                showEnnioDialog(currentIndex);
                return true;

            case R.id.itemRemove:
                dailyOfferList.remove(currentIndex);
                notifyItemRemoved(currentIndex);
                notifyItemRangeChanged(currentIndex, dailyOfferList.size());
                return true;

            default:
                return false;
        }
    }

    private void showEnnioDialog(int currentIndex) {
        Toast.makeText(mContext, "Apri dialog. Index: "+currentIndex, Toast.LENGTH_SHORT).show();
    }

}
