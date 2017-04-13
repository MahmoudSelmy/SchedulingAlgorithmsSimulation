package com.example.techlap.operatingsystems.Process;

/**
 * Created by tech lap on 05/03/2017.
 */

public class ProcessPQ extends Process implements Comparable<ProcessPQ> {
    public int priority;

    public ProcessPQ() {
    }

    public ProcessPQ(ProcessPQ process) {
        this.start = process.start;
        this.PID = process.PID;
        this.end = process.end;
        this.wait = process.wait;
        this.totalMillis = process.totalMillis;
        this.remainingMilis = process.remainingMilis;
        this.arrival = process.arrival;
        this.color = process.color;
        this.priority = process.priority;
    }
    @Override
    public int compareTo(ProcessPQ that) {
        if (this.priority  > that.priority ) return -1;
        if (this.priority  < that.priority ) return 1;
        return 0;
    }
}