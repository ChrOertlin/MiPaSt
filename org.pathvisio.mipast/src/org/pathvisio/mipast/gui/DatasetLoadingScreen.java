package org.pathvisio.mipast.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;

import org.pathvisio.mipast.MiPaStPlugin;
import org.pathvisio.desktop.PvDesktop;



import javax.swing.BoxLayout;
import javax.swing.JButton;
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
	
	JButton miRNABrowse=new JButton("browse");
	JButton transcriptomicsBrowse= new JButton("Browse");
	
	JTextField miRNAText=new JTextField();
	JTextField transcriptomicsText=new JTextField();
	
	JLabel miRNALabel = new JLabel("miRNA Dataset");
	JLabel transcriptomicsLabel= new JLabel("Transcriptomics Dataset");
	
	JButton next = new JButton("Next");
	JButton previous = new JButton("Previous");
	JButton cancel = new JButton("Cancel");
	
	// Initializes the loading screen within Pathvisio
	public DatasetLoadingScreen(PvDesktop desktop, MiPaStPlugin plugin){	
		
		this.plugin = plugin;
		this.desktop = desktop;
		JFrame frame= new JFrame("Loading Screen");
		frame.setBounds(50, 100, 700, 500);
		frame.add(addContents());
		frame.setVisible(true);
		
		
	}
		
	// Here the components for the loading screen are created and added to the frame
	public JComponent addContents(){
		mainPanel= new JPanel();
		mainPanel.setLayout(new GridLayout(4,3));
		
		//Panel that holds the miRNA features
		JPanel miRNAPanel= new JPanel();
		miRNAPanel.setLayout(new FlowLayout());
		miRNAPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		miRNALabel.setPreferredSize(new Dimension(250,24));
		miRNAText.setPreferredSize(new Dimension(200,24));
		miRNAPanel.add(miRNABrowse);
		miRNAPanel.add(miRNAText);
		miRNAPanel.add(miRNALabel);
		
		//Panel that holds the Transcriptomics features
		JPanel transcriptomicsPanel = new JPanel();
		transcriptomicsPanel.setLayout(new FlowLayout());
		transcriptomicsPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		transcriptomicsLabel.setPreferredSize(new Dimension(250,24));
		transcriptomicsText.setPreferredSize(new Dimension(200,24));
		transcriptomicsPanel.add(transcriptomicsBrowse);
		transcriptomicsPanel.add(transcriptomicsText);
		transcriptomicsPanel.add(transcriptomicsLabel);
		
		//Panel that holds the file Headers
		JPanel fileHeaderPanel= new JPanel();
		fileHeaderPanel.setLayout(new FlowLayout());
		fileHeaderPanel.add(new JTextField("TESTPANEL"));
		
		
		//Panel that holds the frame functionality buttons
		JPanel buttonPanel= new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		buttonPanel.add(next);
		buttonPanel.add(previous);	
		buttonPanel.add(cancel);
			
		
		mainPanel.add(miRNAPanel);
		mainPanel.add(transcriptomicsPanel);
		mainPanel.add(fileHeaderPanel);
		mainPanel.add(buttonPanel);
		
		
		
		
		
		return mainPanel;
	}
		
		
		
		
		
	

}
