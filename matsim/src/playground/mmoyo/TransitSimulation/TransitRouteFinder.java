package playground.mmoyo.TransitSimulation;

import java.util.ArrayList;
import java.util.List;

import org.matsim.api.basic.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.network.LinkImpl;
import org.matsim.core.population.ActivityImpl;
import org.matsim.core.population.LegImpl;
import org.matsim.core.population.PersonImpl;
import org.matsim.core.population.routes.LinkNetworkRouteImpl;
import org.matsim.core.population.routes.NetworkRouteWRefs;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;
import org.matsim.transitSchedule.api.TransitSchedule;

import playground.mmoyo.PTRouter.PTRouter;
import playground.mmoyo.PTRouter.PTValues;

/**
 * Receives two acts and returns a list of PT legs connecting their coordinates 
 */
public class TransitRouteFinder {
	private PTRouter ptRouter;
	private LogicIntoPlainTranslator logicToPlainTranslator;
	private PTValues ptValues ;
	
	public TransitRouteFinder(final TransitSchedule transitSchedule, PTValues ptValues){
		this.ptValues = ptValues;
		LogicFactory logicFactory = new LogicFactory(transitSchedule, ptValues);
		this.ptRouter = new PTRouter(logicFactory.getLogicNet(), logicFactory.getLogicPTTimeTable(), ptValues);
		this.logicToPlainTranslator = logicFactory.getLogicToPlainTranslator();
	}
	
	/**returns a list of legs that represent a PT connection between two activities locations*/ 
	public List<LegImpl> calculateRoute (final ActivityImpl fromAct, final ActivityImpl toAct, final PersonImpl person ){
		List<LegImpl> legList = new ArrayList<LegImpl>();
		
		double distToWalk = ptValues.distToWalk(person.getAge());
		Path path = ptRouter.findPTPath(fromAct.getCoord(), toAct.getCoord(), fromAct.getEndTime(), distToWalk);

		if (path!= null){
			List <Link> linkList = new ArrayList<Link>();
			double depTime=fromAct.getEndTime();
			double travTime= depTime;
			
			int i=1;
			String linkType;
			String lastLinkType= null;
			for (Link link: path.links){
				linkType = ((LinkImpl)link).getType();
				
				if (i>1){
					if (!linkType.equals(lastLinkType)){
						if (!lastLinkType.equals("Transfer")){    //transfers will not be described in legs
							LegImpl newLeg = createLeg(selectMode(lastLinkType), linkList, depTime, travTime);  
							legList.add(newLeg);
						}
						depTime=travTime;
						linkList = new ArrayList<Link>();
					}
					if (i == path.links.size()){
						travTime = travTime + ptRouter.ptTravelTime.getLinkTravelTime(link, travTime);
						LegImpl newLeg = createLeg(selectMode(linkType), linkList, depTime, travTime);
						legList.add(newLeg);
					}
				}
				travTime = travTime + ptRouter.ptTravelTime.getLinkTravelTime(link, travTime);
				linkList.add(link);
				lastLinkType = linkType;
				i++;
			}
			legList = logicToPlainTranslator.convertToPlainLeg(legList); //translates the listLeg into the plainNetwork
		}
		return legList;
	}
	
	private TransportMode selectMode(final String linkType){
		TransportMode mode = null;
		if (linkType.equals("Standard")){ 
			mode=  TransportMode.pt;
		}else{
			mode=  TransportMode.walk;
		}
		return mode;
	}
	
	private LegImpl createLeg(TransportMode mode, final List<Link> routeLinks, final double depTime, final double arrivTime){
		double travTime= arrivTime - depTime;  
		List<Node> routeNodeList = new ArrayList<Node>();
		
		double distance=0;
		if (routeLinks.size()>0) routeNodeList.add(routeLinks.get(0).getFromNode());
		for(Link link : routeLinks) {
			distance= distance + link.getLength();
			routeNodeList.add(link.getToNode());
		} 
		
		NetworkRouteWRefs legRoute = new LinkNetworkRouteImpl(null, null);
		legRoute.setDistance(distance);
		legRoute.setLinks(null, routeLinks, null);
		legRoute.setTravelTime(travTime);
		legRoute.setNodes(null, routeNodeList, null);

		LegImpl leg = new LegImpl(mode);
		leg.setRoute(legRoute);
		leg.setDepartureTime(depTime);
		leg.setTravelTime(travTime);
		leg.setArrivalTime(arrivTime);

		return leg;
	}
	
}
