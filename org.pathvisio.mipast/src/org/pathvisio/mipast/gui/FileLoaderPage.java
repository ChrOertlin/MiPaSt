package org.pathvisio.mipast.gui;
//Copyright 2014 BiGCaT
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.bridgedb.gui.SimpleFileFilter;
import org.pathvisio.core.preferences.GlobalPreference;
import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.mipast.DataHolding;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.nexes.wizard.WizardPanelDescriptor;

/**
 * 
 * The FileLoaderPage let's the user load the miRNA and Gene expression data files that will be used
 * for the analysis in this plugin further on.
 * 
 * @author ChrOertlin
 *
 */


class FileLoaderPage extends WizardPanelDescriptor implements ActionListener {

	public static final String IDENTIFIER = "FILE_PAGE";
	
	private PvDesktop standaloneEngine;
	private JCheckBox geneBox;
	private boolean miRNAFileLoaded = false;
	private boolean geneFileLoaded = false;
	private boolean databaseLoaded = false;
	private JButton geneBrowse;
	private JButton dbBrowse;
	private JTextField miRNAText;
	private JTextField geneText;
	private JTextField dbText;
	private File defaultdir;
//	private ImportInformation miRNAImportInformation = DataHolding.getMiRNAImportInformation();
//	private ImportInformation geneImportInformation = DataHolding.getGeneImportInformation();
	private File miRNAFile = DataHolding.getMiRNAFile();
	private File geneFile = DataHolding.getGeneFile();
	
	
	public FileLoaderPage(PvDesktop desktop) {
		super(IDENTIFIER);
		this.standaloneEngine = desktop;
		
	}

	static final String ACTION_GDB = "gdb";

	@Override
	public void actionPerformed(ActionEvent arg0) {
	}

	@Override
	protected Component createContents() {
		CellConstraints cc = new CellConstraints();
		FormLayout layout = new FormLayout(
				"pref,50dlu,pref,50dlu,50dlu,pref,default",
				"8dlu, pref,15dlu,pref,15dlu,pref,4dlu,pref,4dlu,pref,15dlu,pref,4dlu,pref");
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
		dbText = new JTextField();
		builder.add(dbLabel, cc.xy(1, 12));
		builder.add(dbText, cc.xywh(2, 12, 3, 1));
		builder.add(dbBrowse, cc.xy(6, 12));
		dbBrowse.setActionCommand(ACTION_GDB);

		miRNABrowse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				defaultdir = PreferenceManager.getCurrent().getFile(
						GlobalPreference.DIR_LAST_USED_EXPRESSION_IMPORT);
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(defaultdir);
				fc.addChoosableFileFilter(new SimpleFileFilter("Data files",
						"*.txt|*.csv", true));
				int returnVal = fc.showDialog(null, "Open miRNA Datasetfile");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						miRNAFile = fc.getSelectedFile();
						DataHolding.setMiRNAFile(miRNAFile);
						miRNAText.setText(miRNAFile.getAbsolutePath());

						miRNAFileLoaded = true;

						DataHolding.getMiRNAImportInformation().setTxtFile(miRNAFile);
						defaultdir = fc.getCurrentDirectory();
						if (geneBox.isSelected() && geneFileLoaded
								&& databaseLoaded) {
							getWizard().setNextFinishButtonEnabled(true);
						} else if (!geneBox.isSelected() && databaseLoaded) {
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
				fc.setCurrentDirectory(defaultdir);

				fc.addChoosableFileFilter(new SimpleFileFilter("Data files",
						"*.txt|*.csv", true));
				int returnVal = fc.showDialog(null,
						"Open Transcriptomics datasetfile");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					geneFile = fc.getSelectedFile();
					DataHolding.setGeneFile(geneFile);
					try {
						geneText.setText(geneFile.getAbsolutePath());
						geneFileLoaded = true;
						DataHolding.getGeneImportInformation().setTxtFile(geneFile);
						DataHolding.setGeneFileLoaded(true);
						if (miRNAFileLoaded && databaseLoaded) {
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
					if (miRNAFileLoaded && databaseLoaded) {
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
					databaseLoaded = true;
					if (miRNAFileLoaded
							&& ((geneBox.isSelected() && geneFileLoaded)
									&& databaseLoaded || !geneBox.isSelected())
							&& databaseLoaded) {
						getWizard().setNextFinishButtonEnabled(true);
					}
				}
			}
		});

		return builder.getPanel();
	}

	public void aboutToDisplayPanel() {

		getWizard().setPageTitle("Choose file locations");
		if (miRNAFileLoaded
				&& ((geneBox.isSelected() && geneFileLoaded) && databaseLoaded || !geneBox
						.isSelected()) && databaseLoaded) {
			getWizard().setNextFinishButtonEnabled(true);
		} else {
			getWizard().setNextFinishButtonEnabled(false);

		}
		if (PreferenceManager.getCurrent().get(
				GlobalPreference.DB_CONNECTSTRING_GDB) != null) {
			dbText.setText(PreferenceManager.getCurrent().get(
					GlobalPreference.DB_CONNECTSTRING_GDB));
			databaseLoaded = true;

		}

	}

	

	public Object getNextPanelDescriptor() {
		return MiRNAFilesInformationPage.IDENTIFIER;
	}

	public Object getBackPanelDescriptor() {
		return StartInfoPage.IDENTIFIER;
	}

}
