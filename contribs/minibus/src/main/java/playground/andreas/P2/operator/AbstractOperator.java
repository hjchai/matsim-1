/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2011 by the members listed in the COPYING,        *
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

package playground.andreas.P2.operator;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.vehicles.Vehicle;

import playground.andreas.P2.helper.PConfigGroup;
import playground.andreas.P2.helper.PConstants.OperatorState;
import playground.andreas.P2.pbox.PFranchise;
import playground.andreas.P2.replanning.PPlan;
import playground.andreas.P2.replanning.PStrategy;
import playground.andreas.P2.replanning.PStrategyManager;
import playground.andreas.P2.routeProvider.PRouteProvider;
import playground.andreas.P2.scoring.ScoreContainer;

/**
 * Common implementation for all cooperatives, except for replanning
 * 
 * @author aneumann
 *
 */
public abstract class AbstractOperator implements Operator{
	
	protected final static Logger log = Logger.getLogger(AbstractOperator.class);
	
	protected final Id<Operator> id;
	
	private int numberOfPlansTried;
	
	private PFranchise franchise;
	private final double costPerVehicleBuy;
	protected final double costPerVehicleSell;
	private final double costPerVehicleAndDay;
	private final double minOperationTime;
	
	protected OperatorState operatorState;

	protected PPlan bestPlan;
	protected PPlan testPlan;

	protected TransitLine currentTransitLine;
	private int numberOfIterationsForProspecting;
	
	protected double budget;
	protected double score;
	protected double scoreLastIteration;
	protected int numberOfVehiclesInReserve;
	
	protected PRouteProvider routeProvider;
	protected int currentIteration;

	public AbstractOperator(Id<Operator> id, PConfigGroup pConfig, PFranchise franchise){
		this.id = id;
		this.numberOfIterationsForProspecting = pConfig.getNumberOfIterationsForProspecting();
		this.costPerVehicleBuy = pConfig.getPricePerVehicleBought();
		this.costPerVehicleSell = pConfig.getPricePerVehicleSold();
		this.costPerVehicleAndDay = pConfig.getCostPerVehicleAndDay();
		this.minOperationTime = pConfig.getMinOperationTime();
		this.franchise = franchise;
	}

	public boolean init(PRouteProvider pRouteProvider, PStrategy initialStrategy, int iteration, double initialBudget) {
		this.operatorState = OperatorState.PROSPECTING;
		this.budget = initialBudget;
		this.currentIteration = iteration;
		this.routeProvider = pRouteProvider;
		
		this.bestPlan = initialStrategy.run(this);
		if(this.bestPlan == null) {
			// failed to provide a plan, abort intitialization
			return false;
		}
		
		this.testPlan = null;
		this.numberOfPlansTried = 0;
		this.numberOfVehiclesInReserve = 0;
		
		// everything went fine
		return true;
	}

	public void score(TreeMap<Id<Vehicle>, ScoreContainer> driverId2ScoreMap) {
		this.scoreLastIteration = this.score;
		this.score = 0;
		
		// score all plans
		for (PPlan plan : this.getAllPlans()) {
			scorePlan(driverId2ScoreMap, plan);
			this.score += plan.getScore();
			for (TransitRoute route : plan.getLine().getRoutes().values()) {
				route.setDescription(plan.toString(this.budget + this.score));
			}
		}
		
		// score all vehicles not associated with plans
		score -= this.numberOfVehiclesInReserve * this.costPerVehicleAndDay;
		
		if (this.score > 0.0) {
			this.operatorState = OperatorState.INBUSINESS;
		}
		
		if (this.operatorState.equals(OperatorState.PROSPECTING)) {
			if(this.numberOfIterationsForProspecting == 0){
				if (this.score < 0.0) {
					// no iterations for prospecting left and score still negative - terminate
					this.operatorState = OperatorState.BANKRUPT;
				}
			}
			this.numberOfIterationsForProspecting--;
		}

		this.budget += this.score;
		
		// check, if bankrupt
		if(this.budget < 0){
			// insufficient, sell vehicles
			int numberOfVehiclesToSell = -1 * Math.min(-1, (int) Math.floor(this.budget / this.costPerVehicleSell));
			
			int numberOfVehiclesOwned = this.getNumberOfVehiclesOwned();
			
			if(numberOfVehiclesOwned - numberOfVehiclesToSell < 1){
				// can not balance the budget by selling vehicles, bankrupt
				this.operatorState = OperatorState.BANKRUPT;
			}
		}
	}
	
	abstract public void replan(PStrategyManager pStrategyManager, int iteration);
	
	public Id<Operator> getId() {
		return this.id;
	}
	
	public Id<PPlan> getNewPlanId() {
		Id<PPlan> planId = Id.create(this.currentIteration + "_" + numberOfPlansTried, PPlan.class);
		this.numberOfPlansTried++;
		return planId;
	}
	
	public PFranchise getFranchise(){
		return this.franchise;
	}

	@Override
	public double getMinOperationTime() {
		return this.minOperationTime;
	}

	public TransitLine getCurrentTransitLine() {
		if (this.currentTransitLine == null) {
			this.updateCurrentTransitLine();
		}
		return this.currentTransitLine;		
	}	

	public PPlan getBestPlan() {
		return this.bestPlan;
	}

	public List<PPlan> getAllPlans(){
		List<PPlan> plans = new LinkedList<PPlan>();
		if(this.bestPlan != null){
			plans.add(this.bestPlan);
		}
		if(this.testPlan != null){
			plans.add(this.testPlan);
		}		
		return plans;
	}
	
	public double getBudget(){
		return this.budget;
	}

	public int getNumberOfVehiclesOwned() {
		int numberOfVehicles = 0;			
		for (PPlan plan : this.getAllPlans()) {
			numberOfVehicles += plan.getNVehicles();
		}
		numberOfVehicles += this.numberOfVehiclesInReserve;
		return numberOfVehicles;
	}

	public int getCurrentIteration() {
		return this.currentIteration;
	}

	public PRouteProvider getRouteProvider() {
		return this.routeProvider;
	}

	public double getCostPerVehicleBuy() {
		return costPerVehicleBuy;
	}

	public double getCostPerVehicleSell() {
		return costPerVehicleSell;
	}

	@Override
	public OperatorState getOperatorState() {
		return this.operatorState;
	}

	public void setBudget(double budget) {
		this.budget = budget;
	}
	
	protected void updateCurrentTransitLine(){
		this.currentTransitLine = this.routeProvider.createEmptyLineFromOperator(id);
		for (PPlan plan : this.getAllPlans()) {
			for (TransitRoute route : plan.getLine().getRoutes().values()) {
				this.currentTransitLine.addRoute(route);
			}
		}
	}

	private void scorePlan(TreeMap<Id<Vehicle>, ScoreContainer> driverId2ScoreMap, PPlan plan) {
		double totalLineScore = 0.0;
		int totalTripsServed = 0;
		
		for (Id<Vehicle> vehId : plan.getVehicleIds()) {
			totalLineScore += driverId2ScoreMap.get(vehId).getTotalRevenue();
			totalTripsServed += driverId2ScoreMap.get(vehId).getTripsServed();
		}
		
		plan.setScore(totalLineScore);
		plan.setTripsServed(totalTripsServed);
	}
}