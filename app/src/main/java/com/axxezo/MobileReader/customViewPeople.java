package com.axxezo.MobileReader;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class customViewPeople extends RecyclerView.Adapter<customViewPeople.UserViewHolder> {
    private ArrayList<People> mDataSet;

    public customViewPeople(ArrayList<People> mDataSet) {
        this.mDataSet = mDataSet;
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
        holder.people_Nationality.setText(mDataSet.get(position).getNationality());
        holder.icon_entry.setText("");
       // holder.icon_entry.setText("" + mDataSet.get(position).getUser_name().charAt(0));
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView people_DNI, people_Name, people_Nationality, icon_entry;

        UserViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.user_layout);
            people_DNI = (TextView) itemView.findViewById(R.id.people_DNI);
            people_Name = (TextView) itemView.findViewById(R.id.people_name);
            people_Nationality = (TextView) itemView.findViewById(R.id.people_nationality);
            icon_entry = (TextView) itemView.findViewById(R.id.icon_entry);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
