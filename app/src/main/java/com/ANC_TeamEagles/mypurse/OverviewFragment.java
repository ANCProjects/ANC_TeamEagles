package com.ANC_TeamEagles.mypurse;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ANC_TeamEagles.mypurse.utils.Constants;
import com.ANC_TeamEagles.mypurse.utils.PrefManager;
import com.google.firebase.database.DatabaseReference;

import butterknife.BindView;
import butterknife.ButterKnife;


public class OverviewFragment extends Fragment {

    private DatabaseReference reference;
    private PrefManager manager;
    private TransactionAdapter adapter;

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

        adapter = new TransactionAdapter(getActivity(),reference);
        transactionRecycler.setLayoutManager(manager);
        transactionRecycler.setAdapter(adapter);
        transactionRecycler.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));


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


}
