package org.pathvisio.mipast.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.pathvisio.desktop.PvDesktop;

import org.pathvisio.gui.CommonActions.ImportAction;
import org.pathvisio.mipast.io.MiPaStFileReader;


import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.nexes.wizard.Wizard;
import com.nexes.wizard.WizardPanelDescriptor;


/**
 * This class opens and displays the GUI for the data loading of the miRNA and Transcriptomics Dataset
 * @author christian
 *
 */
public class DatasetLoadingScreen extends Wizard {
	
	private ImportInformation importInformation = new ImportInformation();
	FileLoaderPage fpd = new FileLoaderPage();
	FilesInformationPage ipd = new FilesInformationPage();
   
	private PvDesktop desktop;
	
	public DatasetLoadingScreen (PvDesktop pvDesktop)
	{
		this.desktop = pvDesktop;

		getDialog().setTitle ("MiPaSt import wizard");

       registerWizardPanel(fpd);
       registerWizardPanel(ipd);

        setCurrentPanel(FileLoaderPage.IDENTIFIER);
	}
	
	private JTextField miRNAText;
	private JTextField geneText;
	private File miRNAFile;
	private File geneFile;
	
	private List<String> miRNAData;
	private List<String> geneData;
	
	private boolean miRNAFileLoaded = false;
	private boolean geneFileLoaded = false;
	
	private class FileLoaderPage extends WizardPanelDescriptor implements ActionListener {
		public static final String IDENTIFIER = "FILE_PAGE";
		 private boolean dataLoaded = false;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected Component createContents() {
			CellConstraints cc= new CellConstraints();	
			JPanel mainPanel= new JPanel();
			
			mainPanel.setLayout(new FormLayout("pref,50dlu,pref,50dlu,50dlu,pref,default","pref,4dlu,pref,4dlu,pref,4dlu,pref,4dlu,pref,150dlu,pref,4dlu"));
			
			JLabel screenLabel = new JLabel("Load your datasets");
			mainPanel.add(screenLabel, cc.xy(1,1));
			
			JButton miRNABrowse = new JButton("browse");
			JButton geneBrowse = new JButton("Browse");
			
			// miRNA
			miRNAText = new JTextField();
			JLabel miRNALabel = new JLabel("miRNA Dataset");
			
			mainPanel.add(miRNALabel, cc.xy(1, 3));
			mainPanel.add(miRNAText, cc.xywh(2, 3,3,1));
			mainPanel.add(miRNABrowse, cc.xy(6, 3));
			
			// gene
			JLabel transcriptomicsLabel= new JLabel("Transcriptomics Dataset");
			JCheckBox transcriptomicsBox= new JCheckBox("Transcriptomics available");
			
			geneText = new JTextField();
			mainPanel.add(transcriptomicsBox,cc.xy(1, 5));
			
			mainPanel.add(transcriptomicsLabel, cc.xy(1, 7));
			mainPanel.add(geneText, cc.xywh(2, 7,3,1));
			mainPanel.add(geneBrowse, cc.xy(6, 7));
			


			
			miRNABrowse.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					JFileChooser fc = new JFileChooser();
					int returnVal = fc.showDialog(null, "Open miRNA Datasetfile");
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						miRNAFile = fc.getSelectedFile();
						miRNAText.setText(miRNAFile.getAbsolutePath());
						try {
							miRNAData = MiPaStFileReader.readFile(miRNAFile);
							if(miRNAData.size() > 0) {
								System.out.println(miRNAData.size());
								miRNAFileLoaded = true;
							}
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
					}
				}
			});
			
			geneBrowse.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					JFileChooser fc = new JFileChooser();
					int returnVal = fc.showDialog(null, "Open Transcriptomics datasetfile");
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						geneFile = fc.getSelectedFile();
						geneText.setText(geneFile.getAbsolutePath());
						
						try {
							geneData = MiPaStFileReader.readFile(geneFile);
							if(geneData.size() > 0) {
								geneFileLoaded = true;
							}
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						
						
					}
				}
			});
			return mainPanel;
		}
		
		public void aboutToDisplayPanel()
		{
	        ///getWizard().setNextFinishButtonEnabled(dataLoaded);
			getWizard().setPageTitle ("Choose file locations");
		}

	    public FileLoaderPage()
	    {
	        super(IDENTIFIER);
	    }

	    public Object getNextPanelDescriptor()
	    {
	        return "INFORMATIONPAGE_PAGE";//HeaderPage.IDENTIFIER;
	    }

	    public Object getBackPanelDescriptor()
	    {
	        return null;
	    }
	}
	
	private class FilesInformationPage extends WizardPanelDescriptor implements ActionListener {
		public static final String IDENTIFIER = "INFORMATIONPAGE_PAGE";
		public FilesInformationPage() {
			super(IDENTIFIER);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected Component createContents() {
			JPanel informationPanel = new JPanel();
			return informationPanel;
			
		}
		public void aboutToDisplayPanel()
		{
	        ///getWizard().setNextFinishButtonEnabled(dataLoaded);
			getWizard().setPageTitle ("Choose file information");
		}
		public Object getNextPanelDescriptor()
	    {
	        return null;}
		public Object getBackPanelDescriptor()
	    {
	        return null;
	    }
	}
	
	
}
	
	
	

