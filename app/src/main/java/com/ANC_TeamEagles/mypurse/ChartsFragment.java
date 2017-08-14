package com.ANC_TeamEagles.mypurse;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.ANC_TeamEagles.mypurse.App.thisMonthExpenseRef;
import static com.ANC_TeamEagles.mypurse.App.weeklyTransactionRef;


public class ChartsFragment extends Fragment {


    private ValueEventListener weeklyListener;
    private ValueEventListener monthlyListener;

    private ArrayList<String> xAxisData;
    private static String[] xAxisLabel = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
    private HashMap<String,Double> yAxisData;
    private Calendar calendar = Calendar.getInstance();

    private boolean isMonthViewChosen;

    @BindView(R.id.bar_chart)
    BarChart barChart;

    private Unbinder unbinder;

    public ChartsFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        xAxisData = new ArrayList<>();
        xAxisData.add("monday");
        xAxisData.add("tuesday");
        xAxisData.add("wednesday");
        xAxisData.add("thursday");
        xAxisData.add("friday");
        xAxisData.add("saturday");
        xAxisData.add("sunday");

        yAxisData = new HashMap<>();

        setUpFirebaseVariables();

        View view =inflater.inflate(R.layout.charts_fragment, container, false);
        unbinder = ButterKnife.bind(this,view);

        return view;
    }

    public void setUpFirebaseVariables(){


        weeklyListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!isMonthViewChosen){
                    yAxisData.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        yAxisData.put(snapshot.getKey().toLowerCase(), snapshot.getValue(Double.class));
                        Log.e("LOGGER",snapshot.getKey());
                    }

                    setUpBarChart();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        monthlyListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (isMonthViewChosen){

                    yAxisData.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        yAxisData.put(snapshot.getKey().toLowerCase(),snapshot.getValue(Double.class));
                        Log.e("LOGGER",snapshot.getKey());
                    }

                    setUpBarChart();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    private void attachFirebaseListeners(){
        thisMonthExpenseRef.addValueEventListener(monthlyListener);
        weeklyTransactionRef.addValueEventListener(weeklyListener);
    }

    private void detachFirebaseListeners(){
        thisMonthExpenseRef.removeEventListener(monthlyListener);
        weeklyTransactionRef.removeEventListener(weeklyListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        attachFirebaseListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        detachFirebaseListeners();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        unbinder.unbind();

    }

    private void setUpBarChart(){
        List<BarEntry> barEntries = new ArrayList<>();

        for (int i =0; i < xAxisData.size(); i++){
            barEntries.add(new BarEntry(i, new BigDecimal(yAxisData.get(xAxisData.get(i))==null?
                    0: yAxisData.get(xAxisData.get(i)))
                    .floatValue()));
            Log.e("LOGGER",""+(yAxisData.get(xAxisData.get(i))==null?
                    0: yAxisData.get(xAxisData.get(i))));
        }

        BarDataSet dataSet = new BarDataSet(barEntries, "Expenditure summary");
        BarData data = new BarData(dataSet);
        barChart.setData(data);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
        barChart.invalidate();
    }



}
