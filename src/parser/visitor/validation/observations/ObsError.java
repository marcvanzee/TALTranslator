package parser.visitor.validation.observations;

import java.util.HashSet;

import parser.visitor.validation.ValError;
import tal.TALConstants;

/**
 * NOT IN USE
 * 
 * 
 * @author Marc van Zee (marcvanzee@gmail.com) - Linköping University
 *
 */
public class ObsError extends ValError
{
	private HashSet<Integer> timePoints = new HashSet<Integer>();
	private int timeMin = Integer.MAX_VALUE, timeMax = Integer.MIN_VALUE;
		
	public HashSet<Integer> getTimePoints()
	{
		return timePoints;
	}
	
	public void addTimePoints(HashSet<Integer> tps)
	{
		for (int t : tps)
			addTimePoint(t);
	}
	
	public void addTimePoint(int t) {
		timePoints.add(t);
		
		if (t < timeMin) timeMin = t;
		if (t > timeMax) timeMax = t;
	}
	
	public void postProcess(int currentNarrative)
	{
		// for observations we need to make sure that all time points between the lowest
		// and the highest time point in the narrative have an observation statement.
		if (currentNarrative == TALConstants.OBSERVATION)
		{
			// check whether all time points between the minimal time point 
			// and the maximal time point have at least one assertion
			for (int i=timeMin; i<timeMax; i++)
			{
				if (!timePoints.contains(i))
				{
					//setError(ErrorMsgs.ERR_OBS_MIN_MAX_TEMP_GROUNDED, "missing an assertion for time point " + i);
				}
			}
		}
	}
}
