package org.pathvisio.mipast;

import javax.swing.JMenu;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.plugin.Plugin;

public class MiPaStPlugin implements BundleActivator, Plugin {
	
	private JMenu miPaStMenu;
	private PvDesktop desktop;
	
	@Override
	public void init(PvDesktop desktop) {
		this.desktop = desktop;
		miPaStMenu = new JMenu("MiPaStMenu");
		desktop.registerSubMenu("Plugins", miPaStMenu);
	}

	@Override
	public void done() {
		desktop.unregisterSubMenu("Plugins", miPaStMenu);
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
