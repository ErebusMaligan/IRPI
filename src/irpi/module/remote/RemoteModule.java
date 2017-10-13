package irpi.module.remote;

import java.util.Arrays;
import java.util.List;

import gui.windowmanager.WindowManager;
import irpi.module.remote.data.device.RemoteMapData;
import irpi.module.remote.data.device.RemoteMonitor;
import irpi.module.remote.data.macro.MacroData;
import irpi.module.remote.ui.MacroManager;
import irpi.module.remote.ui.RemoteTree;
import irpi.module.remote.ui.SentMonitor;
import module.AppModule;
import module.spi.SPIDataMonitorProvider;
import module.spi.SPIMonitorDataProvider;
import module.spi.SPIWindowDefinitionProvider;
import ssh.SSHSession;
import state.control.BroadcastManager;
import state.monitor.AbstractMonitor;
import state.monitor.MonitorData;
import state.monitor.MonitorManager;

public class RemoteModule extends AppModule implements SPIDataMonitorProvider, SPIMonitorDataProvider, SPIWindowDefinitionProvider {

	public static final String REMOTE_DATA = RemoteMapData.class.getName();
	
	public static final String REMOTE_MONITOR = RemoteMonitor.class.getName();
	
	public static final String MACRO_DATA = MacroData.class.getName();
	
	private RemoteMapData rd;
	
	private MacroData md;
	
	private RemoteMonitor rm;

	@Override
	public void init() {
		rd = new RemoteMapData();
		md = new MacroData();
	}

	@Override
	public void shutdown() {}

	
	@Override
	public void loadWindowDefinitions() {
		WindowManager.addWindowDefinition( new RemoteTree() );
		WindowManager.addWindowDefinition( new SentMonitor() );
		WindowManager.addWindowDefinition( new MacroManager() );
	}

	@Override
	public List<MonitorData> getMonitorData() {
		return Arrays.asList( rd, md );
	}

	@Override
	public List<AbstractMonitor> getDataMonitors() {
		return Arrays.asList( rm );
	}

	@Override
	public void initDataMonitors( MonitorManager manager, BroadcastManager broadcast, SSHSession ssh ) {
		rm = new RemoteMonitor( manager, broadcast, rd, ssh );
	}
}