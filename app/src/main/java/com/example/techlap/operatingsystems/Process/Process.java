package com.example.techlap.operatingsystems.Process;

/**
 * Created by tech lap on 05/03/2017.
 */

public class Process{
    public int color;
    public String PID;
    public int totalMillis;
    public int remainingMilis;
    public int start;
    public int end;
    public int wait = 0;
    public int arrival;

    public Process() {
    }
    public Process(Process process) {
        this.start = process.start;
        this.PID = process.PID;
        this.end = process.end;
        this.wait = process.wait;
        this.totalMillis = process.totalMillis;
        this.remainingMilis = process.remainingMilis;
        this.arrival = process.arrival;
        this.color = process.color;
    }
}
