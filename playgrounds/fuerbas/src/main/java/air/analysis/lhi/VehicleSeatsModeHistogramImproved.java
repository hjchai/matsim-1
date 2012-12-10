/* *********************************************************************** *
 * project: org.matsim.*
 * DgLegHistogramImproved
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2012 by the members listed in the COPYING,        *
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
package air.analysis.lhi;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.core.api.experimental.events.VehicleArrivesAtFacilityEvent;
import org.matsim.core.api.experimental.events.VehicleDepartsAtFacilityEvent;
import org.matsim.core.events.handler.VehicleArrivesAtFacilityEventHandler;
import org.matsim.core.events.handler.VehicleDepartsAtFacilityEventHandler;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.Vehicles;

import air.analysis.categoryhistogram.CategoryHistogram;

/**
 * Improved version of LegHistogram - no maximal time bin size - some additional data is collected as number of seats
 * standing room over time
 * 
 * @author dgrether
 * 
 */
public class VehicleSeatsModeHistogramImproved implements
		VehicleArrivesAtFacilityEventHandler, VehicleDepartsAtFacilityEventHandler {

	private static final Logger log = Logger.getLogger(VehicleSeatsModeHistogramImproved.class);

	private static int noDepartureEventVeh = 0;
	
	private CategoryHistogram histogram;
	
	private Map<Id, VehicleDepartsAtFacilityEvent> vehDepartsEventsByVehicleId = new HashMap<Id, VehicleDepartsAtFacilityEvent>();
	private Vehicles vehicles;
	
	public VehicleSeatsModeHistogramImproved(Vehicles vehicles) {
		this(5 * 60, vehicles);
	}

	/**
	 * Creates a new LegHistogram with the specified binSize and the specified number of bins.
	 * 
	 * @param binSize
	 *          The size of a time bin in seconds.
	 * @param nofBins
	 *          The number of time bins for this analysis.
	 */
	public VehicleSeatsModeHistogramImproved(final int binSize, Vehicles vehicles) {
		this.histogram = new CategoryHistogram(binSize);
		this.vehicles = vehicles;
		reset(0);
	}

	@Override
	public void reset(int iteration) {
		this.vehDepartsEventsByVehicleId.clear();
		this.histogram.reset(iteration);
	}

	@Override
	public void handleEvent(VehicleDepartsAtFacilityEvent event) {
		this.vehDepartsEventsByVehicleId.put(event.getVehicleId(), event);
	}

	@Override
	public void handleEvent(VehicleArrivesAtFacilityEvent event) {
		VehicleDepartsAtFacilityEvent departureEvent = this.vehDepartsEventsByVehicleId.get(event.getVehicleId());
		if (departureEvent == null){
//			log.error("no departure event for vehicle :  " + event.getVehicleId() + " assuming first arrival!");
			noDepartureEventVeh++;
		}
		else {
			Vehicle vehicle = this.vehicles.getVehicles().get(event.getVehicleId());
			int seats = vehicle.getType().getCapacity().getSeats();
			this.histogram.increase(departureEvent.getTime(), seats, "seats");
			this.histogram.decrease(event.getTime(), seats, "seats");
		}
	}

	public CategoryHistogram getCategoryHistogram() {
		return this.histogram;
	}

}
