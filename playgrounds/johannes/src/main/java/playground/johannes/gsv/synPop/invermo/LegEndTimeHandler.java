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

package playground.johannes.gsv.synPop.invermo;

import playground.johannes.synpop.data.Attributable;

/**
 * @author johannes
 *
 */
public class LegEndTimeHandler implements LegAttributeHandler {

	@Override
	public void handle(Attributable leg, int idx, String key, String value) {
		if(key.endsWith("anstd")) {
			leg.setAttribute("endTimeHour", value);
		} else if(key.endsWith("anmin")) {
			leg.setAttribute("endTimeMin", value);
		} else if(key.endsWith("antag")) {
			leg.setAttribute("endTimeDay", value);
		} else if(key.endsWith("anmonat")) {
			leg.setAttribute("endTimeMonth", value);
		} else if(key.endsWith("anjahr")) {
			leg.setAttribute("endTimeYear", value);
		}
	}

}
