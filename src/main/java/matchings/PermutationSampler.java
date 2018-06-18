package matchings;

import java.util.Collections;
import java.util.List;

import bayonet.distributions.Random;
import blang.core.LogScaleFactor;
import blang.distributions.Generators;
import blang.mcmc.ConnectedFactor;
import blang.mcmc.SampledVariable;
import blang.mcmc.Sampler;
import briefj.collections.UnorderedPair;

/**
 * Each time a Permutation is encountered in a Blang model, 
 * this sampler will be instantiated. 
 */
public class PermutationSampler implements Sampler {
  /**
   * This field will be populated automatically with the 
   * permutation being sampled. 
   */
  @SampledVariable Permutation permutation;
  /**
   * This will contain all the elements of the prior or likelihood 
   * (collectively, factors), that depend on the permutation being 
   * resampled. 
   */
  @ConnectedFactor List<LogScaleFactor> numericFactors;

  @Override
  public void execute(Random rand) {
    // Fill this. 
	//Density current - Q(X)
	  double densityBefore = logDensity();
	  
	//generate random pairs and swap two vertices to produce a random proposal outcome
	//I used Dr.Bouchard's permutation solution as a reference to build this code
	  UnorderedPair<Integer, Integer> pair= Generators.distinctPair(rand, permutation.componentSize());
	  Collections.swap(permutation.getConnections(), pair.getFirst(), pair.getSecond());
	 
	 //Density proposal - Q(Y)
	  double densityAfter = logDensity();
	  
	 //We are using MH, hence we need to define acceptance rate to apply MH method
	 //Since proposal density and current density are symmetric, Q(X|Y) and Q(Y|X) cancel out each other and leave only Q(X) and Q(Y) in the acceptance rate formula
	  double acceptanceRate = Math.min(1, Math.exp(densityAfter - densityBefore));
	 
	  //Here, I generated boolean so we can determine if we accept the new state or get back to the current state and repeat the experiment
	  //Also I used Dr.Bouchard's permutation solution to correct my code for the boolean
	  boolean accept = rand.nextBernoulli(acceptanceRate);
	 
	  //if we accept the new state, we do not need to take any additional actions
	 if (accept) {
	 ;
	 }
	 else {
	 //if we do not accept the new state, it should be reversed back to the current state
		 Collections.swap(permutation.getConnections(), pair.getFirst(), pair.getSecond());		 
	 }
   }
  
  private double logDensity() {
    double sum = 0.0;
    for (LogScaleFactor f : numericFactors)
      sum += f.logDensity();
    return sum;
  }
}
