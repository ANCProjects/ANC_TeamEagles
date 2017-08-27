package com.ANC_TeamEagles.mypurse.toBuy;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ANC_TeamEagles.mypurse.App;
import com.ANC_TeamEagles.mypurse.MainActivity;
import com.ANC_TeamEagles.mypurse.R;
import com.ANC_TeamEagles.mypurse.pojo.ToBuy;
import com.ANC_TeamEagles.mypurse.utils.Constants;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nezspencer on 8/18/17.
 */

public class ToBuyAdapter extends FirebaseRecyclerAdapter<ToBuy,ToBuyAdapter.ToBuyHolder> {

    private AppCompatActivity activity;
    public ToBuyAdapter(AppCompatActivity activity, Query ref) {
        super(ToBuy.class, R.layout.layout_tobuy_item, ToBuyHolder.class, ref);
        this.activity = activity;
    }

    @Override
    protected void populateViewHolder(ToBuyHolder viewHolder, final ToBuy model, final int position) {

        viewHolder.itemName.setText(model.getItemName());
        viewHolder.itemPrice.setText(activity.getString(R.string.to_buy_price,
                ""+model.getPrice()));

        if (model.getPriority() == 1)
            viewHolder.cardView.setBackgroundDrawable(activity.getResources()
                    .getDrawable(R.drawable.activatedbackground_red));
        else if (model.getPriority() == 2)
            viewHolder.cardView.setBackgroundDrawable(activity.getResources()
                    .getDrawable(R.drawable.activatedbackground_orange));
        else
            viewHolder.cardView.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable
                    .activatedbackground_white));

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                view.setActivated(true);
                setUpTobuyActionMode(view, position, model);
                return true;
            }
        });

        viewHolder.itemCondition.setText(activity.getString(R.string.to_buy_price,
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
        @BindView(R.id.tv_tobuy_condition)
        TextView itemCondition;

        @BindView(R.id.card_tobuy)
        CardView cardView;

        public ToBuyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }


    }

    private void setUpTobuyActionMode(final View view, final int position, final ToBuy toBuy){
        ActionMode.Callback callback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.tobuy_options,menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                MenuItem menuItem = menu.findItem(R.id.tobuy_mark_bought);
                if (toBuy.isBought())
                    menuItem.setIcon(R.drawable.ic_unmark_bought);
                else
                    menuItem.setIcon(R.drawable.ic_mark_bought_24px);
                return true;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                DatabaseReference reference;
                switch (item.getItemId()){
                    case R.id.tobuy_delete:
                        reference = getRef(position);
                        reference.removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if (databaseError == null)
                                    Toast.makeText(activity,"Deleted",Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                                mode.finish();
                                if (view != null)
                                    view.setActivated(false);

                            }
                        });
                        return true;

                    case R.id.tobuy_mark_bought:
                        reference = getRef(position);
                        if (toBuy.isBought())
                            reference.child("bought").setValue(false);
                        else
                            reference.child("bought").setValue(true);
                        notifyDataSetChanged();
                        mode.finish();
                        if (view != null)
                            view.setActivated(false);
                        return true;
                    default:
                        return false;
                }

            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                if (view != null)
                    view.setActivated(false);
            }
        };

        activity.startSupportActionMode(callback);
    }
}
