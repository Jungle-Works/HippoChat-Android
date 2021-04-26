package com.hippo.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hippo.R;

import java.util.ArrayList;

/**
 * Created by gurmail on 2020-02-07.
 *
 * @author gurmail
 */
public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {

    private ArrayList<String> strings = new ArrayList<>();
    private OnSuggestionClickListener clickListener;

    public SuggestionAdapter(ArrayList<String> strings, OnSuggestionClickListener clickListener) {
        this.strings = strings;
        this.clickListener = clickListener;
    }

    public void updateList(ArrayList<String> strings) {
        this.strings.clear();
        this.strings.addAll(strings);
        //this.strings = strings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.hippo_item_suggestion, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int pos) {
        viewHolder.text.setText(strings.get(pos));
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);

            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clickListener != null) {
                        clickListener.onClicked(strings.get(getAdapterPosition()));
                        strings.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface OnSuggestionClickListener {
        void onClicked(String value);
    }
}
