package org.pathvisio.mipast.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;

import org.pathvisio.mipast.MiPaStPlugin;
import org.pathvisio.desktop.PvDesktop;



import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout.Constraints;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

// This class opens and displays the GUI for the data loading of the miRNA and Transcriptomics Dataset

public class DatasetLoadingScreen extends JDialog {
	private MiPaStPlugin plugin;
	private PvDesktop desktop;
	
	JDialog dialog;
	JPanel mainPanel;
	
	JLabel screenLabel = new JLabel("Load your datasets");
	
	JButton miRNABrowse=new JButton("browse");
	JButton transcriptomicsBrowse= new JButton("Browse");
	
	JTextField miRNAText=new JTextField();
	JTextField transcriptomicsText=new JTextField();
	
	JLabel miRNALabel = new JLabel("miRNA Dataset");
	JLabel transcriptomicsLabel= new JLabel("Transcriptomics Dataset");
	
	JButton next = new JButton("Next");
	JButton previous = new JButton("Previous");
	JButton cancel = new JButton("Cancel");
	
	JCheckBox transcriptomicsBox= new JCheckBox("Transcriptomics available");
	
	JPanel fileHeaderPanel = new JPanel();
	
	// Initializes the loading screen within Pathvisio
	public DatasetLoadingScreen(PvDesktop desktop, MiPaStPlugin plugin){	
		
		this.plugin = plugin;
		this.desktop = desktop;
		JFrame frame= new JFrame("Dataset Loading Screen");
		frame.setBounds(50, 100, 750, 400);
		frame.add(addContents());
		frame.setVisible(true);
		
		
	}
		
	// Here the components for the loading screen are created and added to the frame
	public JComponent addContents(){
		CellConstraints cc= new CellConstraints();
		
		// Components properties
		
		
		mainPanel= new JPanel();
		
		mainPanel.setLayout(new FormLayout("pref,50dlu,pref,50dlu,50dlu,pref,default","pref,4dlu,pref,4dlu,pref,4dlu,pref,4dlu,pref,150dlu,pref"));
		
		mainPanel.add(screenLabel, cc.xy(1,1));
		
		mainPanel.add(miRNALabel, cc.xy(1, 3));
		mainPanel.add(miRNAText, cc.xywh(2, 3,3,1));
		mainPanel.add(miRNABrowse, cc.xy(6, 3));
		
		mainPanel.add(transcriptomicsBox,cc.xy(1, 5));
		
		mainPanel.add(transcriptomicsLabel, cc.xy(1, 7));
		mainPanel.add(transcriptomicsText, cc.xywh(2, 7,3,1));
		mainPanel.add(transcriptomicsBrowse, cc.xy(6, 7));
		
		mainPanel.add(fileHeaderPanel, cc.xywh(1, 9,3,2));
		
		mainPanel.add(next,cc.xy(3,11));
		mainPanel.add(previous,cc.xy(2,11));
		mainPanel.add(cancel,cc.xy(1,11));
		
		
		return mainPanel;
	}
		
		
		
		
		
	

}
