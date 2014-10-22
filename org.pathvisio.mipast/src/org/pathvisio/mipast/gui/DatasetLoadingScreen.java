//Copyright 2014 BiGCaT
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.

package org.pathvisio.mipast.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.SwingWorker;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.bridgedb.gui.SimpleFileFilter;

import org.pathvisio.core.debug.Logger;

import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.core.util.ProgressKeeper.ProgressEvent;
import org.pathvisio.core.util.ProgressKeeper.ProgressListener;

import org.pathvisio.desktop.PvDesktop;

import org.pathvisio.desktop.util.RowNumberHeader;
import org.pathvisio.gexplugin.GexTxtImporter;
import org.pathvisio.gexplugin.ImportInformation;
import org.pathvisio.gui.DataSourceModel;
import org.pathvisio.gui.util.PermissiveComboBox;
import org.pathvisio.mipast.io.ColumnTableModel;
import org.pathvisio.mipast.io.FileMerger;
import org.pathvisio.mipast.io.PreviewTableModel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.nexes.wizard.Wizard;
import com.nexes.wizard.WizardPanelDescriptor;



import org.pathvisio.rip.dialog.LoadFileWizard;
import org.pathvisio.rip.dialog.ColumnPage;
import org.pathvisio.rip.dialog.FilePage;
import org.pathvisio.rip.dialog.ImportPage;


/**
 * 
 * @author ChrOertlin
 * 
 *         This class opens and displays the GUI for the data loading of the
 *         miRNA and Transcriptomics Dataset
 * 
 */
public class DatasetLoadingScreen extends Wizard {
	
	
	private ImportInformation miRNAImportInformation = new ImportInformation();
	private ImportInformation geneImportInformation = new ImportInformation();
	private ImportInformation combinedImportInformation = new ImportInformation();
	private FileLoaderPage fpd = new FileLoaderPage();
	private MiRNAFilesInformationPage ipd = new MiRNAFilesInformationPage();
	private GeneFilesInformationPage ipd2 = new GeneFilesInformationPage();
	private MiRNAColumnPage cpd = new MiRNAColumnPage();
	private GeneColumnPage cpd2 = new GeneColumnPage();
	private FileMergePage fmp = new FileMergePage();
	private RipInfoPage ripi = new RipInfoPage();
	private LoadFileWizard wizard = new LoadFileWizard(null);
	private FilePage ripf;
	private RipColumnPage ripc= new RipColumnPage();
	private ImportPage ripim;

	private final PvDesktop standaloneEngine;

	public DatasetLoadingScreen(PvDesktop pvDesktop) {
		this.standaloneEngine = pvDesktop;
		//ripf= new FilePage(wizard,pvDesktop);
		
		//ripim= new ImportPage(wizard,pvDesktop);
		
		getDialog().setTitle("MiPaSt import wizard");
		//registerWizardPanel(ripf);
		//registerWizardPanel(ripc);
		//registerWizardPanel(ripim);
		registerWizardPanel(fpd);
		registerWizardPanel(ipd);
		registerWizardPanel(ipd2);
		registerWizardPanel(cpd);
		registerWizardPanel(cpd2);
		registerWizardPanel(fmp);
		registerWizardPanel(ripi);
	
		
		setCurrentPanel(FileLoaderPage.IDENTIFIER);
	}

	private File miRNAFile;
	private File geneFile;

