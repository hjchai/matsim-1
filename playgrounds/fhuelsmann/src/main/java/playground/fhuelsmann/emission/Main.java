package playground.fhuelsmann.emission;
/* *********************************************************************** *
 * project: org.matsim.*
 * FhMain.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2009 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.ScenarioImpl;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.api.experimental.events.EventsManager;

import org.matsim.core.events.EventsManagerImpl;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.MatsimNetworkReader;
public class Main {
	
	public static void main (String[] args) throws Exception{

	
//		String eventsFile = "../../detailedEval/teststrecke/sim/output/20090317/ITERS/it.0/0.events.txt.gz";
//		String eventsFile = "../../detailedEval/testRuns/output/100.events.txt.gz";
		String eventsFile ="../../detailedEval/testRuns/output/run8/ITERS/it.0/0.events.txt.gz";
		String netfile ="../../detailedEval/testRuns/output/run8/output_network.xml.gz";
//		String netfile ="../../detailedEval/Net/network-86-85-87-84.xml";
//		String netfile ="../../detailedEval/teststrecke/sim/input/network.xml";
//		String visumRoadFile = "../../detailedEval/teststrecke/sim/inputEmissions/visumnetzlink.txt";
//		String visumRoadHebefaRoadFile = "../../detailedEval/teststrecke/sim/inputEmissions/road_types.txt";
		String Hbefa_Traffic = "../../detailedEval/teststrecke/sim/inputEmissions/hbefa_emission_factors_EU3_D.txt";
		String Hbefa_Cold_Traffic = "../../detailedEval/teststrecke/sim/inputEmissions/hbefa_coldstart_emission_factors.txt";
		
		
		//create an event object
		EventsManager events = new EventsManagerImpl();	
		
		
		ScenarioImpl scenario = new ScenarioImpl();
		Network network = scenario.getNetwork(); 
		new MatsimNetworkReader(scenario).readFile(netfile);
		
		

		//create the handler 
		HbefaTable hbefaTable = new HbefaTable();
		hbefaTable.makeHabefaTable(Hbefa_Traffic);
		
//		System.out.println(hbefaTable.getHbefaTableWithSpeedAndEmissionFactor()[15][0].getVelocity());

/*		runs the emission tool with a data structure that can be sorted by agent and LinkID, not suited for the large scenario*/
//		DataStructureOfSingleEventAttributes handler = new DataStructureOfSingleEventAttributes(network);
		
/*		runs the emission tool by using a modular approach either calculating the emissions per agent or per linkID*/
		TravelTimeEventHandler handler = new TravelTimeEventHandler(network,hbefaTable.getHbefaTableWithSpeedAndEmissionFactor());
		
		LinkAndAgentAccountAnalyseModul linkAccount = handler.getLinkAndAgentAccountAnalyseModul();
		
		
//		TimeAndDistanceEventHandler handler = new TimeAndDistanceEventHandler(network,hbefaTable.getHbefaTableWithSpeedAndEmissionFactor());
	
		
		//add the handler
		events.addHandler(handler);
		
	//	hbefaColdTable hbefaColdTable = new hbefaColdTable();
		//hbefaColdTable.makeHbefaColdTable(Hbefa_Cold_Traffic);
		//hbefaColdTable.printHbefaCold();
		
		
		//create the reader and read the file
		MatsimEventsReader matsimEventReader = new MatsimEventsReader(events);
		
		matsimEventReader.readFile(eventsFile);
		linkAccount.printTotalEmissionsPerLink();
		linkAccount.printTotalEmissionsPerAgent();
		
	
		
		
		
	
		//for cold start emissions 
		//handler.printTable();
		//handler.printTable2();
	//	ColdEmissionFactor em = 
		//	new ColdEmissionFactor(handler.coldDistance,handler.parkingTime,hbefaColdTable.getHbefaColdTable());
		
	//	em.MapForColdEmissionFactor();
		///em.printColdEmissionFactor();
		
	
		
//		HbefaTable hbefaTable = new HbefaTable();
//		hbefaTable.makeHabefaTable(Hbefa_Traffic);
		
//		hbefaColdTable hbefaColdTable = new hbefaColdTable();
//		hbefaColdTable.makeHbefaColdTable(Hbefa_Cold_Traffic);
//		hbefaColdTable.printHbefaCold();
/*		HbefaVisum hbefaVisum = new HbefaVisum(handler.getTravelTimes());
		hbefaVisum.createRoadTypes(visumRoadHebefaRoadFile);
		hbefaVisum.printHbefaVisum();
		hbefaVisum.createMapWithHbefaRoadTypeNumber();	
		
		EmissionFactor emissionFactor = new EmissionFactor(hbefaVisum.map,hbefaTable.getHbefaTableWithSpeedAndEmissionFactor());
		emissionFactor.createEmissionTables();
		emissionFactor.createEmissionFile();
//		emissionFactor.printEmissionTable();*/
		
		
		}
	}	
	
