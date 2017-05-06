package irpi.module.remote.ui;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import gui.layout.WrapLayout;
import gui.windowmanager.WindowDefinition;
import irpi.module.remote.RemoteConstants;
import irpi.module.remote.RemoteModule;
import irpi.module.remote.data.RemoteMapData;
import irpi.module.remote.data.RemoteMonitor;
import state.provider.ApplicationProvider;
import statics.GU;
import statics.UIUtils;

public class RemoteTree implements WindowDefinition, Observer {

	private JTextField file = new JTextField( "/etc/lirc/lircd.conf", 20 );
	
	private ApplicationProvider provider;
	
	private JTabbedPane remoteTab = new JTabbedPane();
	
	@Override
	public JComponent getCenterComponent( Object provider ) {
		this.provider = (ApplicationProvider)provider;
		JPanel ret = new JPanel( new BorderLayout() );
		JPanel parse = new JPanel();
		parse.setLayout( new BoxLayout( parse, BoxLayout.Y_AXIS ) );
		JButton b = new JButton( "Add File Contents" );
		b.addActionListener( e -> ((RemoteMonitor)this.provider.getMonitorManager().getMonitorByName( RemoteModule.REMOTE_MONITOR ) ).parseFile( file.getText() ) );
		Arrays.asList( file, b ).forEach( c -> { 
			parse.add( c );
			GU.spacer( parse );
		} );
		ret.add( parse, BorderLayout.NORTH );
		ret.add( remoteTab, BorderLayout.CENTER );
		UIUtils.setJButton( b );
		UIUtils.setColorsRecursive( ret );
		UIUtils.setTabUI( remoteTab );
		this.provider.getMonitorManager().getDataByName( RemoteModule.REMOTE_DATA ).addObserver( this );
		return ret;
	}

	@Override
	public String getTitle() {
		return RemoteConstants.WD_REMOTE_TREE;
	}
	
	@Override
	public void closed() {
		provider.getMonitorManager().getDataByName( RemoteModule.REMOTE_DATA ).deleteObserver( this );
	}

	@Override
	public void update( Observable o, Object arg ) {
		remoteTab.removeAll();
		RemoteMapData data = (RemoteMapData)provider.getMonitorManager().getDataByName( RemoteModule.REMOTE_DATA );
		data.getRemoteNames().forEach( s -> {
			JPanel remote = new JPanel();
			UIUtils.setColors( remote );
			remote.setLayout( new WrapLayout() );
			data.getRemoteCodes( s ).forEach( (k, v) -> {
				JButton b = GU.createButton( k, e -> ((RemoteMonitor)this.provider.getMonitorManager().getMonitorByName( RemoteModule.REMOTE_MONITOR ) ).sendCommand( s, k ) );
				UIUtils.setJButton( b );
				remote.add( b );
			} );
			remoteTab.addTab( s, remote );
		} );
	}
}