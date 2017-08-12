package com.ANC_TeamEagles.mypurse;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ANC_TeamEagles.mypurse.pojo.TransactionItem;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import java.text.DecimalFormat;

/**
 * Created by EmmanuelBaldwin on 8/6/2017.
 */

public class TransactionAdapter extends FirebaseRecyclerAdapter<TransactionItem, TransactionAdapter.TransactionHolder>{

    private Context context;
    public TransactionAdapter(Context context, Query ref) {
        super(TransactionItem.class, R.layout.layout_transaction_item, TransactionHolder.class, ref);
        this.context = context;
    }

    @Override
    protected void populateViewHolder(TransactionHolder viewHolder, TransactionItem model, int position) {

        String desc = model.getDescription();
        viewHolder.descText.setText(desc);
        viewHolder.symbol.setText(desc.substring(0,1));
        viewHolder.amountText.setText(new DecimalFormat("#,###,###,###").format(model.getAmount()));

        if (model.isIsIncome()){


            GradientDrawable Circle = (GradientDrawable) viewHolder.symbol.getBackground();
            int Color = ContextCompat.getColor(context, R.color.colorPrimary);
            Circle.setColor(Color);
        }
        else {

            GradientDrawable Circle = (GradientDrawable) viewHolder.symbol.getBackground();
            int Color = ContextCompat.getColor(context, R.color.dot_dark_screen1);
            Circle.setColor(Color);

        }

    }

    public static class TransactionHolder extends RecyclerView.ViewHolder{
        TextView descText;
        TextView amountText;
        TextView symbol;


        public TransactionHolder(View itemView) {
            super(itemView);

            amountText = (TextView)itemView.findViewById(R.id.amt_item);
             descText = (TextView)itemView.findViewById(R.id.tv_desc_item);
            symbol = (TextView)itemView.findViewById(R.id.descriptionSymbol);
        }
    }

}
