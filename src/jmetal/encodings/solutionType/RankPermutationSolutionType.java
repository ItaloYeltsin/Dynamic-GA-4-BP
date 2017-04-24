package jmetal.encodings.solutionType;

import java.util.ArrayList;
import java.util.Collections;

import jmetal.core.Problem;
import jmetal.core.SolutionType;
import jmetal.core.Variable;
import jmetal.encodings.variable.Int;
import jmetal.util.JMException;

/**
 * This is a solution type that Creates a solution for problems to ranks of size N among P elements, such that, N <= P.
 * Differently than PermutationSolutionType that shuffles P elements and creates a solution
 * with P elements, this solution type shuffles P elements and creates a solution with size N.
 * An example of problem that could be solved with this kind of solution type is Partial Sorting.
 * 
 * @author Italo Yeltsin
 * @since 24/04/2017 
 */

public class RankPermutationSolutionType extends SolutionType{
	private int rankSize;
	private int currentPosition = 0;
	
	private ArrayList<Integer> number;
	
	public RankPermutationSolutionType(Problem problem, int rankSize) {
		super(problem);
		this.rankSize = rankSize;
		number = new ArrayList<Integer>(problem.getNumberOfVariables());
		for (int i = 0; i < problem.getNumberOfVariables(); i++) {
			number.add(i);
		}
		Collections.shuffle(number);
	}

	@Override
	public Variable[] createVariables() throws ClassNotFoundException {
		Variable [] vars = new Variable[rankSize];
		int remainderSize = number.size() - currentPosition;
		
		if(remainderSize < rankSize ) {
			currentPosition = 0;
			Collections.shuffle(number);
		}
		
		for (int i = 0; i < vars.length; i++) {
				vars[i] = new Int(number.get(currentPosition++), (int)problem_.getLowerLimit(i), (int)problem_.getUpperLimit(i));
		}
		
		return vars;
	}

}
