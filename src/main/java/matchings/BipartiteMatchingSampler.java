package matchings;

import java.util.List;
import bayonet.distributions.Multinomial;
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
public class BipartiteMatchingSampler implements Sampler {
  /**
   * This field will be populated automatically with the 
   * permutation being sampled. 
   */
  @SampledVariable BipartiteMatching matching;
  /**
   * This will contain all the elements of the prior or likelihood 
   * (collectively, factors), that depend on the permutation being 
   * resampled. 
   */
  @ConnectedFactor List<LogScaleFactor> numericFactors;
  @Override
  public void execute(Random rand) {

	  //logDensityBefore	  
	  double logDensityBefore = logDensity();
	  double logQAB, logQBA;
	  int j = 0;
	  
	  //uniformly randomly choose one of the vertices in set X 
	  int vertexFrom = rand.nextInt(matching.componentSize());
			
	  //boolean if the list of free1 contains the index of the randomly chosen vertex
	  boolean isFree = matching.free1().contains(vertexFrom);

	  //if free, get a connection
	  if (isFree) {
    //define j as a randomly chosen free vertex in the component 2
    j = matching.free2().get(rand.nextInt(matching.free2().size()));
  
    //if pick a free vertex
  	//Q(After|Before)
  	logQAB = - Math.log(matching.componentSize() * matching.free2().size());
    
    //int rand_picked = matching.free2().get(j);
    matching.getConnections().set(vertexFrom, j);

	//Q(Before|After)
	logQBA = - Math.log(matching.componentSize());
	  }
	 else
    //else, destroy the connection
	  {
		 
	j = matching.getConnections().get(vertexFrom);
	
	//Q(After|Before) 
	logQAB = - Math.log(matching.componentSize());
	
	matching.getConnections().set(vertexFrom, BipartiteMatching.FREE);
	
	 //Q(Before|After)
	 logQBA = - Math.log(matching.componentSize() * matching.free2().size());
	  }
	  
	  //implement logDensityAfter
	  double logDensityAfter = logDensity();
	  
	  double acceptPr = Math.min(1.0,Math.exp(logDensityAfter + logQBA 
			  -logDensityBefore -logQAB));
	  
	  boolean accept = rand.nextBernoulli(acceptPr);
	  
	  //If accepts, do nothing
	  if (accept)
	      ;
	  else {
	if (isFree) {
		//remove the added connection
		matching.getConnections().set(vertexFrom, BipartiteMatching.FREE);
	}
	else {
		//restore the connection
		matching.getConnections().set(vertexFrom, j);		
	}
	}
	    // Fill this.
  }

  private double logDensity() {
    double sum = 0.0;
    for (LogScaleFactor f : numericFactors)
      sum += f.logDensity();
    return sum;
  }
}
