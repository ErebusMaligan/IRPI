package irpi.module.remote.data.macro;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import irpi.app.constants.IRPIConstants;
import xml.XMLExpansion;
import xml.XMLValues;

public class MacroReference implements MacroChild, XMLValues {

	private String name;

	public MacroReference( String name ) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public List<XMLValues> getChildNodes() {
		return null;
	}

	@Override
	public void loadParamsFromXMLValues( XMLExpansion e ) {
		name = e.get( IRPIConstants.MACRO_NAME );
		
	}

	@Override
	public Map<String, Map<String, String[]>> saveParamsAsXML() {
		Map<String, Map<String, String[]>> ret = new HashMap<>();
		Map<String, String[]> v = new HashMap<>();
		v.put( IRPIConstants.MACRO_NAME, new String[] { name } );
		ret.put( IRPIConstants.MACRO_REFERENCE, v );
		return ret;
	}

	@Override
	public List<MacroChild> getMacroChildren() {
		return null;
	}
	
	@Override
	public String toString() {
		return name;
	}
}