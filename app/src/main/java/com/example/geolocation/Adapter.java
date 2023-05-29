package com.example.geolocation;

import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.Card> {

    List<Route> routes;

    public Adapter(final List<Route> routes) {
        this.routes = routes;
    }

    @NonNull
    @NotNull
    @Override
    public Card onCreateViewHolder(@NonNull @NotNull final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_component, parent, false);
        return new Card(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull final Card holder, int position) {
        holder.routeName.setText(routes.get(position).getName());
        holder.cv.setId(routes.get(position).getId());
    }

    @Override
    public long getItemId(int i) {
        return routes.indexOf(i);
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }
    @Override
    public void onAttachedToRecyclerView(@NotNull final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    public static class Card extends RecyclerView.ViewHolder {
        CardView cv;
        TextView routeName;

        public Card(final View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.card);
            routeName = itemView.findViewById(R.id.route_name);
        }

    }
}
