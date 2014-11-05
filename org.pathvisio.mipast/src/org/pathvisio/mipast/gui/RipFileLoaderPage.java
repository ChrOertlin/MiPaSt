package org.pathvisio.mipast.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


import org.bridgedb.gui.SimpleFileFilter;

import org.pathvisio.rip.RegIntPlugin;
import org.pathvisio.rip.dialog.ColumnPage;
import org.pathvisio.rip.util.RipImportInformation;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.nexes.wizard.WizardPanelDescriptor;

public class RipFileLoaderPage extends WizardPanelDescriptor implements
		ActionListener {

	final static String IDENTIFIER = "Rip_File_Page";
	static final String ACTION_INPUT = "input";
	private RegIntPlugin plugin;

	private JTextField txtInput;

	private JButton btnInput;
	private boolean txtFileComplete = false;
	private List<RipImportInformation> impInfoList = new ArrayList<RipImportInformation>();

	public RipFileLoaderPage(RegIntPlugin plugin) {
		super(IDENTIFIER);
		this.plugin = plugin;
		
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();

		if (ACTION_INPUT.equals(action)) {
			JFileChooser jfc = new JFileChooser();
			jfc.addChoosableFileFilter(new SimpleFileFilter(
					"Interaction files", "*.txt|*.csv|*.tab", true));
			jfc.setMultiSelectionEnabled(true);
			int result = jfc.showDialog(null, "Select interaction file(s)");
			String fileNames = "";
			if (result == JFileChooser.APPROVE_OPTION) {
				File[] files = jfc.getSelectedFiles();
				for (File f : files) {
					RipImportInformation importInformation = new RipImportInformation();
					try {
						importInformation.setTxtFile(f);
						importInformation.setDelimiter("\t");
					} catch (IOException e1) {
						getWizard().setErrorMessage(
								"Exception while reading file: "
										+ e1.getMessage());
						txtFileComplete = false;
					}
					impInfoList.add(importInformation);
					fileNames = fileNames + f.getAbsolutePath() + "; ";
					txtInput.setText(fileNames);
				}
				updateTxtFile();
				if (impInfoList.size() > 0) {
					plugin.setCurrentFile(impInfoList.get(0));
				}
			}
		}
	}

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
				getWizard().setErrorMessage(
						"Specified file to import does not exist");
				txtFileComplete = false;
			}
		}
		getWizard().setNextFinishButtonEnabled(txtFileComplete);

		if (txtFileComplete) {
			getWizard().setErrorMessage(null);
			txtFileComplete = true;
		}
	}

	@Override
	protected Component createContents() {
		txtInput = new JTextField(40);
		btnInput = new JButton("Browse");

		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, pref, 3dlu, pref", "p, 15dlu, p, 15dlu");

		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		CellConstraints cc = new CellConstraints();

		builder.addLabel("Load your interactions files for the Regulatory Interaction Plugin", cc.xy(1,1));
		builder.addSeparator("", cc.xyw(1, 2,5));
		builder.addSeparator("", cc.xyw(1, 4,5));
		builder.addLabel("Interaction file(s)", cc.xy(1, 3));
		builder.add(txtInput, cc.xy(3, 3));
		builder.add(btnInput, cc.xy(5, 3));
	

		btnInput.addActionListener(this);
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

	public void aboutToHidePanel()
	{
		for (RipImportInformation impInfo : impInfoList) {
			impInfo.guessSettings();
		}
		plugin.setImportInformationList(impInfoList);
    }
	
	
	   public void aboutToDisplayPanel() {
	        getWizard().setNextFinishButtonEnabled(txtFileComplete);
			getWizard().setPageTitle ("Choose file locations");
		}

	    

	    public Object getNextPanelDescriptor() {
	        return ColumnPage.IDENTIFIER;
	    }

	    public Object getBackPanelDescriptor()
	    {
	        return RipInfoPage.IDENTIFIER;
	    }
	
}
