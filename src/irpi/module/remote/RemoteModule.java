package irpi.module.remote;

import java.util.Arrays;
import java.util.List;

import gui.windowmanager.WindowManager;
import irpi.module.remote.data.RemoteMapData;
import irpi.module.remote.data.RemoteMonitor;
import irpi.module.remote.ui.RemoteTree;
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
	
	private RemoteMapData rd;
	
	private RemoteMonitor rm;

	@Override
	public void init() {
		rd = new RemoteMapData();
	}

	@Override
	public void shutdown() {}

	
	@Override
	public void loadWindowDefinitions() {
		WindowManager.addWindowDefinition( new RemoteTree() );
	}

	@Override
	public List<MonitorData> getMonitorData() {
		return Arrays.asList( rd );
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
