package org.pathvisio.mipast.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.pathvisio.desktop.util.RowNumberHeader;
import org.pathvisio.gui.DataSourceModel;
import org.pathvisio.gui.util.PermissiveComboBox;
import org.pathvisio.mipast.DataHolding;

import org.pathvisio.mipast.io.ColumnTableModel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.nexes.wizard.WizardPanelDescriptor;

class MiRNAColumnPage extends WizardPanelDescriptor {
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
		if (DataHolding.getGeneFile() != null) {
			return GeneFilesInformationPage.IDENTIFIER;
		} else {
			return FileMergePage.IDENTIFIER;
		}

	}

	public Object getBackPanelDescriptor() {
		return MiRNAFilesInformationPage.IDENTIFIER;
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

		ctm = new ColumnTableModel(DataHolding.getMiRNAImportInformation());
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
				DataHolding.getMiRNAImportInformation().setSyscodeFixed(result);
				columnPageRefresh();
			}
		};
		rbDatabaseAll.addActionListener(rbAction);
		rbSyscodeCol.addActionListener(rbAction);

		miRNADataSource.addListDataListener(new ListDataListener() {
			public void contentsChanged(ListDataEvent arg0) {
				DataHolding.getMiRNAImportInformation().setDataSource(miRNADataSource
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
				DataHolding.getMiRNAImportInformation().setSysodeColumn(cbSyscodeCol
						.getSelectedIndex());

				columnPageRefresh();
			}
		});
		cbIdCol.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				DataHolding.getMiRNAImportInformation().setIdColumn(cbIdCol
						.getSelectedIndex());
				columnPageRefresh();
			}
		});
		return builder.getPanel();
	}

	private void columnPageRefresh() {
		getWizard().setPageTitle("Choose column types");

		if (DataHolding.getMiRNAImportInformation().isSyscodeFixed()) {
			rbDatabaseAll.setSelected(true);
			cbSyscodeCol.setEnabled(false);
			cbDataSource.setEnabled(true);
		} else {
			rbSyscodeCol.setSelected(true);
			cbSyscodeCol.setEnabled(true);
			cbDataSource.setEnabled(false);
		}

		if (DataHolding.getMiRNAImportInformation().isSyscodeFixed()) {
			getWizard().setNextFinishButtonEnabled(true);
		} else {
			if (DataHolding.getMiRNAImportInformation().getDataSource() != null) {
				getWizard().setNextFinishButtonEnabled(true);
			}
		}

		// getWizard().setNextFinishButtonEnabled(error == null);
		// getWizard().setErrorMessage(error == null ? "" : error);

		ctm.refresh();
	}

	private void refreshComboBoxes() {
		miRNADataSource.setSelectedItem(DataHolding.getMiRNAImportInformation()
				.getDataSource());
		cbIdCol.setSelectedIndex(DataHolding.getMiRNAImportInformation().getIdColumn());
		cbSyscodeCol.setSelectedIndex(DataHolding.getMiRNAImportInformation()
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

			String[] cn = DataHolding.getMiRNAImportInformation().getColNames();
			String column = cn[selectedIndex];
			setText(column);
			setFont(list.getFont());

			return this;
		}
	}

	public void aboutToDisplayPanel() {
		DataHolding.getMiRNAImportInformation().setSyscodeFixed(true);
		getWizard().setNextFinishButtonEnabled(false);

		// create an array of size getSampleMaxNumCols()
		Integer[] columns;
		int max = DataHolding.getMiRNAImportInformation().getSampleMaxNumCols();
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

		DataHolding.getMiRNAImportInformation().setSyscodeFixed(rbDatabaseAll.isSelected());
		if (!rbDatabaseAll.isSelected()) {
			DataHolding.getMiRNAImportInformation().setDataSource(miRNADataSource
					.getSelectedDataSource());
		}
	}
}