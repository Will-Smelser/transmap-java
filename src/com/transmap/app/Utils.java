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
	protected static final String HEADER = "FILE,SURVEY_PATH,SURVEY,SECTION_ID,LONGITUDE,LATITUDE";
	protected static final String HEADER_EXT = ",IRI_avg_l,IRI_min_l,IRI_max_l,IRI_std_l,IRI_raw_l"+
			",IRI_avg_r,IRI_min_r,IRI_max_r,IRI_std_r,IRI_raw_r";
	
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
	
    protected static void saveData(PrintWriter writer, String file, String surveyPath, 
    		Map<String,String> info, Map<String,String> gps, List<RoughnessBean> rough)
    				throws IOException
    {

    	StringBuilder line = new StringBuilder(file);
    	line.append(",").append(surveyPath);
    	line.append(",").append(info.get(Survey.SURVEYID));
    	line.append(",").append(info.get(Survey.SECTIONID));
    	line.append(",").append(gps.get(Survey.LON));
    	line.append(",").append(gps.get(Survey.LAT));
    	
    	if(rough == null || rough.size() == 0){
    		writer.println(line);
    		return;
    	}
    	
    	RoughnessBean left  = rough.get(0);
    	RoughnessBean right = rough.get(1);
    	
    	line.append(",").append(left.avg);
    	line.append(",").append(left.min);
    	line.append(",").append(left.max);
    	line.append(",").append(left.std);
    	line.append(",").append(left.iriRaw.replace(",", ";"));
    	
    	line.append(",").append(right.avg);
    	line.append(",").append(right.min);
    	line.append(",").append(right.max);
    	line.append(",").append(right.std);
    	line.append(",").append(right.iriRaw.replace(",", ";"));

    	
    	writer.println(line);
    }
    
    /**
     * Get a list of all directories
     * @param dir
     * @return
     * @throws FileSystemLoopException
     */
    protected static List<File> scanDir2(File dir) throws FileSystemLoopException{
    	return scanDir(dir,true,0);
    }
    
    /**
     * Get a list of all files
     * @param dir
     * @return
     * @throws FileSystemLoopException
     */
    protected static List<File> scanDir(File dir) throws FileSystemLoopException{
    	return scanDir(dir,false,0);
    }
    
    private static List<File> scanDir(File dir, boolean dirList, int depth) throws FileSystemLoopException{
    	List<File> files = new ArrayList<File>();
    	if(!dir.isDirectory()) throw new InvalidParameterException();
    	
    	if(depth > 4) throw new FileSystemLoopException(dir.toString());
    	
    	if(dirList) files.add(dir);
    	
    	for(File file : dir.listFiles()){
    		if(file.isFile() && !dirList) 
    			files.add(file);
    		else if(file.isDirectory()){
    			files.addAll(scanDir(file,dirList,depth+1));
    		}
    	}
    	return files;
    }
}
