package irpi.app.ui.menu;

import static irpi.app.constants.IRPIConstants.MB_AUTO_START;
import static irpi.app.constants.IRPIConstants.MB_COLOR;
import static irpi.app.constants.IRPIConstants.MB_CREATE_TAB;
import static irpi.app.constants.IRPIConstants.MB_DEVICE_LOAD;
import static irpi.app.constants.IRPIConstants.MB_DEVICE_SAVE;
import static irpi.app.constants.IRPIConstants.MB_FILE;
import static irpi.app.constants.IRPIConstants.MB_FRAME;
import static irpi.app.constants.IRPIConstants.MB_MACRO_LOAD;
import static irpi.app.constants.IRPIConstants.MB_MACRO_SAVE;
import static irpi.app.constants.IRPIConstants.MB_REMOVE_TAB;
import static irpi.app.constants.IRPIConstants.MB_SETTINGS;
import static irpi.app.constants.IRPIConstants.MB_SSH;
import static irpi.app.constants.IRPIConstants.MB_SSH_CONNECT;
import static irpi.app.constants.IRPIConstants.MB_SSH_DISCONNECT;
import static irpi.app.constants.IRPIConstants.MB_SYS_MONITOR;
import static irpi.app.constants.IRPIConstants.MB_SYS_MONITOR_START;
import static irpi.app.constants.IRPIConstants.MB_SYS_MONITOR_STOP;
import static irpi.app.constants.IRPIConstants.MB_TABS;
import static irpi.app.constants.IRPIConstants.MB_WINDOWS;
import static irpi.app.constants.IRPIConstants.MB_WINDOW_MANAGEMENT;

import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.plaf.basic.BasicMenuBarUI;

import gui.dialog.OKCancelDialog;
import gui.menubar.GenericActionListener;
import gui.menubar.GenericMenuBar;
import gui.menubar.GenericMenuBarAction;
import irpi.app.IRPI;
import irpi.app.constants.IRPIConstants;
import irpi.module.remote.RemoteConstants;
import irpi.module.remote.RemoteModule;
import irpi.module.remote.data.device.RemoteMapXMLDocumentHandler;
import irpi.module.remote.data.macro.MacroData;
import irpi.module.remote.data.macro.MacroXMLDocumentHandler;
import irpi.module.remote.ui.DefaultFileLocationsDialog;
import ssh.SSHConstants;
import ssh.SSHSession;
import state.provider.ProviderConstants;
import statics.LAFUtils;
import statics.UIUtils;
import ui.log.OutputLogConstants;
import ui.ssh.SSHDialog;
import ui.theme.BasicColorDialog;
import ui.theme.ThemeConstants;
import ui.window.CreateTabDialog;
import xml.XMLValues;

/**
 * @author Daniel J. Rivers
 *         2015
 *
 * Created: Apr 24, 2015, 8:51:30 PM 
 */
public class IRMenuBar extends GenericMenuBar implements Observer {

	private static final long serialVersionUID = 1L;

	private IRPI state;

	public IRMenuBar( IRPI state ) {
		this.state = state;
		for ( SSHSession ssh : state.getSSHManager().getSSHSessions() ) {
			ssh.addObserver( this );
		}
		createFileMenu();
		createSSHMenu();
		createSystemMonitorMenu();
		createSettingsMenu();
		createWindowsMenu();
		this.add( Box.createGlue() );
		update( null, false );

		this.setUI( new BasicMenuBarUI() );
		this.setMinimumSize( new Dimension( 0, 22 ) );
		this.setPreferredSize( new Dimension( this.getWidth(), 22 ) );
		LAFUtils.applyThemedUI( this, ThemeConstants.BACKGROUND, ThemeConstants.FOREGROUND );

		UIUtils.setColors( this );
		this.setBorder( BorderFactory.createLineBorder( ThemeConstants.FOREGROUND ) );
	}

	private void createFileMenu() {
		menu = new JMenu( MB_FILE );
		UIUtils.setColors( menu );
		createItem( MB_DEVICE_SAVE, o -> new RemoteMapXMLDocumentHandler( (XMLValues)state.getMonitorManager().getDataByName( RemoteModule.REMOTE_DATA ) ).createDoc() );
		createItem( MB_DEVICE_LOAD, o -> new RemoteMapXMLDocumentHandler( (XMLValues)state.getMonitorManager().getDataByName( RemoteModule.REMOTE_DATA ) ).loadDoc( null ) );
		menu.add( new JSeparator() );
		createItem( MB_MACRO_SAVE, o -> new MacroXMLDocumentHandler( ( (MacroData)state.getMonitorManager().getDataByName( RemoteModule.MACRO_DATA ) ) ).createDoc() );
		createItem( MB_MACRO_LOAD, o -> new MacroXMLDocumentHandler( ( (MacroData)state.getMonitorManager().getDataByName( RemoteModule.MACRO_DATA ) ) ).loadDoc( null ) );
		this.add( menu );
	}
	
