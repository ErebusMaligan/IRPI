package irpi.module.remote.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import irpi.app.constants.IRPIConstants;
import irpi.module.remote.RemoteConstants;
import process.ProcessManager;
import process.io.ProcessStreamSiphon;
import state.monitor.MonitorData;

public class RemoteMapData extends MonitorData implements ProcessStreamSiphon {

	private Map<String, Map<String, String>> mapping = new HashMap<>();
	
	private boolean remoteBlock = false;
	
	private boolean codesBlock = false;
	
	private boolean rawCodesBlock = false;
	
	private boolean rawBlock = false;
	
	private String rawCode = null;
	
	private String rawName = null;
	
	private String remoteName;
	
	public RemoteMapData() {
		skimmers.put( RemoteConstants.PRINT_FILE, line -> {
			String l = line.trim().replaceAll( "\\s+", " " );
			if ( !l.startsWith( "#" ) ) {//bypass comments entirely
				if ( l.startsWith( "begin remote" ) ) {
					remoteBlock = true;
				}
				if ( l.startsWith( "end remote" ) ) {
					remoteBlock = false;
				}
				if ( l.startsWith( "begin raw_codes" ) ) {
					rawCodesBlock = true;
				}
				if ( l.startsWith( "begin codes" ) ) {
					codesBlock = true;
				}
				if ( l.startsWith( "end raw_codes" ) ) {
					rawCodesBlock = false;
					dataChanged();
				}
				if ( l.startsWith( "end codes" ) ) {
					codesBlock = false;
					dataChanged();
				}
				if ( remoteBlock && !codesBlock && !rawCodesBlock && l.startsWith( "name" ) ) {
					remoteName = l.split( " " )[ 1 ];
					mapping.put( remoteName, new HashMap<>() );
				}
				if ( codesBlock ) {
					if ( !l.startsWith( "begin codes" ) ) {
						String[] code = l.split( " " );
						mapping.get( remoteName ).put( code[ 0 ], code[ 1 ] );
					}
				}
				if ( rawCodesBlock ) {
					if ( !l.startsWith( "begin raw_codes" ) ) {
						if ( rawBlock && line.isEmpty() ) {  //these 2 are not the same and need to be in this order
							mapping.get( remoteName ).put( rawName, rawCode.trim() );
							rawBlock = false;
							rawName = null;
						} else if ( rawBlock && l.startsWith( "name" ) ) {
							rawName = l.split( " " )[ 1 ];
						} else if ( rawBlock && !l.startsWith( "name" ) ) {
							for ( String s : l.split( " " ) ) {
								rawCode += s + " ";
							}
						}
						if ( !rawBlock && line.isEmpty() ) {//these 2 are not the same and need to be in this order
							rawBlock = true;
						}
					}
				}
			}
		} );
		
		skimmers.put( RemoteConstants.IRSEND, line -> {} ); //this doesn't have any feedback, but needs to have a skimmer assigned to the command
		ProcessManager.getInstance().registerSiphon( IRPIConstants.SSH_MASTER_PROCESS_NAME, this );
	}
	
	private void dataChanged() {
		setChanged();
		notifyObservers( null );
	}
	
	public List<String> getRemoteNames() {
		ArrayList<String> ret = new ArrayList<>( mapping.keySet() );
		Collections.sort( ret );
		return Collections.unmodifiableList( ret );
	}
	
	public Map<String, String> getRemoteCodes( String name ) {
		return Collections.unmodifiableMap( mapping.get( name ) );
	}
	
	@Override
	public void notifyProcessEnded( String arg0 ) {}

	@Override
	public void notifyProcessStarted( String arg0 ) {}

}
