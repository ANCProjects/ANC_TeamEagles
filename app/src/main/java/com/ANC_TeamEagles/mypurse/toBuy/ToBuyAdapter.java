package com.ANC_TeamEagles.mypurse.toBuy;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ANC_TeamEagles.mypurse.App;
import com.ANC_TeamEagles.mypurse.MainActivity;
import com.ANC_TeamEagles.mypurse.R;
import com.ANC_TeamEagles.mypurse.pojo.ToBuy;
import com.ANC_TeamEagles.mypurse.utils.Constants;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nezspencer on 8/18/17.
 */

public class ToBuyAdapter extends FirebaseRecyclerAdapter<ToBuy,ToBuyAdapter.ToBuyHolder> {

    private Context context;
    public ToBuyAdapter(Context context, Query ref) {
        super(ToBuy.class, R.layout.layout_tobuy_item, ToBuyHolder.class, ref);
        this.context = context;
    }

    @Override
    protected void populateViewHolder(ToBuyHolder viewHolder, ToBuy model, int position) {

        viewHolder.itemName.setText(model.getItemName());
        viewHolder.itemPrice.setText(context.getString(R.string.to_buy_price,
                ""+model.getPrice()));
        viewHolder.itemPriority.setText(String.valueOf(model.getPriority()));
        viewHolder.itemCondition.setText(context.getString(R.string.to_buy_price,
                ""+model.getWhenToBuy()));

        if (MainActivity.expendableAmtLeft >= model.getWhenToBuy()
                && !App.isToBuyNotificationClicked){
            MainActivity.instance.showNotification("You can now buy "+model.getItemName()
                    +"\nYou have more than "+model.getWhenToBuy(), Constants.FRAG_TO_BUY);
            App.isToBuyNotificationClicked = true;
        }

    }

    public static class ToBuyHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_tobuy_name)
        TextView itemName;
        @BindView(R.id.tv_tobuy_price)
        TextView itemPrice;
        @BindView(R.id.tv_tobuy_prority)
        TextView itemPriority;
        @BindView(R.id.tv_tobuy_condition)
        TextView itemCondition;

        public ToBuyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }
    }
}
