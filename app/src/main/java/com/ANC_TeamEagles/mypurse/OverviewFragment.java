package com.ANC_TeamEagles.mypurse;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.ANC_TeamEagles.mypurse.utils.Constants;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;


public class OverviewFragment extends Fragment {

    private Query reference;
    private TransactionAdapter adapter;

    @BindView(R.id.rv_transactions)
     RecyclerView transactionRecycler;

    public OverviewFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.overview_fragment, container, false);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        ButterKnife.bind(this,view);

        int key = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getInt(Constants.KEY_FILTER,0);

        transactionRecycler.setLayoutManager(linearLayoutManager);
        transactionRecycler.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));

        filterTransactions(key);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public void filterTransactions(int value){


        if (value == 0)
            reference = App.transactionReference;
        else if (value == 1)
            reference = App.transactionReference.orderByChild(Constants.QUERY_INCOME).equalTo(true);
        else
            reference = App.transactionReference.orderByChild(Constants.QUERY_INCOME).equalTo(false);

        adapter = new TransactionAdapter(getActivity(),reference);
        transactionRecycler.setAdapter(adapter);

        PreferenceManager.getDefaultSharedPreferences(MainActivity.instance)
                .edit()
                .putInt(Constants.KEY_FILTER,value).apply();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.tobuy_add).setVisible(false);
        super.onPrepareOptionsMenu(menu);

    }


}
