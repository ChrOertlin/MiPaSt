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

class GeneColumnPage extends WizardPanelDescriptor {
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

		ctm = new ColumnTableModel(DataHolding.getGeneImportInformation());
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
				DataHolding.getGeneImportInformation().setSyscodeFixed(result);
				columnPageRefresh();
			}
		};
		rbDatabaseAll.addActionListener(rbAction);
		rbSyscodeCol.addActionListener(rbAction);

		geneDataSource.addListDataListener(new ListDataListener() {
			public void contentsChanged(ListDataEvent arg0) {
				DataHolding.getGeneImportInformation().setDataSource(geneDataSource
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
				DataHolding.getGeneImportInformation().setSysodeColumn(cbSyscodeCol
						.getSelectedIndex());
				System.out.println(DataHolding.getGeneImportInformation().getSyscodeColumn());
				columnPageRefresh();
			}
		});
		cbIdCol.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				DataHolding.getGeneImportInformation().setIdColumn(cbIdCol
						.getSelectedIndex());
				columnPageRefresh();
			}
		});
		return builder.getPanel();
	}

	private void columnPageRefresh() {
		getWizard().setPageTitle("Choose column types");

		if (DataHolding.getGeneImportInformation().isSyscodeFixed()) {
			rbDatabaseAll.setSelected(true);
			cbSyscodeCol.setEnabled(false);
			cbDataSource.setEnabled(true);
		} else {
			rbSyscodeCol.setSelected(true);
			cbSyscodeCol.setEnabled(true);
			cbDataSource.setEnabled(false);
		}

		if (DataHolding.getGeneImportInformation().isSyscodeFixed()) {
			getWizard().setNextFinishButtonEnabled(true);
		} else {
			if (DataHolding.getGeneImportInformation().getDataSource() != null) {
				getWizard().setNextFinishButtonEnabled(true);
			}
		}

		ctm.refresh();
	}

	private void refreshComboBoxes() {
		geneDataSource.setSelectedItem(DataHolding.getGeneImportInformation()
				.getDataSource());
		cbIdCol.setSelectedIndex(DataHolding.getGeneImportInformation().getIdColumn());
		cbSyscodeCol.setSelectedIndex(DataHolding.getGeneImportInformation()
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

			String[] cn = DataHolding.getGeneImportInformation().getColNames();
			String column = cn[selectedIndex];
			setText(column);
			setFont(list.getFont());

			return this;
		}
	}

	public void aboutToDisplayPanel() {
		DataHolding.getGeneImportInformation().setSyscodeFixed(true);
		getWizard().setNextFinishButtonEnabled(false);

		// create an array of size getSampleMaxNumCols()
		Integer[] columns;
		int max = DataHolding.getGeneImportInformation().getSampleMaxNumCols();
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

		DataHolding.getGeneImportInformation().setSyscodeFixed(rbDatabaseAll.isSelected());
		if (!rbDatabaseAll.isSelected()) {
			DataHolding.getGeneImportInformation().setDataSource(geneDataSource
					.getSelectedDataSource());
		}
	}
}
