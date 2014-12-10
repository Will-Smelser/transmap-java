package com.transmap.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class Survey {
    //fields we are going to match on
    static final String LAT = "Latitude";
    static final String LON = "Longitude";
    static final String SECTIONID = "SectionID";
    static final String SURVEYID = "SurveyID";
    static final String SURVEYPATH = "SurveyPath";
    static final String ROADSECINFO = "RoadSectionInfo";
    static final String GPSCOORD = "GPSCoordinate";
    static final String ROUGHNESSINFO = "RoughnessInformation";
    static final String RUTINFO = "RutInformation";
    
    static Map<String,String> processGPSCoordinate(XMLEventReader reader) throws XMLStreamException{

    	Map<String,String>result = new HashMap<String,String>();
    	while(reader.hasNext()){
    		XMLEvent event = reader.nextEvent();
    		if(event.isEndElement() && GPSCOORD.equals(event.asEndElement().getName().toString())){
    			return result;
    		}
    		if(event.isStartElement()){
    			String value = reader.getElementText();
    			String name = event.asStartElement().getName().toString();
    			result.put(name, value);
    		}
    	}
    	return result;
    }
    
    static RutBean[] processRutMeasurement(XMLEventReader reader) throws XMLStreamException{
    	final String mNodeName = "RutMeasurement";
    	RutBean rutLeft = new RutBean();
    	RutBean rutRight = new RutBean();
    	
    	RutBean[] result = {rutLeft,rutRight};
    	
    	//rut elements
    	final String laneSideName = "LaneSide";
    	final String depthName = "Depth";
    	final String widthName = "Width";
    	
    	while(reader.hasNext()){
    		XMLEvent event = reader.nextEvent();
    		if(event.isEndElement() && RUTINFO.equals(event.asEndElement().getName().toString())){
    			return result;
    		}
    		if(event.isStartElement() && mNodeName.equals(event.asStartElement().getName().toString())){
    			
    			Map<String,String> values = processOneLevel(reader, mNodeName);
    			
    			if(!values.containsKey(laneSideName) || !values.containsKey(depthName) || !values.containsKey(widthName)){
    				throw new XMLStreamException("Expected to have LaneSide, Depth, and Width");
    			}
				
    			if(values.get(laneSideName).equalsIgnoreCase("left")){
					//System.out.println("LEFT, "+values.get(depthName)+", "+ values.get(widthName));
					rutLeft.add(values.get(depthName), values.get(widthName));
				}else if(values.get(laneSideName).equalsIgnoreCase("right")){
					//System.out.println("RIGHT, "+values.get(depthName)+", "+ values.get(widthName));
					rutRight.add(values.get(depthName), values.get(widthName));
				}else
					throw new XMLStreamException("Expected LaneSide to have value Left or Right, but got: "+values.get(laneSideName));
    		}
    	}
    	
    	return result;
    }
    
    static Map<String,String> processOneLevel(XMLEventReader reader, String parentNode) throws XMLStreamException{
    	Map<String,String>result = new HashMap<String,String>();
    	
    	while(reader.hasNext()){
    		XMLEvent evt = reader.nextEvent();
    		if(evt.isStartElement()){
    			result.put(evt.asStartElement().getName().toString(), reader.getElementText());
    		
    		}else if(evt.isEndElement() && parentNode.equals(evt.asEndElement().getName().toString())){
    			break;
    		}
    	}
    	return result;
    }
    
    static Map<String,String> processRoadSectionInfo(XMLEventReader reader) throws XMLStreamException{

    	Map<String,String>result = new HashMap<String,String>();
    	while(reader.hasNext()){
    		XMLEvent event = reader.nextEvent();
    		if(event.isEndElement() && ROADSECINFO.equals(event.asEndElement().getName().toString())){
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
    
    static List<RoughnessBean> processRoughnessInfo(XMLEventReader reader) throws XMLStreamException{
    	final String ROUGHNESS = "Roughness";
    	final String POSX = "PositionX";
    	final String IRI = "IRI";
    	List<RoughnessBean> result = new ArrayList<RoughnessBean>();
    	
    	while(reader.hasNext()){
    		XMLEvent event = reader.nextEvent();
    		//break out if we finish this element
    		if(event.isEndElement() && ROUGHNESSINFO.equals(event.asEndElement().getName().toString())){
    			break;
    		}
    		
    		//we are now in Roughness, where are data really is
    		if(event.isStartElement() && ROUGHNESS.equals(event.asStartElement().getName().toString())){
    			String posx = null, iri = null;
    			while(reader.hasNext()){
    				event = reader.nextEvent();
    				
    				if(event.isEndElement() && ROUGHNESS.equals(event.asEndElement().getName().toString())){
    	    			break;
    	    		}
    				
    				if(event.isStartElement() && POSX.equals(event.asStartElement().getName().toString())){
    					posx = reader.getElementText();
    				}
    				
    				if(event.isStartElement() && IRI.equals(event.asStartElement().getName().toString())){
    					iri = reader.getElementText();
    				}
    			}
    			
    			if(posx == null || iri == null){
    				throw new XMLStreamException(POSX+" or "+IRI+" missing corresponding value");
    			}
    			result.add(new RoughnessBean(posx, iri));
    		}
    		
    	}
    	
    	return result;
    }
}
