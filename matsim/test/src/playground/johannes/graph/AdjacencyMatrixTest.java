/* *********************************************************************** *
 * project: org.matsim.*
 * AdjacencyMatrixTest.java
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
package playground.johannes.graph;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.matsim.testcases.MatsimTestCase;

import playground.johannes.socialnetworks.graph.Graph;
import playground.johannes.socialnetworks.graph.GraphStatistics;
import playground.johannes.socialnetworks.graph.SparseEdge;
import playground.johannes.socialnetworks.graph.SparseGraph;
import playground.johannes.socialnetworks.graph.SparseGraphFactory;
import playground.johannes.socialnetworks.graph.SparseVertex;
import playground.johannes.socialnetworks.graph.Vertex;
import playground.johannes.socialnetworks.graph.generators.ErdosRenyiGenerator;
import playground.johannes.socialnetworks.graph.mcmc.AdjacencyMatrix;

/**
 * @author illenberger
 *
 */
public class AdjacencyMatrixTest extends MatsimTestCase {

	public void testConvert() {
		ErdosRenyiGenerator<SparseGraph, SparseVertex, SparseEdge> generator = new ErdosRenyiGenerator<SparseGraph, SparseVertex, SparseEdge>(new SparseGraphFactory());
		Graph g1 = generator.generate(1000, 0.01, 0);
		
		AdjacencyMatrix m = new AdjacencyMatrix(g1);
		
		Graph g2 = m.getGraph(new SparseGraphFactory());
		
		assertEquals(g2.getVertices().size(), g1.getVertices().size());
		assertEquals(g2.getEdges().size(), g1.getEdges().size());
		assertEquals(GraphStatistics.degreeDistribution(g2).mean(), GraphStatistics.degreeDistribution(g1).mean());
		
//		int addCount = 0;
		for(int i = 0; i < 100; i+=2) {
			m.addEdge(i, i+1);
//				addCount++;
		}
		
//		Graph g3 = m.getGraph(new PlainGraphFactory());
//		assertEquals(g3.getEdges().size(), g1.getEdges().size() + addCount);
		
//		int removeCount = 0;
		for(int i = 0; i < 100; i+=2) {
			m.removeEdge(i+1, i);
//				removeCount++;
		}
		
		Graph g4 = m.getGraph(new SparseGraphFactory());
		assertEquals(g4.getEdges().size(), g1.getEdges().size());
		
		assertEquals(GraphStatistics.degreeDistribution(g2).mean(), GraphStatistics.degreeDistribution(g4).mean());
	}

	public void testCommonNeighbours() {
		AdjacencyMatrix m = new AdjacencyMatrix();
		SparseGraphFactory factory = new SparseGraphFactory();
		SparseGraph g = factory.createGraph();
		
		m.addVertex();
		SparseVertex v0 = factory.addVertex(g);
		
		m.addVertex();
		SparseVertex v1 = factory.addVertex(g);
		
		m.addVertex();
		SparseVertex v2 = factory.addVertex(g);
		
		m.addVertex();
		SparseVertex v3 = factory.addVertex(g);
		
		m.addVertex();
		SparseVertex v4 = factory.addVertex(g);
		
		m.addEdge(0, 1);
		factory.addEdge(g, v0, v1);
		
		m.addEdge(0, 2);
		factory.addEdge(g, v0, v2);
		
		m.addEdge(0, 3);
		factory.addEdge(g, v0, v3);
		
		m.addEdge(2, 1);
		factory.addEdge(g, v2, v1);
		
		m.addEdge(3, 1);
		factory.addEdge(g, v3, v1);
		
		m.addEdge(4, 1);
		factory.addEdge(g, v4, v1);
		
		assertEquals(m.countCommonNeighbours(0, 1), 2);
		assertEquals(m.countCommonNeighbours(1, 0), 2);
		assertEquals(countCommonNeighbours(v0, v1), 2);
		
		assertEquals(m.countCommonNeighbours(0, 4), 1);
		assertEquals(countCommonNeighbours(v0, v4), 1);
	}
	
	private int countCommonNeighbours(Vertex v1, Vertex v2) {
		List<? extends Vertex> n1set = v1.getNeighbours();
		List<? extends Vertex> n2set = v2.getNeighbours();
		
		return CollectionUtils.intersection(n1set, n2set).size();
	}
}
