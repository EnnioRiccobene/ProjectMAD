package com.madgroup.appcompany;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewHoursAdapter extends RecyclerView.Adapter<RecyclerViewHoursAdapter.ViewHolder>{

    private static final String TAG = "RecycleViewHoursAdapter";

    private ArrayList<String> weekdayName = new ArrayList<>();
    private ArrayList<String> dayOldHourPreview = new ArrayList<>();
    private LinkedHashMap<String, String> hourValueMap = new LinkedHashMap<>();
    private String hourValue = "";
    Context mContext;

    public RecyclerViewHoursAdapter(Context mContext, ArrayList<String> weekdayName, ArrayList<String> dayOldHourPreview, LinkedHashMap<String, String> hourValueMap) {
        this.weekdayName = weekdayName;
        this.dayOldHourPreview = dayOldHourPreview;
        this.mContext = mContext;
        this.hourValueMap = hourValueMap;
    }

    @NonNull
    @Override
    public RecyclerViewHoursAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_hours_items, parent, false);
        ViewHolder holder = new ViewHolder(view);

        if (!holder.hiddenhour.isExpanded()) {
            holder.hiddenhour.collapse();
        }

        if (!holder.hiddenbox.isExpanded()) {
            holder.hiddenbox.collapse();
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHoursAdapter.ViewHolder holder, int position) {
        holder.dayCheckBox.setText(weekdayName.get(position));
        holder.dayPreviewHours.setText(dayOldHourPreview.get(position));
        //Gestisco l'apertura e la chiusura delle expandable view con la corrispondente checkbox
        holder.dayCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    holder.hiddenbox.expand();
                } else {
                    holder.hiddenbox.collapse();
                    if(holder.openCheckBox.isChecked()){
                        holder.openCheckBox.setChecked(false);
                    }
                    if(holder.closedCheckBox.isChecked()){
                        holder.closedCheckBox.setChecked(false);
                    }
                    if(holder.othersCheckBox.isChecked()){
                        holder.othersCheckBox.setChecked(false);
                        holder.hiddenhour.collapse();
                    }
                }
            }
        });

        /*Mi assicuro che l'ExpandableLayout compaia e scompaia correttamente
         * e di pulire l'hashmap in caso di deselezione al click della checkbox (senza toccare i valori closed e aperto24h)
         * in pratica se stringa diversa da closed o aperta, elimina il record*/
        holder.othersCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    holder.hiddenhour.expand();
                } else {
                    holder.hiddenhour.collapse();

                    //se il valore nella mappa nel è uno associato a questa checkbox rimuovilo se tolgo la spunta alla checkbox
                    String tmpHour = hourValueMap.get(holder.dayCheckBox.getText().toString());
                    assert tmpHour != null;
                    if(tmpHour.contains(holder.dayCheckBox.getText().toString())){
                        if((!tmpHour.equals(holder.closedCheckBox.getText().toString())) && (!tmpHour.equals(holder.openCheckBox.getText().toString()))){
                            hourValueMap.remove(holder.dayCheckBox.getText().toString());
                        }
                    }
                }
            }
        });

        holder.closedCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((CheckBox) v).isChecked()) {
                    String tmpHour = hourValueMap.get(holder.dayCheckBox.getText().toString());
                    assert tmpHour != null;
                    if(tmpHour.contains(holder.dayCheckBox.getText().toString())){
                        if(tmpHour.equals(holder.closedCheckBox.getText().toString())){
                            hourValueMap.remove(holder.dayCheckBox.getText().toString());
                        }
                    }
                }
            }
        });

        holder.openCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((CheckBox) v).isChecked()) {
                    String tmpHour = hourValueMap.get(holder.dayCheckBox.getText().toString());
                    assert tmpHour != null;
                    if(tmpHour.contains(holder.dayCheckBox.getText().toString())){
                        if(tmpHour.equals(holder.openCheckBox.getText().toString())){
                            hourValueMap.remove(holder.dayCheckBox.getText().toString());
                        }
                    }
                }
            }
        });

        /*Mi assicuro che solo una checkbox "aperto 24h" sia selezionata e implemento l'azione corrispondente*/
        holder.openCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if(holder.closedCheckBox.isChecked()){
                        holder.closedCheckBox.setChecked(false);
                    }
                    if(holder.othersCheckBox.isChecked()){
                        holder.othersCheckBox.setChecked(false);
                        holder.hiddenhour.collapse();
                    }

                    hourValue = holder.openCheckBox.getText().toString();
                    hourValueMap.put(holder.dayCheckBox.getText().toString(), hourValue);
                }
            }
        });

        /*Mi assicuro che solo una checkbox "chiuso" sia selezionata e implemento l'action corrispondente*/
        holder.closedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if(holder.openCheckBox.isChecked()){
                        holder.openCheckBox.setChecked(false);
                    }
                    if(holder.othersCheckBox.isChecked()){
                        holder.othersCheckBox.setChecked(false);
                        holder.hiddenhour.collapse();
                    }
                    hourValue = holder.closedCheckBox.getText().toString();
                    hourValueMap.put(holder.dayCheckBox.getText().toString(), hourValue);
                }
            }
        });

        /*Mi assicuro che solo una checkbox "altri orari" sia selezionata e implemento l'action corrispondente*/
        holder.othersCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(holder.openCheckBox.isChecked()){
                        holder.openCheckBox.setChecked(false);
                    }
                    if(holder.closedCheckBox.isChecked()){
                        holder.closedCheckBox.setChecked(false);
                    }

                    final String[] startSpinner = {holder.startingHours.getSelectedItem().toString()};
                    final String dot = holder.separator.getText().toString();
                    final String[] endSpinner = {holder.endingHours.getSelectedItem().toString()};

                    hourValue = startSpinner[0] + " " + dot + " " + endSpinner[0];
                    hourValueMap.put(holder.dayCheckBox.getText().toString(), hourValue);

                    holder.startingHours.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            startSpinner[0] = holder.startingHours.getSelectedItem().toString();
                            hourValue = startSpinner[0] + " " + dot + " " + endSpinner[0];
                            hourValueMap.put(holder.dayCheckBox.getText().toString(), hourValue);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    holder.endingHours.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            endSpinner[0] = holder.endingHours.getSelectedItem().toString();
                            hourValue = startSpinner[0] + " " + dot + " " + endSpinner[0];
                            hourValueMap.put(holder.dayCheckBox.getText().toString(), hourValue);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return weekdayName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout parentLayout;
        LinearLayout fixedItemLayout;
        CheckBox dayCheckBox;
        TextView dayPreviewHours;
        ExpandableRelativeLayout hiddenhour;
        ExpandableRelativeLayout hiddenbox;
        LinearLayout spinnerItemLayout;
        Spinner startingHours;
        TextView separator;
        Spinner endingHours;
        LinearLayout fixHoursLayout;
        CheckBox openCheckBox;
        CheckBox closedCheckBox;
        CheckBox othersCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.parent_layout);
            fixedItemLayout = itemView.findViewById(R.id.fixedItemLayout);
            dayCheckBox = itemView.findViewById(R.id.dayCheckBox);
            dayPreviewHours = itemView.findViewById(R.id.dayPreviewHours);
            hiddenhour = itemView.findViewById(R.id.hiddenhour);
            spinnerItemLayout = itemView.findViewById(R.id.spinnerItemLayout);
            startingHours = itemView.findViewById(R.id.startingHours);
            separator = itemView.findViewById(R.id.separator);
            endingHours = itemView.findViewById(R.id.endingHours);
            fixHoursLayout = itemView.findViewById(R.id.fixHoursLayout);
            openCheckBox = itemView.findViewById(R.id.openCheckBox);
            closedCheckBox = itemView.findViewById(R.id.closedCheckBox);
            hiddenbox = itemView.findViewById(R.id.hiddenbox);
            othersCheckBox = itemView.findViewById(R.id.othersCheckBox);
        }
    }
}
