package com.transmap.app;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystemLoopException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.events.XMLEvent;

public class Utils {
	static boolean isEqual(String left, XMLEvent evt){
    	QName temp = null;
    	if(evt.isStartElement()){
    		temp = evt.asStartElement().getName();
    	}else if(evt.isEndElement()){
    		temp = evt.asEndElement().getName();
    	}
    	
    	if(temp == null || left == null) return false;
    	
    	return left.equals(temp.toString());
    }
	
    protected static void saveData(PrintWriter writer, String file, String surveyPath, Map<String,String> info, Map<String,String> gps) throws IOException{

    	String line = file+","+surveyPath+","+info.get(Survey.SURVEYID)+","+info.get(Survey.SECTIONID)+
    			","+gps.get(Survey.LON)+","+gps.get(Survey.LAT);
    	
    	writer.println(line);
    }
    
    protected static List<File> scanDir(File dir) throws FileSystemLoopException{
    	return scanDir(dir,0);
    }
    private static List<File> scanDir(File dir, int depth) throws FileSystemLoopException{
    	List<File> files = new ArrayList<File>();
    	if(!dir.isDirectory()) throw new InvalidParameterException();
    	
    	if(depth > 4) throw new FileSystemLoopException(dir.toString());
    	
    	for(File file : dir.listFiles()){
    		if(file.isFile()) 
    			files.add(file);
    		else if(file.isDirectory())
    			files.addAll(scanDir(file,depth+1));
    	}
    	return files;
    }
}
