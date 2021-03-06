/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2016 by the members listed in the COPYING,        *
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

package org.matsim.contrib.dvrp.trafficmonitoring;

import java.util.Map;

import org.matsim.api.core.v01.*;
import org.matsim.api.core.v01.network.*;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.groups.TravelTimeCalculatorConfigGroup;
import org.matsim.core.mobsim.framework.events.MobsimBeforeCleanupEvent;
import org.matsim.core.mobsim.framework.listeners.MobsimBeforeCleanupListener;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.trafficmonitoring.TimeBinUtils;
import org.matsim.vehicles.Vehicle;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.name.Named;


public class VrpTravelTimeEstimator
    implements TravelTime, MobsimBeforeCleanupListener
{
    public static class Params
    {
        private final TravelTime initialTT;
        private final double alpha;


        public Params(TravelTime initialTT, double alpha)
        {
            this.initialTT = initialTT;
            this.alpha = alpha;
        }
    }


    private final TravelTime observedTT;
    private final Network network;

    private final int interval;
    private final int intervalCount;
    private final Map<Id<Link>, double[]> linkTTs;
    private final double alpha;


    @Inject
    public VrpTravelTimeEstimator(Params params, @Named(TransportMode.car) TravelTime observedTT,
            Network network, TravelTimeCalculatorConfigGroup ttCalcConfig)
    {
        this.observedTT = observedTT;
        this.network = network;
        alpha = params.alpha;

        interval = ttCalcConfig.getTraveltimeBinSize();
        intervalCount = TimeBinUtils.getTimeBinCount(ttCalcConfig.getMaxTime(), interval);

        linkTTs = Maps.newHashMapWithExpectedSize(network.getLinks().size());
        init(params.initialTT);
    }


    private void init(TravelTime initialTT)
    {
        for (Link link : network.getLinks().values()) {
            double[] tt = new double[intervalCount];
            updateTTs(link, tt, initialTT, 1.);
            linkTTs.put(link.getId(), tt);
        }
    }


    @Override
    public double getLinkTravelTime(Link link, double time, Person person, Vehicle vehicle)
    {
        //TODO TTC is more flexible (simple averaging vs linear interpolation, etc.)
        int idx = TimeBinUtils.getTimeBinIndex(time, interval, intervalCount);
        return linkTTs.get(link.getId())[idx];
    }


    @Override
    public void notifyMobsimBeforeCleanup(@SuppressWarnings("rawtypes") MobsimBeforeCleanupEvent e)
    {
        for (Link link : network.getLinks().values()) {
            updateTTs(link, linkTTs.get(link.getId()), observedTT, alpha);
        }
    }


    private void updateTTs(Link link, double[] tt, TravelTime travelTime, double alpha)
    {
        for (int i = 0; i < intervalCount; i++) {
            double oldEstimatedTT = tt[i];
            double experiencedTT = travelTime.getLinkTravelTime(link, i * interval, null, null);
            tt[i] = alpha * experiencedTT + (1 - alpha) * oldEstimatedTT;
        }
    }
}
