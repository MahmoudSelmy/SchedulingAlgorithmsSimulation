package com.example.techlap.operatingsystems.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.techlap.operatingsystems.Process.ProcessPQ;
import com.example.techlap.operatingsystems.Queues.PQueue;
import com.example.techlap.operatingsystems.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by tech lap on 07/03/2017.
 */

public class PQAdapterRecycler extends RecyclerView.Adapter<PQAdapterRecycler.Holder> {

    private LayoutInflater mLayoutInflater;
    private PQueue pQueue;
    public List<ProcessPQ> switched =  new ArrayList<>();
    public List<ProcessPQ> finished =  new ArrayList<>();

    public PQAdapterRecycler(Context context , Boolean Preemptivity) {
        mLayoutInflater = LayoutInflater.from(context);
        pQueue = new PQueue(Preemptivity);
    }

    @Override
    public PQAdapterRecycler.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = mLayoutInflater.inflate(R.layout.custom_row_pq, parent, false);
        PQAdapterRecycler.Holder holder = new PQAdapterRecycler.Holder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(PQAdapterRecycler.Holder holder, final int position) {
        //String data = mListData.get(position);
        Log.d("FCFS", String.valueOf(position));

        ProcessPQ data = pQueue.getProcessByPosition(position);
        holder.textDataItem.setText(data.PID);
        holder.textTimer.setText(String.valueOf(data.remainingMilis));
        holder.textPriority.setText(String.valueOf(data.priority));
        holder.view.setBackgroundColor(data.color);
    }

    public void addProcess(int totalMillis , int Priority , Boolean preemptivity,int time) {
        //mListData.add(totalMillis);
        int millis = totalMillis;
        ProcessPQ process = new ProcessPQ();
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        process.priority = Priority;
        process.color = color;
        process.PID = "PID 0x" + Integer.toHexString(color); // generate random color Int for each process
        process.remainingMilis = millis;
        process.totalMillis = millis;
        process.arrival = time;
        Log.d("RTime",""+process.remainingMilis);
        pQueue.pushProcess(process,preemptivity);
        notifyDataSetChanged();
        //notifyItemInserted(pQueue.getProcess());
    }

    public void removeProcess(int time) {
        if (!pQueue.isEmpty()){
            //notifyItemRemoved(0);
            ProcessPQ process = pQueue.popProcess();
            process.end = time;
            switched.add(process);
            finished.add(process);
            Log.d("Chart","switch and finish " + process.start);
            notifyDataSetChanged();
            //notifyItemRemoved(0);
        }
    }

    public ProcessPQ popToCPU(int time){
        ProcessPQ process = pQueue.popToCPU();
        process.start =  time;
        process.wait = process.start - process.arrival;
        pQueue.updateTop(process);
        return process;
    }

    public Boolean isEmpty(){
        return pQueue.isEmpty();
    }

    public void updateTop(int rm ,int time,boolean interrupted){
        Log.d("Task*","iterrupted_Adapter_check_queue "+ pQueue.popToCPU().remainingMilis);
        ProcessPQ process = pQueue.popToCPU();
        ProcessPQ processCopy = new ProcessPQ(process);
        if (rm > 0)
            process.remainingMilis = rm;
        if(interrupted){
            process.arrival = time;
            processCopy.end = time;
            processCopy.totalMillis = processCopy.end - processCopy.start;
            process.totalMillis -= processCopy.totalMillis;
            switched.add(processCopy);
            Log.d("Chart","switch only "+processCopy.arrival+" " + processCopy.start +" "+ processCopy.end);
        }
        pQueue.updateTop(process);
        notifyItemChanged(0);
    }

    @Override
    public int getItemCount() {
        return pQueue.getProcess();
    }


    public static class Holder extends RecyclerView.ViewHolder {
        TextView textDataItem,textTimer,textPriority;
        View view;

        public Holder(View itemView) {
            super(itemView);
            view = itemView;
            textDataItem = (TextView) itemView.findViewById(R.id.text_item);
            textTimer = (TextView) itemView.findViewById(R.id.text_item_counter);
            textPriority = (TextView) itemView.findViewById(R.id.text_item_priority);
        }
    }

}
