package com.example.techlap.operatingsystems.Queues;

import android.util.Log;

import com.example.techlap.operatingsystems.Process.Process;

/**
 * Created by tech lap on 05/03/2017.
 */

public class RoundRobin {
    private int processNo;
    private Node Head = null,Tail = null;
    private int Quantum;

    public int getQuantum() {
        return Quantum;
    }

    public Process pushTick(int pQu) {
        Process process = popProcess();
        process.remainingMilis -= pQu;
        if (process.remainingMilis > 0){
            pushProcess(process);
        }
        return process;
    }

    public void updateTop(Process process) {
        Head.process = process;
    }

    private class Node {
        Process process;
        Node Next;
    }

    public RoundRobin(int Quantum) {
        processNo = 0;
        this.Quantum = Quantum;
        Head = new Node();
        Tail = Head;
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
    public Process pushCycle(){
        Process process = popProcess();
        process.remainingMilis -= Quantum;
        if (process.remainingMilis > 0){
            pushProcess(process);
        }
        return process;
    }
    public int getProcess(){
        return processNo;
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

    public Process popToCPU(){
        if(Head != null)
            return Head.process;
        return null;
    }
    public void updateTop(int rMillis){
        Head.process.remainingMilis = rMillis;
    }
    public void setQuantum(int q){
        Quantum = q;
    }
}
