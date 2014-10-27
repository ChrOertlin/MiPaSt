package org.pathvisio.mipast.io;


import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.pathvisio.mipast.util.RipImportInformation;
import org.pathvisio.mipast.util.RipImportInformation.ColumnType;

/**
 * Adjusted version of the table model used in the column page of the Gex Import Wizard.
 * It includes separate coloring for regulator IDs, target IDs, regulator system codes, target system codes and PubMed IDs.
 */
public class RipColumnTableModel extends AbstractTableModel
{
	static private class HighlightedCellRenderer extends DefaultTableCellRenderer
	{
		RipImportInformation info;

		public HighlightedCellRenderer(RipImportInformation info)
	    {
	    	super();
	    	this.info = info;
	        setOpaque(true); //MUST do this for background to show up.
	    }

	    @Override
	    public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column)
	    {
	    	setBackground(getTypeColor(row, column));
	    	return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}

	    @Override
	    public void setValue (Object value)
	    {
			setText (value.toString());
	    }

	    private static final Color LIGHT_RED = new Color (255, 192, 192);
	    private static final Color LIGHT_GREEN = new Color (192, 255, 192);
	    private static final Color LIGHT_YELLOW = new Color (255, 255, 192);
	    private static final Color LIGHT_MAGENTA = new Color (192, 255, 255);
	    
	    private static final Color LIGHT_BLUE = new Color (192, 192, 255);
	    private static final Color AQUA = new Color (0, 255, 255);

		private Color getTypeColor (int row, int col)
		{
			Color result = Color.LIGHT_GRAY; // nothing
			if (info.isHeaderRow(row))
			{
				result = LIGHT_YELLOW;
			}
			else if (info.isDataRow(row))
			{
				ColumnType type = info.getColumnType(col);
				switch (type)
				{
				case COL_ID_REG: result = LIGHT_GREEN; break;
				case COL_NUMBER: result = Color.WHITE; break;
				case COL_STRING: result = LIGHT_MAGENTA; break;
				case COL_SYSCODE_REG: result = LIGHT_RED; break;
				
				case COL_ID_TAR: result = LIGHT_BLUE; break;
				case COL_SYSCODE_TAR: result = Color.ORANGE; break;
				case COL_PMID: result = AQUA; break;
				}
			}
			return result;
		}
	}


	private RipImportInformation info;

	public RipColumnTableModel (RipImportInformation info)
	{
		this.info = info;
	}

	public void refresh()
	{
		fireTableStructureChanged();
	}

	public TableCellRenderer getTableCellRenderer()
	{
		return new HighlightedCellRenderer(info);
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

	public java.awt.Component getTableCellRendererComponent(
			javax.swing.JTable arg0, Object arg1, boolean arg2, boolean arg3,
			int arg4, int arg5) {
		// TODO Auto-generated method stub
		return null;
	}
}