package irpi.module.remote.data.device;

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
import xml.XMLExpansion;
import xml.XMLValues;

public class RemoteMapData extends MonitorData implements ProcessStreamSiphon, XMLValues {

	public static enum DATA_TYPES { CODES, LAST_CODE };
	
	private Map<String, Map<String, String>> mapping = new HashMap<>();
	
	private boolean remoteBlock = false;
	
	private boolean codesBlock = false;
	
	private boolean rawCodesBlock = false;
	
	private boolean rawBlock = false;
	
	private String rawCode = "";
	
	private String rawName = null;
	
	private String remoteName;
	
	private String[] lastSentCode = new String[ 3 ];
	
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
					dataChanged( DATA_TYPES.CODES );
				}
				if ( l.startsWith( "end codes" ) ) {
					codesBlock = false;
					dataChanged( DATA_TYPES.CODES );
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
							rawCode = "";
						} else if ( !rawBlock && l.startsWith( "name" ) ) {
							rawName = l.split( " " )[ 1 ];
							rawBlock = true;
						} else if ( rawBlock && !l.startsWith( "name" ) ) {
							for ( String s : l.split( " " ) ) {
								rawCode += s + " ";
							}
						}
					}
				}
			}
		} );
		
		skimmers.put( RemoteConstants.IRSEND, line -> {
			if ( line.contains( "SEND_ONCE" ) ) {
				String[] codes = line.split( "SEND_ONCE" )[ 1 ].trim().split( " " );
				lastSentCode[ 0 ] = codes[ 0 ];
				lastSentCode[ 1 ] = codes[ 1 ];
				lastSentCode[ 2 ] = mapping.get( codes[ 0 ] ).get( codes[1 ] );
				dataChanged( DATA_TYPES.LAST_CODE );
			}
		} ); //this doesn't have any feedback, but needs to have a skimmer assigned to the command
		ProcessManager.getInstance().registerSiphon( IRPIConstants.SSH_MASTER_PROCESS_NAME, this );
	}
	
	private void dataChanged( DATA_TYPES type ) {
		notifyObservers( type );
	}
	
	public List<String> getRemoteNames() {
		ArrayList<String> ret = new ArrayList<>( mapping.keySet() );
		Collections.sort( ret );
		return Collections.unmodifiableList( ret );
	}
	
	public Map<String, String> getRemoteCodes( String name ) {
		return Collections.unmodifiableMap( mapping.get( name ) );
	}
	
	public String[] getLastSentCode() {
		return lastSentCode;
	}
	
	@Override
	public void notifyProcessEnded( String arg0 ) {}

	@Override
	public void notifyProcessStarted( String arg0 ) {}

	@Override
	public List<XMLValues> getChildNodes() {
		List<XMLValues> ret = new ArrayList<>();
		for ( String device : mapping.keySet() ) {
			ret.add( new DeviceXMLValues( device, mapping.get( device ) ) ); 
		}
		return ret;
	}

	@Override
	public void loadParamsFromXMLValues( XMLExpansion e ) {
		e = e.getChild( IRPIConstants.DEVICES );
		e.getChildren( IRPIConstants.DEVICE ).forEach( c -> new DeviceXMLValues( null, null ).loadParamsFromXMLValues( c ) );
		dataChanged( DATA_TYPES.CODES );
	}

	@Override
	public Map<String, Map<String, String[]>> saveParamsAsXML() {
		Map<String, Map<String, String[]>> ret = new HashMap<>();
		ret.put( IRPIConstants.DEVICES, null );
		return ret;
	}
	
	
	
	private class DeviceXMLValues implements XMLValues {
		
		private String name;
		
		private Map<String, String> codes;
		
		public DeviceXMLValues( String name, Map<String, String> codes ) {
			this.name = name;
			this.codes = codes;
		}
		
		@Override
		public List<XMLValues> getChildNodes() {
			List<XMLValues> ret = new ArrayList<>();
			for ( String code : codes.keySet() ) {
				ret.add( new CodeXMLValues( name, code, codes.get( code ) ) ); 
			}
			return ret;
		}
		
		@Override
		public void loadParamsFromXMLValues( XMLExpansion e ) {
			name = e.get( IRPIConstants.DEVICE_NAME );
			mapping.put( name, new HashMap<>() );
			e.getChildren( IRPIConstants.CODE ).forEach( c -> new CodeXMLValues( name, null, null ).loadParamsFromXMLValues( c ) );
		}
		
		@Override
		public Map<String, Map<String, String[]>> saveParamsAsXML() {
			Map<String, Map<String, String[]>> ret = new HashMap<>();
			Map<String, String[]> v = new HashMap<>();
			v.put( IRPIConstants.DEVICE_NAME, new String[] { name } );
			ret.put( IRPIConstants.DEVICE, v );
			return ret;
		}
	}
	
	private class CodeXMLValues implements XMLValues {
		
		private String device;
		
		private String name;
		
		private String value;
		
		public CodeXMLValues( String device, String name, String value ) {
			this.device = device;
			this.name = name;
			this.value = value;
		}
		
		@Override
		public List<XMLValues> getChildNodes() {
			return null;
		}
		
		@Override
		public void loadParamsFromXMLValues( XMLExpansion e ) {
			name = e.get( IRPIConstants.CODE_NAME );
			value = e.get( IRPIConstants.CODE_VALUE );
			mapping.get( device ).put( name, value );
		}
		
		@Override
		public Map<String, Map<String, String[]>> saveParamsAsXML() {
			Map<String, Map<String, String[]>> ret = new HashMap<>();
			Map<String, String[]> v = new HashMap<>();
			v.put( IRPIConstants.CODE_VALUE, new String[] { value } );
			v.put( IRPIConstants.CODE_NAME, new String[] { name } );
			ret.put( IRPIConstants.CODE, v );
			return ret;
		}
	}
}
