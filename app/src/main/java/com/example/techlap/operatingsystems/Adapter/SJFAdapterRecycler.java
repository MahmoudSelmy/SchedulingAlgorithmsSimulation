package com.example.techlap.operatingsystems.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.techlap.operatingsystems.Process.ProcessSJF;
import com.example.techlap.operatingsystems.Queues.SJF;
import com.example.techlap.operatingsystems.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by tech lap on 07/03/2017.
 */

public class SJFAdapterRecycler extends RecyclerView.Adapter<FCFSAdapterRecycler.Holder> {
    private LayoutInflater mLayoutInflater;
    private SJF sjf;
    public List<ProcessSJF> switched =  new ArrayList<>();
    public List<ProcessSJF> finished =  new ArrayList<>();

    public SJFAdapterRecycler(Context context , Boolean Preemptivity) {
        mLayoutInflater = LayoutInflater.from(context);
        sjf = new SJF(Preemptivity);
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

        ProcessSJF data = sjf.getProcessByPosition(position);
        holder.textDataItem.setText(data.PID);
        holder.textTimer.setText(String.valueOf(data.remainingMilis));
        holder.view.setBackgroundColor(data.color);
    }

    public void addProcess(int totalMillis , Boolean preemptivity , int time) {
        //mListData.add(totalMillis);
        int millis = totalMillis;
        ProcessSJF process = new ProcessSJF();
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        process.color = color;
        process.PID = "PID 0x" + Integer.toHexString(color); // generate random color Int for each process
        process.remainingMilis = millis;
        process.totalMillis = millis;
        process.arrival = time;
        sjf.pushProcess(process,preemptivity);
        notifyDataSetChanged();
        //notifyItemInserted(sjf.getProcess());
    }

    public void removeProcess(int time) {
        if (!sjf.isEmpty()){
            ProcessSJF process = sjf.popProcess();
            process.end = time;
            switched.add(process);
            finished.add(process);
            Log.d("Chart","switch and finish " + process.start);
            notifyDataSetChanged();
            //notifyItemRemoved(0);
        }
    }

    public void updateTop(int rm,int time,boolean interrupted){
        Log.d("Task*","iterrupted_Adapter "+ rm);
        ProcessSJF process = sjf.popToCPU();
        ProcessSJF processCopy = new ProcessSJF(process);
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
        sjf.updateTop(process);
        Log.d("Task*","iterrupted_Adapter_check_queue "+ sjf.popToCPU().remainingMilis);
        //notifyDataSetChanged();
        notifyItemChanged(0);

    }

    @Override
    public int getItemCount() {
        return sjf.getProcess();
    }

    public ProcessSJF popToCPU(int time){
        ProcessSJF process = sjf.popToCPU();
        process.start =  time;
        process.wait = process.start - process.arrival;
        sjf.updateTop(process);
        return process;
    }

    public Boolean isEmpty(){
        return sjf.isEmpty();
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
