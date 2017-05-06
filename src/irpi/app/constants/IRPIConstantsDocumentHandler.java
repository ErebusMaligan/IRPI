package irpi.app.constants;

import xml.SimpleFileXMLDocumentHandler;
import xml.XMLValues;

/**
 * @author Daniel J. Rivers
 *         2015
 *
 * Created: May 9, 2015, 11:50:53 PM 
 */
public class IRPIConstantsDocumentHandler extends SimpleFileXMLDocumentHandler {
	public IRPIConstantsDocumentHandler( XMLValues con ) {
		this.val = con;
		FILE = "Settings";
		ROOT_NODE_NAME = "IRPI";
		EXT = ".irp";
		READABLE = "IRPI Settings Config";
		DIR = ".";
		init( null, EXT, READABLE + " (" + EXT + ")", DIR, ROOT_NODE_NAME );
	}
}