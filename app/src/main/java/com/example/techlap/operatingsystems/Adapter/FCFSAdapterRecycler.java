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
import com.example.techlap.operatingsystems.Queues.FCFS;
import com.example.techlap.operatingsystems.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by tech lap on 06/03/2017.
 */
public class FCFSAdapterRecycler extends RecyclerView.Adapter<FCFSAdapterRecycler.Holder> {
    private LayoutInflater mLayoutInflater;
    private FCFS fcfs;
    public List<Process> finish =  new ArrayList<>();
    public FCFSAdapterRecycler(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        fcfs = new FCFS();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = mLayoutInflater.inflate(R.layout.custom_row_item, parent, false);
        Holder holder = new Holder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        //String data = mListData.get(position);
        Log.d("FCFS", String.valueOf(position));

        Process data = fcfs.getProcessByPosition(position);
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
        fcfs.pushProcess(process);
        notifyItemInserted(fcfs.getProcess());
    }

    public void removeProcess(int time) {
        if (!fcfs.isEmpty()){
            Process process = fcfs.popProcess();
            process.end = time;
            finish.add(process);
            //notifyItemRemoved(0);
            notifyDataSetChanged();
        }
    }

    public Process popToCPU(int time){
        Process process = fcfs.popToCPU();
        process.start =  time;
        process.wait = process.start - process.arrival;
        fcfs.updateTop(process);
        return process;
    }

    public Boolean isEmpty(){
        return fcfs.isEmpty();
    }

    public void updateTop(int rm){
        fcfs.updateTop(rm);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return fcfs.getProcess();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView textDataItem,textTimer;
        View view;

        public Holder(View itemView) {
            super(itemView);
            view = itemView;
            textDataItem = (TextView) itemView.findViewById(R.id.text_item);
            textTimer = (TextView) itemView.findViewById(R.id.text_item_counter);
        }
    }

}
