package com.ANC_TeamEagles.mypurse.toBuy;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ANC_TeamEagles.mypurse.App;
import com.ANC_TeamEagles.mypurse.R;
import com.ANC_TeamEagles.mypurse.pojo.ToBuy;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ToBuyFragment extends Fragment {

    @BindView(R.id.rv_tobuy)
    RecyclerView recyclerView;

    @BindView(R.id.fab_add_to_buy)
    FloatingActionButton fabAddItem;

    public ToBuyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_buy, container,false);

        ButterKnife.bind(this,view);


        ToBuyAdapter adapter = new ToBuyAdapter((AppCompatActivity) getActivity(), App.toBuyRef.orderByChild
                ("priority"));
        recyclerView.setAdapter(adapter);
        return  view;
    }

    @OnClick(R.id.fab_add_to_buy)
    public void addItem(){

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_to_buy_add,null,
                false);
        final EditText etName = (EditText) view.findViewById(R.id.buy_name);
        final EditText etPrice = (EditText) view.findViewById(R.id.buy_price);
        final Spinner etPriority = (Spinner) view.findViewById(R.id.buy_priority);
        final EditText etCondition = (EditText) view.findViewById(R.id.buy_condition);
        String [] items = getResources().getStringArray(R.array.priority_values);

        etPriority.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout
                .simple_dropdown_item_1line, items));

        new AlertDialog.Builder(getActivity())
                .setView(view)
                .setCancelable(true)
                .setPositiveButton("Add item", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = etName.getText().toString();
                        String price = etPrice.getText().toString();
                        String priority = etPriority.getSelectedItem().toString();
                        String condition = etCondition.getText().toString();

                        if (!TextUtils.isEmpty(name) && TextUtils.isDigitsOnly(price)
                                && TextUtils.isDigitsOnly(priority) && TextUtils.isDigitsOnly
                                (condition)){

                            ToBuy toBuy = new ToBuy(name,Double.parseDouble(price),
                                    Double.parseDouble(condition),
                                    Integer.parseInt(priority), false);

                            String key = App.toBuyRef.push().getKey();

                            HashMap<String,Object> map = new HashMap<String, Object>();

                            map.put("/"+key+"/",toBuy);

                            App.toBuyRef.updateChildren(map, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                    if (databaseError == null){
                                        Toast.makeText(getActivity()," added!", Toast
                                                .LENGTH_LONG).show();

                                    }
                                }
                            });

                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }



}
