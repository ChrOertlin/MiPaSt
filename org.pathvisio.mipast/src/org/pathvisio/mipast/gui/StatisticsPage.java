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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.gui.SimpleFileFilter;
import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.data.DataException;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.gui.ProgressDialog;
import org.pathvisio.gui.SwingEngine;
import org.pathvisio.mipast.util.MiPastZScoreCalculator;
import org.pathvisio.statistics.Column;
import org.pathvisio.statistics.StatisticsPathwayResult;
import org.pathvisio.statistics.StatisticsPlugin;
import org.pathvisio.statistics.StatisticsResult;
import org.pathvisio.statistics.StatisticsTableModel;
import org.pathvisio.statistics.StatisticsPlugin.StatisticsDlg;
import org.pathvisio.statistics.StatisticsPlugin.StatisticsPreference;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.nexes.wizard.WizardPanelDescriptor;

/**
 * 
 * The StatisticsPage is lets the user load the Pathway file on which the statistics should be performed.
 * Furthermore, the user has the option to  save the statistical output to a file. 
 * 
 * @author ChrOertlin
 *
 */

public class StatisticsPage extends WizardPanelDescriptor implements
		ActionListener {

	public final static String IDENTIFIER = "STAT_PAGE";

	final static String ACTION_CALCULATE = "calculate";
	final static String ACTION_PW_DIR = "browse";
	final static String ACTION_SAVE = "save";

	
	
	private SwingEngine se;
	private PvDesktop desktop;

	private JLabel pathwayLabel;

	private JButton calculate;
	private JButton save;
	private JButton browsePathway;

	private JTextField pathwayDirText;

	private JTable tblResult;
	private JPanel pathwayPanel;

	private JPanel buttonPanel;
	private JLabel lblResult;

	private StatisticsResult result = null;



	public StatisticsPage(PvDesktop desktop) {
		super(IDENTIFIER);
		this.desktop = desktop;
		se = desktop.getSwingEngine();
	}

	@Override
	protected Component createContents() {
		CellConstraints cc = new CellConstraints();
		FormLayout pwLayout = new FormLayout(
				"4dlu,pref,4dlu,pref,4dlu,pref,4dlu", "4dlu,pref,4dlu");

		FormLayout btnLayout = new FormLayout("4dlu, pref, 4dlu,pref",
				"4dlu, pref, 4dlu");
		FormLayout layout = new FormLayout(
				"4dlu, pref:grow, 4dlu, pref, 4dlu,pref,4dlu",
				"4dlu, fill:[pref,250dlu], 4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, fill:min:grow");

		pathwayPanel = new JPanel(pwLayout);

		buttonPanel = new JPanel(btnLayout);
		PanelBuilder builder = new PanelBuilder(layout);

		pathwayLabel = new JLabel("Pathway directory:");
		pathwayDirText = new JTextField(40);
		browsePathway = new JButton("Browse");

		calculate = new JButton("Calculate");
		save = new JButton("Save Results");

		tblResult = new JTable();

		pathwayPanel.add(pathwayLabel, cc.xy(2, 2));
		pathwayPanel.add(pathwayDirText, cc.xy(4, 2));
		pathwayPanel.add(browsePathway, cc.xy(6, 2));

		buttonPanel.add(calculate, cc.xy(2, 2));
		buttonPanel.add(save, cc.xy(4, 2));

		builder.add(pathwayPanel, cc.xy(2, 2));
		builder.add(buttonPanel, cc.xy(2, 4));
		builder.add(new JScrollPane(tblResult), cc.xy(2, 12));

		tblResult.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				int row = tblResult.getSelectedRow();
				final StatisticsPathwayResult sr = ((StatisticsTableModel) (tblResult
						.getModel())).getRow(row);

				// TODO: here I want to use SwingEngine.openPathway, but I need
				// to
				// be able to wait until the process is finished!
				se.openPathway(sr.getFile());
			}
		});

		calculate.addActionListener(this);
		calculate.setActionCommand(ACTION_CALCULATE);

		save.addActionListener(this);
		save.setActionCommand(ACTION_SAVE);

		browsePathway.addActionListener(this);
		browsePathway.setActionCommand(ACTION_PW_DIR);

		return builder.getPanel();
	}

	public void aboutToDisplayPanel() {
		getWizard().setPageTitle("Perform Statistics");
	}

	public Object getNextPanelDescriptor() {
		return FINISH;
	}

	public Object getBackPanelDescriptor() {
		return CriterionPage.IDENTIFIER;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if (ACTION_CALCULATE.equals(action)) {

		}

		else if (ACTION_SAVE.equals(action)) {
			doSave();
		}

		else if (ACTION_PW_DIR.equals(action)) {
			JFileChooser jfc = new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			jfc.setCurrentDirectory(new File(pathwayDirText.getText()));
			if (jfc.showDialog(null, "Choose") == JFileChooser.APPROVE_OPTION) {
				String newVal = "" + jfc.getSelectedFile();
				pathwayDirText.setText(newVal);

			}
		}
	}

	private void doSave() {
		JFileChooser jfc = new JFileChooser();
		jfc.setDialogTitle("Save results");
		jfc.setFileFilter(new SimpleFileFilter("Tab delimited text", "*.txt",
				true));
		jfc.setDialogType(JFileChooser.SAVE_DIALOG);
		jfc.setCurrentDirectory(PreferenceManager.getCurrent().getFile(
				StatisticsPreference.STATS_DIR_LAST_USED_RESULTS));
		if (jfc.showDialog(null, "Save") == JFileChooser.APPROVE_OPTION) {
			File f = jfc.getSelectedFile();
			PreferenceManager.getCurrent().setFile(
					StatisticsPreference.STATS_DIR_LAST_USED_RESULTS,
					jfc.getCurrentDirectory());
			if (!f.toString().endsWith(".txt")) {
				f = new File(f + ".txt");
			}
			try {
				result.save(f);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Could not save results: "
						+ e.getMessage());
				Logger.log.error("Could not save results", e);
			}
		}
	}

	/**
	 * asynchronous statistics calculation function
	 */
	private void doCalculate(final File pwDir) {
		save.setEnabled(false);

		ProgressKeeper pk = new ProgressKeeper(100);
		final ZScoreWorker worker = new ZScoreWorker(pwDir, se.getGdbManager()
				.getCurrentGdb(), pk);
		ProgressDialog d = new ProgressDialog(
				JOptionPane.getFrameForComponent(desktop.getFrame()),
				"Calculating Z-scores", pk, true, true);
		worker.execute();
		d.setVisible(true);
	}

	private class ZScoreWorker extends SwingWorker<StatisticsResult, Void> {
		private final MiPastZScoreCalculator calculator;
		private ProgressKeeper pk;

		// temporary model that will be filled with intermediate results.
		private StatisticsTableModel temp;
		private boolean useMappFinder;

		ZScoreWorker(File pwDir, IDMapper gdb, ProgressKeeper pk) {
			this.pk = pk;
			calculator = new MiPastZScoreCalculator(null, pwDir, null, gdb, pk, null);
			temp = new StatisticsTableModel();
			temp.setColumns(new Column[] { Column.PATHWAY_NAME, Column.R,
					Column.N, Column.TOTAL, Column.PCT, Column.ZSCORE,
					Column.PERMPVAL });
			tblResult.setModel(temp);
			useMappFinder = PreferenceManager.getCurrent().getBoolean(
					StatisticsPreference.MAPPFINDER_COMPATIBILITY);
		}

		@Override
		protected StatisticsResult doInBackground() throws IDMapperException,
				DataException {
			StatisticsResult result;

			if (useMappFinder) {
				result = calculator.calculateMappFinder();
			} else {
				result = calculator.calculateAlternative();
			}
			return result;
		}

		@Override
		protected void done() {
			if (!pk.isCancelled()) {
				StatisticsResult result;
				try {
					result = get();
					if (result.stm.getRowCount() == 0) {
						JOptionPane
								.showMessageDialog(null,
										"0 results found, did you choose the right directory?");
					} else {
						// replace temp tableModel with definitive one
						tblResult.setModel(result.stm);
						lblResult.setText("<html>Rows in data (N): "
								+ result.getBigN()
								+ "<br>Rows meeting criterion (R): "
								+ result.getBigR());
						StatisticsDlg.result = result;
						// dlg.pack();
					}
				} catch (InterruptedException e) {
					JOptionPane.showMessageDialog(
							null,
							"Exception while calculating statistics\n"
									+ e.getMessage());
					Logger.log.error("Statistics calculation exception", e);
				} catch (ExecutionException e) {
					JOptionPane.showMessageDialog(
							null,
							"Exception while calculating statistics\n"
									+ e.getMessage());
					Logger.log.error("Statistics calculation exception", e);
				}
			}
			calculate.setEnabled(true);
			save.setEnabled(true);
		}
	}

}
