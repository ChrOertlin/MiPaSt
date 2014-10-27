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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
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
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;

import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.gui.SimpleFileFilter;
import org.bridgedb.rdb.construct.DBConnector;

import org.pathvisio.core.data.GdbManager;
import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.debug.StopWatch;

import org.pathvisio.core.preferences.GlobalPreference;
import org.pathvisio.core.preferences.PreferenceManager;
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
import org.pathvisio.mipast.io.RipColumnTableModel;
import org.pathvisio.mipast.util.LoadFileWizard;
import org.pathvisio.mipast.util.RipImportInformation;
import org.pathvisio.rip.Interaction;
import org.pathvisio.rip.RegIntPlugin;
import org.pathvisio.rip.dialog.ColumnPage;
import org.pathvisio.rip.preferences.RIPreferences;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.nexes.wizard.Wizard;
import com.nexes.wizard.WizardPanelDescriptor;

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
	private StartInfoPage sip = new StartInfoPage();
	private FileLoaderPage fpd = new FileLoaderPage();
	private MiRNAFilesInformationPage ipd = new MiRNAFilesInformationPage();
	private GeneFilesInformationPage ipd2 = new GeneFilesInformationPage();
	private MiRNAColumnPage cpd = new MiRNAColumnPage();
	private GeneColumnPage cpd2 = new GeneColumnPage();
	private FileMergePage fmp = new FileMergePage();
	private RipInfoPage ripi = new RipInfoPage();
	private RipFilePage ripf = new RipFilePage();
	private RipColumnPage ripc = new RipColumnPage();
	private RipImportPage ripim = new RipImportPage();
	private RipImportInformation importInformation = new RipImportInformation();
	private LoadFileWizard wizard = new LoadFileWizard();

	private final PvDesktop standaloneEngine;

	public DatasetLoadingScreen(PvDesktop pvDesktop) {
		this.standaloneEngine = pvDesktop;

		getDialog().setTitle("MiPaSt import wizard");

		registerWizardPanel(sip);
		registerWizardPanel(fpd);
		registerWizardPanel(ipd);
		registerWizardPanel(ipd2);
		registerWizardPanel(cpd);
		registerWizardPanel(cpd2);
		registerWizardPanel(fmp);
		registerWizardPanel(ripi);
		registerWizardPanel(ripf);
		registerWizardPanel(ripc);
		registerWizardPanel(ripim);

		setCurrentPanel(StartInfoPage.IDENTIFIER);
	}

	private File miRNAFile;
	private File geneFile;

	private class StartInfoPage extends WizardPanelDescriptor {
		private static final String IDENTIFIER = "START_INFO_PAGE";

		public StartInfoPage() {
			super(IDENTIFIER);

		}

		public Object getNextPanelDescriptor() {
			return FileLoaderPage.IDENTIFIER;
		}

		public Object getBackPanelDescriptor() {
			return null;
		}

		@Override
		protected Component createContents() {
			FormLayout layout = new FormLayout("pref", "pref");

			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();

			JLabel ripInfo = new JLabel(
					"Step 1: Load interaction miRNA and Transcriptomics file(s); press next for the import wizard");
			builder.add(ripInfo, cc.xy(1, 1));
			return builder.getPanel();
		}

		public void aboutToDisplayPanel() {
			getWizard().setNextFinishButtonEnabled(true);
			getWizard().setBackButtonEnabled(true);
			System.out.print(standaloneEngine.getSwingEngine().getGdbManager()
					.getGeneDb());
			// if (!standaloneEngine.getSwingEngine().getGdbManager()
			// .isConnected() ||
			// !standaloneEngine.getSwingEngine().getGdbManager().getGeneDb().toString().contains("Hs"))
			// {
			//
			// //databaseLoaded();
			//
			// }

			getWizard().setPageTitle("MiPaSt plugin");

		}

		// public void databaseLoaded() {
		//
		// String message =
		// "Please load a human gene database: pathvisio/data/select gene database";
		// JOptionPane.showMessageDialog(new JFrame(), message, "Dialog",
		// JOptionPane.ERROR_MESSAGE);
		// getWizard().setNextFinishButtonEnabled(false);

		// }

	}

	private class FileLoaderPage extends WizardPanelDescriptor implements
			ActionListener {
		public static final String IDENTIFIER = "FILE_PAGE";
		private JCheckBox geneBox;
		private boolean miRNAFileLoaded = false;
		private boolean geneFileLoaded = false;
		private JButton geneBrowse;
		private JButton dbBrowse;
		private JTextField miRNAText;
		private JTextField geneText;
		static final String ACTION_INPUT = "input";
		static final String ACTION_OUTPUT = "output";
		static final String ACTION_GDB = "gdb";

		@Override
		public void actionPerformed(ActionEvent arg0) {
		}

		@Override
		protected Component createContents() {
			CellConstraints cc = new CellConstraints();
			FormLayout layout = new FormLayout(
					"pref,50dlu,pref,50dlu,50dlu,pref,default",
					"8dlu, pref,15dlu,pref,15dlu,pref,4dlu,pref,4dlu,pref,150dlu,pref,4dlu,pref");
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
			builder.addSeparator("", cc.xyw(1, 10, 6));

			// database
			JLabel dbLabel = new JLabel("Load gene database");
			dbBrowse = new JButton("Browse");
			final JTextField dbText = new JTextField();
			builder.add(dbLabel, cc.xy(1, 12));
			builder.add(dbText, cc.xywh(2, 12, 3, 1));
			builder.add(dbBrowse, cc.xy(6, 12));
			dbBrowse.setActionCommand(ACTION_GDB);

			miRNABrowse.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					File defaultdir = PreferenceManager.getCurrent().getFile(GlobalPreference.DIR_LAST_USED_EXPRESSION_IMPORT);
					JFileChooser fc = new JFileChooser();
					fc.setCurrentDirectory(defaultdir);
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
							defaultdir = fc.getCurrentDirectory();
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

			dbBrowse.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String action = e.getActionCommand();

					if (ACTION_GDB.equals(action)) {
						standaloneEngine.selectGdb("Gene");
						dbText.setText(PreferenceManager.getCurrent().get(
								GlobalPreference.DB_CONNECTSTRING_GDB));

					} else if (ACTION_OUTPUT.equals(action)) {
						try {
							DBConnector dbConn = standaloneEngine
									.getGexManager().getDBConnector();

						} catch (Exception ex) {
							JOptionPane.showMessageDialog(getPanelComponent(),
									"The database connector is not supported"

							);
							Logger.log.error("No gex database connector", ex);
						}
					}
				}
			});

			return builder.getPanel();
		}

		public void aboutToDisplayPanel() {

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
			return StartInfoPage.IDENTIFIER;
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
			return "RIP_FILE_PAGE";
		}

		public Object getBackPanelDescriptor() {
			return "FILE_MERGE_PAGE";
		}

		@Override
		protected Component createContents() {
			FormLayout layout = new FormLayout("pref", "pref,pref,pref");

			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();

			JLabel ripInfo = new JLabel(
					"Step 2: Load interaction file(s); press Next");
			builder.addSeparator("", cc.xyw(1, 1, 1));
			builder.add(ripInfo, cc.xy(1, 2));
			builder.addSeparator("", cc.xyw(1, 3, 1));
			return builder.getPanel();
		}

		public void aboutToDisplayPanel() {

			getWizard().setNextFinishButtonEnabled(true);
			getWizard().setBackButtonEnabled(true);
		}

	}

	private class RipFilePage extends WizardPanelDescriptor {

		private final static String IDENTIFIER = "RIP_FILE_PAGE";

		public RipFilePage() {
			super(IDENTIFIER);

		}

		static final String ACTION_INPUT = "input";

		private JTextField txtInput;

		private JButton btnInput;
		private boolean txtFileComplete = false;
		private List<RipImportInformation> impInfoList = new ArrayList<RipImportInformation>();

		private void updateTxtFile() {
			String fileName = txtInput.getText();
			String[] buffer = fileName.split("; ");
			if (buffer.length != 0) {
				boolean exists = true;
				for (int i = 0; i < buffer.length; i++) {
					if (exists) {
						if (!buffer[i].equals("")) {
							File file = new File(buffer[i]);
							if (!file.exists()) {
								exists = false;
							}
						}
					}
				}
				if (exists) {
					txtFileComplete = true;
				} else {
					wizard.setErrorMessage("Specified file to import does not exist");
					txtFileComplete = false;
				}
			}
			getWizard().setNextFinishButtonEnabled(txtFileComplete);

			if (txtFileComplete) {
				wizard.setErrorMessage(null);
				txtFileComplete = true;
			}
		}

		public void aboutToDisplayPanel() {
			getWizard().setNextFinishButtonEnabled(txtFileComplete);
			getWizard().setPageTitle("Choose interaction file locations");
		}

		public Object getNextPanelDescriptor() {
			return "RIP_COLUMN_PAGE";
		}

		public Object getBackPanelDescriptor() {
			return "RIP_INFO_PAGE";
		}

		public JPanel createContents() {
			txtInput = new JTextField(40);
			btnInput = new JButton("Browse");

			FormLayout layout = new FormLayout(
					"right:pref, 3dlu, pref, 3dlu, pref", "p, 3dlu, p");

			PanelBuilder builder = new PanelBuilder(layout);
			builder.setDefaultDialogBorder();

			CellConstraints cc = new CellConstraints();

			builder.addLabel("Interaction file(s)", cc.xy(1, 1));
			builder.add(txtInput, cc.xy(3, 1));
			builder.add(btnInput, cc.xy(5, 1));

			btnInput.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String action = e.getActionCommand();

					if (ACTION_INPUT.equals(action)) {
						JFileChooser jfc = new JFileChooser();
						jfc.addChoosableFileFilter(new SimpleFileFilter(
								"Interaction files", "*.txt|*.csv|*.tab", true));
						jfc.setMultiSelectionEnabled(true);
						int result = jfc.showDialog(null,
								"Select interaction file(s)");
						String fileNames = "";
						if (result == JFileChooser.APPROVE_OPTION) {
							File[] files = jfc.getSelectedFiles();
							for (File f : files) {
								//RipImportInformation importInformation = new RipImportInformation();
								try {
									importInformation.setTxtFile(f);
									importInformation.setDelimiter("\t");
								} catch (IOException e1) {
									wizard.setErrorMessage("Exception while reading file: "
											+ e1.getMessage());
									txtFileComplete = false;
								}
								impInfoList.add(importInformation);
								fileNames = fileNames + f.getAbsolutePath()
										+ "; ";
								txtInput.setText(fileNames);
							}
							updateTxtFile();
							if (impInfoList.size() > 0) {
								wizard.setCurrentFile(impInfoList.get(0));
							}
						}
					}
				}
			});
			btnInput.setActionCommand(ACTION_INPUT);

			txtInput.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent arg0) {
					updateTxtFile();
				}

				public void insertUpdate(DocumentEvent arg0) {
					updateTxtFile();
				}

				public void removeUpdate(DocumentEvent arg0) {
					updateTxtFile();
				}

			});

			return builder.getPanel();
		}

		public void aboutToHidePanel() {
			for (RipImportInformation impInfo : impInfoList) {
				impInfo.guessSettings();
			}
			wizard.setImportInformationList(impInfoList);
		}
	}
	
	
	private class RipColumnPage extends WizardPanelDescriptor{
		
		private final static String IDENTIFIER = "RIP_COLUMN_PAGE";
		public RipColumnPage(){
			super(IDENTIFIER);
		}
		 

		   // private RipImportInformation importInformation;
		   
		    
		    private RipColumnTableModel ctm;
			private JTable tblColumn;

		    private JComboBox cbColIdReg;
		    private JComboBox cbColIdTar;
		    private JComboBox cbColSyscodeReg;
		    private JComboBox cbColSyscodeTar;
		    private JRadioButton rbFixedNoReg;
		    private JRadioButton rbFixedYesReg;
		    private JComboBox cbDataSourceReg;
		    private JRadioButton rbFixedNoTar;
		    private JRadioButton rbFixedYesTar;
		    private JComboBox cbDataSourceTar;
		    private DataSourceModel mDataSourceReg;
		    private DataSourceModel mDataSourceTar;
		    private JCheckBox checkPMID;
		    private JComboBox cbPMID;
		    private JScrollPane bottomPanel;
		    private JPanel listPanel;
		    private JList jList;
		    private JPanel panel;
		    private Map<RipImportInformation, Boolean> finishedFiles;

		  

		    public Object getNextPanelDescriptor()
		    {
		    	return "RIP_IMPORT_PAGE";
		    }

		    public Object getBackPanelDescriptor()
		    {
		    	return "RIP_FILE_PAGE";
		    }
		    
		    protected JPanel createContents()
			{
		    	bottomPanel = new JScrollPane();
		    	listPanel = new JPanel();
		    	int x = 1;
//		    	if (wizard.getImportInformationList() != null && wizard.getImportInformationList().size() > 0) {//TODO wizard gave nullpointerexception, temporary fix only builds full panel when abouttodisplaypanel is called
//		    		x = wizard.getImportInformationList().size();
//		    	}
		    	RipImportInformation[] impInfoArray = new RipImportInformation[x];
//		    	impInfoArray = wizard.getImportInformationList().toArray(impInfoArray);
		    	jList = new JList(impInfoArray);
		    	jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		    	jList.addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
//						updateBottomPanel(wizard.getImportInformationList().get(jList.getSelectedIndex()));
					}
				});
		    	panel = new JPanel();
		    	panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		    	listPanel.add(new JLabel("Select an interaction file to configure:"));
		    	listPanel.add(jList);
		    	panel.add(listPanel);
		    	panel.add(bottomPanel);
		    	panel.setPreferredSize(new Dimension(764, 482));//combined with the default wizard buttons & border fits exactly in 800x600 resolution
		    	
		    	return panel;
			}
		    
		    /**
		     * Saves the settings for the previously selected interaction file and updates the bottom panel to show the settings for the newly selected interaction file.
		     * @param importInformation The {@link ImportInformation} of the newly selected interaction file
		     */
		    private void updateBottomPanel(final RipImportInformation importInformation) {
		    	//save settings for previously selected file
		    	if (rbFixedYesReg != null){//checks if the bottomPanel is actually created (not done during instantiation, only after loading files)
			    	wizard.getCurrentFile().setSyscodeFixedReg(rbFixedYesReg.isSelected());
			    	wizard.getCurrentFile().setSyscodeFixedTar(rbFixedYesTar.isSelected());
			    	if (rbFixedYesReg.isSelected())
			    	{
			    		wizard.getCurrentFile().setDataSourceReg(mDataSourceReg.getSelectedDataSource());
			    	}
			    	if (rbFixedYesTar.isSelected())
			    	{
			    		wizard.getCurrentFile().setDataSourceTar(mDataSourceTar.getSelectedDataSource());
			    	}
			    	wizard.getCurrentFile().setPMIDColumnEnabled(checkPMID.isSelected());
			    	if (checkPMID.isSelected()) {
			    		wizard.getCurrentFile().setPMIDColumn(cbPMID.getSelectedIndex());
			    	}
		    	}
		    	
		    	wizard.setCurrentFile(importInformation);
		    	panel.remove(bottomPanel);
			    FormLayout layout = new FormLayout (
			    		"pref, 7dlu, pref:grow",
			    		"p, 5dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 15dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 15dlu, p, 3dlu, fill:[100dlu,min]:grow");

			    PanelBuilder builder = new PanelBuilder(layout);
			    builder.setDefaultDialogBorder();

			    CellConstraints cc = new CellConstraints();
				rbFixedNoReg = new JRadioButton("Select a column to specify system code for regulators");
				rbFixedYesReg = new JRadioButton("Use the same system code for all rows for regulators");
				rbFixedNoTar = new JRadioButton("Select a column to specify system code for targets");
				rbFixedYesTar = new JRadioButton("Use the same system code for all rows for targets");
				ButtonGroup bgSyscodeColReg = new ButtonGroup ();
				bgSyscodeColReg.add (rbFixedNoReg);
				bgSyscodeColReg.add (rbFixedYesReg);
				ButtonGroup bgSyscodeColTar = new ButtonGroup ();
				bgSyscodeColTar.add (rbFixedNoTar);
				bgSyscodeColTar.add (rbFixedYesTar);

				cbColIdReg = new JComboBox();
				cbColSyscodeReg = new JComboBox();
				cbColIdTar = new JComboBox();
				cbColSyscodeTar = new JComboBox();

				mDataSourceReg = new DataSourceModel();
				mDataSourceTar = new DataSourceModel();
				cbDataSourceReg = new PermissiveComboBox(mDataSourceReg);
				cbDataSourceTar = new PermissiveComboBox(mDataSourceTar);
				
				checkPMID = new JCheckBox("Select PubMed ID column");
				cbPMID = new JComboBox();
				
				ctm = new RipColumnTableModel(importInformation);
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
				builder.add (scrTable, cc.xyw(1,25,3));
			    builder.addLabel("Current file: " + importInformation.getTxtFile(), cc.xyw(1, 1, 3));
			    
				builder.addLabel ("Select primary identifier column for regulators:", cc.xy(1,3));
				builder.add (cbColIdReg, cc.xy(3,3));

				builder.add (rbFixedNoReg, cc.xyw(1,5,3));
				builder.add (cbColSyscodeReg, cc.xy(3,7));
				builder.add (rbFixedYesReg, cc.xyw (1,9,3));
				builder.add (cbDataSourceReg, cc.xy (3,11));
				
				builder.addSeparator("", cc.xyw(1, 12, 3));

				builder.addLabel ("Select primary identifier column for targets:", cc.xy(1,13));
				builder.add (cbColIdTar, cc.xy(3,13));

				builder.add (rbFixedNoTar, cc.xyw(1,15,3));
				builder.add (cbColSyscodeTar, cc.xy(3,17));
				builder.add (rbFixedYesTar, cc.xyw (1,19,3));
				builder.add (cbDataSourceTar, cc.xy (3,21));
				
				builder.addSeparator("", cc.xyw(1,22,3));
				
				builder.add(checkPMID, cc.xy(1, 23));
				builder.add(cbPMID, cc.xy(3, 23));


				ActionListener rbActionReg = new ActionListener() {
					public void actionPerformed (ActionEvent ae)
					{
						boolean result = (ae.getSource() == rbFixedYesReg);
						importInformation.setSyscodeFixedReg(result);
				    	columnPageRefresh();
					}
				};
				rbFixedYesReg.addActionListener(rbActionReg);
				rbFixedNoReg.addActionListener(rbActionReg);
				
				ActionListener rbActionTar = new ActionListener() {
					public void actionPerformed (ActionEvent ae)
					{
						boolean result = (ae.getSource() == rbFixedYesTar);
						importInformation.setSyscodeFixedTar(result);
				    	columnPageRefresh();
					}
				};
				rbFixedYesTar.addActionListener(rbActionTar);
				rbFixedNoTar.addActionListener(rbActionTar);

				mDataSourceReg.addListDataListener(new ListDataListener()
				{
					public void contentsChanged(ListDataEvent arg0)
					{
						importInformation.setDataSourceReg(mDataSourceReg.getSelectedDataSource());
					}

					public void intervalAdded(ListDataEvent arg0) {}

					public void intervalRemoved(ListDataEvent arg0) {}
				});
				mDataSourceTar.addListDataListener(new ListDataListener()
				{
					public void contentsChanged(ListDataEvent arg0)
					{
						importInformation.setDataSourceTar(mDataSourceTar.getSelectedDataSource());
					}

					public void intervalAdded(ListDataEvent arg0) {}

					public void intervalRemoved(ListDataEvent arg0) {}
				});

				cbColSyscodeReg.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae)
					{
						importInformation.setSysodeColumnReg(cbColSyscodeReg.getSelectedIndex());
						columnPageRefresh();
					}
				});
				cbColIdReg.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae)
					{
						importInformation.setIdColumnReg(cbColIdReg.getSelectedIndex());
				    	columnPageRefresh();
					}
				});
				cbColSyscodeTar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae)
					{
						importInformation.setSysodeColumnTar(cbColSyscodeTar.getSelectedIndex());
						columnPageRefresh();
					}
				});
				cbColIdTar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae)
					{
						importInformation.setIdColumnTar(cbColIdTar.getSelectedIndex());
				    	columnPageRefresh();
					}
				});
				
				ActionListener PMIDListener = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (e.getSource() == checkPMID) {
							cbPMID.setEnabled(checkPMID.isSelected());
							importInformation.setPMIDColumnEnabled(checkPMID.isSelected());
							columnPageRefresh();
						} else if (e.getSource() == cbPMID) {
							importInformation.setPMIDColumn(cbPMID.getSelectedIndex());
							columnPageRefresh();
						}
					}
				};
				checkPMID.addActionListener(PMIDListener);
				cbPMID.addActionListener(PMIDListener);
				JScrollPane scroll = new JScrollPane(builder.getPanel());
				bottomPanel.removeAll();
				bottomPanel = scroll;
				bottomPanel.revalidate();
				
				// create an array of size getSampleMaxNumCols()
		    	Integer[] cn;
		    	int max = importInformation.getSampleMaxNumCols();
				cn = new Integer[max];
				for (int i = 0; i < max; ++i) cn[i] = i;

		    	cbColIdReg.setRenderer(new ColumnNameRenderer());
		    	cbColSyscodeReg.setRenderer(new ColumnNameRenderer());
		    	cbColIdReg.setModel(new DefaultComboBoxModel(cn));
		    	cbColSyscodeReg.setModel(new DefaultComboBoxModel(cn));
		    	cbColIdTar.setRenderer(new ColumnNameRenderer());
		    	cbColSyscodeTar.setRenderer(new ColumnNameRenderer());
		    	cbColIdTar.setModel(new DefaultComboBoxModel(cn));
		    	cbColSyscodeTar.setModel(new DefaultComboBoxModel(cn));
		    	
		    	cbPMID.setRenderer(new ColumnNameRenderer());
		    	cbPMID.setModel(new DefaultComboBoxModel(cn));

		    	finishedFiles.put(importInformation, true);
		    	
				columnPageRefresh();
				refreshComboBoxes();

		    	ctm.refresh();
		    	
		    	panel.add(bottomPanel);
		    	panel.revalidate();
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
						if (clickedCol != importInformation.getSyscodeColumnReg())
							popup.add(new SyscodeColRegAction());
						if (clickedCol != importInformation.getIdColumnReg())
							popup.add(new IdColRegAction());
						if (clickedCol != importInformation.getSyscodeColumnTar())
							popup.add(new SyscodeColTarAction());
						if (clickedCol != importInformation.getIdColumnTar())
							popup.add(new IdColTarAction());
						popup.show(e.getComponent(),
								e.getX(), e.getY());
					}
				}

				private class SyscodeColRegAction extends AbstractAction
				{
					public SyscodeColRegAction()
					{
						putValue(Action.NAME, "Regulator SystemCode column");
					}

					public void actionPerformed(ActionEvent arg0)
					{
						// if id and code column are about to be the same, swap them
						if (clickedCol == importInformation.getIdColumnReg())
							importInformation.setIdColumnReg(importInformation.getSyscodeColumnReg());
						importInformation.setSysodeColumnReg(clickedCol);
						columnPageRefresh();
					}
				}
				private class SyscodeColTarAction extends AbstractAction
				{
					public SyscodeColTarAction()
					{
						putValue(Action.NAME, "Target SystemCode column");
					}

					public void actionPerformed(ActionEvent arg0)
					{
						// if id and code column are about to be the same, swap them
						if (clickedCol == importInformation.getIdColumnTar())
							importInformation.setIdColumnTar(importInformation.getSyscodeColumnTar());
						importInformation.setSysodeColumnTar(clickedCol);
						columnPageRefresh();
					}
				}

				private class IdColRegAction extends AbstractAction
				{
					public IdColRegAction()
					{
						putValue(Action.NAME, "Regulator Identifier column");
					}

					public void actionPerformed(ActionEvent arg0)
					{
						// if id and code column are about to be the same, swap them
						if (clickedCol == importInformation.getSyscodeColumnReg())
							importInformation.setSysodeColumnReg(importInformation.getIdColumnReg());
						importInformation.setIdColumnReg(clickedCol);
						columnPageRefresh();
					}
				}
				private class IdColTarAction extends AbstractAction
				{
					public IdColTarAction()
					{
						putValue(Action.NAME, "Target Identifier column");
					}

					public void actionPerformed(ActionEvent arg0)
					{
						// if id and code column are about to be the same, swap them
						if (clickedCol == importInformation.getSyscodeColumnTar())
							importInformation.setSysodeColumnTar(importInformation.getIdColumnTar());
						importInformation.setIdColumnTar(clickedCol);
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
						importInformation.setFirstDataRow(clickedRow);
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
						importInformation.setFirstHeaderRow(clickedRow);
						columnPageRefresh();
					}
				}

		    }

		    private void columnPageRefresh()
		    {
		    	importInformation = wizard.getCurrentFile();
		    	String error = null;
				if (importInformation.isSyscodeFixedReg())
				{
					rbFixedYesReg.setSelected (true);
					cbColSyscodeReg.setEnabled (false);
					cbDataSourceReg.setEnabled (true);
				}
				else
				{
					rbFixedNoReg.setSelected (true);
					cbColSyscodeReg.setEnabled (true);
					cbDataSourceReg.setEnabled (false);

					if (importInformation.getIdColumnReg() == importInformation.getSyscodeColumnReg()) {
		    			error = "Regulator System code column and Id column can't be the same";
		    		} else if (!importInformation.isSyscodeFixedTar() && importInformation.getSyscodeColumnReg() == importInformation.getSyscodeColumnTar()) {
		    			error = "Regulator and target system code columns can't be the same";
		    		} else if (importInformation.getIdColumnTar() == importInformation.getSyscodeColumnReg()) {
		    			error = "Target ID column and Regulator system code column can't be the same";
		    		} 
				}
				if (importInformation.isSyscodeFixedTar())
				{
					rbFixedYesTar.setSelected (true);
					cbColSyscodeTar.setEnabled (false);
					cbDataSourceTar.setEnabled (true);
				}
				else
				{
					rbFixedNoTar.setSelected (true);
					cbColSyscodeTar.setEnabled (true);
					cbDataSourceTar.setEnabled (false);

					if (importInformation.getIdColumnTar() == importInformation.getSyscodeColumnTar()) {
		    			error = "Target System code column and Id column can't be the same";
		    		} else if (importInformation.getIdColumnReg() == importInformation.getSyscodeColumnTar()) {
		    			error = "Regulator ID column and Target system code column can't be the same";
		    		}
				}
				if (importInformation.isPMIDColumnEnabled()) {
					checkPMID.setSelected(true);
					cbPMID.setEnabled(true);
					
					if (!importInformation.isSyscodeFixedReg() && importInformation.getPMIDColumn() == importInformation.getSyscodeColumnReg()) {
						error = "Regulator system code column and PMID column can't be the same";
					} else if (!importInformation.isSyscodeFixedTar() && importInformation.getPMIDColumn() == importInformation.getSyscodeColumnTar()) {
						error = "Target system code column and PMID column can't be the same";
					} else if (importInformation.getIdColumnReg() == importInformation.getPMIDColumn()) {
		    			error = "Regulator ID column and PMID column can't be the same";
		    		} else if (importInformation.getIdColumnTar() == importInformation.getPMIDColumn()) {
		    			error = "Target ID column and PMID column can't be the same";
		    		}
				} else {
					checkPMID.setSelected(false);
					cbPMID.setEnabled(false);
				}
				if (importInformation.getIdColumnReg() == importInformation.getIdColumnTar()) {
					error = "Regulator ID column and Target ID column can'tbe the same";
				}

		    	if(!finishedFiles.containsValue(false)) {
				    getWizard().setNextFinishButtonEnabled(error == null);
		    	}
			    getWizard().setErrorMessage(error == null ? "" : error);
				getWizard().setPageTitle ("Choose column types");

		    	ctm.refresh();
		    }

		    private void refreshComboBoxes()
		    {
		    	if (importInformation.getSampleMaxNumCols() > 0) {
			    	mDataSourceReg.setSelectedItem(importInformation.getDataSourceReg());
					cbColIdReg.setSelectedIndex(importInformation.getIdColumnReg());
			    	mDataSourceTar.setSelectedItem(importInformation.getDataSourceTar());
					cbColIdTar.setSelectedIndex(importInformation.getIdColumnTar());
					cbColSyscodeReg.setSelectedIndex(importInformation.getSyscodeColumnReg());
					cbColSyscodeTar.setSelectedIndex(importInformation.getSyscodeColumnTar());
					cbPMID.setSelectedIndex(importInformation.getPMIDColumn());
		    	}
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

					String[] cn = importInformation.getColNames();
					String column = cn[selectedIndex];
					setText(column);
					setFont(list.getFont());

					return this;
				}
			}

		    public void aboutToDisplayPanel()
		    {
		    	getWizard().setNextFinishButtonEnabled(false);
		    	
		    	importInformation = wizard.getCurrentFile();
		    	finishedFiles = new HashMap<RipImportInformation, Boolean>();
		    	for (RipImportInformation impInfo : wizard.getImportInformationList()) {
		    		finishedFiles.put(impInfo, false);
		    	}
		    	
		    	listPanel.removeAll();
		    	String[] fileNameArray = new String[wizard.getImportInformationList().size()];
		    	for (int i = 0; i < wizard.getImportInformationList().size(); i++) {
		    		fileNameArray[i] = wizard.getImportInformationList().get(i).getTxtFile().getName();
		    	}
		    	jList = new JList(fileNameArray);
		    	jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		    	jList.addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						updateBottomPanel(wizard.getImportInformationList().get(jList.getSelectedIndex()));
					}
				});
		    	listPanel.add(new JLabel("Select an interaction file to configure:"));
		    	listPanel.add(jList);
		    	
		    	jList.revalidate();
		    	jList.setSelectedIndex(0);
		    	listPanel.revalidate();
		    }

		    @Override
		    public void aboutToHidePanel()
		    {
		    	importInformation.setSyscodeFixedReg(rbFixedYesReg.isSelected());
		    	importInformation.setSyscodeFixedTar(rbFixedYesTar.isSelected());
		    	if (rbFixedYesReg.isSelected())
		    	{
			    	importInformation.setDataSourceReg(mDataSourceReg.getSelectedDataSource());
		    	}
		    	if (rbFixedYesTar.isSelected())
		    	{
			    	importInformation.setDataSourceTar(mDataSourceTar.getSelectedDataSource());
		    	}
		    	importInformation.setPMIDColumnEnabled(checkPMID.isSelected());
		    	if (checkPMID.isSelected()) {
		    		importInformation.setPMIDColumn(cbPMID.getSelectedIndex());
		    	}
		    }
		
	}
	
	private class RipImportPage extends WizardPanelDescriptor implements ProgressListener{
		private final static String IDENTIFIER = "RIP_IMPORT_PAGE";
		RegIntPlugin plugin;
		LoadFileWizard wizard= new LoadFileWizard();
		public RipImportPage(){
			super(IDENTIFIER);
		}
		private final int PROGRESS_INTERVAL = 50;

	    public Object getNextPanelDescriptor()
	    {
	        return FINISH;
	    }

	    public Object getBackPanelDescriptor()
	    {
	        return ColumnPage.IDENTIFIER;
	    }

	    private JProgressBar progressSent;
	    private JTextArea progressText;
	    private ProgressKeeper pk;
	    private JLabel lblTask;
		private StopWatch stopwatch;
		private int progress;

	    @Override
	    public void aboutToCancel()
	    {
	    	// let the progress keeper know that the user pressed cancel.
	    	pk.cancel();
	    }

		protected JPanel createContents()
		{
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
	        progressText.setEditable(false);
	        
	        progress = 0;

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
//	    	imb = new InteractionMapBuilder(plugin, pk, progressText, progressSent);
	    	
			getWizard().setPageTitle ("Load interaction file(s)");
			
			int x = 0;
	    	for (RipImportInformation impInfo : wizard.getImportInformationList()) {//get total number of lines for all interaction files
	    		try {
		    		InputStream is = new BufferedInputStream(new FileInputStream(impInfo.getTxtFile()));
		    	    try {
		    	        byte[] c = new byte[1024];
		    	        int readChars = 0;
		    	        while ((readChars = is.read(c)) != -1) {
		    	            for (int i = 0; i < readChars; ++i) {
		    	                if (c[i] == '\n')
		    	                    ++x;
		    	            }
		    	        }
		    	    } finally {
		    	        is.close();
		    	    }
	    		}catch (IOException e) {
	    			e.printStackTrace();
	    		}
	    	}
	    	pk = new ProgressKeeper(x);
	    	progressSent.setMaximum(x);
	    	
	        setProgressValue(0);
	        setProgressText("");

	        getWizard().setNextFinishButtonEnabled(false);
	        getWizard().setBackButtonEnabled(false);
	    }
	    
	    public void displayingPanel()
	    {
	    	SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					pk.setTaskName("Loading interaction file(s)");
			    	try{
			    		stopwatch = new StopWatch();
						stopwatch.start();
			    		for (RipImportInformation importInformation : wizard.getImportInformationList()) {
			    			progressText.append("Loading " + importInformation.getTxtFile().getName() + "...\n");
			    	    	Map<Xref,List<Interaction>> interactions = plugin.getInteractions();
			    			int RegId = importInformation.getIdColumnReg();
			    			int TarId = importInformation.getIdColumnTar();
			    			FileReader fr = new FileReader(importInformation.getTxtFile());
			    			BufferedReader in = new BufferedReader(fr);
			    			String line = new String();
			    			for(int i = 0; i < importInformation.getFirstDataRow(); i++) {
			    				in.readLine();
			    			}
			    			while ((line = in.readLine()) != null) {
			    				String[] str = line.split(importInformation.getDelimiter());
			    				if (str.length >= importInformation.getSampleMaxNumCols()) {
			    					String regulatorString = str[RegId];
			    					String targetString = str[TarId];

			    					if (!regulatorString.equals("") && !targetString.equals("")) {
			    						Xref regulator;
			    						DataSource dsReg;
			    						Xref target;
			    						DataSource dsTar;
			    						if (importInformation.isSyscodeFixedReg()) {
			    							dsReg = importInformation.getDataSourceReg();
			    						} else {
			    							dsReg = DataSource.getBySystemCode(str[importInformation.getSyscodeColumnReg()]);
			    						}
			    						regulator = new Xref(regulatorString, dsReg);
			    						plugin.addUsedDataSource(dsReg);
			    						if (importInformation.isSyscodeFixedTar()) {
			    							dsTar = importInformation.getDataSourceTar();
			    						} else {
			    							dsTar = DataSource.getBySystemCode(str[importInformation.getSyscodeColumnTar()]);
			    						}
			    						target = new Xref(targetString, dsTar);
			    						plugin.addUsedDataSource(dsTar);
			    						
			    						Interaction thisInteraction = new Interaction(regulator, target, importInformation.getTxtFile());
			    						
			    						if (importInformation.isPMIDColumnEnabled()) {
			    							String PMID = str[importInformation.getPMIDColumn()];
			    							thisInteraction.setPMID(PMID);
			    						}
			    						String miscInfo = "<table border=\"1\">";
			    						for (int i = 0; i < importInformation.getColNames().length; i++) {
			    							if (i != importInformation.getIdColumnReg() && i != importInformation.getIdColumnTar()) {
			    								if (!importInformation.isSyscodeFixedReg() && i == importInformation.getSyscodeColumnReg()) {
			    									//do nothing
			    								} else if (!importInformation.isSyscodeFixedTar() && i == importInformation.getSyscodeColumnTar()) {
			    									//do nothing
			    								} else if (importInformation.isPMIDColumnEnabled() && i == importInformation.getPMIDColumn()) {
			    									//do nothing
			    								} else {
			    									miscInfo += "<tr><td>" + importInformation.getColNames()[i] + "</td><td>" + str[i] + "</td></tr>";
			    								}
			    							}
			    						}
			    						miscInfo += "</table>";
			    						if (!miscInfo.equals("<table border=\"1\"></table>")) {
			    							thisInteraction.setMiscInfo(miscInfo);
			    						}
			    						
			    				    	DataSource[] usedDataSourceArray = new DataSource[plugin.getUsedDataSources().size()];
			    				    	usedDataSourceArray = plugin.getUsedDataSources().toArray(usedDataSourceArray);
			    						Set<Xref> crfsReg = new HashSet<Xref>();
			    						crfsReg.add(regulator);
			    						Set<Xref> crfsTar = new HashSet<Xref>();
			    						crfsTar.add(target);
			    						for (IDMapper aMapper : plugin.getDesktop().getSwingEngine().getGdbManager().getCurrentGdb().getMappers()) {
			    							Set<Xref> someRegXrefs = aMapper.mapID(regulator, usedDataSourceArray);
			    							crfsReg.addAll(someRegXrefs);
			    							Set<Xref> someTarXrefs = aMapper.mapID(target, usedDataSourceArray);
			    							crfsTar.addAll(someTarXrefs);
			    						}
			    						
			    						boolean regulatorAlreadyInKeys = false;
			    						boolean targetAlreadyInKeys = false;
			    						boolean interactionAlreadyExists = false;
			    						Xref regulatorInKeys = null;
			    						Xref targetInKeys = null;
			    						for (Xref xrefReg : crfsReg) {
			    							if (interactions.containsKey(xrefReg)) {
			    								regulatorAlreadyInKeys = true;
			    								regulatorInKeys = xrefReg;
			    								for (Interaction anInteraction : interactions.get(xrefReg)) {
			    									if (crfsTar.contains(anInteraction.getTarget())) {
			    										interactionAlreadyExists = true;
			    										if (anInteraction.getPMID().equals("")) {
			    											anInteraction.setPMID(thisInteraction.getPMID());
			    										}
			    										anInteraction.addFile(importInformation.getTxtFile());
			    									}
			    								}
			    							}
			    						}
			    						for (Xref xrefTar : crfsTar) {
			    							if (interactions.containsKey(xrefTar)) {
			    								targetAlreadyInKeys = true;
			    								targetInKeys = xrefTar;
			    								for (Interaction anInteraction : interactions.get(xrefTar)) {
			    									if (crfsReg.contains(anInteraction.getRegulator())) {
			    										interactionAlreadyExists = true;
			    										if (anInteraction.getPMID().equals("")) {
			    											anInteraction.setPMID(thisInteraction.getPMID());
			    										}
			    										anInteraction.addFile(importInformation.getTxtFile());
			    									}
			    								}
			    							}
			    						}
			    						
			    						if (!interactionAlreadyExists) {
			    							if (regulatorAlreadyInKeys) {
			    								if (!targetAlreadyInKeys) {
			    									thisInteraction = new Interaction(regulatorInKeys, target, importInformation.getTxtFile());
			    									interactions.get(regulatorInKeys).add(thisInteraction);
			    									List<Interaction> intList = new ArrayList<Interaction>();
			    									intList.add(thisInteraction);
			    									interactions.put(target, intList);
			    								} else {
			    									thisInteraction = new Interaction(regulatorInKeys, targetInKeys, importInformation.getTxtFile());
			    									interactions.get(regulatorInKeys).add(thisInteraction);
			    									interactions.get(targetInKeys).add(thisInteraction);
			    								}
			    							} else {
			    								if (!targetAlreadyInKeys) {
			    									List<Interaction> intListReg = new ArrayList<Interaction>();
			    									intListReg.add(thisInteraction);
			    									interactions.put(regulator, intListReg);
			    									List<Interaction> intListTar = new ArrayList<Interaction>();
			    									intListTar.add(thisInteraction);
			    									interactions.put(target, intListTar);
			    								} else {
			    									thisInteraction = new Interaction(regulator, targetInKeys, importInformation.getTxtFile());
			    									List<Interaction> intList = new ArrayList<Interaction>();
			    									intList.add(thisInteraction);
			    									interactions.put(regulator, intList);
			    									interactions.get(targetInKeys).add(thisInteraction);
			    								}
			    							}
			    						}
			    					}
			    				}
			    				progress++;
			    				if (progress % PROGRESS_INTERVAL == 0) {
			    					progressSent.setValue(progress);
			    					pk.setProgress(progress);
			    				}
//			    				progressSent.setValue(progress);
//			    				pk.setProgress(progress);
			    			}
			    			plugin.getIntFiles().add(importInformation.getTxtFile());
			    			progressText.append("Finished loading " + importInformation.getTxtFile().getName() + "\n");
			    		}
			    		progressText.append("Added " + progress + " entries in " + stopwatch.stop() + "ms\n");
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (IDMapperException e) {
						e.printStackTrace();
					}
			    	return null;
				}

				@Override
				public void done() {
					progressSent.setValue(pk.getTotalWork());
					pk.finished();
					pk.setTaskName("Finished");
					getWizard().setNextFinishButtonEnabled(true);
					getWizard().setBackButtonEnabled(true);
					
					LinkedHashSet<File> files = new LinkedHashSet<File>();
					for (RipImportInformation impInfo : wizard.getImportInformationList()) {
						files.add(impInfo.getTxtFile());
					}
					RIPreferences.getPreferences().setSelectedIntFiles(files);
				}
			};
			sw.execute();
	    }

		public void progressEvent(ProgressEvent e)
		{
			switch(e.getType())
			{
				case ProgressEvent.FINISHED:
					progressSent.setValue(pk.getTotalWork());
				case ProgressEvent.TASK_NAME_CHANGED:
					lblTask.setText(pk.getTaskName());//TODO fix, doesn't update the label text
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
	
}
