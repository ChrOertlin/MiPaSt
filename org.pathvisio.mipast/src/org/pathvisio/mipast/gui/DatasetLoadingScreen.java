package org.pathvisio.mipast.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.pathvisio.mipast.MiPaStPlugin;
import org.pathvisio.desktop.PvDesktop;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;


import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;



// This class opens and displays the GUI for the data loading of the miRNA and Transcriptomics Dataset

public class DatasetLoadingScreen extends JDialog {
	private MiPaStPlugin plugin;
	private PvDesktop desktop;
	
	
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
	
	static JPanel miRNAHeaderPanel = new JPanel();
	static JPanel transcriptomicsHeaderPanel = new JPanel();
	static DefaultTableModel miRNATableModel;
	static DefaultTableModel transcriptomicsTableModel;
	
	JTable miRNATable= new JTable(miRNATableModel);
	JTable transcriptomicsTable= new JTable(transcriptomicsTableModel);
	
	boolean miRNAFileLoaded;
	boolean transcriptomicsFileLoaded;
	
	static ArrayList<String> miRNAArray;
	static ArrayList<String> transcriptomicsArray;
	
	JFileChooser fc=new JFileChooser();
	
	File miRNAFile;
	File transcriptomicsFile;
	
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
		mainPanel= new JPanel();
		
		mainPanel.setLayout(new FormLayout("pref,50dlu,pref,50dlu,50dlu,pref,default","pref,4dlu,pref,4dlu,pref,4dlu,pref,4dlu,pref,150dlu,pref,4dlu"));
		
		mainPanel.add(screenLabel, cc.xy(1,1));
		
		mainPanel.add(miRNALabel, cc.xy(1, 3));
		mainPanel.add(miRNAText, cc.xywh(2, 3,3,1));
		mainPanel.add(miRNABrowse, cc.xy(6, 3));
		
		mainPanel.add(transcriptomicsBox,cc.xy(1, 5));
		
		mainPanel.add(transcriptomicsLabel, cc.xy(1, 7));
		mainPanel.add(transcriptomicsText, cc.xywh(2, 7,3,1));
		mainPanel.add(transcriptomicsBrowse, cc.xy(6, 7));
		
		miRNAHeaderPanel.add(new JScrollPane(miRNATable),BorderLayout.CENTER);
		transcriptomicsHeaderPanel.add(new JScrollPane(transcriptomicsTable), BorderLayout.CENTER);
		
		mainPanel.add(miRNAHeaderPanel, cc.xywh(1, 9,3,2));
		mainPanel.add(transcriptomicsHeaderPanel, cc.xywh(4, 9, 3, 2));
		
		mainPanel.add(next,cc.xy(6,11));
		mainPanel.add(previous,cc.xy(3,11));
		mainPanel.add(cancel,cc.xy(1,11));
		
		miRNABrowse.addActionListener(new BrowseActionListener());
		transcriptomicsBrowse.addActionListener(new BrowseActionListener());
		
