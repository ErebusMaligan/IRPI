package irpi.module.remote.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import gui.windowmanager.WindowDefinition;
import irpi.module.remote.RemoteConstants;
import irpi.module.remote.RemoteModule;
import irpi.module.remote.data.device.RemoteMapData;
import listeners.BasicObservable;
import listeners.BasicObserver;
import state.provider.ApplicationProvider;
import statics.UIUtils;

public class SentMonitor implements WindowDefinition, BasicObserver {

	private ApplicationProvider provider;
	
	private JLabel remote = new JLabel( "", JLabel.LEFT );
	
	private JLabel codeName = new JLabel( "", JLabel.LEFT );
	
	private JTextArea code = new JTextArea();

	@Override
	public JComponent getCenterComponent( Object provider ) {
		this.provider = (ApplicationProvider)provider;
		JPanel ret = new JPanel( new BorderLayout() );
		JPanel north = new JPanel( new GridLayout( 1, 4 ) );
		JLabel r = new JLabel( "Remote Name:  ", JLabel.RIGHT );
		JLabel cn = new JLabel( "Code Name:  ", JLabel.RIGHT);
		JLabel c = new JLabel( "Raw Code:  ", JLabel.LEFT );
		Arrays.asList( r, remote, cn, codeName ).forEach( l -> north.add( l ) );
		JPanel center = new JPanel( new BorderLayout() );
		center.add( c, BorderLayout.NORTH );
		JScrollPane scroll = new JScrollPane( code );
		UIUtils.setJScrollPane( scroll );
		center.add( scroll, BorderLayout.CENTER );
		ret.add( north, BorderLayout.NORTH );
		ret.add( center, BorderLayout.CENTER );
		UIUtils.setColorsRecursive( ret );
		code.setEditable( false );
		code.setLineWrap( true );
		code.setColumns( 80 );
		this.provider.getMonitorManager().getDataByName( RemoteModule.REMOTE_DATA ).addObserver( this );
		return ret;
	}

	@Override
	public String getTitle() {
		return RemoteConstants.WD_SEND_MONITOR;
	}

	@Override
	public void closed() {
		provider.getMonitorManager().getDataByName( RemoteModule.REMOTE_DATA ).deleteObserver( this );
	}
	
	@Override
	public void update( BasicObservable o, Object arg ) {
		if ( arg.equals( RemoteMapData.DATA_TYPES.LAST_CODE ) ) {
			RemoteMapData data = (RemoteMapData)provider.getMonitorManager().getDataByName( RemoteModule.REMOTE_DATA );
			String[] lsc = data.getLastSentCode();
			remote.setText( lsc[ 0 ] );
			codeName.setText( lsc[ 1 ] );
			code.setText( lsc[ 2 ] );
		}
	}

}
