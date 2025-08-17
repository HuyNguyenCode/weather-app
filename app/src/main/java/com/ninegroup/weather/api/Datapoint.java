package com.ninegroup.weather.api;

import com.google.gson.annotations.SerializedName;

public class Datapoint {
    @SerializedName("x")
    public Long x;
    @SerializedName("y")
    public Float y;

    public Long getX() {
        return x;
    }

    public void setX(Long x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }
}

