package com.example.geolocation;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.example.geolocation.databinding.CardComponentBinding;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<CardViewHolder> {

    private final List<Route> routes;
    private final CardClickListnener clickListnener;

    public Adapter(final List<Route> routes, final CardClickListnener cardClickListnener) {
        this.routes = routes;
        this.clickListnener = cardClickListnener;
    }

    @Override
    public CardViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        LayoutInflater from = LayoutInflater.from(parent.getContext());
        CardComponentBinding binding = CardComponentBinding.inflate(from, parent, false);
        return new CardViewHolder(binding, this.clickListnener);
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, int position) {
        holder.bindRoute(this.routes.get(position));
    }

    @Override
    public int getItemCount() {
        return this.routes.size();
    }
}
