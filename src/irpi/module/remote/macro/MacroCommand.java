package irpi.module.remote.macro;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import irpi.app.constants.IRPIConstants;
import xml.XMLExpansion;
import xml.XMLValues;

public class MacroCommand implements MacroChild {

	private String device;
	
	private String code;
	
	public MacroCommand( String device, String code ) {
		this.device = device;
		this.code = code;
	}
	
	@Override
	public List<MacroChild> getMacroChildren() {
		return null;
	}

	@Override
	public List<XMLValues> getChildNodes() {
		return null;
	}

	@Override
	public void loadParamsFromXMLValues( XMLExpansion e ) {
		device = e.get( IRPIConstants.DEVICE_NAME );
		code = e.get( IRPIConstants.CODE_NAME );
	}

	@Override
	public Map<String, Map<String, String[]>> saveParamsAsXML() {
		Map<String, Map<String, String[]>> ret = new HashMap<>();
		Map<String, String[]> v = new HashMap<>();
		v.put( IRPIConstants.DEVICE_NAME, new String[] { device } );
		v.put( IRPIConstants.CODE_NAME, new String[] { code } );
		ret.put( IRPIConstants.MACRO_COMMAND, v );
		return ret;
	}
	
	public String getDevice() {
		return device;
	}

	public String getCode() {
		return code;
	}
	
	@Override
	public String toString() {
		return device + " : " + code;
	}
}