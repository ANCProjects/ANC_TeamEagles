package com.ANC_TeamEagles.mypurse;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.drawable.GradientDrawable;
import com.ANC_TeamEagles.mypurse.pojo.TransactionItem;
import com.ANC_TeamEagles.mypurse.utils.Constants;
import com.ANC_TeamEagles.mypurse.utils.PrefManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;


public class OverviewFragment extends Fragment {

    private DatabaseReference reference;
    private PrefManager manager;
    TransactionAdapter adapter;

    @BindView(R.id.rv_transactions)
    RecyclerView transactionRecycler;

    public OverviewFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        manager = new PrefManager(getActivity());
        reference = App.userRef.child(Constants.NODE_TRANSACTION);
        View view = inflater.inflate(R.layout.overview_fragment, container, false);
        final LinearLayoutManager manager = new LinearLayoutManager(getActivity());


        ButterKnife.bind(this,view);

        adapter = new TransactionAdapter();
        transactionRecycler.setLayoutManager(manager);
        transactionRecycler.setAdapter(adapter);
        transactionRecycler.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = adapter.getItemCount();
                int lastVisiblePosition =
                        manager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    transactionRecycler.scrollToPosition(positionStart);
                }
            }
        });

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("LOGGER", ""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        reference.addValueEventListener(eventListener);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    public class TransactionAdapter extends FirebaseRecyclerAdapter<TransactionItem, Holder>{


        public TransactionAdapter() {
            super(TransactionItem.class, R.layout.layout_transaction_item, Holder.class, reference);

            Log.e("LOGGER"," here");
        }

        @Override
        protected TransactionItem parseSnapshot(DataSnapshot snapshot) {
            Log.e("LOGGER", ""+snapshot.getChildrenCount());
            return super.parseSnapshot(snapshot);
        }

        @Override
        protected void populateViewHolder(Holder viewHolder, TransactionItem model, int position) {
            viewHolder.descText.setText(model.getDescription());
            viewHolder.amountText.setText(String.valueOf(model.getAmount()));
            char des = viewHolder.descText.getText().charAt(0);
            char desc =    Character.toUpperCase(des);
           viewHolder.symbol.setText(Character.toString(desc));

            Log.e("LOGGER", model.getDescription());

            Log.e("LOGGER",""+model.isIsIncome());
            if (model.isIsIncome()){

              //  viewHolder.itemView.setBackgroundColor(
               //         getActivity().getResources().getColor(android.R.color.holo_green_light));
                GradientDrawable Circle = (GradientDrawable) viewHolder.symbol.getBackground();
                int Color = ContextCompat.getColor(getContext(), R.color.colorPrimary);
               Circle.setColor(Color);
            }
            else {
              //  viewHolder.itemView.setBackgroundColor(
                //        getActivity().getResources().getColor(android.R.color.holo_red_light));
                GradientDrawable Circle = (GradientDrawable) viewHolder.symbol.getBackground();
                int Color = ContextCompat.getColor(getContext(), R.color.dot_dark_screen1);
                Circle.setColor(Color);

            }

        }


    }

    public static class Holder extends RecyclerView.ViewHolder{

        TextView descText;
        TextView amountText;
       TextView symbol;

        public Holder(View itemView) {
            super(itemView);
            descText= (TextView)itemView.findViewById(R.id.tv_desc_item);
            amountText = (TextView) itemView.findViewById(R.id.amt_item);
            symbol = (TextView) itemView.findViewById(R.id.descriptionSymbol);

        }
    }

}
