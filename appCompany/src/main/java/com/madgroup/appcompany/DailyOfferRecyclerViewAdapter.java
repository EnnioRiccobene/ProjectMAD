package com.madgroup.appcompany;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blackcat.currencyedittext.CurrencyEditText;

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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.daily_offer_item, viewGroup, false);
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
        popup.inflate(R.menu.daily_offer_item_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.itemEdit:
                showDialog(currentIndex);
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

    private void showDialog(final int currentIndex) {

        Dish currentDish = dailyOfferList.get(currentIndex);

        // custom dialog
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle(R.string.edit_dish);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dish_dialog);

        ImageButton dialogDismiss = (ImageButton) dialog.findViewById(R.id.dialogDismiss);
        ImageButton dialogConfirm = (ImageButton) dialog.findViewById(R.id.dialogConfirm);
        final CircleImageView dishImage = (CircleImageView) dialog.findViewById(R.id.dishImage);
        final EditText editDishName = (EditText) dialog.findViewById(R.id.editDishName);
        final EditText editDishDescription = (EditText) dialog.findViewById(R.id.editDishDescription);
        final EditText editDishQuantity = (EditText) dialog.findViewById(R.id.editDishQuantity);
        final CurrencyEditText editPrice = dialog.findViewById(R.id.editPrice);

        editDishName.setText(currentDish.getName());
        editDishDescription.setText(currentDish.getDescription());
        editDishQuantity.setText(""+currentDish.getAvailableQuantity());
        editPrice.setText((""+currentDish.getPrice()));
        dishImage.setImageBitmap(currentDish.getPhoto());

        dialogDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String dishName = editDishName.getText().toString();
                float dishPrice = 10;
                        //Float.parseFloat(editPrice.getText().toString());
                int dishQuantity = Integer.parseInt(editDishQuantity.getText().toString());
                String dishDesc = editDishDescription.getText().toString();
                Bitmap dishPhoto = ((BitmapDrawable) dishImage.getDrawable()).getBitmap();
                //todo fare un parsing della stringa per togliere il simbolo dell'euro e tutto quello che non è float
                //todo far funzionare il metodo per visualizzare l'editText come valuta
                // TODO FARE I CONTROLLI
                Dish currentDish = dailyOfferList.get(currentIndex);
                currentDish.setName(dishName);
                currentDish.setAvailableQuantity(dishQuantity);
                currentDish.setDescription(dishDesc);
                currentDish.setPhoto(dishPhoto);
                currentDish.setPrice(dishPrice);
                notifyItemChanged(currentIndex);
                dialog.dismiss();
            }
        });

        dialog.show();
    }



}
