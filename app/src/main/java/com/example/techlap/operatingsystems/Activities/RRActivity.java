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

import com.example.techlap.operatingsystems.Adapter.RRAdapterRecycler;
import com.example.techlap.operatingsystems.Gchart.ProgressItem;
import com.example.techlap.operatingsystems.Process.Process;
import com.example.techlap.operatingsystems.R;

import java.util.ArrayList;

public class RRActivity extends AppCompatActivity {
    private EditText mInput,mInputQ;
    private RecyclerView mRecyclerView;
    private Toolbar mToolbar;
    private RRAdapterRecycler mAdapter;
    private TextView proId,proCounter;
    private Handler handler;
    private Thread thread;
    private int pQu = 0;
    private Boolean playState,fromPause;
    private int time = 0;
    private int rrSize = 0,count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rr);
        setupToolbar();
        initViews();
    }

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
    }

    private void initViews() {
        mInput = (EditText) findViewById(R.id.text_input);
        mInputQ = (EditText) findViewById(R.id.text_input_q);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerAnimatedItems);
        mAdapter = new RRAdapterRecycler(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        playState = false;
        fromPause = false;
        proId = (TextView) findViewById(R.id.cpu_process);
        proCounter = (TextView) findViewById(R.id.cpu_timer);

        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                proCounter.setText(String.valueOf(msg.arg1));
                time++;
                if(msg.arg1 == 0 && msg.arg2 == 0){
                    //time--;
                    Log.i("RRobine","Branch1 "+count +" "+ time);
                    if(fromPause){
                        fromPause = false;
                        mAdapter.pushTick(pQu,time);
                        pQu = 0;
                        Log.i("RRobine","Branch2 "+count +" "+ time);
                    }else {
                        mAdapter.pushCycle(time);
                        Log.i("RRobine","Branch3 "+count +" "+ time);
                    }
                    prepareThread();
                }
                if(msg.arg2 == 1){
                    // TODO : update top element of the recycler
                    Log.i("RRobine","Branch4 "+count +" "+ time);
                    if(fromPause){
                        pQu = msg.getData().getInt("pQu");
                        Log.i("RRobine","Branch5 "+count +" "+ time);
                    }
                    mAdapter.updateTop(msg.arg1);
                    playState = false;
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
            rrSize++;
            Log.i("",num+" is a number");
        } catch (NumberFormatException e) {
            Log.i("", text + " is not a number");
            //TODO :  implement dialoge here

        }
    }

    public void setQuantum(View view) {
        String text = mInputQ.getText().toString();
        try {
            int num = Integer.parseInt(text);
            mAdapter.setQuantum(num);
        } catch (NumberFormatException e) {
            //TODO :  implement dialoge here
        }
    }

    private void prepareThread(){
        Log.i("RRobine",++count +" "+ time);
        if(!mAdapter.isEmpty() && playState ) {
            final Process process = mAdapter.popToCPU(time);
            proId.setText(process.PID);
            proCounter.setText(String.valueOf(process.remainingMilis));
            proId.setTextColor(process.color);
            Runnable task;
            if (fromPause){
                task = new MyTask(process.remainingMilis,pQu);
                Log.d("pause*","fromPause"+process.remainingMilis % mAdapter.getQuantum());
            }else{
                if (process.remainingMilis < mAdapter.getQuantum())
                    task = new MyTask(process.remainingMilis,process.remainingMilis);
                else
                    task = new MyTask(process.remainingMilis,mAdapter.getQuantum());
            }
            thread = new Thread(task);
            thread.start();
        }else{
            playState = false;
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

                        for (Process process : mAdapter.switched){
                            Log.d("Chart","dialog "+ process.PID +" "+ process.start +" "+ process.end);
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
                        bundle.putString("Size",""+rrSize);
                        Intent intent = new Intent(RRActivity.this,ChartActivity.class);
                        intent.putExtras(bundle);
                        dialog.dismiss();
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

    public class MyTask implements Runnable {
        int remainingMillis;
        int quantum;
        public MyTask(int remainingMillis,int time) {
            // store parameter for later user
            this.remainingMillis = remainingMillis;
            quantum = time;
        }

        public void run() {
            try {
                while (quantum > 0) {
                    Thread.sleep(1);
                    Message message = Message.obtain();
                    quantum--;
                    message.arg1 = -- remainingMillis;
                    if (quantum == 0)
                        message.arg1 = 0;
                    message.arg2=0;
                    handler.sendMessage(message);
                    //Log.i("RRobine","T1");
                }
            }
            catch (InterruptedException e) {
                Message message = Message.obtain();
                message.arg2=1;
                message.arg1= remainingMillis;
                Bundle bundle = new Bundle();
                bundle.putInt("pQu",quantum);
                message.setData(bundle);
                Log.d("Task*","iterrupted_thread "+remainingMillis);
                handler.sendMessageAtFrontOfQueue(message);
                /*
                handler.postAtFrontOfQueue(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.updateTop(remainingMillis);
                    }
                });*/
            }
        }
    }

    public void pauseSimulation(View view) {
        if (playState && thread != null){
            fromPause = true;
            thread.interrupt();
        }
    }

    public void startSimulation(View view) {
        if (!playState){
            playState = true;
            setQuantum(null);
            prepareThread();
        }
    }
}

