package com.example;

public class StringStatistics {
    private long count = 0;
    private int minLength = Integer.MAX_VALUE;
    private int maxLength = 0;

    public void addValue(String value) {
        count++;
        int length = value.length();
        if (length < minLength) {
            minLength = length;
        }
        if (length > maxLength) {
            maxLength = length;
        }
    }

    public long getCount() {
        return count;
    }

    public int getMinLength() {
        return count > 0 ? minLength : 0;
    }

    public int getMaxLength() {
        return count > 0 ? maxLength : 0;
    }
}
