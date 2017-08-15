package com.ANC_TeamEagles.mypurse;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import com.ANC_TeamEagles.mypurse.pojo.TransactionItem;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import static com.ANC_TeamEagles.mypurse.utils.Helpers.formatAmount;

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
        viewHolder.descText.setText(desc.substring(0, 1).toUpperCase() + desc.substring(1));
        viewHolder.symbol.setText(desc.substring(0,1).toUpperCase());
        viewHolder.amountText.setText(formatAmount(model.getAmount()));
        CharSequence time = DateUtils.getRelativeDateTimeString(context,model.getTimeCreated(),
                DateUtils.SECOND_IN_MILLIS,DateUtils.DAY_IN_MILLIS,DateUtils.FORMAT_ABBREV_ALL);
        viewHolder.date.setText(time);



        if (model.isIsIncome()){
            viewHolder.amountText.setText(" +N"+ formatAmount(model.getAmount()));

            viewHolder.amountText.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary));


            GradientDrawable Circle = (GradientDrawable) viewHolder.symbol.getBackground();
            int Color = ContextCompat.getColor(context, R.color.colorPrimary);
            Circle.setColor(Color);
        }
        else {
            viewHolder.amountText.setText("- N"+ formatAmount(model.getAmount()));
            viewHolder.amountText.setTextColor(ContextCompat.getColor(context,R.color.dot_dark_screen1));

            GradientDrawable Circle = (GradientDrawable) viewHolder.symbol.getBackground();
            int Color = ContextCompat.getColor(context, R.color.dot_dark_screen1);
            Circle.setColor(Color);

        }

    }

    public static class TransactionHolder extends RecyclerView.ViewHolder{
        TextView descText;
        TextView amountText;
        TextView symbol;
        TextView date;


        public TransactionHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.captureDate);
            amountText = (TextView)itemView.findViewById(R.id.amt_item);
            descText = (TextView)itemView.findViewById(R.id.tv_desc_item);
            symbol = (TextView)itemView.findViewById(R.id.descriptionSymbol);
        }
    }

}
