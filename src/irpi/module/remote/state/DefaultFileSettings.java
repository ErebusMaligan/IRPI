package irpi.module.remote.state;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import irpi.app.constants.IRPIConstants;
import xml.XMLExpansion;
import xml.XMLValues;

public class DefaultFileSettings implements XMLValues {

	private String macro = "./macros.xml";
	
	private String device = "./devices.xml";
	
	@Override
	public List<XMLValues> getChildNodes() { return null; }

	@Override
	public void loadParamsFromXMLValues( XMLExpansion e ) {
		macro = e.get( IRPIConstants.MACRO );
		device = e.get( IRPIConstants.DEVICE );
	}

	@Override
	public Map<String, Map<String, String[]>> saveParamsAsXML() {
		Map<String, Map<String, String[]>> ret = new HashMap<String, Map<String, String[]>>();
		Map<String, String[]> values = new HashMap<String, String[]>();
		ret.put( IRPIConstants.XDEFAULT_FILES, values );
		values.put( IRPIConstants.MACRO, new String[] { macro } );
		values.put( IRPIConstants.DEVICE, new String[] { device } );
		return ret;
	}

	public String getMacro() {
		return macro;
	}

	public void setMacro( String macro ) {
		this.macro = macro;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice( String device ) {
		this.device = device;
	}
}