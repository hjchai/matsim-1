/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2014 by the members listed in the COPYING,        *
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
package playground.agarwalamit.emissions;

import java.util.Set;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.emissions.EmissionModule;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.events.IterationEndsEvent;
import org.matsim.core.controler.events.IterationStartsEvent;
import org.matsim.core.controler.events.ShutdownEvent;
import org.matsim.core.controler.events.StartupEvent;
import org.matsim.core.controler.listener.IterationEndsListener;
import org.matsim.core.controler.listener.IterationStartsListener;
import org.matsim.core.controler.listener.ShutdownListener;
import org.matsim.core.controler.listener.StartupListener;
import org.matsim.core.events.algorithms.EventWriterXML;
import org.matsim.roadpricing.RoadPricingTravelDisutilityFactory;

import playground.benjamin.internalization.EmissionCostModule;
import playground.benjamin.internalization.EmissionInternalizationHandler;

/**
 * A different controller listner to internalize emission toll in each iteration to use randomized router
 * @author amit after benjamin
 */
public class InternalizeEmissionsRoadPricingControlerListner implements StartupListener, IterationStartsListener, IterationEndsListener, ShutdownListener {
	public static final Logger LOGGER = Logger.getLogger(InternalizeEmissionsRoadPricingControlerListner.class);

	Controler controler;
	EmissionModule emissionModule;
	EmissionCostModule emissionCostModule;
	String emissionEventOutputFile;
	EventWriterXML emissionEventWriter;
	EmissionInternalizationHandler emissionInternalizationHandler;
	RoadPricingTravelDisutilityFactory travelDisutilityFactory;
	Set<Id<Link>> hotspotLinks;
	EmissionRoadPricing emissionPricingScheme;

	int iteration;
	int firstIt;
	int lastIt;

	public InternalizeEmissionsRoadPricingControlerListner(EmissionModule emissionModule, EmissionCostModule emissionCostModule) {
		this.emissionModule = emissionModule;
		this.emissionCostModule = emissionCostModule;
	}

	@Override
	public void notifyStartup(StartupEvent event) {
//		controler = event.getServices();
//
//		this.emissionPricingScheme = new EmissionRoadPricing(15*60, controler.getScenario(),this.emissionModule, this.emissionCostModule);
//		this.emissionPricingScheme.run();
//		travelDisutilityFactory = new RoadPricingTravelDisutilityFactory(
//					services.getTravelDisutilityFactory(), emissionPricingScheme.getScheme(), services.getConfig().planCalcScore().getMarginalUtilityOfMoney()
//					) ;
//		travelDisutilityFactory.setSigma(3.);
//		controler.addOverridingModule(new AbstractModule() {
//			@Override
//			public void install() {
//				bindCarTravelDisutilityFactory().toInstance(travelDisutilityFactory);
//			}
//		});
//
//		EventsManager eventsManager = controler.getEvents();
//		eventsManager.addHandler(emissionModule.getWarmEmissionHandler());
//		eventsManager.addHandler(emissionModule.getColdEmissionHandler());
//
//		firstIt = services.getConfig().services().getFirstIteration();
//		lastIt = services.getConfig().services().getLastIteration();
		throw new RuntimeException();
	}

	@Override
	public void notifyIterationStarts(IterationStartsEvent event) {
		iteration = event.getIteration();
		
		LOGGER.info("creating new emission internalization handler...");
		emissionInternalizationHandler = new EmissionInternalizationHandler(controler, emissionCostModule, hotspotLinks);
		LOGGER.info("adding emission internalization module to emission events stream...");
		emissionModule.getEmissionEventsManager().addHandler(emissionInternalizationHandler);

		if(iteration == firstIt || iteration == lastIt){
			emissionEventOutputFile = controler.getControlerIO().getIterationFilename(iteration, "emission.events.xml.gz");
			LOGGER.info("creating new emission events writer...");
			emissionEventWriter = new EventWriterXML(emissionEventOutputFile);
			LOGGER.info("adding emission events writer to emission events stream...");
			emissionModule.getEmissionEventsManager().addHandler(emissionEventWriter);
		}
	}

	@Override
	public void notifyIterationEnds(IterationEndsEvent event) {

		LOGGER.info("removing emission internalization module from emission events stream...");
		emissionModule.getEmissionEventsManager().removeHandler(emissionInternalizationHandler);

		if(iteration == firstIt || iteration == lastIt){
			LOGGER.info("removing emission events writer from emission events stream...");
			emissionModule.getEmissionEventsManager().removeHandler(emissionEventWriter);
			LOGGER.info("closing emission events file...");
			emissionEventWriter.closeFile();
		}
	}

	@Override
	public void notifyShutdown(ShutdownEvent event) {
		emissionModule.writeEmissionInformation(emissionEventOutputFile);
	}

	public void setHotspotLinks(Set<Id<Link>> hotspotLinks) {
		this.hotspotLinks = hotspotLinks;
	}
}