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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.core.util.ProgressKeeper.ProgressEvent;
import org.pathvisio.core.util.ProgressKeeper.ProgressListener;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.gexplugin.GexTxtImporter;
import org.pathvisio.mipast.DataHolding;
import org.pathvisio.mipast.io.FileMerger;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.nexes.wizard.WizardPanelDescriptor;


/**
 * 
 * This Page is for the import of the Data files into the gexmanager of PathVisio. If only miRNA data is loaded,
 * the file containing the miRNA data will be used. If both miRNA data and Gene data are loaded,
 * both files get merged and then this merged file will be loaded into the gexmanager.
 * 
 * @author ChrOertlin
 *
 */


class FileMergePage extends WizardPanelDescriptor implements ProgressListener {

	private PvDesktop standaloneEngine;
	public static final String IDENTIFIER = "FILE_MERGE_PAGE";

	public FileMergePage(PvDesktop desktop) {
		super(IDENTIFIER);
		this.standaloneEngine = desktop;

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
				if (DataHolding.getGeneImportInformation().getTxtFile() != null) {
					try {
						pk.setTaskName("Merging expression data files");
						DataHolding
								.getCombinedImportInformation()
								.setTxtFile(
										fm.createCombinedFile(
												DataHolding
														.getMiRNAImportInformation(),
												DataHolding
														.getGeneImportInformation()));
						if (DataHolding.getCombinedImportInformation()
								.getTxtFile() != null && fm.getSharedHeader()) {
							pk.report("Expression data files sucesscully merged.\n"
									+ "Combined file created:\n"
									+ fm.createCombinedFile(
											DataHolding
													.getMiRNAImportInformation(),
											DataHolding
													.getGeneImportInformation())
											.getAbsolutePath());

						} else {
							pk.report("Expression data files sucesscully merged.\n"
									+ "Combined file created:\n"
									+ fm.createCombinedFile(
											DataHolding
													.getMiRNAImportInformation(),
											DataHolding
													.getMiRNAImportInformation())
											.getAbsolutePath()
									+ "\n"
									+ "No shared headers found, no shared visualization possible! \n");

						}
//						standaloneEngine.getGexManager().setCurrentGex(
//								fm.getCombinedFile().getName(), true);
						DataHolding.getCombinedImportInformation().setGexName(
								DataHolding.getCombinedImportInformation()
										.getTxtFile().getAbsolutePath());

						pk.setTaskName("Importing expression dataset file(s)");
						
						
						
						GexTxtImporter.importFromTxt(
								DataHolding.getCombinedImportInformation(), pk,
								standaloneEngine.getSwingEngine()
										.getGdbManager().getCurrentGdb(),
								standaloneEngine.getGexManager());
				

					} catch (Exception e) {
						Logger.log.error("During import", e);
						setProgressValue(0);
						setProgressText("An Error Has Occurred: "
								+ e.getMessage() + "\nSee the log for details");
						e.printStackTrace();

						getWizard().setBackButtonEnabled(true);
					} finally {
						pk.finished();
					}
					return null;
				} else {

					try {

//						standaloneEngine.getGexManager().setCurrentGex(
//								DataHolding.getMiRNAFile().getName(), true);
						pk.setTaskName("Importing expression dataset file(s)");
						DataHolding.getMiRNAImportInformation().setGexName(
								DataHolding.getMiRNAImportInformation()
										.getTxtFile().getAbsolutePath());
						GexTxtImporter.importFromTxt(
								DataHolding.getMiRNAImportInformation(), pk,
								standaloneEngine.getSwingEngine()
										.getGdbManager().getCurrentGdb(),
								standaloneEngine.getGexManager());
						

					} catch (Exception e) {
						Logger.log.error("During import", e);
						setProgressValue(0);
						setProgressText("An Error Has Occurred: "
								+ e.getMessage() + "\nSee the log for details");

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
