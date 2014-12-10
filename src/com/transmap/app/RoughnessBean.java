package com.transmap.app;

import java.util.ArrayList;
import java.util.List;

public class RoughnessBean {
	final protected double posX;
	final private List<Double> iri = new ArrayList<Double>();
	final protected double avg, max, min, std;
	final protected String iriRaw;
	
	public RoughnessBean(String posX, String iriValues){
		this.posX = Double.valueOf(posX);
		this.iriRaw = iriValues;
		
		double avgTmp = 0.0, variance = 0.0, maxTmp = Double.MIN_NORMAL, minTmp = Double.MAX_VALUE;
		
		double count = 0.0;
		for(String part : iriValues.split("\\s")){
			count = count + 1.0;
			double val = Double.valueOf(part);
			if(val == 0){
				//unexpected
			}
			
			iri.add(val);
			
			avgTmp = avgTmp + val;
			
			if(maxTmp < val)
				maxTmp = val;
			
			if(minTmp > val)
				minTmp = val;
			
			variance = variance + val * val;
		}
		
		std = Math.sqrt((variance / (count-1)));
		avg = avgTmp / count;
		min = minTmp;
		max = maxTmp;
	}
	
	public Double[] getIRI(){
		return (Double[]) iri.toArray();
	}
}
