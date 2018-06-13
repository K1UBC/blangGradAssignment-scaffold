
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

public class BipartiteMatchingSamplerRough implements Sampler {

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

	

	  

	  //uniformly randomly choose one of the vertices in set X 

	  int vertexFrom = Generators.discreteUniform(rand, 0, matching.componentSize());

          

	  // k is the number of free nodes in component 2

	  int k = matching.free2().size();

  

	  

	  //boolean if the list of free1 contains the index of the randomly chosen vertex

	  boolean isFree = matching.free1().contains(vertexFrom);

	  

	  //if free, get a connection

	  if (isFree) {

	matching.getConnections().set(vertexFrom, matching.free2().get(Generators.discreteUniform(rand, 0, matching.free2().size())));

    

	//if pick a free vertex

	//Q(After|Before)

	double logQAB = Math.log(matching.componentSize() * matching.free2().size());

			

	//Q(Before|After)

	double logQBA = - Math.log(matching.componentSize());

	  }

	

	 else

    //else, destroy the connection

	  {

	matching.getConnections().set(vertexFrom, BipartiteMatching.FREE);

	

	//if pick a connected vertex

	//Q(After|Before) 

	 double logQAB = - Math.log(matching.componentSize());

				  

	//Q(Before|After)

	 double logQBA = - Math.log(matching.componentSize() * matching.free2().size());

	  }

	 

	  //implement logDensityAfter

	  double logDensityAfter = logDensity();

	  

	  double acceptPr = Math.min(1,Math.exp(logDensityAfter + logQBA -logDensityBefore -logQAB));

	  

	  //If accepts, do nothing

	  if (Generators.bernoulli(rand, acceptPr))

	      ;

	  else {

	if (isFree) {

		//remove the added connection

		matching.getConnections().set(i, BipartiteMatching.FREE);

	}

	else {

		//restore the connection

		matching.getConnections().set(vertexFrom, matching.free2().get(Generators.discreteUniform(rand, 0, matching.free2().size())));

	    

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
  
  private double logDensity() {
    double sum = 0.0;
    for (LogScaleFactor f : numericFactors)
      sum += f.logDensity();
    return sum;
  }
}