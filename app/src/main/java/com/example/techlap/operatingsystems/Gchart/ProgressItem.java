package com.example.techlap.operatingsystems.Gchart;

import java.io.Serializable;

/**
 * Created by tech lap on 24/03/2017.
 */

public class ProgressItem implements Serializable {
    public int color;
    public float progressItemPercentage;
    public int wait;
    public int start;
    public int end;
    public int arrival;
    public String id;
}
