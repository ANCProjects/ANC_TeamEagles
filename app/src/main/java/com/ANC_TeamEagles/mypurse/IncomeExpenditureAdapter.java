package com.ANC_TeamEagles.mypurse;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by EmmanuelBaldwin on 8/6/2017.
 */

public class IncomeExpenditureAdapter extends ArrayAdapter<IncomeExpenditure>{
    private Context context;
    private int resource;

    public IncomeExpenditureAdapter(Context context, int resource, ArrayList<IncomeExpenditure> listItems){
        super(context, resource, listItems);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

//        if(listItemView == null){
//            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.income_expenditure_listitem, parent, false);
//        }else {
//            ViewGroup viewGroup = (ViewGroup)listItemView.getParent();
//            viewGroup.removeView(listItemView);
//        }

        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listItemView=inflater.inflate(resource, parent,false);

        IncomeExpenditure currentPosition = getItem(position);

        TextView descriptionIconTextView = (TextView)listItemView.findViewById(R.id.descriptionSymbol);
        descriptionIconTextView.setText(currentPosition.getDescriptionIcon());

        TextView descriptionTextView = (TextView)listItemView.findViewById(R.id.description);
        descriptionTextView.setText(currentPosition.getDescriptionText());

        TextView amountTextView = (TextView)listItemView.findViewById(R.id.amount);
        amountTextView.setText(currentPosition.getAmountText());

        TextView dateCapturedTextView = (TextView)listItemView.findViewById(R.id.captureDate);
        dateCapturedTextView.setText(currentPosition.getDateCaptured());

        return listItemView;
    }
}
