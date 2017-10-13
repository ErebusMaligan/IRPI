package irpi.module.remote.data.device;

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
public class RemoteMapXMLDocumentHandler extends BasicXMLDocumentHandler {
	
	protected XMLValues val;
	
	public RemoteMapXMLDocumentHandler( XMLValues data ) {
		this.val = data;
		String ROOT_NODE_NAME = IRPIConstants.DATA;
		String EXT = ".xml";
		String READABLE = "IRPI Device/Code Data";
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