	private class FileLoaderPage extends WizardPanelDescriptor implements
			ActionListener {
		public static final String IDENTIFIER = "FILE_PAGE";
		private JCheckBox geneBox;
		private boolean miRNAFileLoaded = false;
		private boolean geneFileLoaded = false;
		private JButton geneBrowse;
		private JTextField miRNAText;
		private JTextField geneText;

		@Override
		public void actionPerformed(ActionEvent arg0) {
		}

		@Override
		protected Component createContents() {
			CellConstraints cc = new CellConstraints();
			FormLayout layout = new FormLayout(
					"pref,50dlu,pref,50dlu,50dlu,pref,default",
					"8dlu, pref,15dlu,pref,15dlu,pref,4dlu,pref,4dlu,pref,150dlu,pref,4dlu");
			PanelBuilder builder = new PanelBuilder(layout);

			JLabel screenLabel = new JLabel("Load your datasets");
			builder.add(screenLabel, cc.xy(1, 2));

			builder.addSeparator("", cc.xyw(1, 3, 6));

			// miRNA
			miRNAText = new JTextField();
			JLabel miRNALabel = new JLabel("miRNA Dataset");
			JButton miRNABrowse = new JButton("Browse");
			builder.add(miRNALabel, cc.xy(1, 4));
			builder.add(miRNAText, cc.xywh(2, 4, 3, 1));
			builder.add(miRNABrowse, cc.xy(6, 4));

			builder.addSeparator("", cc.xyw(1, 5, 6));

			// gene
			JLabel geneLabel = new JLabel("Transcriptomics Dataset");
			geneBox = new JCheckBox("Transcriptomics available");

			geneBrowse = new JButton("Browse");
			geneBrowse.setEnabled(geneBox.isSelected());
			geneText = new JTextField();
			geneText.setEnabled(geneBox.isSelected());

			builder.add(geneBox, cc.xy(1, 6));

			builder.add(geneLabel, cc.xy(1, 8));
			builder.add(geneText, cc.xywh(2, 8, 3, 1));
			builder.add(geneBrowse, cc.xy(6, 8));

			miRNABrowse.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {

					JFileChooser fc = new JFileChooser();
					fc.addChoosableFileFilter(new SimpleFileFilter(
							"Data files", "*.txt|*.csv", true));
					int returnVal = fc.showDialog(null,
							"Open miRNA Datasetfile");
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						try {
							miRNAFile = fc.getSelectedFile();
							miRNAText.setText(miRNAFile.getAbsolutePath());

							miRNAFileLoaded = true;

							miRNAImportInformation.setTxtFile(miRNAFile);

							if (geneBox.isSelected() && geneFileLoaded) {
								getWizard().setNextFinishButtonEnabled(true);
							} else if (!geneBox.isSelected()) {
								getWizard().setNextFinishButtonEnabled(true);
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
					fc.addChoosableFileFilter(new SimpleFileFilter(
							"Data files", "*.txt|*.csv", true));
					int returnVal = fc.showDialog(null,
							"Open Transcriptomics datasetfile");
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						geneFile = fc.getSelectedFile();
						try {
							geneText.setText(geneFile.getAbsolutePath());
							geneFileLoaded = true;
							geneImportInformation.setTxtFile(geneFile);
							if (miRNAFileLoaded) {
								getWizard().setNextFinishButtonEnabled(true);
							}
						} catch (IOException e2) {
						
							e2.printStackTrace();
						}
					}
				}
			});

			geneBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					geneBrowse.setEnabled(geneBox.isSelected());
					geneText.setEnabled(geneBox.isSelected());
					if (!geneBox.isSelected()) {
						geneFile = null;
						geneText.setText("");
						geneFileLoaded = false;
						if (miRNAFileLoaded) {
							getWizard().setNextFinishButtonEnabled(true);
						}
					} else {
						getWizard().setNextFinishButtonEnabled(false);
					}
				}
			});

			return builder.getPanel();
		}

		public void aboutToDisplayPanel() {
			if (!standaloneEngine.getSwingEngine().getGdbManager()
					.isConnected()) {
				databaseLoaded();

			}

			getWizard().setPageTitle("Choose file locations");
			if (miRNAFileLoaded
					&& ((geneBox.isSelected() && geneFileLoaded) || !geneBox
							.isSelected())) {
				getWizard().setNextFinishButtonEnabled(true);
			} else {
				getWizard().setNextFinishButtonEnabled(false);

			}
		}

		public FileLoaderPage() {
			super(IDENTIFIER);
		}

		public Object getNextPanelDescriptor() {
			return "miRNA_INFORMATIONPAGE_PAGE";// HeaderPage.IDENTIFIER;
		}

		public Object getBackPanelDescriptor() {
			return null;
		}

		public void databaseLoaded() {

			String message = "Please load a gene database: pathvisio/data/select gene database";
			JOptionPane.showMessageDialog(new JFrame(), message, "Dialog",
					JOptionPane.ERROR_MESSAGE);
			getWizard().setNextFinishButtonEnabled(false);

		}
	}

	/**
	 * Set information for the miRNA expression data file
	 */
	private class MiRNAFilesInformationPage extends WizardPanelDescriptor
			implements ActionListener {
		public static final String IDENTIFIER = "miRNA_INFORMATIONPAGE_PAGE";

		private JRadioButton seperatorTab;
		private JRadioButton seperatorComma;
		private JRadioButton seperatorSemi;
		private JRadioButton seperatorSpace;
		private JRadioButton seperatorOther;
		private JLabel fileName;
		private PreviewTableModel prevTable;
		private JTable tblPreview;

		public MiRNAFilesInformationPage() {
			super(IDENTIFIER);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
		}

		@Override
		protected Component createContents() {
			FormLayout layout = new FormLayout(
					"pref, 3dlu,pref, 3dlu, pref, 3dlu, pref, pref:grow",
					"p,3dlu,p, 3dlu, p, 3dlu, p, 15dlu, fill:[100dlu,min]:grow");

			PanelBuilder builder = new PanelBuilder(layout);
			builder.setDefaultDialogBorder();

			CellConstraints cc = new CellConstraints();
			fileName = new JLabel();
			seperatorTab = new JRadioButton("tab");
			seperatorComma = new JRadioButton("comma");
			seperatorSemi = new JRadioButton("semicolon");
			seperatorSpace = new JRadioButton("space");
			seperatorOther = new JRadioButton("other");
			ButtonGroup bgSeparator = new ButtonGroup();
			bgSeparator.add(seperatorTab);
			bgSeparator.add(seperatorComma);
			bgSeparator.add(seperatorSemi);
			bgSeparator.add(seperatorSpace);
			bgSeparator.add(seperatorOther);

			builder.add(fileName, cc.xy(1, 1));
			builder.add(seperatorTab, cc.xy(1, 3));
			builder.add(seperatorComma, cc.xy(1, 5));
			builder.add(seperatorSemi, cc.xy(1, 7));
			builder.add(seperatorSpace, cc.xy(3, 3));
			builder.add(seperatorOther, cc.xy(3, 5));

			final JTextField txtOther = new JTextField(3);
			builder.add(txtOther, cc.xy(5, 3));

			prevTable = new PreviewTableModel(miRNAImportInformation);
			tblPreview = new JTable(prevTable);
			tblPreview.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			JScrollPane scrTable = new JScrollPane(tblPreview);

			builder.add(scrTable, cc.xyw(1, 9, 8));

			txtOther.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					miRNAImportInformation.setDelimiter(txtOther.getText());
					miRNAImportInformation.guessSettings();
					prevTable.refresh();
					seperatorOther.setSelected(true);
				}
			});

			seperatorComma.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					miRNAImportInformation.setDelimiter(",");
					prevTable.refresh();
				}
			});
			seperatorTab.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					miRNAImportInformation.setDelimiter("\t");
					prevTable.refresh();
				}
			});
			seperatorSemi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					miRNAImportInformation.setDelimiter(";");
					prevTable.refresh();
				}
			});
			seperatorSpace.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					miRNAImportInformation.setDelimiter(" ");
					prevTable.refresh();
				}
			});

			return builder.getPanel();
		}

		public void aboutToDisplayPanel() {

			fileName.setText(miRNAFile.getName());
			getWizard().setPageTitle("Choose data delimiter for miRNA file:");

			prevTable.refresh(); // <- doesn't work somehow
			String del = miRNAImportInformation.getDelimiter();
			if (del.equals("\t")) {
				seperatorTab.setSelected(true);

			} else if (del.equals(",")) {
				seperatorComma.setSelected(true);
			} else if (del.equals(";")) {
				seperatorSemi.setSelected(true);
			} else if (del.equals(" ")) {
				seperatorSpace.setSelected(true);
			} else {
				seperatorOther.setSelected(true);
			}
		}

		public Object getNextPanelDescriptor() {
			return "miRNA_COLUMN_PAGE";
		}

		public Object getBackPanelDescriptor() {
			return "FILE_PAGE";
		}
	}

	/**
	 * Set Column information for the miRNA expression data
	 */
	private class MiRNAColumnPage extends WizardPanelDescriptor {
		public static final String IDENTIFIER = "miRNA_COLUMN_PAGE";

		private ColumnTableModel ctm;
		private JTable tblColumn;

		private JComboBox cbIdCol;
		private JComboBox cbSyscodeCol;
		private JRadioButton rbSyscodeCol;
		private JRadioButton rbDatabaseAll;
		private JComboBox cbDataSource;
		private DataSourceModel miRNADataSource;

		public MiRNAColumnPage() {
			super(IDENTIFIER);
		}

		public Object getNextPanelDescriptor() {
			if (geneFile == null) {
				return "FILE_MERGE_PAGE";
			} else {
				return "gene_INFORMATIONPAGE_PAGE";
			}

		}

		public Object getBackPanelDescriptor() {
			return "miRNA_INFORMATIONPAGE_PAGE";
		}

		@Override
		protected JPanel createContents() {
			FormLayout layout = new FormLayout("5dlu, pref, 7dlu, pref:grow",
					"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, fill:[100dlu,min]:grow");

			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();

			// id column
			builder.addLabel("ID column:", cc.xy(2, 1));
			cbIdCol = new JComboBox();
			builder.add(cbIdCol, cc.xy(4, 1));

			// sys code button group
			ButtonGroup bgSyscodeCol = new ButtonGroup();

			// not fixed system code

			miRNADataSource = new DataSourceModel();
			String[] types = { "protein", "gene", "probe" };
			miRNADataSource.setTypeFilter(types);

			cbDataSource = new PermissiveComboBox(miRNADataSource);

			// system code column
			rbDatabaseAll = new JRadioButton("Select database for all Rows:");
			bgSyscodeCol.add(rbDatabaseAll);
			builder.add(rbDatabaseAll, cc.xy(2, 3));
			builder.add(cbDataSource, cc.xy(4, 3));

			rbSyscodeCol = new JRadioButton("Select system code column:");
			cbSyscodeCol = new JComboBox();
			bgSyscodeCol.add(rbSyscodeCol);
			builder.add(cbSyscodeCol, cc.xy(4, 5));
			builder.add(rbSyscodeCol, cc.xy(2, 5));

			ctm = new ColumnTableModel(miRNAImportInformation);
			tblColumn = new JTable(ctm);
			tblColumn.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			tblColumn.setDefaultRenderer(Object.class,
					ctm.getTableCellRenderer());
			tblColumn.setCellSelectionEnabled(false);

			JTable rowHeader = new RowNumberHeader(tblColumn);
			JScrollPane scrTable = new JScrollPane(tblColumn);

			JViewport jv = new JViewport();
			jv.setView(rowHeader);
			jv.setPreferredSize(rowHeader.getPreferredSize());
			scrTable.setRowHeader(jv);

			builder.add(scrTable, cc.xyw(1, 11, 4));

			ActionListener rbAction = new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					boolean result = (ae.getSource() == rbDatabaseAll);
					miRNAImportInformation.setSyscodeFixed(result);
					columnPageRefresh();
				}
			};
			rbDatabaseAll.addActionListener(rbAction);
			rbSyscodeCol.addActionListener(rbAction);

			miRNADataSource.addListDataListener(new ListDataListener() {
				public void contentsChanged(ListDataEvent arg0) {
					miRNAImportInformation.setDataSource(miRNADataSource
							.getSelectedDataSource());

					columnPageRefresh();
				}

				public void intervalAdded(ListDataEvent arg0) {
				}

				public void intervalRemoved(ListDataEvent arg0) {
				}
			});

			cbSyscodeCol.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					miRNAImportInformation.setSysodeColumn(cbSyscodeCol
							.getSelectedIndex());
					System.out.println(miRNAImportInformation
							.getSyscodeColumn());
					columnPageRefresh();
				}
			});
			cbIdCol.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					miRNAImportInformation.setIdColumn(cbIdCol
							.getSelectedIndex());
					columnPageRefresh();
				}
			});
			return builder.getPanel();
		}

		private void columnPageRefresh() {
			getWizard().setPageTitle("Choose column types");

			if (miRNAImportInformation.isSyscodeFixed()) {
				rbDatabaseAll.setSelected(true);
				cbSyscodeCol.setEnabled(false);
				cbDataSource.setEnabled(true);
			} else {
				rbSyscodeCol.setSelected(true);
				cbSyscodeCol.setEnabled(true);
				cbDataSource.setEnabled(false);
			}

			if (miRNAImportInformation.isSyscodeFixed()) {
				getWizard().setNextFinishButtonEnabled(true);
			} else {
				if (miRNAImportInformation.getDataSource() != null) {
					getWizard().setNextFinishButtonEnabled(true);
				}
			}

			// getWizard().setNextFinishButtonEnabled(error == null);
			// getWizard().setErrorMessage(error == null ? "" : error);

			ctm.refresh();
		}

		private void refreshComboBoxes() {
			miRNADataSource.setSelectedItem(miRNAImportInformation
					.getDataSource());
			cbIdCol.setSelectedIndex(miRNAImportInformation.getIdColumn());
			cbSyscodeCol.setSelectedIndex(miRNAImportInformation
					.getSyscodeColumn());
		}

		/**
		 * A simple cell Renderer for combo boxes that use the column index
		 * integer as value, but will display the column name String
		 */
		private class ColumnNameRenderer extends JLabel implements
				ListCellRenderer {
			public ColumnNameRenderer() {
				setOpaque(true);
				setHorizontalAlignment(CENTER);
				setVerticalAlignment(CENTER);
			}

			/*
			 * This method finds the image and text corresponding to the
			 * selected value and returns the label, set up to display the text
			 * and image.
			 */
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				// Get the selected index. (The index param isn't
				// always valid, so just use the value.)
				int selectedIndex = ((Integer) value).intValue();

				if (isSelected) {
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

		public void aboutToDisplayPanel() {
			miRNAImportInformation.setSyscodeFixed(true);
			getWizard().setNextFinishButtonEnabled(false);

			// create an array of size getSampleMaxNumCols()
			Integer[] columns;
			int max = miRNAImportInformation.getSampleMaxNumCols();
			columns = new Integer[max];
			for (int i = 0; i < max; ++i)
				columns[i] = i;

			cbIdCol.setRenderer(new ColumnNameRenderer());
			cbSyscodeCol.setRenderer(new ColumnNameRenderer());
			cbIdCol.setModel(new DefaultComboBoxModel(columns));
			cbSyscodeCol.setModel(new DefaultComboBoxModel(columns));
			columnPageRefresh();
			refreshComboBoxes();

			ctm.refresh();
		}

		@Override
		public void aboutToHidePanel() {

			miRNAImportInformation.setSyscodeFixed(rbDatabaseAll.isSelected());
			if (!rbDatabaseAll.isSelected()) {
				miRNAImportInformation.setDataSource(miRNADataSource
						.getSelectedDataSource());
			}
		}
	}

	/**
	 * Set the data delimiter for the gene expression file.
	 */
	private class GeneFilesInformationPage extends WizardPanelDescriptor
			implements ActionListener {
		public static final String IDENTIFIER = "gene_INFORMATIONPAGE_PAGE";

		private JRadioButton seperatorTab;
		private JRadioButton seperatorComma;
		private JRadioButton seperatorSemi;
		private JRadioButton seperatorSpace;
		private JRadioButton seperatorOther;
		private JLabel fileName;
		private PreviewTableModel prevTable;
		private JTable tblPreview;

		public GeneFilesInformationPage() {
			super(IDENTIFIER);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		protected Component createContents() {
			FormLayout layout = new FormLayout(
					"pref, 3dlu,pref, 3dlu, pref, 3dlu, pref, pref:grow",
					"p,3dlu,p, 3dlu, p, 3dlu, p, 15dlu, fill:[100dlu,min]:grow");

			PanelBuilder builder = new PanelBuilder(layout);
			builder.setDefaultDialogBorder();

			CellConstraints cc = new CellConstraints();

			fileName = new JLabel();
			seperatorTab = new JRadioButton("tab");
			seperatorComma = new JRadioButton("comma");
			seperatorSemi = new JRadioButton("semicolon");
			seperatorSpace = new JRadioButton("space");
			seperatorOther = new JRadioButton("other");
			ButtonGroup bgSeparator = new ButtonGroup();
			bgSeparator.add(seperatorTab);
			bgSeparator.add(seperatorComma);
			bgSeparator.add(seperatorSemi);
			bgSeparator.add(seperatorSpace);
			bgSeparator.add(seperatorOther);

			builder.add(fileName, cc.xy(1, 1));
			builder.add(seperatorTab, cc.xy(1, 3));
			builder.add(seperatorComma, cc.xy(1, 5));
			builder.add(seperatorSemi, cc.xy(1, 7));
			builder.add(seperatorSpace, cc.xy(3, 3));
			builder.add(seperatorOther, cc.xy(3, 5));

			final JTextField txtOther = new JTextField(3);
			builder.add(txtOther, cc.xy(5, 3));

			prevTable = new PreviewTableModel(geneImportInformation);
			tblPreview = new JTable(prevTable);
			tblPreview.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			JScrollPane scrTable = new JScrollPane(tblPreview);

			builder.add(scrTable, cc.xyw(1, 9, 8));

			txtOther.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					geneImportInformation.setDelimiter(txtOther.getText());
					geneImportInformation.guessSettings();
					prevTable.refresh();
					seperatorOther.setSelected(true);
				}
			});

			seperatorComma.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					geneImportInformation.setDelimiter(",");
					prevTable.refresh();
				}
			});

			seperatorTab.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					geneImportInformation.setDelimiter("\t");
					prevTable.refresh();
				}
			});

			seperatorSemi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					geneImportInformation.setDelimiter(";");
					prevTable.refresh();
				}
			});

			seperatorSpace.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					geneImportInformation.setDelimiter(" ");
					prevTable.refresh();
				}
			});

			return builder.getPanel();
		}

		public void aboutToDisplayPanel() {

			getWizard().setPageTitle("Choose data delimiter for genes file");
			fileName.setText(geneFile.getName());
			prevTable.refresh(); // <- doesn't work somehow
			String del = geneImportInformation.getDelimiter();
			if (del.equals("\t")) {
				seperatorTab.setSelected(true);
			} else if (del.equals(",")) {
				seperatorComma.setSelected(true);
			} else if (del.equals(";")) {
				seperatorSemi.setSelected(true);
			} else if (del.equals(" ")) {
				seperatorSpace.setSelected(true);
			} else {
				seperatorOther.setSelected(true);
			}
		}

		public Object getNextPanelDescriptor() {
			return "gene_COLUMN_PAGE";
		}

		public Object getBackPanelDescriptor() {
			return "miRNA_COLUMN_PAGE";
		}
	}

	/**
	 * Columnpage2 is used to specificy the database or the systemCode column
	 * for the gene expression data.
	 * 
	 * @author ChrOertlin
	 * 
	 */

	private class GeneColumnPage extends WizardPanelDescriptor {
		public static final String IDENTIFIER = "gene_COLUMN_PAGE";

		private ColumnTableModel ctm;
		private JTable tblColumn;
		String error = null;
		private JComboBox cbIdCol;
		private JComboBox cbSyscodeCol;
		private JRadioButton rbDatabaseAll;
		private JRadioButton rbSyscodeCol;
		private JComboBox cbDataSource;
		private DataSourceModel geneDataSource;
		private boolean dataSourceSelected = false;

		public GeneColumnPage() {
			super(IDENTIFIER);
		}

		public Object getNextPanelDescriptor() {
			return "FILE_MERGE_PAGE";
		}

		public Object getBackPanelDescriptor() {
			return "gene_INFORMATIONPAGE_PAGE";
		}

		@Override
		protected JPanel createContents() {
			FormLayout layout = new FormLayout("5dlu, pref, 7dlu, pref:grow",
					"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, fill:[100dlu,min]:grow");

			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();

			// id column
			builder.addLabel("ID column:", cc.xy(2, 1));
			cbIdCol = new JComboBox();
			builder.add(cbIdCol, cc.xy(4, 1));

			// sys code button group
			ButtonGroup bgSyscodeCol = new ButtonGroup();

			// not fixed system code

			geneDataSource = new DataSourceModel();
			String[] types = { "protein", "gene", "probe" };
			geneDataSource.setTypeFilter(types);

			cbDataSource = new PermissiveComboBox(geneDataSource);
			rbDatabaseAll = new JRadioButton("Select database for all Rows:");
			bgSyscodeCol.add(rbDatabaseAll);
			builder.add(rbDatabaseAll, cc.xy(2, 3));
			builder.add(cbDataSource, cc.xy(4, 3));

			// system code column
			rbSyscodeCol = new JRadioButton("Select system code column:");
			bgSyscodeCol.add(rbSyscodeCol);
			cbSyscodeCol = new JComboBox();
			builder.add(cbSyscodeCol, cc.xy(4, 5));
			builder.add(rbSyscodeCol, cc.xy(2, 5));

			ctm = new ColumnTableModel(geneImportInformation);
			tblColumn = new JTable(ctm);
			tblColumn.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			tblColumn.setDefaultRenderer(Object.class,
					ctm.getTableCellRenderer());
			tblColumn.setCellSelectionEnabled(false);

			JTable rowHeader = new RowNumberHeader(tblColumn);
			JScrollPane scrTable = new JScrollPane(tblColumn);

			JViewport jv = new JViewport();
			jv.setView(rowHeader);
			jv.setPreferredSize(rowHeader.getPreferredSize());
			scrTable.setRowHeader(jv);

			builder.add(scrTable, cc.xyw(1, 11, 4));

			ActionListener rbAction = new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					boolean result = (ae.getSource() == rbDatabaseAll);
					geneImportInformation.setSyscodeFixed(result);
					columnPageRefresh();
				}
			};
			rbDatabaseAll.addActionListener(rbAction);
			rbSyscodeCol.addActionListener(rbAction);

			geneDataSource.addListDataListener(new ListDataListener() {
				public void contentsChanged(ListDataEvent arg0) {
					geneImportInformation.setDataSource(geneDataSource
							.getSelectedDataSource());

					columnPageRefresh();
				}

				public void intervalAdded(ListDataEvent arg0) {
				}

				public void intervalRemoved(ListDataEvent arg0) {
				}
			});

			cbSyscodeCol.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					geneImportInformation.setSysodeColumn(cbSyscodeCol
							.getSelectedIndex());
					System.out.println(geneImportInformation.getSyscodeColumn());
					columnPageRefresh();
				}
			});
			cbIdCol.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					geneImportInformation.setIdColumn(cbIdCol
							.getSelectedIndex());
					columnPageRefresh();
				}
			});
			return builder.getPanel();
		}

		private void columnPageRefresh() {
			getWizard().setPageTitle("Choose column types");

			if (geneImportInformation.isSyscodeFixed()) {
				rbDatabaseAll.setSelected(true);
				cbSyscodeCol.setEnabled(false);
				cbDataSource.setEnabled(true);
			} else {
				rbSyscodeCol.setSelected(true);
				cbSyscodeCol.setEnabled(true);
				cbDataSource.setEnabled(false);
			}

			if (geneImportInformation.isSyscodeFixed()) {
				getWizard().setNextFinishButtonEnabled(true);
			} else {
				if (geneImportInformation.getDataSource() != null) {
					getWizard().setNextFinishButtonEnabled(true);
				}
			}

			ctm.refresh();
		}

		private void refreshComboBoxes() {
			geneDataSource.setSelectedItem(geneImportInformation
					.getDataSource());
			cbIdCol.setSelectedIndex(geneImportInformation.getIdColumn());
			cbSyscodeCol.setSelectedIndex(geneImportInformation
					.getSyscodeColumn());
		}

		/**
		 * A simple cell Renderer for combo boxes that use the column index
		 * integer as value, but will display the column name String
		 */
		private class ColumnNameRenderer extends JLabel implements
				ListCellRenderer {
			public ColumnNameRenderer() {
				setOpaque(true);
				setHorizontalAlignment(CENTER);
				setVerticalAlignment(CENTER);
			}

			/*
			 * This method finds the image and text corresponding to the
			 * selected value and returns the label, set up to display the text
			 * and image.
			 */
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				// Get the selected index. (The index param isn't
				// always valid, so just use the value.)
				int selectedIndex = ((Integer) value).intValue();

				if (isSelected) {
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

		public void aboutToDisplayPanel() {
			geneImportInformation.setSyscodeFixed(true);
			getWizard().setNextFinishButtonEnabled(false);

			// create an array of size getSampleMaxNumCols()
			Integer[] columns;
			int max = geneImportInformation.getSampleMaxNumCols();
			columns = new Integer[max];
			for (int i = 0; i < max; ++i)
				columns[i] = i;

			cbIdCol.setRenderer(new ColumnNameRenderer());
			cbSyscodeCol.setRenderer(new ColumnNameRenderer());
			cbIdCol.setModel(new DefaultComboBoxModel(columns));
			cbSyscodeCol.setModel(new DefaultComboBoxModel(columns));
			columnPageRefresh();
			refreshComboBoxes();

			ctm.refresh();
		}

		@Override
		public void aboutToHidePanel() {

			geneImportInformation.setSyscodeFixed(rbDatabaseAll.isSelected());
			if (!rbDatabaseAll.isSelected()) {
				geneImportInformation.setDataSource(geneDataSource
						.getSelectedDataSource());
			}
		}
	}

	/**
	 * Filemerger page used to merge the two different expression data files and
	 * import them into PathVisio. In case of one expression data file, import
	 * starts directly.
	 * 
	 * @author ChrOertlin
	 * 
	 */
	private class FileMergePage extends WizardPanelDescriptor implements
			ProgressListener {
		public static final String IDENTIFIER = "FILE_MERGE_PAGE";

		public FileMergePage() {
			super(IDENTIFIER);

		}

		public Object getNextPanelDescriptor() {
			return "RIP_INFO_PAGE";
		}

		public Object getBackPanelDescriptor() {
			return "gene_COLUMN_PAGE";
		}

		private JProgressBar progressSent;
		private JTextArea progressText;
		private ProgressKeeper pk;
		private JLabel lblTask;
		private FileMerger fm = new FileMerger();

		protected JPanel createContents() {
			FormLayout layout = new FormLayout("fill:[100dlu,min]:grow",
					"pref, pref, fill:pref:grow");

			DefaultFormBuilder builder = new DefaultFormBuilder(layout);
			builder.setDefaultDialogBorder();

			pk = new ProgressKeeper((int) 1E6);
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

		public void setProgressValue(int i) {
			progressSent.setValue(i);
		}

		public void setProgressText(String msg) {
			progressText.setText(msg);
		}

		@Override
		public void aboutToCancel() {
			// let the progress keeper know that the user pressed cancel.
			pk.cancel();
		}

		public void aboutToDisplayPanel() {
			getWizard().setPageTitle("Perform import");
			setProgressValue(0);
			setProgressText("");

			getWizard().setNextFinishButtonEnabled(false);
			getWizard().setBackButtonEnabled(false);
		}

		public void displayingPanel() {

			SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					if (geneImportInformation.getTxtFile() != null) {
						try {
							pk.setTaskName("Merging expression data files");
							combinedImportInformation.setTxtFile(fm
									.createCombinedFile(miRNAImportInformation,
											geneImportInformation));
							if (combinedImportInformation.getTxtFile() != null
									&& fm.getSharedHeader()) {
								pk.report("Expression data files sucesscully merged.\n"
										+ "Combined file created:\n"
										+ fm.createCombinedFile(
												miRNAImportInformation,
												geneImportInformation)
												.getAbsolutePath());
							} else {
								pk.report("Expression data files sucesscully merged.\n"
										+ "Combined file created:\n"
										+ fm.createCombinedFile(
												miRNAImportInformation,
												geneImportInformation)
												.getAbsolutePath()
										+ "\n"
										+ "No shared headers found, no shared visualization possible! \n");
							}

							combinedImportInformation.setDelimiter("/t");
							combinedImportInformation.setIdColumn(0);
							combinedImportInformation.setSyscodeFixed(true);
							combinedImportInformation.setSysodeColumn(1);

							standaloneEngine.getGexManager().setCurrentGex(
									combinedImportInformation.getTxtFile()
											.getName(), true);

							combinedImportInformation
									.setGexName(combinedImportInformation
											.getTxtFile().getAbsolutePath());
							pk.setTaskName("Importing expression dataset file(s)");

							GexTxtImporter.importFromTxt(
									combinedImportInformation, pk,
									standaloneEngine.getSwingEngine()
											.getGdbManager().getCurrentGdb(),
									standaloneEngine.getGexManager());

						} catch (Exception e) {
							Logger.log.error("During import", e);
							setProgressValue(0);
							setProgressText("An Error Has Occurred: "
									+ e.getMessage()
									+ "\nSee the log for details");
							e.printStackTrace();

							getWizard().setBackButtonEnabled(true);
						} finally {
							pk.finished();
						}
						return null;
					} else {

						try {

							standaloneEngine.getGexManager().setCurrentGex(
									miRNAFile.getName(), true);
							pk.setTaskName("Importing expression dataset file(s)");
							miRNAImportInformation
									.setGexName(miRNAImportInformation
											.getTxtFile().getAbsolutePath());
							GexTxtImporter.importFromTxt(
									miRNAImportInformation, pk,
									standaloneEngine.getSwingEngine()
											.getGdbManager().getCurrentGdb(),
									standaloneEngine.getGexManager());
							System.out.print("tried");

						} catch (Exception e) {
							Logger.log.error("During import", e);
							setProgressValue(0);
							setProgressText("An Error Has Occurred: "
									+ e.getMessage()
									+ "\nSee the log for details");

							getWizard().setBackButtonEnabled(true);
						} finally {
							pk.finished();
						}
						return null;
					}

				}

				@Override
				public void done() {
					getWizard().setNextFinishButtonEnabled(true);
					getWizard().setBackButtonEnabled(true);
				}
			};
			sw.execute();
		}

		@Override
		public void progressEvent(ProgressEvent e) {
			switch (e.getType()) {
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
	}

	private class RipInfoPage extends WizardPanelDescriptor {
		private static final String IDENTIFIER = "RIP_INFO_PAGE";

		public RipInfoPage() {
			super(IDENTIFIER);

		}
		public Object getNextPanelDescriptor() {
			return ColumnPage.IDENTIFIER;
		}

		public Object getBackPanelDescriptor() {
			return "FILE_MERGE_PAGE";
		}

		@Override
		protected Component createContents() {
			FormLayout layout = new FormLayout("pref", "pref");

			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();

			JLabel ripInfo = new JLabel(
					"Step 2: Load interaction file(s); press Next");
			builder.add(ripInfo, cc.xy(1, 1));
			return builder.getPanel();
		}

		public void aboutToDisplayPanel() {
		
			getWizard().setNextFinishButtonEnabled(true);
			getWizard().setBackButtonEnabled(true);
		}

	}
	private class RipColumnPage{
		
		
	}


}
