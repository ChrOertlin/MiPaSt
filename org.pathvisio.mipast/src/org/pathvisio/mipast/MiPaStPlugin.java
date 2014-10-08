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

package org.pathvisio.mipast;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.plugin.Plugin;
import org.pathvisio.mipast.gui.DatasetLoadingScreen;

/**
 * 
 * @author ChrOertlin
 * @author mkutmon
 * 
 * This class implements the PathVisio plugin 
 * interface and also registeres the plugin
 * with the OSGi registry.
 * The MiPaSt menu is added when initialized by
 * the PvDesktop
 *
 */
public class MiPaStPlugin implements BundleActivator, Plugin {
	
	private JMenu miPaStMenu;
	private PvDesktop desktop;
	private JMenuItem menuLoadFiles;
	private JMenuItem help;
	
	/**
	 * init gets called by PvDesktop to
	 * initialize the plugin
	 */
	@Override
	public void init(PvDesktop desktop) {
		this.desktop = desktop;
		JMenu menu = createMiPaStMenu();
		desktop.registerSubMenu("Plugins", menu);
	}

	/**
	 * creates MiPaSt Menu that is added in
	 * the PathVisio 'Plugins' menu
	 */
	public JMenu createMiPaStMenu(){
		miPaStMenu = new JMenu("MiPaStMenu");
		
		menuLoadFiles = new JMenuItem("Load dataset files");
		menuLoadFiles.addActionListener(new menuLoadFilesActionListener());
		
		help = new JMenuItem("Help");
		help.addActionListener(new HelpActionListener());
		
		miPaStMenu.add(menuLoadFiles);
		miPaStMenu.add(help);
		
		return miPaStMenu;
	}

	@Override
	public void done() {
		desktop.unregisterSubMenu("Plugins", miPaStMenu);
	}

	/**
	 * Opens URL to help page of the plugin
	 */
	private class HelpActionListener implements ActionListener{
		
		public void helpURL() throws Exception{
			Desktop helpBrowse= Desktop.getDesktop();
			helpBrowse.browse(new URI("https://www.google.com"));
		}
		
		public void actionPerformed(ActionEvent e){
			try {
				helpURL();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Opens file import dialog
	 */
	private class menuLoadFilesActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			DatasetLoadingScreen wizard = new DatasetLoadingScreen(desktop);
			wizard.showModalDialog(desktop.getSwingEngine().getFrame());
		}
	}
	
	
	@Override
	public void start(BundleContext context) throws Exception {
		context.registerService(Plugin.class.getName(), this, null);
		
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		this.done();
	}
}
