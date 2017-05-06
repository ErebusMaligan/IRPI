package irpi.app.ui;

import java.awt.BorderLayout;
import java.awt.GraphicsDevice;

import irpi.app.ui.menu.IRMenuBar;
import state.provider.ApplicationProvider;
import statics.UIUtils;
import ui.window.AbstractApplicationFrame;

public class IRPIFrame extends AbstractApplicationFrame {

	private static final long serialVersionUID = 1L;
	
	private ApplicationProvider provider;
	
	public IRPIFrame( ApplicationProvider provider, GraphicsDevice d  ) {
		super( provider.getTabManager(), provider.getSSHManager(), provider.getMonitorManager(), provider, d );
		this.provider = provider;
		finishConstructor();
	}
	
	@Override
	public void construct() {
		setLayout( new BorderLayout() );
		add( provider.getTabManager().getTabPane(), BorderLayout.CENTER );
		add( new IRMenuBar( provider ), BorderLayout.NORTH );
	}

	@Override
	public void finishInit() {
		UIUtils.setColors( this.getContentPane() );		
		setVisible( true );
	}
}