package org.pathvisio.mipast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperStack;
import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.core.util.FileUtils;
import org.pathvisio.desktop.gex.GexManager;
import org.pathvisio.gexplugin.GexTxtImporter;
import org.pathvisio.gexplugin.ImportInformation;
import org.pathvisio.rip.util.RipImportInformation;


public class TestMain {
	private static IDMapperStack loadedGdbs = new IDMapperStack();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PreferenceManager.init();
		GexManager gexManager = new GexManager();
		ImportInformation info = new ImportInformation();
		RipImportInformation ripInfo = new RipImportInformation();
		
		String idColNum = "0";
		String sysColNum = "1";
		String syscode = "Il";
		String gexFileName = "combinedTxt.txt";
		String resultdir = "/home/bigcat/Desktop";
		String gexfileName = "";
		String dbDir = "/home/bigcat/Desktop";
		String inputfileName = "combinedTxt.txt";

		File dbFile = new File("/home/bigcat/Desktop/Hs_Derby_20130701.bridge");
		IDMapper gdb;
		try {
			Class.forName("org.bridgedb.rdb.IDMapperRdb");
			gdb = BridgeDb.connect("idmapper-pgdb:" + dbFile);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IDMapperException e) {
			e.printStackTrace();
		}

		try {
			info.setTxtFile(new File("/home/bigcat/Desktop/genes.txt"));
			info.setDelimiter("\t");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!idColNum.isEmpty()) {
			int idcolnum = Integer.parseInt(idColNum);
			idcolnum = idcolnum;
			info.setIdColumn(idcolnum);
			System.out.print("Id Column: " + info.getColNames()[idcolnum]);
		}
		if (!sysColNum.isEmpty()) {
			int syscolnum = Integer.parseInt(sysColNum);
			syscolnum = syscolnum;
			info.setSyscodeFixed(false);
			info.setSysodeColumn(syscolnum);
			System.out
					.print("Syscode column: " + info.getColNames()[syscolnum]);
		} else {
			if (syscode != null && !syscode.isEmpty()) {
				info.setSyscodeFixed(true);
				info.setDataSource(DataSource.getBySystemCode(syscode));
			} else {
				System.err
						.print("Neither Syscode or syscode column was specified!");
			}
		}

		if (!gexFileName.isEmpty()) {
			gexfileName = resultdir + gexFileName;
		} else {
			gexfileName = resultdir + inputfileName;
		}

		info.setGexName(gexfileName);
		idmapperLoader(dbDir);
		GexTxtImporter.importFromTxt(info, null, getLoadedGdbs(), gexManager);

	}

	protected static void idmapperLoader(String dbDir) {
		removeLoadedGdbs();
		if (dbDir.contains(";")) {
			String dbFiles[] = dbDir.split(";");
			for (String dbFile : dbFiles) {
				loadGdb(dbFile);
			}
		} else {
			File dbFile = new File(dbDir);
			if (dbFile.exists()) {
				if (dbFile.isDirectory()) {
					loadGdbs(dbDir);
				} else {
					loadGdb(dbDir);
				}
			}
		}
	}

	protected static IDMapper loadGdb(String dbfile) {
		File dbFile = new File(dbfile);
		IDMapper gdb;
		try {
			Class.forName("org.bridgedb.rdb.IDMapperRdb");
			gdb = BridgeDb.connect("idmapper-pgdb:" + dbFile);
			getLoadedGdbs().addIDMapper(gdb);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IDMapperException e) {
			e.printStackTrace();
		}
		return getLoadedGdbs();
	}

	protected static IDMapper loadGdbs(String dbDir) {
		File dbDirectory = new File(dbDir);
		List<File> bridgeFiles = FileUtils
				.getFiles(dbDirectory, "bridge", true);
		if (bridgeFiles.size() != 0) {
			for (File dbFile : bridgeFiles) {
				try {
					Class.forName("org.bridgedb.rdb.IDMapperRdb");
					IDMapper gdb = BridgeDb.connect("idmapper-pgdb:" + dbFile);
					getLoadedGdbs().addIDMapper(gdb);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IDMapperException e) {
					e.printStackTrace();
				}
			}
		}
		return getLoadedGdbs();
	}

	protected List<String> listLoadedGdbs() {
		try {
			Class.forName("org.bridgedb.rdb.IDMapperRdb");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> gdbList = new ArrayList<String>();
		for (IDMapper gdb : getLoadedGdbs().getMappers()) {
			gdbList.add(gdb.toString());
		}
		return gdbList;
	}

	protected void removeGdb(String dbfile) {
		File dbFile = new File(dbfile);
		try {
			Class.forName("org.bridgedb.rdb.IDMapperRdb");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IDMapper gdb = null;
		try {
			gdb = BridgeDb.connect("idmapper-pgdb:" + dbFile);
		} catch (IDMapperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getLoadedGdbs().removeIDMapper(gdb);
	}

	protected static void removeLoadedGdbs() {
		try {
			Class.forName("org.bridgedb.rdb.IDMapperRdb");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (IDMapper gdb : getLoadedGdbs().getMappers()) {
			getLoadedGdbs().removeIDMapper(gdb);
		}
	}

	/**
	 * @return the loadedGdbs
	 */
	public static IDMapperStack getLoadedGdbs() {
		return loadedGdbs;
	}

	/**
	 * @param loadedGdbs
	 *            the loadedGdbs to set
	 */
	public void setLoadedGdbs(IDMapperStack loadedGdbs) {
		TestMain.loadedGdbs = loadedGdbs;
	}
	
	
	

}
