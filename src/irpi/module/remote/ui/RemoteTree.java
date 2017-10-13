package irpi.module.remote.ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import gui.layout.WrapLayout;
import gui.windowmanager.WindowDefinition;
import irpi.module.remote.RemoteConstants;
import irpi.module.remote.RemoteModule;
import irpi.module.remote.data.device.RemoteMapData;
import irpi.module.remote.data.device.RemoteMapData.DATA_TYPES;
import irpi.module.remote.data.device.RemoteMonitor;
import irpi.module.remote.data.macro.MacroData;
import state.provider.ApplicationProvider;
import statics.GU;
import statics.UIUtils;

public class RemoteTree implements WindowDefinition, Observer {

	private JTextField file = new JTextField( "/etc/lirc/lircd.conf", 20 );
	
	private ApplicationProvider provider;
	
	private JTabbedPane remoteTab = new JTabbedPane();
	
	private JTabbedPane split = new JTabbedPane();
	
	private JPanel macro = new JPanel();
	
	@Override
	public JComponent getCenterComponent( Object provider ) {
		this.provider = (ApplicationProvider)provider;
		
		JPanel parse = new JPanel();
		parse.setLayout( new BoxLayout( parse, BoxLayout.X_AXIS ) );
		JButton b = new JButton( "Add File Contents" );
		b.addActionListener( e -> ((RemoteMonitor)this.provider.getMonitorManager().getMonitorByName( RemoteModule.REMOTE_MONITOR ) ).parseFile( file.getText() ) );
		Arrays.asList( file, b ).forEach( c -> { 
			parse.add( c );
			GU.spacer( parse );
		} );
		
		JPanel center = new JPanel( new BorderLayout() );
		JPanel north = new JPanel();
		macro.setLayout( new WrapLayout() );
		north.setLayout( new BoxLayout( north, BoxLayout.Y_AXIS ) );
		north.add( parse );
		GU.spacer( north );
		center.add( north, BorderLayout.NORTH );
		JScrollPane mScroll = new JScrollPane( macro );
		UIUtils.setJScrollPane( mScroll );
		mScroll.getVerticalScrollBar().setUnitIncrement( 15 );
		split.addTab( "Macros", mScroll );
		split.addTab( "Devices", center );
		center.add( remoteTab, BorderLayout.CENTER );
		
		UIUtils.setJButton( b );
		UIUtils.setColorsRecursive( split );
		Arrays.asList( remoteTab, split ).forEach( c -> UIUtils.setTabUI( c ) );
		Arrays.asList( RemoteModule.REMOTE_DATA, RemoteModule.MACRO_DATA ).forEach( s -> this.provider.getMonitorManager().getDataByName( s ).addObserver( this ) );
		return split;
	}

	private void loadMacros() {
		macro.removeAll();
		MacroData md =  (MacroData)this.provider.getMonitorManager().getDataByName( RemoteModule.MACRO_DATA );
		md.getMacros().forEach( m -> {
			macro.add( IRPIUIUtils.createButton( m.toString(), e -> ((RemoteMonitor)this.provider.getMonitorManager().getMonitorByName( RemoteModule.REMOTE_MONITOR ) ).sendMacro( m ) ) );
		} );
		macro.revalidate();
	}
	
	@Override
	public String getTitle() {
		return RemoteConstants.WD_REMOTE_TREE;
	}
	
	@Override
	public void closed() {
		Arrays.asList( RemoteModule.REMOTE_DATA, RemoteModule.MACRO_DATA ).forEach( s -> provider.getMonitorManager().getDataByName( s ).deleteObserver( this ) );
	}

	@Override
	public void update( Observable o, Object arg ) {
		MacroData md =  (MacroData)this.provider.getMonitorManager().getDataByName( RemoteModule.MACRO_DATA );
		if ( o.equals( md ) ) {
			loadMacros();
		} else {
			if ( arg.equals( DATA_TYPES.CODES ) ) {
				remoteTab.removeAll();
				RemoteMapData data = (RemoteMapData)provider.getMonitorManager().getDataByName( RemoteModule.REMOTE_DATA );
				data.getRemoteNames().forEach( s -> {
					JPanel remote = new JPanel();
					UIUtils.setColors( remote );
					remote.setLayout( new WrapLayout() );
					Map<String, String> codes = data.getRemoteCodes( s );
					List<String> l = new ArrayList<>( codes.keySet() );
					Collections.sort( l );
					l.forEach( k -> remote.add( IRPIUIUtils.createButton( k, e -> ((RemoteMonitor)this.provider.getMonitorManager().getMonitorByName( RemoteModule.REMOTE_MONITOR ) ).sendCommand( s, k ) ) ) );
					JScrollPane scroll = new JScrollPane( remote );
					UIUtils.setJScrollPane( scroll );
					scroll.getVerticalScrollBar().setUnitIncrement( 15 );
					SwingUtilities.invokeLater( () -> remoteTab.addTab( s, scroll ) );
				} );
			}
		}
	}
}