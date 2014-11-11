package org.pathvisio.mipast.io;

import org.pathvisio.data.Criterion;
import org.pathvisio.mipast.DataHolding;

public class PositiveGeneList {

	private static Criterion miRNAUpCrit;
	private static Criterion geneUpCrit;
	private static Criterion miRNADownCrit;
	private static Criterion geneDownCrit;
	
	public void retrieveCriteria(){
		
		miRNAUpCrit.setExpression(DataHolding.getMiRNAUpCrit());
		geneUpCrit.setExpression( DataHolding.getGeneUpCrit());
		miRNADownCrit.setExpression(DataHolding.getMiRNADownCrit());
		geneDownCrit.setExpression(DataHolding.getGeneDownCrit());	
	}
	
	
}
