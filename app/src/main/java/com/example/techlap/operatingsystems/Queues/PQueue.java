package com.example.techlap.operatingsystems.Queues;

import android.util.Log;

import com.example.techlap.operatingsystems.Process.ProcessPQ;

/**
 * Created by tech lap on 05/03/2017.
 */

public class PQueue {
    private boolean preemptivity;
    private int processNo;
    private Node Head = null,Tail = null;
    private Node excuting = null;

    public void updateTop(ProcessPQ process) {
        Head.process = process;
    }

    private class Node {
        // previous is first in order of excution
        ProcessPQ process;
        Node Next;
        Node Previous;
    }

    public PQueue(boolean preemptivity) {
        this.preemptivity = preemptivity;
        processNo = 0;
    }

    public boolean isEmpty(){
        return processNo == 0;
    }

    public void pushProcess(ProcessPQ process , Boolean preemptivity){
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

    private void generalPush(Node start,ProcessPQ process , Boolean preemptivity){
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
            while (process.compareTo(test.process) <= 0){
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

    public ProcessPQ popProcess(){
        int counts = 0;
        excuting = Head;
        ProcessPQ process = Head.process;
        if(Head.Next != null){
            Head = Head.Next;
            Head.Previous = null;
        }else{
            Head = new Node();
        }
        processNo--;
        return process;
    }

    public ProcessPQ getProcessByPosition(int position){
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

    public ProcessPQ popToCPU(){
        if(Head != null)
            return Head.process;
        return null;
    }

    public void updateTop(int rMillis){
        Log.d("Task*","iterrupted_update_queue "+rMillis);
        Head.process.remainingMilis = rMillis;
    }

    public int getProcess(){
        return processNo;
    }
    /*
    public static void main(String[] args) {
        In inMillis = new In(args[0]);      // input file
        In inPriority = new In(args[1]);
        int i = 0;
        PQueue fcfs = new PQueue(false);
        while (!inMillis.isEmpty()) {
            ProcessPQ process = new ProcessPQ();
            process.PID = "P" + i;
            int time = inMillis.readInt();
            process.totalMillis = time;
            process.remainingMilis = time;
            process.priority = inPriority.readInt();
            //process.priority = 1;
            fcfs.pushProcess(process);
            i++;
        }
        System.out.println("# process = " + fcfs.getProcess());
        while (!fcfs.isEmpty()){
            ProcessPQ process = fcfs.popProcess();
            System.out.println("process = " +process.PID + process.totalMillis);
        }
    }*/
}
