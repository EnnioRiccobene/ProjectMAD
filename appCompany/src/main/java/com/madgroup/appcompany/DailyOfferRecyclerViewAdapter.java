package com.madgroup.appcompany;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

// Creo un ViewHolder (che contiene il layout della cella) e l'Adapter lo setto di tipo ViewHolder

public class DailyOfferRecyclerViewAdapter extends FirebaseRecyclerAdapter<Dish, FindDishViewHolder>
        implements PopupMenu.OnMenuItemClickListener {

    private ArrayList<Dish> dailyOfferList = new ArrayList<>();
    private Context mContext;
    private int currentIndex = -1;
    private DatabaseReference dishRef;
    public DailyOfferActivity.AdapterHandler adapterhandler;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public DailyOfferRecyclerViewAdapter(@NonNull FirebaseRecyclerOptions<Dish> options, DatabaseReference dishRef, Context mContext) {
        super(options);
        this.dishRef = dishRef;
        this.mContext = mContext;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.itemEdit:
                showDialog(currentIndex);
                return true;

            case R.id.itemRemove:
                //rimuovo il piatto dal db e dall'arrayList
                dishRef.child(dailyOfferList.get(currentIndex).getId()).removeValue();
                dailyOfferList.remove(currentIndex);
                notifyItemRemoved(currentIndex);
                notifyItemRangeChanged(currentIndex, dailyOfferList.size());
                return true;

            default:
                return false;
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull final FindDishViewHolder viewHolder, final int i, @NonNull Dish dish) {
        viewHolder.dishName.setText(dish.getName());
        // Rappresentazione con due cifre decimali
        float dishFprice = Float.valueOf(dish.getPrice().replace("€", "").replace("$", "")
                .replace("£", "").replace(",", ".").replaceAll("\\s",""));
        BigDecimal dishPrice = new BigDecimal(dishFprice).setScale(2, RoundingMode.HALF_UP);
        viewHolder.dishPrice.setText("" + dishPrice + " €"); //todo: aggiustare prezzo, valuta e multilingua
        viewHolder.dishQuantity.setText("Quantità disponibile: " + dish.getAvailableQuantity());
        viewHolder.dishDescription.setText(dish.getDescription());
        viewHolder.dishPhoto.setImageBitmap(dish.getPhoto());

        dailyOfferList.add(new Dish(dish.getId(), dish.getName(), dish.getPrice(), dish.getAvailableQuantity(), dish.getDescription()));

        viewHolder.popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditPopup(viewHolder.popupButton, i);
            }
        });
    }

    @NonNull
    @Override
    public FindDishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_offer_item, parent, false);
        FindDishViewHolder holder = new FindDishViewHolder(view);
        return holder;
    }


//    public DailyOfferRecyclerViewAdapter(Context mContext, ArrayList<Dish> dailyOfferList, DatabaseReference dishRef) {
//        this.dailyOfferList = dailyOfferList;
//        this.mContext = mContext;
//        this.dishRef = dishRef;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.daily_offer_item, viewGroup, false);
//        ViewHolder holder = new ViewHolder(view);
//        return holder;
//    }
//
//    @SuppressLint("SetTextI18n")
//    @Override
//    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
//        viewHolder.dishName.setText(dailyOfferList.get(i).getName());
//        // Rappresentazione con due cifre decimali
//        BigDecimal dishPrice = new BigDecimal(dailyOfferList.get(i).getPrice()).setScale(2, RoundingMode.HALF_UP);
//        viewHolder.dishPrice.setText(""+dishPrice+" €"); //todo: aggiustare prezzo, valuta e multilingua
//        viewHolder.dishQuantity.setText("Quantità disponibile: "+dailyOfferList.get(i).getAvailableQuantity());
//        viewHolder.dishDescription.setText(dailyOfferList.get(i).getDescription());
//        viewHolder.dishPhoto.setImageBitmap(dailyOfferList.get(i).getPhoto());
//
//        viewHolder.popupButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showEditPopup(viewHolder.popupButton, i);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return dailyOfferList.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//
//        RelativeLayout layout;
//        TextView dishName;
//        TextView dishDescription;
//        TextView dishPrice;
//        CircleImageView dishPhoto;
//        TextView dishQuantity;
//        ImageView popupButton;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            layout = itemView.findViewById(R.id.dish_item_layout);
//            dishName = itemView.findViewById(R.id.dish_name);
//            dishDescription = itemView.findViewById(R.id.dish_description);
//            dishPhoto = itemView.findViewById(R.id.dish_photo);
//            dishPrice = itemView.findViewById(R.id.dish_price);
//            dishQuantity = itemView.findViewById(R.id.dish_quantity);
//            popupButton = itemView.findViewById(R.id.popupButton);
//        }
//    }
//
    public void showEditPopup(View v, int index) {
        currentIndex = index;
        PopupMenu popup = new PopupMenu(mContext, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.daily_offer_item_menu);
        popup.show();
    }

