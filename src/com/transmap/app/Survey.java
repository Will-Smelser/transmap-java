package com.transmap.app;

import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class Survey {
    //fields we are going to match on
    static final String LAT = "Longitude";
    static final String LON = "Latitude";
    static final String SECTIONID = "SectionID";
    static final String SURVEYID = "SurveyID";
    static final String SURVEYPATH = "SurveyPath";
    static final String ROADSECINFO = "RoadSectionInfo";
    static final String GPSCOORD = "GPSCoordinate";
    
    static Map<String,String> processGPSCoordinate(XMLEventReader reader) throws XMLStreamException{

    	Map<String,String>result = new HashMap<String,String>();
    	while(reader.hasNext()){
    		XMLEvent event = reader.nextEvent();
    		if(event.isEndElement() && "GPSCoordinate".equals(event.asEndElement().getName().toString())){
    			return result;
    		}
    		if(event.isStartElement()){
    			String value = reader.getElementText();;
    			String name = event.asStartElement().getName().toString();
    			result.put(name, value);
    		}
    	}
    	return result;
    }
    
    static Map<String,String> processRoadSectionInfo(XMLEventReader reader) throws XMLStreamException{

    	Map<String,String>result = new HashMap<String,String>();
    	while(reader.hasNext()){
    		XMLEvent event = reader.nextEvent();
    		if(event.isEndElement() && "RoadSectionInfo".equals(event.asEndElement().getName().toString())){
    			break;
    		}
    		if(event.isStartElement()){
    			String value = reader.getElementText();
    			String name = event.asStartElement().getName().toString();
    			result.put(name, value);
    		}
    	}
    	return result;
    }
}
