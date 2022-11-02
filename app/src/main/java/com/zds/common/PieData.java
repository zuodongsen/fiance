package com.zds.common;

import java.util.ArrayList;
import java.util.List;

public class PieData {
    public Float pieValue;
    public String pieString;
    public String pieStringDown;

    public List<String> pieStringPoly;

    public Integer pieColor;
    public Float pieAngleStart;
    public Float pieAngleSwap;
    public float linePoint[] = new float[8];
    public float textPoint[] = new float[3];

    public PieData(float pieValue_, String pieString_) {
        this.pieValue = pieValue_;
        this.pieString = pieString_;
        this.pieStringPoly = new ArrayList<>();
    }

    public String toSting() {
        return this.pieString;
    }
}
