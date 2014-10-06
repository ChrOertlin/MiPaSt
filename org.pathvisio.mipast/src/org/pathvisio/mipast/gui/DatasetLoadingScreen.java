package org.pathvisio.mipast.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bridgedb.*;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.bridgedb.gui.SimpleFileFilter;
import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.core.util.ProgressKeeper.ProgressEvent;
import org.pathvisio.core.util.ProgressKeeper.ProgressListener;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.util.RowNumberHeader;
import com.nexes.wizard.WizardPanelDescriptor;
import org.pathvisio.mipast.io.ColumnTableModel;
import org.pathvisio.gexplugin.GexTxtImporter;
import org.pathvisio.gexplugin.ImportInformation;



import org.pathvisio.mipast.io.FileMerger;
import org.pathvisio.mipast.io.PreviewTableModel;
import org.pathvisio.gui.DataSourceModel;
import org.pathvisio.gui.CommonActions.ImportAction;
import org.pathvisio.gui.util.PermissiveComboBox;
import org.pathvisio.mipast.io.MiPaStFileReader;


import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
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
	
	private ImportInformation miRNAImportInformation = new ImportInformation();
	private ImportInformation geneImportInformation= new ImportInformation();
	private FileLoaderPage fpd = new FileLoaderPage();
	private FilesInformationPage ipd = new FilesInformationPage();
	private FilesInformationPage2 ipd2 = new FilesInformationPage2();
	private ColumnPage cpd=new ColumnPage();
	private ColumnPage2 cpd2= new ColumnPage2();
	private FileMergePage fmp= new FileMergePage();
	private File mergedFile;
	
	private PvDesktop desktop;
	
	public DatasetLoadingScreen (PvDesktop pvDesktop)
	{
		this.desktop = pvDesktop;

		getDialog().setTitle ("MiPaSt import wizard");

       registerWizardPanel(fpd);
       registerWizardPanel(ipd);
       registerWizardPanel(ipd2);
       registerWizardPanel(cpd);
       registerWizardPanel(cpd2);
       registerWizardPanel(fmp);
       
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
	private boolean checkBox=false;
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
			final JButton geneBrowse = new JButton("Browse");
			
			// miRNA
			miRNAText = new JTextField();
			JLabel miRNALabel = new JLabel("miRNA Dataset");
			
			mainPanel.add(miRNALabel, cc.xy(1, 3));
			mainPanel.add(miRNAText, cc.xywh(2, 3,3,1));
			mainPanel.add(miRNABrowse, cc.xy(6, 3));
			
			// gene
			JLabel geneLabel= new JLabel("Transcriptomics Dataset");
			JCheckBox geneBox= new JCheckBox("Transcriptomics available");
			geneBrowse.setEnabled(checkBox);
			geneText = new JTextField();
			geneText.setEnabled(checkBox);
			
			mainPanel.add(geneBox,cc.xy(1, 5));
			
			mainPanel.add(geneLabel, cc.xy(1, 7));
			mainPanel.add(geneText, cc.xywh(2, 7,3,1));
			mainPanel.add(geneBrowse, cc.xy(6, 7));
			


			
			miRNABrowse.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					JFileChooser fc = new JFileChooser();
					fc.addChoosableFileFilter(new SimpleFileFilter("Data files", "*.txt|*.csv", true));
					int returnVal = fc.showDialog(null, "Open miRNA Datasetfile");
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						miRNAFile = fc.getSelectedFile();
						miRNAText.setText(miRNAFile.getAbsolutePath());
						
						try {
							miRNAData = MiPaStFileReader.readFile(miRNAFile);
							if(miRNAData.size() > 0) {
								System.out.println(miRNAData.size());
								miRNAFileLoaded=true;
								dataLoaded=true;
								System.out.println(dataLoaded);
								checkLoaded();
								
								
								
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
					fc.addChoosableFileFilter(new SimpleFileFilter("Data files", "*.txt|*.csv", true));
					int returnVal = fc.showDialog(null, "Open Transcriptomics datasetfile");
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						geneFile = fc.getSelectedFile();
						geneText.setText(geneFile.getAbsolutePath());
						
						try {
							geneData = MiPaStFileReader.readFile(geneFile);
							if(geneData.size() > 0) {
								geneFileLoaded=true;
								dataLoaded=true;
								System.out.println(dataLoaded);
								
								checkLoaded();
								
							}
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						
						
					}
				}
			});
			
			
			
			geneBox.addChangeListener(new ChangeListener(){

				@Override
				public void stateChanged(ChangeEvent ae) {
					if(checkBox ==false){
					checkBox =true;
					dataLoaded = false;
					getWizard().setNextFinishButtonEnabled(dataLoaded);
					geneBrowse.setEnabled(checkBox);
					geneText.setEnabled(checkBox);
					}
					
				
				}
				
			
					
				});
				
		
			return mainPanel;
		}
		
		public void checkLoaded() throws IOException{
			if (miRNAFileLoaded == true && geneFileLoaded != true){
				miRNAImportInformation.setTxtFile(miRNAFile);
			}
			if (miRNAFileLoaded ==true && geneFileLoaded == true){
				miRNAImportInformation.setTxtFile(miRNAFile);
				geneImportInformation.setTxtFile(geneFile);
			}
			getWizard().setNextFinishButtonEnabled(dataLoaded);
		}
		
		
		public void aboutToDisplayPanel()
		{
			getWizard().setNextFinishButtonEnabled(dataLoaded);
			getWizard().setPageTitle ("Choose file locations");
		}

	    public FileLoaderPage()
	    {
	        super(IDENTIFIER);
	    }

	    public Object getNextPanelDescriptor()
	    {
	        return "miRNA_INFORMATIONPAGE_PAGE";//HeaderPage.IDENTIFIER;
	    }

	    public Object getBackPanelDescriptor()
	    {
	        return null;
	    }
	}
	
	/** Set information for the miRNA expression data file*/
	private class FilesInformationPage extends WizardPanelDescriptor implements ActionListener {
		public static final String IDENTIFIER = "miRNA_INFORMATIONPAGE_PAGE";
		
		private JRadioButton seperatorTab;
		private JRadioButton seperatorComma;
		private JRadioButton seperatorSemi;
		private JRadioButton seperatorSpace;
		private JRadioButton seperatorOther;
		
		private PreviewTableModel prevTable;
		private JTable tblPreview;
		
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
			FormLayout layout = new FormLayout (
		    		"pref, 3dlu, pref, 3dlu, pref, pref:grow",
		    		"p, 3dlu, p, 3dlu, p, 15dlu, fill:[100dlu,min]:grow");

		    PanelBuilder builder = new PanelBuilder(layout);
		    builder.setDefaultDialogBorder();

		    CellConstraints cc = new CellConstraints();

			seperatorTab = new JRadioButton ("tab");
			seperatorComma = new JRadioButton ("comma");
			seperatorSemi = new JRadioButton ("semicolon");
			seperatorSpace = new JRadioButton ("space");
			seperatorOther = new JRadioButton ("other");
			ButtonGroup bgSeparator = new ButtonGroup();
			bgSeparator.add (seperatorTab);
			bgSeparator.add (seperatorComma);
			bgSeparator.add (seperatorSemi);
			bgSeparator.add (seperatorSpace);
			bgSeparator.add (seperatorOther);

			builder.add (seperatorTab, cc.xy(1,1));
			builder.add (seperatorComma, cc.xy(1,3));
			builder.add (seperatorSemi, cc.xy(1,5));
			builder.add (seperatorSpace, cc.xy(3,1));
			builder.add (seperatorOther, cc.xy(3,3));

			final JTextField txtOther = new JTextField(3);
			builder.add (txtOther, cc.xy(5, 3));

			prevTable = new PreviewTableModel(miRNAImportInformation);
			tblPreview = new JTable(prevTable);
			tblPreview.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			JScrollPane scrTable = new JScrollPane(tblPreview);

			builder.add (scrTable, cc.xyw(1,7,6));

			txtOther.addActionListener(new ActionListener () {

				public void actionPerformed(ActionEvent arg0)
				{
					miRNAImportInformation.setDelimiter (txtOther.getText());
					miRNAImportInformation.guessSettings();
					prevTable.refresh();
					seperatorOther.setSelected (true);
				}


			})
			;

			seperatorComma.addActionListener(new ActionListener()
			{
				public void actionPerformed (ActionEvent ae)
				{
					miRNAImportInformation.setDelimiter(",");
					prevTable.refresh();
				}

			});
			seperatorTab.addActionListener(new ActionListener()
			{
				public void actionPerformed (ActionEvent ae)
				{
					miRNAImportInformation.setDelimiter("\t");
					prevTable.refresh();
				}

			});
			seperatorSemi.addActionListener(new ActionListener()
			{
				public void actionPerformed (ActionEvent ae)
				{
					miRNAImportInformation.setDelimiter(";");
					prevTable.refresh();
				}
			});
			seperatorSpace.addActionListener(new ActionListener()
			{
				public void actionPerformed (ActionEvent ae)
				{
					miRNAImportInformation.setDelimiter(" ");
					prevTable.refresh();
				}

			});

			return builder.getPanel();
			
		}
		public void aboutToDisplayPanel()
		{
	        ///getWizard().setNextFinishButtonEnabled(dataLoaded);
			
			getWizard().setPageTitle ("Choose data delimiter for miRNA file");

	    	prevTable.refresh(); //<- doesn't work somehow
	    	String del = miRNAImportInformation.getDelimiter();
	    	if (del.equals ("\t"))
	    	{
	    		seperatorTab.setSelected(true);
	    	}
	    	else if (del.equals (","))
			{
	    		seperatorComma.setSelected(true);
			}
	    	else if (del.equals (";"))
			{
	    		seperatorSemi.setSelected(true);
			}
	    	else if (del.equals (" "))
			{
	    		seperatorSpace.setSelected(true);
			}
	    	else
	    	{
	    		seperatorOther.setSelected (true);
	    	}
	    }
	
		
		public Object getNextPanelDescriptor()
	    {
	        return "miRNA_COLUMN_PAGE";}
		public Object getBackPanelDescriptor()
	    {
	        return "FILE_PAGE";
	        
	    }
	}
	
	/** Set Column information for the miRNA expression data*/
	
	private class ColumnPage extends WizardPanelDescriptor
	{
	    public static final String IDENTIFIER = "miRNA_COLUMN_PAGE";

	    private ColumnTableModel ctm;
		private JTable tblColumn;

	    private JComboBox cbColId;
	    private JComboBox cbColSyscode;
	    private JRadioButton rbFixedNo;
	    private JRadioButton rbFixedYes;
	    private JComboBox cbDataSource;
	    private DataSourceModel miRNADataSource;

	    public ColumnPage()
	    {
	        super(IDENTIFIER);
	    }

	    public Object getNextPanelDescriptor()
	    {
	    	if (checkBox == true){
	        return "gene_INFORMATIONPAGE_PAGE";}
	    	else{return null;}
	    }

	    public Object getBackPanelDescriptor()
	    {
	        return "miRNA_INFORMATIONPAGE_PAGE";
	    }

	    @Override
		protected JPanel createContents()
		{
		    FormLayout layout = new FormLayout (
		    		"pref, 7dlu, pref:grow",
		    		"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, fill:[100dlu,min]:grow");

		    PanelBuilder builder = new PanelBuilder(layout);
		    builder.setDefaultDialogBorder();

		    CellConstraints cc = new CellConstraints();

			rbFixedNo = new JRadioButton("Select a column to specify system code");
			rbFixedYes = new JRadioButton("Use the same system code for all rows");
			ButtonGroup bgSyscodeCol = new ButtonGroup ();
			bgSyscodeCol.add (rbFixedNo);
			bgSyscodeCol.add (rbFixedYes);

			cbColId = new JComboBox();
			cbColSyscode = new JComboBox();

			miRNADataSource = new DataSourceModel();
			String[] types = {"metabolite","protein","gene","interaction","probe"};
			miRNADataSource.setTypeFilter(types);
			cbDataSource = new PermissiveComboBox(miRNADataSource);

			ctm = new ColumnTableModel(miRNAImportInformation);
			tblColumn = new JTable(ctm);
			tblColumn.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			tblColumn.setDefaultRenderer(Object.class, ctm.getTableCellRenderer());
			tblColumn.setCellSelectionEnabled(false);

			tblColumn.getTableHeader().addMouseListener(new ColumnPopupListener());
			JTable rowHeader = new RowNumberHeader(tblColumn);
			rowHeader.addMouseListener(new RowPopupListener());
			JScrollPane scrTable = new JScrollPane(tblColumn);

			JViewport jv = new JViewport();
		    jv.setView(rowHeader);
		    jv.setPreferredSize(rowHeader.getPreferredSize());
		    scrTable.setRowHeader(jv);
//		    scrTable.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, rowHeader
//		            .getTableHeader());

			builder.addLabel ("Select primary identifier column:", cc.xy(1,1));
			builder.add (cbColId, cc.xy(3,1));

			builder.add (rbFixedNo, cc.xyw(1,3,3));
			builder.add (cbColSyscode, cc.xy(3,5));
			builder.add (rbFixedYes, cc.xyw (1,7,3));
			builder.add (cbDataSource, cc.xy (3,9));

			builder.add (scrTable, cc.xyw(1,11,3));

			ActionListener rbAction = new ActionListener() {
				public void actionPerformed (ActionEvent ae)
				{
					boolean result = (ae.getSource() == rbFixedYes);
					miRNAImportInformation.setSyscodeFixed(result);
			    	columnPageRefresh();
				}
			};
			rbFixedYes.addActionListener(rbAction);
			rbFixedNo.addActionListener(rbAction);

			miRNADataSource.addListDataListener(new ListDataListener()
			{
				public void contentsChanged(ListDataEvent arg0)
				{
					miRNAImportInformation.setDataSource(miRNADataSource.getSelectedDataSource());
				}

				public void intervalAdded(ListDataEvent arg0) {}

				public void intervalRemoved(ListDataEvent arg0) {}
			});

			cbColSyscode.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae)
				{
					miRNAImportInformation.setSysodeColumn(cbColSyscode.getSelectedIndex());
					columnPageRefresh();
				}
			});
			cbColId.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae)
				{
					miRNAImportInformation.setIdColumn(cbColId.getSelectedIndex());
			    	columnPageRefresh();
				}
			});
			return builder.getPanel();
		}

	    private class ColumnPopupListener extends MouseAdapter
	    {
	    	@Override public void mousePressed (MouseEvent e)
			{
				showPopup(e);
			}

			@Override public void mouseReleased (MouseEvent e)
			{
				showPopup(e);
			}

			int clickedCol;

			private void showPopup(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					JPopupMenu popup;
					popup = new JPopupMenu();
					clickedCol = tblColumn.columnAtPoint(e.getPoint());
					if (clickedCol != miRNAImportInformation.getSyscodeColumn())
						popup.add(new SyscodeColAction());
					if (clickedCol != miRNAImportInformation.getIdColumn())
						popup.add(new IdColAction());
					popup.show(e.getComponent(),
							e.getX(), e.getY());
				}
			}

			private class SyscodeColAction extends AbstractAction
			{
				public SyscodeColAction()
				{
					putValue(Action.NAME, "SystemCode column");
				}

				public void actionPerformed(ActionEvent arg0)
				{
					// if id and code column are about to be the same, swap them
					if (clickedCol == miRNAImportInformation.getIdColumn())
						miRNAImportInformation.setIdColumn(miRNAImportInformation.getSyscodeColumn());
					miRNAImportInformation.setSysodeColumn(clickedCol);
					columnPageRefresh();
				}
			}

			private class IdColAction extends AbstractAction
			{
				public IdColAction()
				{
					putValue(Action.NAME, "Identifier column");
				}

				public void actionPerformed(ActionEvent arg0)
				{
					// if id and code column are about to be the same, swap them
					if (clickedCol == miRNAImportInformation.getSyscodeColumn())
						miRNAImportInformation.setSysodeColumn(miRNAImportInformation.getIdColumn());
					miRNAImportInformation.setIdColumn(clickedCol);
					columnPageRefresh();
				}
			}
	    }

	    private class RowPopupListener extends MouseAdapter
	    {
	    	@Override public void mousePressed (MouseEvent e)
			{
				showPopup(e);
			}

			@Override public void mouseReleased (MouseEvent e)
			{
				showPopup(e);
			}

			int clickedRow;

			private void showPopup(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					JPopupMenu popup;
					popup = new JPopupMenu();
					clickedRow = tblColumn.rowAtPoint(e.getPoint());
					popup.add(new DataStartAction());
					popup.add(new HeaderStartAction());
					popup.show(e.getComponent(),
							e.getX(), e.getY());
				}
			}

			private class DataStartAction extends AbstractAction
			{
				public DataStartAction()
				{
					putValue(Action.NAME, "First data row");
				}

				public void actionPerformed(ActionEvent arg0)
				{
					miRNAImportInformation.setFirstDataRow(clickedRow);
					columnPageRefresh();
				}
			}

			private class HeaderStartAction extends AbstractAction
			{
				public HeaderStartAction()
				{
					putValue(Action.NAME, "First header row");
				}

				public void actionPerformed(ActionEvent arg0)
				{
					miRNAImportInformation.setFirstHeaderRow(clickedRow);
					columnPageRefresh();
				}
			}

	    }

	    private void columnPageRefresh()
	    {
	    	String error = null;
			if (miRNAImportInformation.isSyscodeFixed())
			{
				rbFixedYes.setSelected (true);
				cbColSyscode.setEnabled (false);
				cbDataSource.setEnabled (true);
			}
			else
			{
				rbFixedNo.setSelected (true);
				cbColSyscode.setEnabled (true);
				cbDataSource.setEnabled (false);

				if (miRNAImportInformation.getIdColumn() == miRNAImportInformation.getSyscodeColumn())
	    		{
	    			error = "System code column and Id column can't be the same";
	    		}
			}
		    getWizard().setNextFinishButtonEnabled(error == null);
		    getWizard().setErrorMessage(error == null ? "" : error);
			getWizard().setPageTitle ("Choose column types");

	    	ctm.refresh();
	    }

	    private void refreshComboBoxes()
	    {
	    	miRNADataSource.setSelectedItem(miRNAImportInformation.getDataSource());
			cbColId.setSelectedIndex(miRNAImportInformation.getIdColumn());
			cbColSyscode.setSelectedIndex(miRNAImportInformation.getSyscodeColumn());
	    }

	    /**
	     * A simple cell Renderer for combo boxes that use the
	     * column index integer as value,
	     * but will display the column name String
	     */
	    private class ColumnNameRenderer extends JLabel implements ListCellRenderer
	    {
			public ColumnNameRenderer()
			{
				setOpaque(true);
				setHorizontalAlignment(CENTER);
				setVerticalAlignment(CENTER);
			}

			/*
			* This method finds the image and text corresponding
			* to the selected value and returns the label, set up
			* to display the text and image.
			*/
			public Component getListCellRendererComponent(
			                        JList list,
			                        Object value,
			                        int index,
			                        boolean isSelected,
			                        boolean cellHasFocus)
			{
				//Get the selected index. (The index param isn't
				//always valid, so just use the value.)
				int selectedIndex = ((Integer)value).intValue();

				if (isSelected)
				{
					setBackground(list.getSelectionBackground());
					setForeground(list.getSelectionForeground());
				} else {
					setBackground(list.getBackground());
					setForeground(list.getForeground());
				}

				String[] cn = miRNAImportInformation.getColNames();
				String column = cn[selectedIndex];
				setText(column);
				setFont(list.getFont());

				return this;
			}
		}

	    public void aboutToDisplayPanel()
	    {
	    	// create an array of size getSampleMaxNumCols()
	    	Integer[] cn;
	    	int max = miRNAImportInformation.getSampleMaxNumCols();
    		cn = new Integer[max];
    		for (int i = 0; i < max; ++i) cn[i] = i;

	    	cbColId.setRenderer(new ColumnNameRenderer());
	    	cbColSyscode.setRenderer(new ColumnNameRenderer());
	    	cbColId.setModel(new DefaultComboBoxModel(cn));
	    	cbColSyscode.setModel(new DefaultComboBoxModel(cn));

			columnPageRefresh();
			refreshComboBoxes();

	    	ctm.refresh();
	    }

	    @Override
	    public void aboutToHidePanel()
	    {
	    	miRNAImportInformation.setSyscodeFixed(rbFixedYes.isSelected());
	    	if (rbFixedYes.isSelected())
	    	{
		    	miRNAImportInformation.setDataSource(miRNADataSource.getSelectedDataSource());
	    	}
	    }
	}
