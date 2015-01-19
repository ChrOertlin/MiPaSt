package org.pathvisio.mipast.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bridgedb.Xref;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.mipast.DataHolding;
import org.pathvisio.rip.Interaction;
import org.pathvisio.rip.RegIntPlugin;

public class BackgroundsetMethods {
	
	private static PvDesktop desktop;
	private RegIntPlugin plugin;
	
	
	public  BackgroundsetMethods(RegIntPlugin plugin){
		this.plugin= plugin;
		
		
	}
	
	
	public void datasetMethod(){
		Set<Xref> geneMeasuredAndInteraction = new HashSet<Xref>();
		for(Xref x: DataHolding.getAllGenesList()){
			if(plugin.getInteractions().containsKey(x)){
				
				for(Xref y : DataHolding.allmiRNAList){
					if(plugin.getInteractions().containsKey(y)){
						
						geneMeasuredAndInteraction.add(x);
					}
					
				}
				
			}
		}
		
	}

	
	
	public void pathwayMethod(){
		Set<Xref> geneMeasuredAndInPathway = new HashSet<Xref>();
		
		for(Xref x : DataHolding.getAllGenesList()){
			if(plugin.getInteractions().containsKey(x) && DataHolding.pathwayGenes.contains(x)){
				geneMeasuredAndInPathway.add(x);
			}
		}
	}
}
