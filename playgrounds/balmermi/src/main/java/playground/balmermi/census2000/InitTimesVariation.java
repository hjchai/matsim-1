/* *********************************************************************** *
 * project: org.matsim.*
 * InitTimesVariation.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2007 by the members listed in the COPYING,        *
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

package playground.balmermi.census2000;

import java.io.IOException;

import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.population.algorithms.PersonAlgorithm;
import org.matsim.core.population.io.MatsimPopulationReader;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.population.io.PopulationWriter;
import org.matsim.core.population.io.StreamingPopulationReader;
import org.matsim.core.population.io.StreamingUtils;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;

import playground.balmermi.census2000.modules.PersonVaryTimes;

public class InitTimesVariation {

	public static void varyInitTimes(Config config) {

		System.out.println("MATSim-IIDM: vary init times.");

		MutableScenario scenario = (MutableScenario) ScenarioUtils.createScenario(ConfigUtils.createConfig());

		//////////////////////////////////////////////////////////////////////

		System.out.println("  reading network xml file...");
		Network network = scenario.getNetwork();
		new MatsimNetworkReader(scenario.getNetwork()).readFile(config.network().getInputFile());
		System.out.println("  done.");

		//////////////////////////////////////////////////////////////////////

		System.out.println("  setting up plans objects...");
//		Population reader = (Population) scenario.getPopulation();
		StreamingPopulationReader reader = new StreamingPopulationReader( scenario ) ; 
		StreamingUtils.setIsStreaming(reader, true);
		PopulationWriter plansWriter = new PopulationWriter(null, network);
		plansWriter.startStreaming(null);//config.plans().getOutputFile());
//		PopulationReader plansReader = new MatsimPopulationReader(scenario);
		System.out.println("  done.");

		//////////////////////////////////////////////////////////////////////

		System.out.println("  adding person modules... ");
		reader.addAlgorithm(new PersonVaryTimes());
		System.out.println("  done.");

		//////////////////////////////////////////////////////////////////////

		System.out.println("  reading, processing, writing plans...");
		final PersonAlgorithm algo = plansWriter;
		reader.addAlgorithm(algo);
//		plansReader.readFile(config.plans().getInputFile());
		reader.readFile(config.plans().getInputFile());
		PopulationUtils.printPlansCount(reader) ;
		plansWriter.closeStreaming();
		System.out.println("  done.");

		//////////////////////////////////////////////////////////////////////

		System.out.println("done.");
		System.out.println();
	}

	//////////////////////////////////////////////////////////////////////
	// main
	//////////////////////////////////////////////////////////////////////

	public static void main(final String[] args) throws IOException {
		Gbl.startMeasurement();

		Config config = ConfigUtils.loadConfig(args[0]);

		varyInitTimes(config);

		Gbl.printElapsedTime();
	}
}
