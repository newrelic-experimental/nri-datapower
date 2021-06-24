package com.newrelic.labs.infra.datapower.processors;

import java.util.Date;
import java.util.HashMap;

/**
 * A {@link MetricProcessor} for metric values over a time interval.
 */
public class EpochProcessor implements MetricProcessor {

	class PreviousValue {
	    private Number lastValue = null;
	    private Date lastTime = null;	
	}
	
	HashMap<String, PreviousValue> previousValuesMap = new HashMap<String, PreviousValue>();
    
    /**
     * Constructs an {@code EpochProcessor}
     */
    public EpochProcessor() {
        super();
    }

    /**
     * Process a metric value over a time interval.
     * Calling process for a metric value at an interval less than 1 second is not supported. Null is returned for sub-second processing.
     */
	@Override
	public Number process(String metricPath, String value) throws MetricProcessingException {
		try {
			Number lastValue = null;
			Date lastTime = null;
			PreviousValue previousValue = previousValuesMap.get(metricPath);
			if (previousValue == null) {
				previousValue = new PreviousValue(); 
				previousValuesMap.put(metricPath, previousValue);
			} else {
				lastValue = previousValue.lastValue;
				lastTime = previousValue.lastTime;
			}
			
			Float val = Float.parseFloat(value);
	        Date currentTime = new Date();
	        Number ret = null;

	        if (val != null && lastValue != null && lastTime != null && currentTime.after(lastTime)) {
	            long timeDiffInSeconds = (currentTime.getTime() - lastTime.getTime()) / 1000;
	            if (timeDiffInSeconds > 0) {
	                ret = (val.floatValue() - lastValue.floatValue()) / timeDiffInSeconds;
	                if (ret.floatValue() < 0) {
	                    ret = null;
	                }
	            }
	        }

	        previousValue.lastValue = val;
	        previousValue.lastTime = currentTime;
	        return ret;
		} catch (Exception e) {
			throw new MetricProcessingException(e);
		}

	}

}
