package com.geolocatepoop;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.geolocatepoop.databinding.CardComponentBinding;


public class CardViewHolder extends RecyclerView.ViewHolder {
    private final CardComponentBinding componentBinding;
    private final CardClickListnener clickListnener;
    public CardViewHolder(final CardComponentBinding cardComponentBinding, CardClickListnener cardClickListnener) {
        super(cardComponentBinding.getRoot());
        this.componentBinding = cardComponentBinding;
        this.clickListnener = cardClickListnener;
    }

    public void bindRoute(Route route) {
        this.componentBinding.routeName.setText(route.getName());
        this.componentBinding.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListnener.onClick(route);

            }
        });
    }

}
