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


public class ExpenditureFragment extends Fragment {




    public ExpenditureFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.expenditure_fragment, container, false);
        Query incomeQuery = App.userRef.child(Constants.NODE_TRANSACTION)
                .orderByChild(Constants.QUERY_INCOME)
                .equalTo(false);

        TransactionAdapter adapter = new TransactionAdapter(getActivity(),incomeQuery);
        RecyclerView expenditureRecycler = (RecyclerView) view.findViewById(R.id.rv_expense);
        expenditureRecycler.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        expenditureRecycler.setAdapter(adapter);

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
