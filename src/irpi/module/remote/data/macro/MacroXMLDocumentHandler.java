package irpi.module.remote.data.macro;

import org.w3c.dom.Document;

import irpi.app.constants.IRPIConstants;
import xml.BasicXMLDocumentHandler;
import xml.XMLExpansion;
import xml.XMLUtils;
import xml.XMLValues;

/**
 * @author Daniel J. Rivers
 *         2015
 *
 * Created: May 9, 2015, 11:50:53 PM 
 */
public class MacroXMLDocumentHandler extends BasicXMLDocumentHandler {
	
	protected XMLValues val;
	
	public MacroXMLDocumentHandler( XMLValues data ) {
		this.val = data;
		String ROOT_NODE_NAME = IRPIConstants.DATA;
		String EXT = ".xml";
		String READABLE = "IRPI Macro Data";
		String DIR = ".";
		init( null, EXT, READABLE + " (" + EXT + ")", DIR, ROOT_NODE_NAME );
	}

	@Override
	protected void parseDocument( XMLExpansion e ) {
		val.loadParamsFromXMLValues( e );
	}

	@Override
	protected void writeDocument( Document doc ) {
		XMLUtils.writeNodeRecursively( doc, val, doc.getFirstChild() );
	}
}