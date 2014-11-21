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


import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.pathvisio.gexplugin.ImportInformation;
import org.pathvisio.mipast.DataHolding;
import org.pathvisio.mipast.io.PreviewTableModel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.nexes.wizard.WizardPanelDescriptor;

/**
 * 
 * The MiRNAFilesInformationPage lets the user specify the delimiter for the miRNA expression data set.
 * 
 * @author ChrOertlin
 *
 */

class MiRNAFilesInformationPage extends WizardPanelDescriptor implements
		ActionListener {
	public static final String IDENTIFIER = "miRNA_INFORMATIONPAGE_PAGE";

	private JRadioButton seperatorTab;
	private JRadioButton seperatorComma;
	private JRadioButton seperatorSemi;
	private JRadioButton seperatorSpace;
	private JRadioButton seperatorOther;
	private JLabel fileName;
	private PreviewTableModel prevTable;
	private JTable tblPreview;

	private ImportInformation miRNAImportInformation = DataHolding
			.getMiRNAImportInformation();
	

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

		prevTable = new PreviewTableModel(DataHolding.getMiRNAImportInformation());
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

		fileName.setText(DataHolding.getMiRNAFile().getName());
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
		return MiRNAColumnPage.IDENTIFIER;
	}

	public Object getBackPanelDescriptor() {
		return FileLoaderPage.IDENTIFIER;
	}
}
