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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.techlap.operatingsystems.Adapter.PQAdapterRecycler;
import com.example.techlap.operatingsystems.Gchart.ProgressItem;
import com.example.techlap.operatingsystems.Process.ProcessPQ;
import com.example.techlap.operatingsystems.R;

import java.util.ArrayList;

public class PQActivity extends AppCompatActivity {

    private EditText mInput,mInputPriority;
    private RecyclerView mRecyclerView;
    private Toolbar mToolbar;
    private PQAdapterRecycler mAdapter;
    private TextView proId,proCounter;
    private Handler handler;
    private Thread thread = null;
    private RadioGroup radioGroup;
    private Boolean preemptivity;
    private Boolean inPlay,inPause, inStart;
    private ArrayList<ProcessPQ> buffer;
    private int time = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pq);
        setupToolbar();
        initViews();
    }
    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
    }

    private void initViews() {
        mInput = (EditText) findViewById(R.id.text_input);
        mInputPriority = (EditText) findViewById(R.id.text_input_priority);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerAnimatedItems);
        mAdapter = new PQAdapterRecycler(this , false);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        proId = (TextView) findViewById(R.id.cpu_process);
        proCounter = (TextView) findViewById(R.id.cpu_timer);
        preemptivity = false;
        inPlay = false;
        inPause =  true;
        inStart = true;
        buffer = new ArrayList<ProcessPQ>();
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.radio_non_pre){
                    preemptivity = false;
                }else{
                    preemptivity = true;
                }
                Toast.makeText(PQActivity.this,""+preemptivity, Toast.LENGTH_SHORT).show();
            }
        });

        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                proCounter.setText(String.valueOf(msg.arg1));
                time++;
                if(msg.arg1 == 0 && msg.arg2 == 0){
                    mAdapter.removeProcess(time);
                    prepareThread();
                }
                if(msg.arg2 == 1) {
                    // TODO : update top element of the recycler
                    time--;
                    Log.d("Task*", "iterrupted_Handler" + msg.arg1);
                    if(buffer.size() >0){
                        Log.d("Task*", "iterrupted_Handler_while");
                        mAdapter.updateTop(msg.arg1,time,preemptivity);
                        ProcessPQ process = buffer.get(buffer.size() - 1);
                        mAdapter.addProcess(process.remainingMilis, process.priority, preemptivity,time);
                        buffer.clear();
                        Log.d("Task*", "iterrupted_Handler_play_again" + buffer.isEmpty());
                        prepareThread();
                    }else {
                        mAdapter.updateTop(msg.arg1,time,false);
                    }
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
        String textP = mInputPriority.getText().toString();
        try {
            int num = Integer.parseInt(text)*1000;
            int num1 = Integer.parseInt(textP);
            if(inStart){
                //  push from first
                mAdapter.addProcess(num,num1,true,time);
            }else if(inPause){
                Log.d("Task*","iterrupted_Add "+ num);
                if (!mAdapter.isEmpty()){
                    mAdapter.updateTop(-1,time,preemptivity);
                }
                mAdapter.addProcess(num,num1,preemptivity,time);
            }else if (inPlay&&buffer.isEmpty()){
                ProcessPQ processPQ = new ProcessPQ();
                processPQ.priority= num1;
                processPQ.remainingMilis = num;
                processPQ.totalMillis = num;
                processPQ.PID = "pseudo";
                buffer.add(processPQ);
                Log.d("Task*","iterrupt_And_buffer "+ num);
                if(thread != null){
                    if (!thread.isInterrupted()){
                        Log.d("Task*","interrupt_signal_Add ");
                        thread.interrupt();
                        Log.d("Task*","interrupt_signal_Add*");
                    }
                }
            }
            Log.i("",num+" is a number");
        } catch (NumberFormatException e) {
            Log.i("", text + " is not a number");
            //TODO :  implement dialoge here
        }

    }
    public void startSimulation(View view) {
        if (!inPlay&&inPause){
            inPlay = true;
            inPause = false;
            inStart = false;
            prepareThread();
        }
    }
    public void pauseSimulation(View view) {
        if (inPlay&&!inPause) {
            thread.interrupt();
            inPlay = false;
            inPause = true;
        }
    }

    private void prepareThread(){
        if(!mAdapter.isEmpty()&&buffer.isEmpty()) {
            final ProcessPQ process = mAdapter.popToCPU(time);
            proId.setText(process.PID);
            proCounter.setText(String.valueOf(process.remainingMilis));
            proId.setTextColor(process.color);
            Runnable task = new MyTask(process.remainingMilis);
            thread = new Thread(task);
            thread.start();
        }else{
            inPause = true;
            inPlay = false;
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

                        for (ProcessPQ process : mAdapter.switched){
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
                        bundle.putString("Size",""+mAdapter.finished.size());
                        Intent intent = new Intent(PQActivity.this,ChartActivity.class);
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
    public class MyTask implements Runnable {
        int remainingMillis;
        int noProcess;
        public MyTask(int time) {
            // store parameter for later user
            remainingMillis = time;
        }

        public void run() {
            try {
                while (remainingMillis > 0) {
                    Thread.sleep(1);
                    Message message = Message.obtain();
                    message.arg1 = -- remainingMillis;
                    message.arg2=0;
                    handler.sendMessage(message);
                }
            }
            catch (InterruptedException e) {
                Message message = Message.obtain();
                message.arg1 = remainingMillis;
                message.arg2=1;
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
}
