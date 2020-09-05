package com.sullivan.mrs;

import java.io.Serializable;

public class PlottablePoints implements Serializable {

    private static final long serialVersionUID = 445454;

    double time;
    double sensor_1;

    public PlottablePoints() {

    }
    public PlottablePoints(double time,double value) {
        this.time = time;
        this.sensor_1 = value;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getSensor_1() {
        return sensor_1;
    }

    public void setSensor_1(double value) {
        this.sensor_1 = value;
    }

    @Override
    public String toString() {
        return "PlottablePoints{" +
                "time=" + time +
                ", sensor_1=" + sensor_1 +
                '}';
    }
}
