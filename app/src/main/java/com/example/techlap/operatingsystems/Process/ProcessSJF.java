package com.example.techlap.operatingsystems.Process;

/**
 * Created by tech lap on 05/03/2017.
 */

public class ProcessSJF extends Process implements Comparable<ProcessSJF> {

    public ProcessSJF() {
    }

    public ProcessSJF(ProcessSJF processSJF) {
        this.start = processSJF.start;
        this.PID = processSJF.PID;
        this.end = processSJF.end;
        this.wait = processSJF.wait;
        this.totalMillis = processSJF.totalMillis;
        this.remainingMilis = processSJF.remainingMilis;
        this.arrival = processSJF.arrival;
        this.color = processSJF.color;
    }

    @Override
    public int compareTo(ProcessSJF that) {
        if (this.remainingMilis  < that.remainingMilis ) return -1;
        if (this.remainingMilis  > that.remainingMilis ) return 1;
        return 0;
    }
}
