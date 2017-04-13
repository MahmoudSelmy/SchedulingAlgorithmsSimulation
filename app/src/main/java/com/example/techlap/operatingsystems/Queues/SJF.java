package com.example.techlap.operatingsystems.Queues;

import android.util.Log;

import com.example.techlap.operatingsystems.Process.ProcessSJF;

/**
 * Created by tech lap on 05/03/2017.
 */

public class SJF {
    private boolean preemptivity;
    private int processNo;
    private Node Head = null;
    private Node excuting = null;

    private class Node {
        // previous is first in order of excution
        ProcessSJF process;
        Node Next;
        Node Previous;
    }

    public SJF(boolean preemptivity) {
        this.preemptivity = preemptivity;
        processNo = 0;
    }

    public boolean isEmpty(){
        return processNo == 0;
    }

    public void pushProcess(ProcessSJF process , Boolean preemptivity){
        if(processNo == 0){
            Node Oldhead = Head;
            Head = new Node();
            Head.Previous = null;
            Head.process  = process;
            Head.Next =Oldhead;
            //System.out.println("# process = " + Head.process.remainingMilis);
        }else{
            if(preemptivity){
                generalPush(Head,process,preemptivity);
            }else {
                generalPush(Head.Next,process,preemptivity);
            }
        }
        processNo++;
    }

    private void generalPush(Node start, ProcessSJF process , Boolean preemptivity){
        Node pushed = new Node();
        pushed.process = process;
        Node test = start;
        Boolean last = false;
        Boolean first = true;
        if (!preemptivity){
            first = false;
        }
        if(start == null){
            test = Head;
            last = true;
        }else{
            while (process.compareTo(test.process) >= 0){
                if(test.Next == null){
                    last =true;
                    break;
                }else {
                    test = test.Next;
                    first = false;
                }
            }
        }
        if (last){
            pushed.Previous = test;
            test.Next = pushed;
            System.out.println("#last");
        }else if (first){
            //down
            Head = pushed;
            pushed.Next = test;
            test.Previous = pushed;
            System.out.println("#first");
        }else{
            //up
            pushed.Previous = test.Previous;
            test.Previous.Next = pushed;
            //down
            pushed.Next = test;
            test.Previous = pushed;
            System.out.println("#middle");
        }
    }

    public ProcessSJF popProcess(){
        excuting = Head;
        ProcessSJF process = Head.process;
        if(Head.Next != null){
            Head = Head.Next;
            Head.Previous = null;
        }else{
            Head = new Node();
        }
        processNo--;
        return process;
    }

    public ProcessSJF getProcessByPosition(int position){
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

    public ProcessSJF popToCPU(){
        if(Head != null)
            return Head.process;
        return null;
    }

    public void updateTop(int rMillis){
        Log.d("Task*","iterrupted_update_queue "+rMillis);
        Head.process.remainingMilis = rMillis;
    }
    public void updateTop(ProcessSJF process) {
        Head.process = process;
    }

    /*
    public static void main(String[] args) {
        In in = new In(args[0]);      // input file
        int i = 0;
        SJF fcfs = new SJF(false);
        while (!in.isEmpty()) {
            ProcessSJF process = new ProcessSJF();
            process.PID = "P" + i;
            int time = in.readInt();
            process.totalMillis = time;
            process.remainingMilis = time;
            fcfs.pushProcess(process);
            i++;
        }
        System.out.println("# process = " + fcfs.getProcess());
        while (!fcfs.isEmpty()){
            ProcessSJF process = fcfs.popProcess();
            System.out.println("process = " +process.PID + process.totalMillis);
        }
    }*/
}