package com.example.shiwu.customviewstudy.prectise;

/**
 * Created by shiwu on 2017/8/19.
 */
public class DrawData {
    private float rate;
    private int color;
    private String name;

    public DrawData(float rate, int color, String name) {
        this.rate = rate;
        this.color = color;
        this.name = name;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