//    @Override
//    public boolean onMenuItemClick(MenuItem item) {
//        switch (item.getItemId()) {
//
//            case R.id.itemEdit:
//                showDialog(currentIndex);
//                return true;
//
//            case R.id.itemRemove:
//                //rimuovo il piatto dal db e dall'arrayList
//                dishRef.child(dailyOfferList.get(currentIndex).getId());
//                dailyOfferList.remove(currentIndex);
//                notifyItemRemoved(currentIndex);
//                notifyItemRangeChanged(currentIndex, dailyOfferList.size());
//                return true;
//
//            default:
//                return false;
//        }
//    }

    // Dialog per la modifica di un piatto già esistente
    //todo:update piatto nel db
    private void showDialog(final int currentIndex) {

        Dish currentDish = dailyOfferList.get(currentIndex);

        // custom dialog
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle(R.string.edit_dish);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dish_dialog);

        // Modifico il campo currentDialog di DailyOfferActivity
        if (this.adapterhandler != null) {
            this.adapterhandler.setCurrentDialog(dialog);
        }

        ImageButton dialogDismiss = (ImageButton) dialog.findViewById(R.id.dialogDismiss);
        ImageButton dialogConfirm = (ImageButton) dialog.findViewById(R.id.dialogConfirm);
        final CircleImageView dishImage = (CircleImageView) dialog.findViewById(R.id.dishImage);
        final EditText editDishName = (EditText) dialog.findViewById(R.id.editDishName);
        final EditText editDishDescription = (EditText) dialog.findViewById(R.id.editDishDescription);
        final EditText editDishQuantity = (EditText) dialog.findViewById(R.id.editDishQuantity);
        final CurrencyEditText editPrice = dialog.findViewById(R.id.editPrice);

        editDishName.setText(currentDish.getName());
        editDishDescription.setText(currentDish.getDescription());
        editDishQuantity.setText(""+currentDish.getAvailableQuantity());//editPrice.setText((""+currentDish.getPrice()));
        // Prezzo in rappresentazione decimale con due cifre dopo la virgola
        float dishFprice = Float.valueOf(currentDish.getPrice().replace(",", ".").replace("", "")
                .replace("€", "").replace("$", "").replaceAll("\\s",""));
        BigDecimal dishPrice = new BigDecimal(dishFprice).setScale(2, RoundingMode.HALF_UP);
        editPrice.setText(""+dishPrice);
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

                String local = editPrice.getLocale().toString();
                Long rawVal = editPrice.getRawValue();
                String formattedVal = editPrice.formatCurrency(Long.toString(rawVal));
                String floatStringVal = "";
                float floatPrice = 0;

                if(local.equals("en_US")){
                    floatStringVal = formattedVal.replace(",", "").replace("$", "").replaceAll("\\s","");
                    floatPrice = Float.parseFloat(floatStringVal);
                } else if(local.equals("en_GB")){
                    floatStringVal = formattedVal.replace(",", "").replace("£", "").replaceAll("\\s","");
                    floatPrice = Float.parseFloat(floatStringVal);
                } else if(local.equals("it_IT")){
                    floatStringVal = formattedVal.replace(".", "").replace(",", ".").replace("€", "").replaceAll("\\s","");
                    floatPrice = Float.parseFloat(floatStringVal);
                }

                if (editDishName.getText().toString().isEmpty() || editDishQuantity.getText().toString().isEmpty()) {
                    Toast.makeText(mContext, mContext.getString(R.string.requiredString), Toast.LENGTH_SHORT).show();
                } else if (floatPrice==0) {
                    Toast.makeText(mContext, mContext.getString(R.string.requiredPrice), Toast.LENGTH_SHORT).show();
                } else {
                    if (editDishDescription.getText().toString().isEmpty()) {
                        int dishQuantity = Integer.parseInt(editDishQuantity.getText().toString());
                        Bitmap dishPhoto = ((BitmapDrawable) dishImage.getDrawable()).getBitmap();

                        Dish currentDish = dailyOfferList.get(currentIndex);
                        currentDish.setName(editDishName.getText().toString());
                        currentDish.setAvailableQuantity(String.valueOf(dishQuantity));
                        currentDish.setDescription("");
                        currentDish.setPhoto(dishPhoto);
                        currentDish.setPrice(String.valueOf(floatPrice));
                    } else {
                        int dishQuantity = Integer.parseInt(editDishQuantity.getText().toString());
                        String dishDesc = editDishDescription.getText().toString();
                        Bitmap dishPhoto = ((BitmapDrawable) dishImage.getDrawable()).getBitmap();

                        Dish currentDish = dailyOfferList.get(currentIndex);
                        currentDish.setName(editDishName.getText().toString());
                        currentDish.setAvailableQuantity(String.valueOf(dishQuantity));
                        currentDish.setDescription(dishDesc);
                        currentDish.setPhoto(dishPhoto);
                        currentDish.setPrice(String.valueOf(floatPrice));

                        notifyItemChanged(currentIndex);
                        dialog.dismiss();
                    }
                }
            }
        });

        dialog.show();
    }

}


class FindDishViewHolder extends RecyclerView.ViewHolder {

    RelativeLayout layout;
    TextView dishName;
    TextView dishDescription;
    TextView dishPrice;
    CircleImageView dishPhoto;
    TextView dishQuantity;
    ImageView popupButton;

    public FindDishViewHolder(@NonNull View itemView) {
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