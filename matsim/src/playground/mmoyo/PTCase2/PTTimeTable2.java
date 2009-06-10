package playground.mmoyo.PTCase2;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.matsim.api.basic.v01.Id;
import org.matsim.core.api.network.Link;
import org.matsim.core.basic.v01.IdImpl;
import org.matsim.core.api.network.Network;
import org.matsim.core.utils.misc.Time;
import playground.mmoyo.input.PTLinesReader2;
import playground.mmoyo.PTRouter.*;

/**
 * Contains the information of departures for every station and PTLine
 * @param TimeTableFile path of file with departure information
 */
public class PTTimeTable2{
	private PTLinesReader2 ptLinesReader = new PTLinesReader2();
	private List<PTLine> ptLineList; 
	private Map<Id,Double> linkTravelTimeMap = new TreeMap<Id,Double>();
	private Map<Id,double[]> nodeDeparturesMap = new TreeMap<Id,double[]>();
	private Map <Id, Link> nextLinkMap = new TreeMap <Id, Link>();
	private static Time time;
	
	public PTTimeTable2(String TimeTableFile){
		ptLinesReader.readFile(TimeTableFile);
		this.ptLineList = ptLinesReader.ptLineList;
	}

	public PTTimeTable2(){
	}
	
	/**
	 * gets the map with travel time information for standard links
	 */
	public void setMaps(Map<Id,Double> linkTravelTimeMap){
		this.linkTravelTimeMap = linkTravelTimeMap;
	}
	
	public void setptLineList(List<PTLine> ptLineList){
		this.ptLineList= ptLineList;
	}
	
	/**
	 * Reads every ptline information and creates  
	 * Minutemap to save average travel time from the fist station to any station of the ptLine 
	 * NodeDepartureTime to save all departures times in a day for each node   
	 */
	public void calculateTravelTimes(Network networklayer){
		// -> make its own class in input and the pttimetable gets this external data
		Map<Id,Double> minuteMap = new TreeMap<Id,Double>();
		
		for (PTLine ptLine : ptLineList){
			int x= 0;
			for (Id idNode:  ptLine.getNodeRoute()){
			
				/**Create a map with nodes-minutes after departure*/
				double minAfterDep = new Double(ptLine.getMinutes().get(x));
				minuteMap.put(idNode, minAfterDep);

				/**Fills the map with arrivals for every node*/
				double[] departuresArray =new double[ptLine.getDepartures().size()];					
				int y=0;
				for (String departure : ptLine.getDepartures()){
					//It could happen that a departure occurs after 24:00 (86,400 seconds after midnight)
					//In this case the departure will be set at the beginning as a early departure of the day
					double dep = time.parseTime(departure) + (minAfterDep*60);
					if (dep > 86400) dep=dep-86400;
					departuresArray[y++]=  dep;
				}
				Arrays.sort(departuresArray);
				nodeDeparturesMap.put(idNode, departuresArray);
				x++;
			
				/*
				PTNode ptNode = (PTNode)networklayer.getNode(idNode);
				Link[] arrlink= ptNode.getOutLinks().values().toArray(new Link[1]);
				Link link = arrlink[0];
				//System.out.println(link.getFromNode().getId() + "--" + link.getId() + "-->" + link.getToNode().getId());
				*/
				//if (link.getType().equals("Standard")){
				//Id idFromNode = link.getFromNode().getId();
				//Id idToNode = link.getToNode().getId();
				//double travelTime=minuteMap.get(idToNode)- minuteMap.get(idFromNode); 
				//linkTravelTimeMap.put(link.getId(), travelTime);
				//}
			}
		}
		
		/*
		//Creates the map with link-travel time
		for (Link link : networklayer.getLinks().values()) {
			if (link.getType().equals("Standard")){
				Id idFromNode = link.getFromNode().getId();
				Id idToNode = link.getToNode().getId();
				double travelTime=minuteMap.get(idToNode)- minuteMap.get(idFromNode); 
				linkTravelTimeMap.put(link.getId(), travelTime);
			}
		}
		networklayer =null;
		*/
	}
	
	public double getTravelTime(Link link){
		return linkTravelTimeMap.get(link.getId()); 
	}

	/**
	 * Returns the waiting time in a transfer link head node after a given time //minutes
	 */
	public double getTransferTime(Link link, double time){
	 	double nextDeparture = nextDepartureB(link.getToNode().getId(),time); 
		double transferTime= 0;
		if (nextDeparture>=time){
			transferTime= nextDeparture-time;
		}else{
			//wait till next day first departure
			transferTime= 86400-time+ nextDeparture;
		}
		return transferTime;
	}
	
	/**
	*A binary search returns the next departure in a node after a given time 
	*If the time is greater than the last departure, 
	*returns the first departure(of the next day)
	*/
	public double nextDepartureB(Id idPTNode,  double time){//,
		double[]arrDep= nodeDeparturesMap.get(idPTNode);
		int length = arrDep.length;
		int index =  Arrays.binarySearch(arrDep, time);
		if (index<0){
			index = -index;
			if (index <= length)index--; else index=0;	
		}else{
			if (index < (length-1))index++; else index=0;	
		}
		return arrDep[index];
	}

	public List<PTLine> getPtLineList() {
		return ptLineList;
	}
	
	public Map<Id, Link> getNextLinkMap() {
		return nextLinkMap;
	}

	
	public void putNextDTLink(Id id, Link link) {
		//->This should not be necessary
		nextLinkMap.put(id, link);
	}
	
		
	public void printDepartures(){
		for (Map.Entry <Id,double[]> departuresMap : nodeDeparturesMap.entrySet()){
			System.out.println("\n node:" + departuresMap.getKey());
			double[] departures = departuresMap.getValue();
			for (int x=0; x< departures.length; x++){
				System.out.print(departures[x] + " ");
			}
		}
	}
	
		
}


/* ******************************
 * Time Methods


//Converts a string in format hh:mm into integer representing seconds after the midnight
private double strTimeToDbl(String strDeparture){
	return time.parseTime(strDeparture);
}

//Converts integers representing a time into format "hh:mm"
private String TimeToString(double dblDeparture){
	return time.writeTime(dblDeparture, "HH:mm");
}
*/