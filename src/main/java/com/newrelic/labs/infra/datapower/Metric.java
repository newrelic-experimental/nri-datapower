package com.newrelic.labs.infra.datapower;

/**
 * @author preddy
 */
public class Metric {
    public final String name;
    public final String unit;
    public final Number value;

    public Metric(String name, String unit, Number value) {
        this.name = name;
        this.unit = unit;
        this.value = value;
    }
}
