package org.pathvisio.mipast;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;

import java.awt.Desktop;
import java.net.URI;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JFrame;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.plugin.Plugin;
import org.pathvisio.mipast.gui.DatasetLoadingScreen;

public class MiPaStPlugin implements BundleActivator, Plugin {
	
	private JMenu miPaStMenu;
	private PvDesktop desktop;
	private JMenuItem menuLoadFiles;
	private JMenuItem help;
	private JMenuItem documentation;
	private MiPaStPlugin plugin;
	
	@Override
	public void init(PvDesktop desktop) {
		this.desktop = desktop;
		
		
		menuItems();
	}

	public void menuItems(){
		miPaStMenu = new JMenu("MiPaStMenu");
		desktop.registerSubMenu("Plugins", miPaStMenu);
		
		menuLoadFiles = new JMenuItem("Load dataset files");
		menuLoadFiles.addActionListener(new menuLoadFilesActionListener());
		
		help = new JMenuItem("Help");
		help.addActionListener(new HelpActionListener());
		
		documentation = new JMenuItem("Documentation");
		documentation.addActionListener(new DocumentationActionListener());
		
		miPaStMenu.add(menuLoadFiles);
		miPaStMenu.add(help);
		miPaStMenu.add(documentation);
		
	}

	
	@Override
	public void done() {
		desktop.unregisterSubMenu("Plugins", miPaStMenu);
	}

	//Opens URL to help page of the plugin
	class HelpActionListener implements ActionListener{
		
		public void helpURL() throws Exception{
		Desktop helpBrowse= Desktop.getDesktop();
		helpBrowse.browse(new URI("https://www.google.com"));}
		
		public void actionPerformed(ActionEvent e){
		try {
			helpURL();
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}
		
		
		    
		}
		
	}
	
	class DocumentationActionListener implements ActionListener{
		
		public void documentationURL() throws Exception{
		Desktop documentationBrowse= Desktop.getDesktop();
		documentationBrowse.browse(new URI("https://www.google.com"));}
		
		public void actionPerformed(ActionEvent e){
		try {
			documentationURL();
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}
		
		
		    
		}
		
	
		}
		
	
		
	class menuLoadFilesActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			DatasetLoadingScreen dls = new DatasetLoadingScreen(desktop,plugin);
			dls.setVisible(true);
		
		}
	}
	
	
// Bundle Activator Starts the plugin withit PathVisio
	@Override
	public void start(BundleContext context) throws Exception {
		context.registerService(Plugin.class.getName(), this, null);
		
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		this.done();
	}


}
