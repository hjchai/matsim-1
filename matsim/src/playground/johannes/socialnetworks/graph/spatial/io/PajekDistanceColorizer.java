/* *********************************************************************** *
 * project: org.matsim.*
 * PajekDistanceColorizer.java
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

/**
 * 
 */
package playground.johannes.socialnetworks.graph.spatial.io;

import gnu.trove.TObjectDoubleHashMap;
import playground.johannes.socialnetworks.graph.io.PajekColorizer;
import playground.johannes.socialnetworks.graph.spatial.SpatialEdge;
import playground.johannes.socialnetworks.graph.spatial.SpatialGraph;
import playground.johannes.socialnetworks.graph.spatial.SpatialGraphStatistics;
import playground.johannes.socialnetworks.graph.spatial.SpatialVertex;

/**
 * @author illenberger
 *
 */
public class PajekDistanceColorizer extends PajekColorizer<SpatialVertex, SpatialEdge> {

	private double d_min;
	
	private double d_max;
	
	private TObjectDoubleHashMap<SpatialVertex> d_mean;
	
	private boolean logScale = false;
	
	@SuppressWarnings("unchecked")
	public PajekDistanceColorizer(SpatialGraph graph, boolean logScale) {
		super();
		setLogScale(logScale);
		d_mean = (TObjectDoubleHashMap<SpatialVertex>) SpatialGraphStatistics.meanEdgeLength(graph);
		
		d_min = Double.MAX_VALUE;
		d_max = Double.MIN_VALUE;
		for(double value : d_mean.getValues()) {
			d_min = Math.min(value, d_min); 
			d_max = Math.max(value, d_max);
		}
	}
	
	public void setLogScale(boolean flag) {
		logScale = flag;
	}
	
	@Override
	public String getEdgeColor(SpatialEdge e) {
		return getColor(-1);
	}

	@Override
	public String getVertexFillColor(SpatialVertex v) {
		double color = -1;
		if(logScale) {
			double min = Math.log(d_min + 1);
			double max = Math.log(d_max + 1);
			color = (Math.log(d_mean.get(v)+ 1) - min) / (max - min);
		} else {
			color = (d_mean.get(v) - d_min)/(d_max - d_min);
		}
		
		return getColor(color);
	}

}
