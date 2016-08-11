package com.cardsui.ScoreCard;

import com.fima.cardsui.objects.RecyclableCard;
import com.xy.fy.main.R;

import android.view.View;
import android.widget.TextView;

public class MyCard extends RecyclableCard {

    public MyCard(String title) {
        super(title);
    }

    @Override
    protected int getCardLayoutId() {
        return R.layout.card_ex;
    }

    @Override
    protected void applyTo(View convertView) {
        ((TextView) convertView.findViewById(R.id.title)).setText(title);
    }
}
