package irpi.module.remote.ui;

import java.awt.BorderLayout;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import gui.dialog.OKCancelDialog;
import irpi.module.remote.RemoteModule;
import irpi.module.remote.data.device.RemoteMapData;
import irpi.module.remote.data.macro.Macro;
import irpi.module.remote.data.macro.MacroChild;
import irpi.module.remote.data.macro.MacroCommand;
import irpi.module.remote.data.macro.MacroData;
import state.provider.ApplicationProvider;
import statics.GU;
import statics.UIUtils;

public class NewMacroCommandDialog extends OKCancelDialog {

	private static final long serialVersionUID = 1L;
	
	private JComboBox<Macro> macros = new JComboBox<>();
	
	private JComboBox<String> devices = new JComboBox<>();
	
	private JComboBox<String> commands = new JComboBox<>();
	
	private JRadioButton macro = new JRadioButton( "Macro", false );
	
	private JRadioButton command = new JRadioButton( "Command", true );
	
	private MacroChild child = null;
	
	public NewMacroCommandDialog( ApplicationProvider provider ) {
		super( provider.getFrame(), "Add To Macro...", true );
		this.setLayout( new BorderLayout() );
		RemoteMapData rmd = ( (RemoteMapData)provider.getMonitorManager().getDataByName( RemoteModule.REMOTE_DATA ) );
		rmd.getRemoteNames().forEach( s -> devices.addItem( s ) );
		MacroData md = ( (MacroData)provider.getMonitorManager().getDataByName( RemoteModule.MACRO_DATA ) );
		md.getMacros().forEach( m -> macros.addItem( m ) );
		
		devices.addActionListener( e -> {
			if ( devices.getItemCount() != 0 ) {
				commands.removeAllItems();
				rmd.getRemoteCodes( devices.getItemAt( devices.getSelectedIndex() ) ).keySet().forEach( s -> commands.addItem( s ) );
			}
		} );
		command.addActionListener( e -> {
			boolean sel = command.isSelected();
			if ( sel ) {
				macro.setSelected( !sel );
				macros.setEnabled( !sel );
				Arrays.asList( devices, commands ).forEach( c -> c.setEnabled( sel ) );
			}
		} );
		macro.addActionListener( e -> {
			boolean sel = macro.isSelected();
			if ( sel ) {
				command.setSelected( !sel );
				macros.setEnabled( sel );
				Arrays.asList( devices, commands ).forEach( c -> c.setEnabled( !sel ) );
			}
		} );
		JPanel center = new JPanel();
		center.setLayout( new BoxLayout( center, BoxLayout.Y_AXIS ) );
		GU.hp( center, macro, macros );
		GU.spacer( this );
		GU.hp( center, command, devices, commands );
		GU.spacer( this );
		this.add( center, BorderLayout.CENTER );
		this.add( getButtonPanel(), BorderLayout.SOUTH );
		UIUtils.setColorsRecursive( this );
		this.pack();
	}
	
	@Override
	public void ok() {
		if ( macro.isSelected() ) {
			if ( macros.getSelectedIndex() != -1 ) {
				child = macros.getItemAt( macros.getSelectedIndex() );
			}
		} else {
			if ( devices.getSelectedIndex() != -1 && commands.getSelectedIndex() != -1 ) {
				child = new MacroCommand( devices.getItemAt( devices.getSelectedIndex() ), commands.getItemAt( commands.getSelectedIndex() ) );
			}
		}
		super.ok();
	}
	
	public MacroChild getMacroChild() {
		return child;
	}
}