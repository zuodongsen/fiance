package com.zds.common;

public class BarData {
    public int value;
    public String barString;
    public String pieStringDown;

    public Integer pieColor;
    public Float pieAngleStart;
    public Float pieAngleSwap;
    public float linePoint[] = new float[8];
    public float textPoint[] = new float[3];

    public BarData(int value_, String barString_) {
        this.value = value_;
        this.barString = barString_;
    }
}