		return mainPanel;
	}
		// Functionality for the Browse buttons, opens a browse dialog and let's the user select a file.
	public class BrowseActionListener implements ActionListener{
		
		
		
		
		public void actionPerformed(ActionEvent e){
			if (e.getSource() == miRNABrowse) {
		        int returnVal = fc.showDialog(null, "Open miRNA Datasetfile");
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            miRNAFile = fc.getSelectedFile();
		            miRNAText.setText(miRNAFile.getAbsolutePath());
		            miRNAFileLoaded= true;
				} 
			}
			
			if (e.getSource() == transcriptomicsBrowse) {
		        int returnVal = fc.showDialog(null, "Open Transcriptomics datasetfile");
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            transcriptomicsFile = fc.getSelectedFile();
		            transcriptomicsText.setText(transcriptomicsFile.getAbsolutePath());
		            transcriptomicsFileLoaded=true;
				} 
			}
		}	
	}
		
	public static class DisplayHeaderTables{
		
		public static void checkLoadedFiles(boolean a,boolean b){
		if (a == true || b == true){
			// add contents to either of the tables
			if(a == true){
				for(int i=0;i<miRNAArray.size(); i++){
					Object[] rows= {miRNAArray.get(i)}; 
					miRNATableModel.addRow(rows);
					miRNAHeaderPanel.revalidate();
				}
			}
			if(b == true){
				for(int i=0;i<transcriptomicsArray.size(); i++){
					Object[] rows= {transcriptomicsArray.get(i)}; 
					transcriptomicsTableModel.addRow(rows);
					transcriptomicsHeaderPanel.revalidate();
				}
			}
			
		}
		
		if (a == true && b == true){
			// add contents to both table
			for(int i=0; i< miRNAArray.size();i++){
				Object[] rows= {miRNAArray.get(i)}; 
				miRNATableModel.addRow(rows);
				miRNAHeaderPanel.revalidate();
			}
			for(int i=0; i< transcriptomicsArray.size();i++){
				Object[] rows= {transcriptomicsArray.get(i)}; 
				transcriptomicsTableModel.addRow(rows);
				transcriptomicsHeaderPanel.revalidate();
			}
			
			
		}
		
		
		
		
		
		}
		
		
	}
		
	
	// Can handle files with the same headers The program needs to handle the files earlier to let the user
	// select columns that should get paired and they should be the same in both files.
	public class FileMerger{
		
		
		public  File fileMerger() throws FileNotFoundException{
			
			String miRNAExt= fc.getTypeDescription(miRNAFile);
			String transcriptomicsExt = fc.getTypeDescription(transcriptomicsFile);
			if (miRNAExt != transcriptomicsExt){
				// Raise error files do not have the same extension?
			}
			
			FileReader miRNAFileReader = new FileReader(miRNAFile);
			FileReader transcriptomicsFileReader = new FileReader(transcriptomicsFile);
			
			//Create an ArrayList of the files selected with the browse button in the GUI
			miRNAArray= new ArrayList<String>(readFileIntoArray(miRNAFileReader, "miRNA", miRNAExt));
			transcriptomicsArray= new ArrayList<String>(readFileIntoArray(transcriptomicsFileReader, "gene", transcriptomicsExt));
			
			// Merge the ArrayLists together into one big array
			ArrayList<String> mergedArrays = new ArrayList<String>(mergeArrays(miRNAArray, transcriptomicsArray));
			
			// Write the merged arrays to a file and return it
			File mergedFile= new File("mergedFile"+ miRNAExt);
			try{
			BufferedWriter output = new BufferedWriter(new FileWriter(mergedFile));
			for (int i=0; i< mergedArrays.size();i++){
				output.write(mergedArrays.get(i));
				
			}
			output.close();
			}
			catch(IOException e){
				e.printStackTrace();
			}
			
			return mergedFile;
			}
		
		
		// Method that reads the selected files and puts them into an arraylist, specified for both csv(, seperated) and txt(tab seperated)
		// is this needed actually??
		public List<String> readFileIntoArray(FileReader fileReader, String type, String ext){
			List<String> lineArray=new ArrayList<String>();
			String line = null;
			String dataType = new String(type);
			
			if (ext == "csv"){
			try {
			BufferedReader reader= new BufferedReader(fileReader);
			while((line= reader.readLine())!= null){
				if(line.matches("^[a-Z].*$")){
					lineArray.add(line +"," + "type");
				}
				else{
					lineArray.add(line +"," + dataType);
				}
			}
			}
			catch (Exception e){
				e.printStackTrace();
				
			}
			}
			if (ext == "txt"){
				try {
				BufferedReader reader= new BufferedReader(fileReader);
				while((line= reader.readLine())!= null){
					if(line.matches("^[a-Z].*$")){
						lineArray.add(line +"/t" + "type");
					}
					else{
						lineArray.add(line +"/t" + dataType);
					}
				}
				}
				catch (Exception e){
					e.printStackTrace();
					
				}
				}
			
			return lineArray;
		}
	
		// The method that merges the arrays together into on big array
		public ArrayList<String> mergeArrays(ArrayList<String> array1, ArrayList<String> array2){
			ArrayList<String> mergedArray = new ArrayList<String>();
			for (int i=0; i< array1.size();i++){
				mergedArray.add(array1.get(i));
				
			}
			for (int i=1 ;i<array2.size();i++){
				mergedArray.add(array2.get(i));
			}
			
			return mergedArray;
		}
		
		
		public void applyMerge(){
			
				try {
					fileMerger();
					
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			
			
		}
		
		}
	
}


	



