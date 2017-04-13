package com.example.techlap.operatingsystems.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.techlap.operatingsystems.Gchart.CustomSeekBar;
import com.example.techlap.operatingsystems.Gchart.ProgressItem;
import com.example.techlap.operatingsystems.R;

import java.util.ArrayList;

public class ChartActivity extends Activity{

    private CustomSeekBar seekbar;
    private ArrayList<ProgressItem> progressItemList;
    private TextView tPid,tStart,tEnd,tWait,tArrival,tAverage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        seekbar = ((CustomSeekBar) findViewById(R.id.seekBar0));

        tPid = (TextView) findViewById(R.id.process_id);
        tStart = (TextView) findViewById(R.id.process_start);
        tEnd = (TextView) findViewById(R.id.process_end);
        tWait = (TextView) findViewById(R.id.process_wait);
        tArrival = (TextView) findViewById(R.id.process_arrival);
        tAverage = (TextView) findViewById(R.id.average_wt);
        progressItemList = (ArrayList<ProgressItem>) getIntent().getSerializableExtra("items");
        initDataToSeekbar();
        initAverageCard();
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub
                Log.d("Progress",""+progress);
                int order = -1;
                float offset = 0;
                for(ProgressItem item : progressItemList){
                    if (progress >= offset){
                        offset += item.progressItemPercentage;
                        order++;
                    }
                }
                Log.d("Progress","order = "+order+" offset = "+offset*100);
                tPid.setText(progressItemList.get(order).id);
                tArrival.setText(Integer.toString(progressItemList.get(order).arrival));
                tStart.setText(Integer.toString(progressItemList.get(order).start));
                tEnd.setText(Integer.toString(progressItemList.get(order).end));
                tWait.setText(Integer.toString(progressItemList.get(order).wait));
            }
        });
    }

    private void initAverageCard() {
        float average = 0;
        int size = 1;
        for(ProgressItem item : progressItemList){
            average = average + item.wait;
        }
        String avg = getIntent().getExtras().getString("Size");
        if (avg != null){
            size = Integer.valueOf(avg);
            average = average /(size*1000);
            tAverage.setText(average + "sec");
            initCard();
            return;
        }

        average = average /(progressItemList.size()*1000);
        tAverage.setText(average + "sec");
        initCard();
    }
    private void initCard(){
        tPid.setText(progressItemList.get(0).id);
        tArrival.setText(Integer.toString(progressItemList.get(0).arrival));
        tStart.setText(Integer.toString(progressItemList.get(0).start));
        tEnd.setText(Integer.toString(progressItemList.get(0).end));
        tWait.setText(Integer.toString(progressItemList.get(0).wait));
    }
    private void initDataToSeekbar() {
        Log.d("Size",""+progressItemList.get(1).progressItemPercentage);
        seekbar.initData(progressItemList);
        seekbar.invalidate();
    }

}