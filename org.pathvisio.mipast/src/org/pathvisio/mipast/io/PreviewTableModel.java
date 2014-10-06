package org.pathvisio.mipast.io;



import javax.swing.table.AbstractTableModel;

import org.pathvisio.gexplugin.ImportInformation;


/**
 * for table used in the header page of the GexImportWizard.
 */
public class PreviewTableModel extends AbstractTableModel
{

	private ImportInformation info;

	public PreviewTableModel (ImportInformation info)
	{
		this.info = info;
	}

	public void refresh()
	{
		fireTableStructureChanged();
	}

	public int getColumnCount()
	{
		return info.getSampleMaxNumCols();
	}

	public int getRowCount()
	{
		return info.getSampleNumRows();
	}

	public Object getValueAt(int row, int col)
	{
		return info.getSampleData(row, col);
	}
}
