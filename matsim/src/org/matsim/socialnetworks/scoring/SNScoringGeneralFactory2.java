package org.matsim.socialnetworks.scoring;

import java.util.ArrayList;
import java.util.Hashtable;

import org.matsim.population.Act;
import org.matsim.population.Plan;
import org.matsim.scoring.ScoringFunction;
import org.matsim.scoring.ScoringFunctionFactory;



public class SNScoringGeneralFactory2 implements ScoringFunctionFactory {

	private String factype;
//	private TrackEventsOverlap teo;
	private Hashtable<Act,ArrayList<Double>> actStats;
	private ScoringFunctionFactory factory;

	public SNScoringGeneralFactory2(String factype, ScoringFunctionFactory sf, Hashtable<Act,ArrayList<Double>> actStats) {
		this.factype=factype;
		this.actStats=actStats;
		this.factory=sf;

	}

	public ScoringFunction getNewScoringFunction(final Plan plan) {
//		return new SNScoringMaxFriendFoeRatio(plan, this.factype, this.scorer);
		return new SocializingScoringFunction2(plan, this.factory.getNewScoringFunction(plan), factype, actStats);
	}


}