package com.ANC_TeamEagles.mypurse;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ANC_TeamEagles.mypurse.utils.Constants;
import com.google.firebase.database.Query;


public class IncomeFragment extends Fragment {




    public IncomeFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.income_fragment, container, false);

        Query incomeQuery = App.transactionReference
                .orderByChild(Constants.QUERY_INCOME).equalTo(true);

        RecyclerView incomeRecycler = (RecyclerView) view.findViewById(R.id.rv_income);
        TransactionAdapter listAdapter = new TransactionAdapter(getActivity(),incomeQuery);
        incomeRecycler.setAdapter(listAdapter);
        incomeRecycler.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));


        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

}
