package com.example;

public class NumberStatistics {
    private long count = 0;
    private double min = Double.POSITIVE_INFINITY;
    private double max = Double.NEGATIVE_INFINITY;
    private double sum = 0.0;

    public void addValue(double value) {
        count++;
        if (value < min) {
            min = value;
        }
        if (value > max) {
            max = value;
        }
        sum += value;
    }

    public long getCount() {
        return count;
    }

    public double getMin() {
        return count > 0 ? min : 0;
    }

    public double getMax() {
        return count > 0 ? max : 0;
    }

    public double getSum() {
        return sum;
    }

    public double getAverage() {
        return  count > 0? sum / count : 0;
    }
}
