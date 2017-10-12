package irpi.module.remote.macro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import irpi.app.constants.IRPIConstants;
import xml.XMLExpansion;
import xml.XMLValues;

public class Macro implements MacroChild, XMLValues, Comparable<Macro> {

	private List<MacroChild> children = new ArrayList<>();

	private String name;

	public Macro( String name ) {
		this.name = name;
	}
	
	public void addMacroChild( MacroChild child, int place ) {
		children.add( place, child );
	}
	
	public void removeMacroChild( int place ) {
		children.remove( place );
	}
		
	@Override
	public List<MacroChild> getMacroChildren() {
		return children;
	}

	@Override
	public List<XMLValues> getChildNodes() {
		return new ArrayList<XMLValues>( children );
	}

	@Override
	public void loadParamsFromXMLValues( XMLExpansion e ) {
		name = e.get( IRPIConstants.MACRO_NAME );
		for ( XMLExpansion child : e.getChildren() ) {
			MacroChild c = child.getName().equals( IRPIConstants.MACRO ) ? new Macro( null ) : new MacroCommand( null, null );
			c.loadParamsFromXMLValues( child );
			children.add( c );
		}
	}

	@Override
	public Map<String, Map<String, String[]>> saveParamsAsXML() {
		Map<String, Map<String, String[]>> ret = new HashMap<>();
		Map<String, String[]> v = new HashMap<>();
		v.put( IRPIConstants.MACRO_NAME, new String[] { name } );
		ret.put( IRPIConstants.MACRO, v );
		return ret;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo( Macro o ) {
		return name.compareTo( o.getName() );
	}
}