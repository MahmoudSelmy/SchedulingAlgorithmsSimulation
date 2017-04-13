package com.example.techlap.operatingsystems.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.techlap.operatingsystems.Process.Process;
import com.example.techlap.operatingsystems.Queues.RoundRobin;
import com.example.techlap.operatingsystems.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by tech lap on 07/03/2017.
 */

public class RRAdapterRecycler extends RecyclerView.Adapter<FCFSAdapterRecycler.Holder> {
    private LayoutInflater mLayoutInflater;
    private RoundRobin roundRobin;
    private int quantum = 10;
    public List<Process> switched =  new ArrayList<>();
    public List<Process> finished =  new ArrayList<>();

    public RRAdapterRecycler(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        roundRobin = new RoundRobin(quantum);
    }
    @Override
    public FCFSAdapterRecycler.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = mLayoutInflater.inflate(R.layout.custom_row_item, parent, false);
        FCFSAdapterRecycler.Holder holder = new FCFSAdapterRecycler.Holder(row);
        return holder;
    }
    @Override
    public void onBindViewHolder(FCFSAdapterRecycler.Holder holder, final int position) {
        //String data = mListData.get(position);
        Log.d("FCFS", String.valueOf(position));

        Process data = roundRobin.getProcessByPosition(position);
        holder.textDataItem.setText(data.PID);
        holder.textTimer.setText(String.valueOf(data.remainingMilis));
        holder.view.setBackgroundColor(data.color);
    }

    public void addProcess(int totalMillis,int time) {
        //mListData.add(totalMillis);
        int millis = totalMillis;
        Process process = new Process();

        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        process.color = color;
        process.PID = "PID 0x" + Integer.toHexString(color); // generate random color Int for each process
        process.remainingMilis = millis;
        process.totalMillis = millis;
        process.arrival = time;
        roundRobin.pushProcess(process);
        notifyItemInserted(roundRobin.getProcess());
    }

    public void pushCycle(int time) {
        if (!roundRobin.isEmpty()){
            Process process = roundRobin.popToCPU();
            Process processCopy = new Process(process);
            processCopy.end = time;
            process.arrival = time;
            processCopy.totalMillis = processCopy.end - processCopy.start;
            switched.add(processCopy);
            roundRobin.updateTop(process);
            roundRobin.pushCycle();
            notifyDataSetChanged();
        }else{
            //TODO : snakbar "queue is empty"
        }
    }

    public void setQuantum(int q){
        roundRobin.setQuantum(q);
    }

    public Process popToCPU(int time){
        Process process = roundRobin.popToCPU();
        process.start =  time;
        process.wait = process.start - process.arrival;
        roundRobin.updateTop(process);
        return process;
    }

    public Boolean isEmpty(){
        return roundRobin.isEmpty();
    }

    public int getQuantum(){
        return roundRobin.getQuantum();
    }
    public void updateTop(int rm){
        roundRobin.updateTop(rm);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return roundRobin.getProcess();
    }

    public void pushTick(int pQu, int time) {
        Process process = roundRobin.popToCPU();
        Process processCopy = new Process(process);
        processCopy.end = time;
        process.arrival = time;
        processCopy.totalMillis = processCopy.end - processCopy.start;
        switched.add(processCopy);
        roundRobin.updateTop(process);
        roundRobin.pushTick(pQu);
        notifyDataSetChanged();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView textDataItem;
        View view;

        public Holder(View itemView) {
            super(itemView);
            view = itemView;
            textDataItem = (TextView) itemView.findViewById(R.id.text_item);
        }
    }

}
