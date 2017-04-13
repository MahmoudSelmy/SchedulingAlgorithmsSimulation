package com.example.techlap.operatingsystems.Queues;

import android.util.Log;

import com.example.techlap.operatingsystems.Process.Process;

/**
 * Created by tech lap on 05/03/2017.
 */

public class FCFS {
    private int processNo;
    private Node Head = null,Tail = null;
    private Node excuting;


    private class Node {
        Process process;
        Node Next;
    }

    public FCFS() {
        Head = new Node();
        Tail = Head;
        processNo = 0;
    }

    public boolean isEmpty(){
        return processNo == 0;
    }

    public void pushProcess(Process process){
        if(isEmpty()){
            Head.process = process;
            Tail = new Node();
            Head.Next = Tail;
        }else {
            Tail.process = process;
            Node temp = new Node();
            Tail.Next =temp;
            Tail = temp;
        }
        processNo++;
    }
    public Process popToCPU(){
        if(Head != null)
            return Head.process;
        return null;
    }
    public Process popProcess(){
        Process process = Head.process;
        Head = Head.Next;
        processNo--;
        if (processNo == 0){
            Head = new Node();
            Tail = Head;
        }
        return process;
    }
    public Process getProcessByPosition(int position){
        if(!isEmpty()){
            Node test = Head;
            Log.d("FCFS*", String.valueOf(position));
            while (position > 0){
                Log.d("FCFS**", test.process.PID);
                test = test.Next;
                position--;
            }
            Log.d("FCFS*", test.process.PID);
            return test.process;
        }
        return null;
    }
    public int getProcess(){
        return processNo;
    }
    public void updateTop(int rMillis){

        Head.process.remainingMilis = rMillis;
    }
    public void updateTop(Process process) {
        Head.process = process;
    }
    /*
    public static void main(String[] args) {
        In in = new In(args[0]);      // input file
        int i = 0;
        FCFS fcfs = new FCFS();
        while (!in.isEmpty()&&!perc.percolates()) {
            Process process = new Process();
            process.PID = "P" + i;
            int time = in.readInt();
            process.totalMillis = time;
            process.remainingMilis = time;
            fcfs.pushProcess(process);
        }
        System.out.println("# process = " + fcfs.getProcess());
        while (!fcfs.isEmpty()){
            Process process = fcfs.popProcess();
            System.out.println("process = " +process.PID + process.totalMillis);
        }
    }*/

}
