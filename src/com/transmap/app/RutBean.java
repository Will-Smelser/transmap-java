package com.transmap.app;

import java.util.ArrayList;
import java.util.List;

public class RutBean {
	
	private List<Double> width =  new ArrayList<Double>();
	private List<Double> depth =  new ArrayList<Double>();
	
	public void add(String depth, String width){
		this.width.add(Double.valueOf(width));
		this.depth.add(Double.valueOf(depth));
	}	
	
	public double getAvgWidth(){
		double sum = 0.0;
		for(double d:width)
			sum += d;
		
		return sum / Double.valueOf(width.size());
	}
		
	public double getMaxDepth(){
		double max = 0.0;
		for(double d: depth)
			max = Math.max(d, max);
		return max;
	}
	
	public double getMinDepth(){
		double min = Double.MAX_VALUE;
		for(double d: depth)
			min = Math.min(d, min);
		return min;
	}
	
	public double getAvgDepth(){
		double sum = 0.0;
		for(double d:depth)
			sum += d;
		
		return sum / Double.valueOf(depth.size());
	}
	
	public double getStdDepth(){
		double avg = getAvgDepth();
		double sum = 0.0;
		for(double d:depth)
			sum += Math.pow((d-avg), 2);
		
		return Math.sqrt(sum / Double.valueOf(depth.size()-1.0));
	}
}