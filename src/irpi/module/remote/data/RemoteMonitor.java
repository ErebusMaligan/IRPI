package irpi.module.remote.data;

import irpi.module.remote.RemoteConstants;
import ssh.SSHSession;
import state.control.BroadcastManager;
import state.monitor.AbstractMonitor;
import state.monitor.MonitorData;
import state.monitor.MonitorManager;

public class RemoteMonitor extends AbstractMonitor {

	public RemoteMonitor( MonitorManager manager, BroadcastManager broadcast, MonitorData data, SSHSession ssh ) {
		super( manager, broadcast, data, ssh, 1000l );
		log = false;
	}
	
	public void parseFile( String file ) {
		queueCDL( createAction( RemoteConstants.PRINT_FILE, RemoteConstants.PRINT_FILE + " " + file ) );
	}
	
	public void sendCommand( String remote, String commandName ) {
		queueCDL( createAction( RemoteConstants.IRSEND, RemoteConstants.IRSEND + " " + RemoteConstants.SEND_ONCE + " " + remote + " " + commandName ) );
	}

	@Override
	protected void runOnce() throws InterruptedException {}
	
	@Override
	protected void runLoop() throws InterruptedException {}
}