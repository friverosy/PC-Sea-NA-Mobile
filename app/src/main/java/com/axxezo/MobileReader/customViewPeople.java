package com.axxezo.MobileReader;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;

public class customViewPeople extends RecyclerView.Adapter<customViewPeople.UserViewHolder> implements Filterable {
    private ArrayList<Cards> mDataSet;
    private ArrayList<Cards> filteredmDataSet;
    private cardsFilter cardsfilter;

    public customViewPeople(ArrayList<Cards> mDataSet) {
        this.mDataSet = mDataSet;
        this.filteredmDataSet = mDataSet;
    }


    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_row, parent, false);
        UserViewHolder userViewHolder = new UserViewHolder(v);
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.people_Name.setText(mDataSet.get(position).getName());
        holder.people_DNI.setText(mDataSet.get(position).getDocument());
        holder.people_destination.setText(mDataSet.get(position).getDestination());

        switch (mDataSet.get(position).getIsInside()) {
            case 0:
                holder.icon_entry.setText("");
                holder.icon_entry.setBackground(holder.icon_entry.getContext().getResources().getDrawable(R.drawable.circular_textview_blank));
                break;
            case 1:
                holder.icon_entry.setText("E");
                holder.icon_entry.setBackground(holder.icon_entry.getContext().getResources().getDrawable(R.drawable.circular_textview_embarked));
                break;
            case 2:
                holder.icon_entry.setText("D");
                holder.icon_entry.setBackground(holder.icon_entry.getContext().getResources().getDrawable(R.drawable.circular_textview_landed));
                break;

            //  holder.icon_entry.setText("");
            // holder.icon_entry.setText("" + mDataSet.get(position).getUser_name().charAt(0));
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }


    @Override
    public Filter getFilter() {
        if (cardsfilter == null)
            cardsfilter = new cardsFilter();
        return cardsfilter;
    }

    public Cards getItem(int position) {
        return mDataSet.get(position);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView people_DNI, people_Name, people_destination;
        TextView icon_entry;
        Spinner spinner_destination;

        UserViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.user_layout);
            people_DNI = (TextView) itemView.findViewById(R.id.people_DNI);
            people_Name = (TextView) itemView.findViewById(R.id.people_name);
            people_destination = (TextView) itemView.findViewById(R.id.people_destination);
            icon_entry = (TextView) itemView.findViewById(R.id.icon_entry);
            spinner_destination = (Spinner) itemView.findViewById(R.id.spinner_destination);


        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public String toString() {
        return "customViewPeople{" +
                "mDataSet=" + mDataSet +
                '}';
    }

    private class cardsFilter extends Filter {
        private customViewPeople adapter;

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            final FilterResults results = new FilterResults();
            ArrayList<Cards> filterList = new ArrayList<Cards>();
            if (constraint.length() == 0) {
                filterList.addAll(filteredmDataSet);
            } else {
                final String[] parts = constraint.toString().split("\\,");
                String filterPatternOrigin = "";
                String filterPatternDestination = "";
                if (parts[0].equals("< TODOS >") || parts[1].equals("< TODOS >")) {
                    filterPatternOrigin = parts[0].trim();
                    filterPatternDestination = parts[1].trim();
                    if (parts[0].equals("< TODOS >") && parts[1].equals("< TODOS >"))
                        filterList.addAll(filteredmDataSet);
                    else
                        for (final Cards cards : filteredmDataSet) {
                            if (parts[0].equals("< TODOS >")) {
                                if (cards.getDestination().contains(filterPatternDestination))
                                    filterList.add(cards);
                            } else if (parts[1].equals("< TODOS >"))
                                if (cards.getOrigin().contains(filterPatternOrigin))
                                    filterList.add(cards);
                        }
                }
                if (!parts[0].isEmpty() || !parts[1].isEmpty()) {
                    filterPatternOrigin = parts[0].trim();
                    filterPatternDestination = parts[1].trim();
                    Log.e("error", "origin:" + parts[0] + " destination" + parts[1]);
                    for (final Cards cards : filteredmDataSet) {
                        if (cards.getOrigin().contains(filterPatternOrigin) && cards.getDestination().contains(filterPatternDestination)) {
                            filterList.add(cards);
                        }
                    }
                }
                //Log.e("LIST","list size: "+filterList.size());
            }
            results.values = filterList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mDataSet = ((ArrayList<Cards>) results.values);
            notifyDataSetChanged();
        }
    }
}
