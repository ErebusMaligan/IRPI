package irpi.app;

import static irpi.app.constants.IRPIConstants.SSH_MASTER_PROCESS_NAME;
import static irpi.app.constants.IRPIConstants.XSETTINGS;

import java.awt.Color;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import gui.windowmanager.WindowManagerConstants;
import irpi.app.constants.IRPIConstants;
import irpi.app.constants.IRPIConstantsDocumentHandler;
import irpi.app.ui.IRPIFrame;
import irpi.module.remote.RemoteModule;
import irpi.module.remote.data.device.RemoteMapXMLDocumentHandler;
import irpi.module.remote.data.macro.MacroData;
import irpi.module.remote.data.macro.MacroXMLDocumentHandler;
import irpi.module.remote.state.DefaultFileSettings;
import module.AppModule;
import module.spi.SPIDataMonitorProvider;
import module.spi.SPIRealTimeMonitorProvider;
import ssh.SSHConstants;
import ssh.SSHSettings;
import state.provider.ApplicationProvider;
import state.provider.AutoSettings;
import state.provider.DebugSettings;
import state.provider.ProviderConstants;
import ui.terminal.os.OSTerminalSettings;
import ui.terminal.panel.TerminalWindowManager;
import ui.theme.BasicColorSettings;
import ui.theme.ThemeConstants;
import ui.window.WindowConstants;
import ui.window.WindowSettings;
import xml.XMLExpansion;
import xml.XMLValues;

public class IRPI extends ApplicationProvider implements XMLValues {

	private AutoSettings autoS;
	
	private DebugSettings debugS;
	
	private WindowSettings winS;
	
	private SSHSettings sshS;
	
	private BasicColorSettings colorS;
	
	private DefaultFileSettings defFileS;
	
	@Override
	protected void handleAutoStart() {
		super.handleAutoStart();
		if ( ProviderConstants.AUTO ) {
			new MacroXMLDocumentHandler( ( (MacroData)getMonitorManager().getDataByName( RemoteModule.MACRO_DATA ) ) ).loadDoc( new File( defFileS.getMacro() ) );
			new RemoteMapXMLDocumentHandler( (XMLValues)getMonitorManager().getDataByName( RemoteModule.REMOTE_DATA ) ).loadDoc( new File( defFileS.getDevice() ) );
		}
	}
	
	@Override 
	protected void initOtherSPI() {
		//init and add all data monitors to monitor map
		manager.getModulesBySPIType( SPIDataMonitorProvider.class ).forEach( m -> { 
			m.initDataMonitors( mm, this, sshm.getSSHSession( SSH_MASTER_PROCESS_NAME ) );
			m.getDataMonitors().forEach( d -> mm.getMonitors().put( d.getClass().getName(), d ) );
		} );
		
		//init and add all RT data monitors to monitor map
		manager.getModulesBySPIType( SPIRealTimeMonitorProvider.class ).forEach( m -> { 
			m.initRTDataMonitors( mm, this, sshm.getSSHSession( SSH_MASTER_PROCESS_NAME ) );
			m.getRTDataMonitors().forEach( d -> mm.getMonitors().put( d.getClass().getName(), d ) );
		} );
	}

	@Override
	protected void init() {
//		WindowConstants.APP_ICON = new ImageIcon( ApplicationConstants.class.getResource( "freenas.png" ) );
		WindowConstants.FRAME_TITLE = "IRPI";
		
		ThemeConstants.BACKGROUND = Color.BLACK;
		ThemeConstants.FOREGROUND = Color.RED;
		ThemeConstants.FOREGROUND_DARKER = ThemeConstants.FOREGROUND.darker().darker().darker().darker();
		ThemeConstants.TRANSPARENT_BG = new Color( ThemeConstants.BACKGROUND.getRed(), ThemeConstants.BACKGROUND.getGreen(), ThemeConstants.BACKGROUND.getBlue(), 96 );
		
		SSHConstants.SSH_SESSION_IDS = new String[] { SSH_MASTER_PROCESS_NAME };
		super.init();
		
		autoS = new AutoSettings();
		debugS = new DebugSettings();
		winS = new WindowSettings();
		sshS = new SSHSettings();
		colorS = new BasicColorSettings();
		defFileS = new DefaultFileSettings();
		
		doc = new IRPIConstantsDocumentHandler( this );
	}
	
	@Override
	protected void initFrame() {
		frame = new IRPIFrame( this, determineMonitor() );
	}

	@Override
	protected List<AppModule> getModuleList() {
		return Arrays.asList( new RemoteModule() );
	}

	@Override
	public List<XMLValues> getChildNodes() { 
		return Arrays.asList( new XMLValues[] { debugS, autoS, winS, sshS, colorS, tab, defFileS } ); 
	}
	
	@Override
	public void loadParamsFromXMLValues( XMLExpansion root ) {
		XMLExpansion e = root;
		if ( root.getChild( XSETTINGS ) != null ) {
			e = root.getChild( XSETTINGS );
		}
		if ( e.getChild( ProviderConstants.XAUTO ) != null ) {
			autoS.loadParamsFromXMLValues( e.getChild( ProviderConstants.XAUTO ) );
		}
		if ( e.getChild( ProviderConstants.XDEBUG ) != null ) {
			debugS.loadParamsFromXMLValues( e.getChild( ProviderConstants.XDEBUG ) );
		}
		if ( e.getChild( WindowConstants.XWINDOW ) != null ) {
			winS.loadParamsFromXMLValues( e.getChild( WindowConstants.XWINDOW ) );
		}
		if ( e.getChild( SSHConstants.XSSH ) != null ) {
			sshS.loadParamsFromXMLValues( e.getChild( SSHConstants.XSSH ) );
		}
		if ( e.getChild( ThemeConstants.XCOLORS ) != null ) {
			colorS.loadParamsFromXMLValues( e.getChild( ThemeConstants.XCOLORS ) );
		}
		if ( e.getChild( WindowManagerConstants.XTABS ) != null ) {
			tab.loadParamsFromXMLValues( e.getChild( WindowManagerConstants.XTABS ) );
		}
		if ( e.getChild( IRPIConstants.XDEFAULT_FILES ) != null ) {
			defFileS.loadParamsFromXMLValues( e.getChild( IRPIConstants.XDEFAULT_FILES ) );
		}
	}

	@Override
	public Map<String, Map<String, String[]>> saveParamsAsXML() {
		Map<String, Map<String, String[]>> ret = new HashMap<String, Map<String, String[]>>();
		Map<String, String[]> values = new HashMap<>();
		ret.put( XSETTINGS, values );
		return ret;
	}
	
	public static void main( String[] args ) {
		TerminalWindowManager.getInstance().OS = System.getProperty( "os.name" ).contains( "Win" ) ? OSTerminalSettings.WINDOWS : OSTerminalSettings.LINUX;
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ( info.getName().contains( "Nimbus" ) ) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch ( ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e ) {
			System.err.println( "Critical JVM Failure!" );
			e.printStackTrace();
		}
		new IRPI();
	}

	public DefaultFileSettings getDefFileS() {
		return defFileS;
	}
}