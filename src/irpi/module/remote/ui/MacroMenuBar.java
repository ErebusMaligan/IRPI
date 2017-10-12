package irpi.module.remote.ui;

import static irpi.app.constants.IRPIConstants.MB_FILE;
import static irpi.app.constants.IRPIConstants.MB_MACRO_LOAD;
import static irpi.app.constants.IRPIConstants.MB_MACRO_SAVE;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JMenu;
import javax.swing.plaf.basic.BasicMenuBarUI;

import gui.menubar.GenericActionListener;
import gui.menubar.GenericMenuBar;
import gui.menubar.GenericMenuBarAction;
import irpi.module.remote.RemoteModule;
import irpi.module.remote.macro.MacroData;
import irpi.module.remote.macro.MacroXMLDocumentHandler;
import state.provider.ApplicationProvider;
import statics.LAFUtils;
import statics.UIUtils;
import ui.theme.ThemeConstants;

/**
 * @author Daniel J. Rivers
 *         2015
 *
 * Created: Apr 24, 2015, 8:51:30 PM 
 */
public class MacroMenuBar extends GenericMenuBar {

	private static final long serialVersionUID = 1L;

	private ApplicationProvider state;

	public MacroMenuBar( ApplicationProvider state ) {
		this.state = state;
		createSSHMenu();
		this.add( Box.createGlue() );

		this.setUI( new BasicMenuBarUI() );
		this.setMinimumSize( new Dimension( 0, 22 ) );
		this.setPreferredSize( new Dimension( this.getWidth(), 22 ) );
		LAFUtils.applyThemedUI( this, ThemeConstants.BACKGROUND, ThemeConstants.FOREGROUND );

		UIUtils.setColors( this );
		this.setBorder( BorderFactory.createLineBorder( ThemeConstants.FOREGROUND ) );
	}

	private void createSSHMenu() {
		menu = new JMenu( MB_FILE );
		UIUtils.setColors( menu );
		createItem( MB_MACRO_SAVE, o -> new MacroXMLDocumentHandler( ( (MacroData)state.getMonitorManager().getDataByName( RemoteModule.MACRO_DATA ) ) ).createDoc() );
		createItem( MB_MACRO_LOAD, o -> new MacroXMLDocumentHandler( ( (MacroData)state.getMonitorManager().getDataByName( RemoteModule.MACRO_DATA ) ) ).loadDoc() );
		this.add( menu );
	}


	@Override
	public void createBaseItem( GenericMenuBarAction action ) {
		item.addActionListener( new GenericActionListener( action, this ) );
		UIUtils.setColors( item );
		menu.add( item );
		buttonMap.put( item.getText(), item );
	}

}