	private void createSSHMenu() {
		menu = new JMenu( MB_SSH );
		UIUtils.setColors( menu );
		createItem( MB_SSH_CONNECT, o -> {
			for ( SSHSession ssh : state.getSSHManager().getSSHSessions() ) {
				ssh.connect();
			}
		} );
		menu.add( new JSeparator() );
		createItem( MB_SSH_DISCONNECT, o -> {
			for ( SSHSession ssh : state.getSSHManager().getSSHSessions() ) {
				ssh.disconnect();
			}
		} );
		this.add( menu );
	}

	private void createSystemMonitorMenu() {
		menu = new JMenu( MB_SYS_MONITOR );
		UIUtils.setColors( menu );
		createItem( MB_SYS_MONITOR_START, o -> state.getMonitorManager().startAllMonitors() );
		menu.add( new JSeparator() );
		createItem( MB_SYS_MONITOR_STOP, o -> state.getMonitorManager().stopAllMonitors() );
		this.add( menu );
	}

	private void createSettingsMenu() {
		menu = new JMenu( MB_SETTINGS );
		UIUtils.setColors( menu );
		createCheckItem( MB_AUTO_START, o -> {
			ProviderConstants.AUTO = !ProviderConstants.AUTO;
			state.writeSettings();
		}, ProviderConstants.AUTO );
		menu.add( new JSeparator() );
		createItem( IRPIConstants.MB_DEFAULT_FILES, o -> new DefaultFileLocationsDialog( state ).setVisible( true ) );
		menu.add( new JSeparator() );
		createItem( MB_FRAME, o -> state.getFrame().saveWindowSettings( state ) );
		createItem( MB_SSH, o -> new SSHDialog( state.getFrame(), state ).setVisible( true ) );
		createItem( MB_COLOR, o -> new BasicColorDialog( state ).setVisible( true ) );
//		menu.add( new JSeparator() );  //setting debug mode works, but at the moment there is nothing that turns on as a result of the setting
//		createCheckItem( MB_DEBUG_MODE, o -> {
//			DEBUG = !DEBUG;
//			state.writeSettings();
//		}, DEBUG );
		this.add( menu );
	}

	private void createWindowsMenu() {
		JMenu win = menu = new JMenu( MB_WINDOW_MANAGEMENT );
		UIUtils.setColors( menu );
		menu = new JMenu( MB_TABS );
		UIUtils.setColors( menu );
		createItem( MB_CREATE_TAB, o -> {
			CreateTabDialog t = new CreateTabDialog( state.getFrame() );
			t.setVisible( true );
			if ( t.getResult() == OKCancelDialog.OK ) {
				state.getTabManager().addTab( t.getTabName() );
			}
		} );
		createItem( MB_REMOVE_TAB, o -> state.getTabManager().removeSelectedTab() );
		win.add( menu );
		win.add( new JSeparator() );

		JMenu windows = menu = new JMenu( MB_WINDOWS );
		UIUtils.setColors( menu );
		createWindowItem( OutputLogConstants.WD_OUTPUT_LOG );
		menu.add( new JSeparator() );
		createWindowItem( SSHConstants.WD_SSH );
		menu.add( new JSeparator() );
		createWindowItem( RemoteConstants.WD_REMOTE_TREE );
		menu.add( new JSeparator() );
		createWindowItem( RemoteConstants.WD_SEND_MONITOR );
		win.add( windows );
		menu.add( new JSeparator() );
		createWindowItem( RemoteConstants.WD_MACRO_MANAGER );
		win.add( windows );

		menu = win;
		this.add( menu );
	}

	private void createWindowItem( String command ) {
		createItem( command, o -> state.getTabManager().instantiateWindow( command, null ) );
	}

	@Override
	public void createBaseItem( GenericMenuBarAction action ) {
		item.addActionListener( new GenericActionListener( action, this ) );
		UIUtils.setColors( item );
		menu.add( item );
		buttonMap.put( item.getText(), item );
	}

	@Override
	public void update( Observable o, Object arg ) {
		boolean c = (Boolean)arg;
		if ( c ) {
			enabled( buttonMap.get( MB_SSH_CONNECT ), false );
			enabled( buttonMap.get( MB_SSH_DISCONNECT ), true );
		} else {
			enabled( buttonMap.get( MB_SSH_CONNECT ), true );
			enabled( buttonMap.get( MB_SSH_DISCONNECT ), false );
		}
	}

	private void enabled( JMenuItem item, boolean b ) {
		if ( b ) {
			UIUtils.setColors( item );
		} else { //if you don't do this, the items stay theme colored, even when disabled which is stupid...
			item.setForeground( null );
			item.setBackground( null );
		}
		item.setEnabled( b );
	}
}