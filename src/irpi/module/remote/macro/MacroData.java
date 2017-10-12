package irpi.module.remote.macro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import irpi.app.constants.IRPIConstants;
import state.monitor.MonitorData;
import xml.XMLExpansion;
import xml.XMLValues;

public class MacroData extends MonitorData implements XMLValues {

	private Map<String, Macro> macros = new HashMap<>();
	
	public List<Macro> getMacros() {
		List<Macro> ret = new ArrayList<>( macros.values() );
		Collections.sort( ret );
		return ret;
	}
	
	public List<String> getMacroNames() {
		return new ArrayList<>( macros.keySet() );
	}
	
	public void addMacro( Macro macro ) {
		macros.put( macro.getName(), macro );
		this.setChanged();
		this.notifyObservers();
	}

	@Override
	public List<XMLValues> getChildNodes() {
		return new ArrayList<>( macros.values() );
	}

	@Override
	public void loadParamsFromXMLValues( XMLExpansion e ) {
		e = e.getChild( IRPIConstants.MACROS );
		e.getChildren( IRPIConstants.MACRO ).forEach( c -> { 
			Macro m = new Macro( null );
			m.loadParamsFromXMLValues( c );
			addMacro( m );
		} );
	}

	@Override
	public Map<String, Map<String, String[]>> saveParamsAsXML() {
		Map<String, Map<String, String[]>> ret = new HashMap<>();
		ret.put( IRPIConstants.MACROS, null );
		return ret;
	}
	
	@Override
	public void notifyProcessEnded( String arg0 ) {}

	@Override
	public void notifyProcessStarted( String arg0 ) {}
}