/** Set the information for the gene expression file*/
	
	private class FilesInformationPage2 extends WizardPanelDescriptor implements ActionListener {
		public static final String IDENTIFIER = "gene_INFORMATIONPAGE_PAGE";
		
		private JRadioButton seperatorTab;
		private JRadioButton seperatorComma;
		private JRadioButton seperatorSemi;
		private JRadioButton seperatorSpace;
		private JRadioButton seperatorOther;
		
		private PreviewTableModel prevTable;
		private JTable tblPreview;
		
		public FilesInformationPage2() {
			super(IDENTIFIER);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected Component createContents() {
			FormLayout layout = new FormLayout (
		    		"pref, 3dlu, pref, 3dlu, pref, pref:grow",
		    		"p, 3dlu, p, 3dlu, p, 15dlu, fill:[100dlu,min]:grow");

		    PanelBuilder builder = new PanelBuilder(layout);
		    builder.setDefaultDialogBorder();

		    CellConstraints cc = new CellConstraints();

			seperatorTab = new JRadioButton ("tab");
			seperatorComma = new JRadioButton ("comma");
			seperatorSemi = new JRadioButton ("semicolon");
			seperatorSpace = new JRadioButton ("space");
			seperatorOther = new JRadioButton ("other");
			ButtonGroup bgSeparator = new ButtonGroup();
			bgSeparator.add (seperatorTab);
			bgSeparator.add (seperatorComma);
			bgSeparator.add (seperatorSemi);
			bgSeparator.add (seperatorSpace);
			bgSeparator.add (seperatorOther);

			builder.add (seperatorTab, cc.xy(1,1));
			builder.add (seperatorComma, cc.xy(1,3));
			builder.add (seperatorSemi, cc.xy(1,5));
			builder.add (seperatorSpace, cc.xy(3,1));
			builder.add (seperatorOther, cc.xy(3,3));

			final JTextField txtOther = new JTextField(3);
			builder.add (txtOther, cc.xy(5, 3));

			prevTable = new PreviewTableModel(geneImportInformation);
			tblPreview = new JTable(prevTable);
			tblPreview.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			JScrollPane scrTable = new JScrollPane(tblPreview);

			builder.add (scrTable, cc.xyw(1,7,6));

			txtOther.addActionListener(new ActionListener () {

				public void actionPerformed(ActionEvent arg0)
				{
					geneImportInformation.setDelimiter (txtOther.getText());
					geneImportInformation.guessSettings();
					prevTable.refresh();
					seperatorOther.setSelected (true);
				}


			})
			;

			seperatorComma.addActionListener(new ActionListener()
			{
				public void actionPerformed (ActionEvent ae)
				{
					geneImportInformation.setDelimiter(",");
					prevTable.refresh();
				}

			});
			seperatorTab.addActionListener(new ActionListener()
			{
				public void actionPerformed (ActionEvent ae)
				{
					geneImportInformation.setDelimiter("\t");
					prevTable.refresh();
				}

			});
			seperatorSemi.addActionListener(new ActionListener()
			{
				public void actionPerformed (ActionEvent ae)
				{
					geneImportInformation.setDelimiter(";");
					prevTable.refresh();
				}
			});
			seperatorSpace.addActionListener(new ActionListener()
			{
				public void actionPerformed (ActionEvent ae)
				{
					geneImportInformation.setDelimiter(" ");
					prevTable.refresh();
				}

			});

			return builder.getPanel();
			
		}
		public void aboutToDisplayPanel()
		{
	        ///getWizard().setNextFinishButtonEnabled(dataLoaded);
			
			getWizard().setPageTitle ("Choose data delimiter for genes file");

	    	prevTable.refresh(); //<- doesn't work somehow
	    	String del = geneImportInformation.getDelimiter();
	    	if (del.equals ("\t"))
	    	{
	    		seperatorTab.setSelected(true);
	    	}
	    	else if (del.equals (","))
			{
	    		seperatorComma.setSelected(true);
			}
	    	else if (del.equals (";"))
			{
	    		seperatorSemi.setSelected(true);
			}
	    	else if (del.equals (" "))
			{
	    		seperatorSpace.setSelected(true);
			}
	    	else
	    	{
	    		seperatorOther.setSelected (true);
	    	}
	    }
	
		
		public Object getNextPanelDescriptor()
	    {
	        return "gene_COLUMN_PAGE";}
		public Object getBackPanelDescriptor()
	    {
	        return "miRNA_COLUMN_PAGE";
	    }
	}
	
	private class ColumnPage2 extends WizardPanelDescriptor
	{
	    public static final String IDENTIFIER = "gene_COLUMN_PAGE";

	    private ColumnTableModel ctm;
		private JTable tblColumn;

	    private JComboBox cbColId;
	    private JComboBox cbColSyscode;
	    private JRadioButton rbFixedNo;
	    private JRadioButton rbFixedYes;
	    private JComboBox cbDataSource;
	    private DataSourceModel geneDataSource;

	    public ColumnPage2()
	    {
	        super(IDENTIFIER);
	    }

	    public Object getNextPanelDescriptor()
	    {
	        return "FILE_MERGE_PAGE";
	    }

	    public Object getBackPanelDescriptor()
	    {
	        return "gene_INFORMATIONPAGE_PAGE";
	    }

	    @Override
		protected JPanel createContents()
		{
		    FormLayout layout = new FormLayout (
		    		"pref, 7dlu, pref:grow",
		    		"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, fill:[100dlu,min]:grow");

		    PanelBuilder builder = new PanelBuilder(layout);
		    builder.setDefaultDialogBorder();

		    CellConstraints cc = new CellConstraints();

			rbFixedNo = new JRadioButton("Select a column to specify system code");
			rbFixedYes = new JRadioButton("Use the same system code for all rows");
			ButtonGroup bgSyscodeCol = new ButtonGroup ();
			bgSyscodeCol.add (rbFixedNo);
			bgSyscodeCol.add (rbFixedYes);

			cbColId = new JComboBox();
			cbColSyscode = new JComboBox();

			geneDataSource = new DataSourceModel();
			String[] types = {"metabolite","protein","gene","interaction","probe"};
			geneDataSource.setTypeFilter(types);
			cbDataSource = new PermissiveComboBox(geneDataSource);

			ctm = new ColumnTableModel(geneImportInformation);
			tblColumn = new JTable(ctm);
			tblColumn.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			tblColumn.setDefaultRenderer(Object.class, ctm.getTableCellRenderer());
			tblColumn.setCellSelectionEnabled(false);

			tblColumn.getTableHeader().addMouseListener(new ColumnPopupListener());
			JTable rowHeader = new RowNumberHeader(tblColumn);
			rowHeader.addMouseListener(new RowPopupListener());
			JScrollPane scrTable = new JScrollPane(tblColumn);

			JViewport jv = new JViewport();
		    jv.setView(rowHeader);
		    jv.setPreferredSize(rowHeader.getPreferredSize());
		    scrTable.setRowHeader(jv);
//		    scrTable.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, rowHeader
//		            .getTableHeader());

			builder.addLabel ("Select primary identifier column:", cc.xy(1,1));
			builder.add (cbColId, cc.xy(3,1));

			builder.add (rbFixedNo, cc.xyw(1,3,3));
			builder.add (cbColSyscode, cc.xy(3,5));
			builder.add (rbFixedYes, cc.xyw (1,7,3));
			builder.add (cbDataSource, cc.xy (3,9));

			builder.add (scrTable, cc.xyw(1,11,3));

			ActionListener rbAction = new ActionListener() {
				public void actionPerformed (ActionEvent ae)
				{
					boolean result = (ae.getSource() == rbFixedYes);
					geneImportInformation.setSyscodeFixed(result);
			    	columnPageRefresh();
				}
			};
			rbFixedYes.addActionListener(rbAction);
			rbFixedNo.addActionListener(rbAction);

			geneDataSource.addListDataListener(new ListDataListener()
			{
				public void contentsChanged(ListDataEvent arg0)
				{
					geneImportInformation.setDataSource(geneDataSource.getSelectedDataSource());
				}

				public void intervalAdded(ListDataEvent arg0) {}

				public void intervalRemoved(ListDataEvent arg0) {}
			});

			cbColSyscode.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae)
				{
					geneImportInformation.setSysodeColumn(cbColSyscode.getSelectedIndex());
					columnPageRefresh();
				}
			});
			cbColId.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae)
				{
					geneImportInformation.setIdColumn(cbColId.getSelectedIndex());
			    	columnPageRefresh();
				}
			});
			return builder.getPanel();
		}

	    private class ColumnPopupListener extends MouseAdapter
	    {
	    	@Override public void mousePressed (MouseEvent e)
			{
				showPopup(e);
			}

			@Override public void mouseReleased (MouseEvent e)
			{
				showPopup(e);
			}

			int clickedCol;

			private void showPopup(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					JPopupMenu popup;
					popup = new JPopupMenu();
					clickedCol = tblColumn.columnAtPoint(e.getPoint());
					if (clickedCol != geneImportInformation.getSyscodeColumn())
						popup.add(new SyscodeColAction());
					if (clickedCol != geneImportInformation.getIdColumn())
						popup.add(new IdColAction());
					popup.show(e.getComponent(),
							e.getX(), e.getY());
				}
			}

			private class SyscodeColAction extends AbstractAction
			{
				public SyscodeColAction()
				{
					putValue(Action.NAME, "SystemCode column");
				}

				public void actionPerformed(ActionEvent arg0)
				{
					// if id and code column are about to be the same, swap them
					if (clickedCol == geneImportInformation.getIdColumn())
						geneImportInformation.setIdColumn(geneImportInformation.getSyscodeColumn());
					geneImportInformation.setSysodeColumn(clickedCol);
					columnPageRefresh();
				}
			}

			private class IdColAction extends AbstractAction
			{
				public IdColAction()
				{
					putValue(Action.NAME, "Identifier column");
				}

				public void actionPerformed(ActionEvent arg0)
				{
					// if id and code column are about to be the same, swap them
					if (clickedCol == geneImportInformation.getSyscodeColumn())
						geneImportInformation.setSysodeColumn(geneImportInformation.getIdColumn());
					geneImportInformation.setIdColumn(clickedCol);
					columnPageRefresh();
				}
			}
	    }

	    private class RowPopupListener extends MouseAdapter
	    {
	    	@Override public void mousePressed (MouseEvent e)
			{
				showPopup(e);
			}

			@Override public void mouseReleased (MouseEvent e)
			{
				showPopup(e);
			}

			int clickedRow;

			private void showPopup(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					JPopupMenu popup;
					popup = new JPopupMenu();
					clickedRow = tblColumn.rowAtPoint(e.getPoint());
					popup.add(new DataStartAction());
					popup.add(new HeaderStartAction());
					popup.show(e.getComponent(),
							e.getX(), e.getY());
				}
			}

			private class DataStartAction extends AbstractAction
			{
				public DataStartAction()
				{
					putValue(Action.NAME, "First data row");
				}

				public void actionPerformed(ActionEvent arg0)
				{
					geneImportInformation.setFirstDataRow(clickedRow);
					columnPageRefresh();
				}
			}

			private class HeaderStartAction extends AbstractAction
			{
				public HeaderStartAction()
				{
					putValue(Action.NAME, "First header row");
				}

				public void actionPerformed(ActionEvent arg0)
				{
					geneImportInformation.setFirstHeaderRow(clickedRow);
					columnPageRefresh();
				}
			}

	    }

	    private void columnPageRefresh()
	    {
	    	String error = null;
			if (geneImportInformation.isSyscodeFixed())
			{
				rbFixedYes.setSelected (true);
				cbColSyscode.setEnabled (false);
				cbDataSource.setEnabled (true);
			}
			else
			{
				rbFixedNo.setSelected (true);
				cbColSyscode.setEnabled (true);
				cbDataSource.setEnabled (false);

				if (geneImportInformation.getIdColumn() == geneImportInformation.getSyscodeColumn())
	    		{
	    			error = "System code column and Id column can't be the same";
	    		}
			}
		    getWizard().setNextFinishButtonEnabled(error == null);
		    getWizard().setErrorMessage(error == null ? "" : error);
			getWizard().setPageTitle ("Choose column types");

	    	ctm.refresh();
	    }

	    private void refreshComboBoxes()
	    {
	    	geneDataSource.setSelectedItem(geneImportInformation.getDataSource());
			cbColId.setSelectedIndex(geneImportInformation.getIdColumn());
			cbColSyscode.setSelectedIndex(geneImportInformation.getSyscodeColumn());
	    }

	    /**
	     * A simple cell Renderer for combo boxes that use the
	     * column index integer as value,
	     * but will display the column name String
	     */
	    private class ColumnNameRenderer extends JLabel implements ListCellRenderer
	    {
			public ColumnNameRenderer()
			{
				setOpaque(true);
				setHorizontalAlignment(CENTER);
				setVerticalAlignment(CENTER);
			}

			/*
			* This method finds the image and text corresponding
			* to the selected value and returns the label, set up
			* to display the text and image.
			*/
			public Component getListCellRendererComponent(
			                        JList list,
			                        Object value,
			                        int index,
			                        boolean isSelected,
			                        boolean cellHasFocus)
			{
				//Get the selected index. (The index param isn't
				//always valid, so just use the value.)
				int selectedIndex = ((Integer)value).intValue();

				if (isSelected)
				{
					setBackground(list.getSelectionBackground());
					setForeground(list.getSelectionForeground());
				} else {
					setBackground(list.getBackground());
					setForeground(list.getForeground());
				}

				String[] cn = geneImportInformation.getColNames();
				String column = cn[selectedIndex];
				setText(column);
				setFont(list.getFont());

				return this;
			}
		}

	    public void aboutToDisplayPanel()
	    {
	    	// create an array of size getSampleMaxNumCols()
	    	Integer[] cn;
	    	int max = geneImportInformation.getSampleMaxNumCols();
    		cn = new Integer[max];
    		for (int i = 0; i < max; ++i) cn[i] = i;

	    	cbColId.setRenderer(new ColumnNameRenderer());
	    	cbColSyscode.setRenderer(new ColumnNameRenderer());
	    	cbColId.setModel(new DefaultComboBoxModel(cn));
	    	cbColSyscode.setModel(new DefaultComboBoxModel(cn));

			columnPageRefresh();
			refreshComboBoxes();

	    	ctm.refresh();
	    }

	    @Override
	    public void aboutToHidePanel()
	    {
	    	geneImportInformation.setSyscodeFixed(rbFixedYes.isSelected());
	    	if (rbFixedYes.isSelected())
	    	{
		    	geneImportInformation.setDataSource(geneDataSource.getSelectedDataSource());
	    	}
	    }
	}
	
	private class FileMergePage extends WizardPanelDescriptor implements ProgressListener
	{
		public static final String IDENTIFIER = "FILE_MERGE_PAGE";
		public FileMergePage() {
			super(IDENTIFIER);
			
		}
		public Object getNextPanelDescriptor()
	    {
	        return null;
	    }

	    public Object getBackPanelDescriptor()
	    {
	        return "gene_COLUMN_PAGE";
	    }

	    private JProgressBar progressSent;
	    private JTextArea progressText;
	    private ProgressKeeper pk;
	    private JLabel lblTask;
		@Override
		public void progressEvent(ProgressEvent e) {
			switch(e.getType())
			{
				case ProgressEvent.FINISHED:
					progressSent.setValue(pk.getTotalWork());
				case ProgressEvent.TASK_NAME_CHANGED:
					lblTask.setText(pk.getTaskName());
					break;
				case ProgressEvent.REPORT:
					progressText.append(e.getProgressKeeper().getReport() + "\n");
					break;
				case ProgressEvent.PROGRESS_CHANGED:
					progressSent.setValue(pk.getProgress());
					break;
			}
			
		}

		@Override
		protected Component createContents() {
			FormLayout layout = new FormLayout(
	    			"fill:[100dlu,min]:grow",
	    			"pref, pref, fill:pref:grow"
	    	);

	    	DefaultFormBuilder builder = new DefaultFormBuilder(layout);
	    	builder.setDefaultDialogBorder();

        	pk = new ProgressKeeper((int)1E6);
        	pk.addListener(this);
			progressSent = new JProgressBar(0, pk.getTotalWork());
	        builder.append(progressSent);
	        builder.nextLine();
	        lblTask = new JLabel();
	        builder.append(lblTask);

	        progressText = new JTextArea();

			builder.append(new JScrollPane(progressText));
			return builder.getPanel();
			
		}
		
		public void setProgressValue(int i)
	    {
	        progressSent.setValue(i);
	    }

	    public void setProgressText(String msg)
	    {
	        progressText.setText(msg);
	    }

	    public void aboutToDisplayPanel()
	    {
			getWizard().setPageTitle ("Perform import");
	        setProgressValue(0);
	        setProgressText("");

	        getWizard().setNextFinishButtonEnabled(false);
	        getWizard().setBackButtonEnabled(false);
	    }
	    
	    public void displayingPanel()
	    {
			SwingWorker<File, File> sw = new SwingWorker<File, File>() {
				@Override protected File doInBackground() throws Exception {
					pk.setTaskName("Merging data expression files");
					FileMerger fm= new FileMerger();
					mergedFile=fm.fileMerger(miRNAData, geneData);
					
					 {
						pk.finished();
					}
					return mergedFile;
				}

				@Override public void done()
				{
					getWizard().setNextFinishButtonEnabled(true);
					getWizard().setBackButtonEnabled(true);
				}
			};
			sw.execute();
	    }
	}
}
	
	
	

	


	



