package com.example.techlap.operatingsystems.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.techlap.operatingsystems.Adapter.FCFSAdapterRecycler;
import com.example.techlap.operatingsystems.Gchart.ProgressItem;
import com.example.techlap.operatingsystems.Process.Process;
import com.example.techlap.operatingsystems.R;

import java.util.ArrayList;

public class FcfsActivity extends AppCompatActivity {
    private EditText mInput;
    private RecyclerView mRecyclerView;
    private Toolbar mToolbar;
    private FCFSAdapterRecycler mAdapter;
    private TextView proId,proCounter;
    private Handler handler;
    private Thread thread;
    private int time = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcfs);
        setupToolbar();
        initViews();
    }
    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
    }

    private void initViews() {
        mInput = (EditText) findViewById(R.id.text_input);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerAnimatedItems);
        mAdapter = new FCFSAdapterRecycler(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        proId = (TextView) findViewById(R.id.cpu_process);
        proCounter = (TextView) findViewById(R.id.cpu_timer);

        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                proCounter.setText(String.valueOf(msg.arg1));
                time++;
                if(msg.arg1 == 0 && msg.arg2 == 0){
                    mAdapter.removeProcess(time);
                    prepareThread();
                }
                if(msg.arg2 == 1){
                    // TODO : update top element of the recycler
                    mAdapter.updateTop(msg.arg1);
                    time--;
                }
                super.handleMessage(msg);
            }
        };

    }

    /**
     * Invoked after user hits the button to add an Item to the RecyclerView, check the contents of the EditText,
     * if it has valid contents, add the item to the Adapter of the RecyclerView
     *
     * @param view The Button that was clicked after user types text in the EditText
     */
    public void addProcess(View view) {
        //check if the EditText has valid contents
        String text = mInput.getText().toString();
        try {
            int num = Integer.parseInt(text)*1000;
            mAdapter.addProcess(num,time);
            Log.i("",num+" is a number");
        } catch (NumberFormatException e) {
            Log.i("", text + " is not a number");
            //TODO :  implement dialoge here

        }
    }

    public void startSimulation(View view) {
        prepareThread();
    }
    private void prepareThread(){
        if(!mAdapter.isEmpty()) {
            final Process process = mAdapter.popToCPU(time);
            proId.setText(process.PID);
            proCounter.setText(String.valueOf(process.remainingMilis));
            proId.setTextColor(process.color);
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (process.remainingMilis > 0) {
                            Thread.sleep(1);
                            Message message = Message.obtain();
                            message.arg1 = --process.remainingMilis;
                            message.arg2=0;
                            handler.sendMessage(message);
                        }
                    }
                    catch (InterruptedException e) {
                        Message message = Message.obtain();
                        message.arg1 = process.remainingMilis;
                        message.arg2=1;
                        handler.sendMessage(message);
                    }
                }
            });
            thread.start();
        }else{
            if (time == 0){
                proId.setText("Add process..");
                proCounter.setText("---");
            }else{
                proId.setText("IDLE");
                proCounter.setText("---");
                showAlertToChart();
            }
        }
    }
    private void showAlertToChart(){
        new AlertDialog.Builder(this)
                .setTitle("End Simulation !!")
                .setMessage("Do you want to switched simulation to get the final stats?" + time)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        ArrayList<ProgressItem> items = new ArrayList<ProgressItem>();
                        for (Process process : mAdapter.finish){
                            ProgressItem item = new ProgressItem();
                            item.color = process.color;
                            item.progressItemPercentage = ((float) process.totalMillis/time)*100;
                            item.wait = process.wait;
                            item.start = process.start;
                            item.end = process.end;
                            item.id = process.PID;
                            item.arrival = process.arrival;
                            items.add(item);
                        }
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("items",items);
                        Intent intent = new Intent(FcfsActivity.this,ChartActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    public void pauseSimulation(View view) {
        thread.interrupt();
    }
}