////		public  File fileMerger() throws FileNotFoundException{
////			
////			String miRNAExt= fc.getTypeDescription(miRNAFile);
////			String transcriptomicsExt = fc.getTypeDescription(geneFile);
////			if (miRNAExt != transcriptomicsExt){
////				// Raise error files do not have the same extension?
////			}
////			
////			FileReader miRNAFileReader = new FileReader(miRNAFile);
////			FileReader transcriptomicsFileReader = new FileReader(geneFile);
////			
////			//Create an ArrayList of the files selected with the browse button in the GUI
////			miRNAArray= new ArrayList<String>(readFileIntoArray(miRNAFileReader, "miRNA", miRNAExt));
////			transcriptomicsArray= new ArrayList<String>(readFileIntoArray(transcriptomicsFileReader, "gene", transcriptomicsExt));
////			
////			// Merge the ArrayLists together into one big array
////			ArrayList<String> mergedArrays = new ArrayList<String>(mergeArrays(miRNAArray, transcriptomicsArray));
////			
////			// Write the merged arrays to a file and return it
////			File mergedFile= new File("mergedFile"+ miRNAExt);
////			try{
////			BufferedWriter output = new BufferedWriter(new FileWriter(mergedFile));
////			for (int i=0; i< mergedArrays.size();i++){
////				output.write(mergedArrays.get(i));
////				
////			}
////			output.close();
////			}
////			catch(IOException e){
////				e.printStackTrace();
////			}
////			
////			return mergedFile;
////			}
////		
////		
////		
////		// Method that reads the selected files and puts them into an arraylist, specified for both csv(, seperated) and txt(tab seperated)
////		// is this needed actually??
////		public List<String> readFileIntoArray(FileReader fileReader, String type, String ext){
////			List<String> lineArray=new ArrayList<String>();
////			String line = null;
////			String dataType = new String(type);
////			// check if csv delimiter is /t or ,
////			if (ext == "csv"){
////			try {
////			BufferedReader reader= new BufferedReader(fileReader);
////			while((line= reader.readLine())!= null){
////				if(line.matches("^[a-Z].*$")){
////					lineArray.add(line +"," + "type");
////				}
////				else{
////					lineArray.add(line +"," + dataType);
////				}
////			}
////			}
////			catch (Exception e){
////				e.printStackTrace();
////				
////			}
////			}
////			if (ext == "txt"){
////				try {
////				BufferedReader reader= new BufferedReader(fileReader);
////				while((line= reader.readLine())!= null){
////					if(line.matches("^[a-Z].*$")){
////						lineArray.add(line +"/t" + "type");
////					}
////					else{
////						lineArray.add(line +"/t" + dataType);
////					}
////				}
////				}
////				catch (Exception e){
////					e.printStackTrace();
////					
////				}
////				}
////			
////			return lineArray;
////		}
////	
////		// The method that merges the arrays together into on big array
////		public ArrayList<String> mergeArrays(ArrayList<String> array1, ArrayList<String> array2){
////			ArrayList<String> mergedArray = new ArrayList<String>();
////			for (int i=0; i< array1.size();i++){
////				mergedArray.add(array1.get(i));
////				
////			}
////			for (int i=1 ;i<array2.size();i++){
////				mergedArray.add(array2.get(i));
////			}
////			
////			return mergedArray;
////		}
////		
////		
////		public void applyMerge(){
////			
////				try {
////					fileMerger();
////					
//// 					} catch (FileNotFoundException e1) {
////					// TODO Auto-generated catch block
////					e1.printStackTrace();
////				}
////			}
////		}



	



