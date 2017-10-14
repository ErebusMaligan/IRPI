package irpi.module.remote.ui;

import java.awt.BorderLayout;
import java.awt.MouseInfo;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import gui.dialog.OKCancelDialog;
import gui.windowmanager.WindowDefinition;
import irpi.module.remote.RemoteConstants;
import irpi.module.remote.RemoteModule;
import irpi.module.remote.data.macro.Macro;
import irpi.module.remote.data.macro.MacroChild;
import irpi.module.remote.data.macro.MacroData;
import state.provider.ApplicationProvider;
import statics.GU;
import statics.LAFUtils;
import statics.UIUtils;

public class MacroManager implements WindowDefinition, Observer {

	private ApplicationProvider provider;
	
	private JTextField macroName = new JTextField();
	
	private DefaultListModel<MacroChild> model = new DefaultListModel<>();
	
	private JList<MacroChild> commands = new JList<>( model );
	
	private JComboBox<Macro> macros = new JComboBox<>();
	
	private JPanel main;
	
	private JTextField macroCategory = new JTextField();
	
	{
		commands.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
	}
	
	@Override
	public JComponent getCenterComponent( Object provider ) {
		this.provider = (ApplicationProvider)provider;
		main = new JPanel( new BorderLayout() );
		main.add( getNewMacroPanel(), BorderLayout.NORTH );
		main.add( getMacroViewerPanel(), BorderLayout.CENTER );
		Arrays.asList( RemoteModule.REMOTE_DATA, RemoteModule.MACRO_DATA ).forEach( s -> this.provider.getMonitorManager().getDataByName( s ).addObserver( this ) );
		LAFUtils.applySimpleUI( macros, UIUtils.BACKGROUND, UIUtils.FOREGROUND );
		UIUtils.setColorsRecursive( main );
		return main;
	}
	
	private JPanel getNewMacroPanel() {
		JPanel ret = new JPanel();
		ret.setLayout( new BoxLayout( ret, BoxLayout.Y_AXIS ) );
		JButton b = new JButton( "Add" );
		b.addActionListener( e -> {
			MacroData md = ( (MacroData)this.provider.getMonitorManager().getDataByName( RemoteModule.MACRO_DATA ) );
			if ( !macroName.getText().isEmpty() && !md.getMacroNames().contains( macroName.getText() ) ) {
				md.addMacro( new Macro( macroName.getText() ) );
				macroName.setText( "" );
			} else {
				JOptionPane.showMessageDialog( provider.getFrame(), "Macro name must be non empty and unique", "Failed to add Macro", JOptionPane.ERROR_MESSAGE );
			}
		} );
		GU.hp( ret, new JLabel( "New Macro Name: ", JLabel.RIGHT ), macroName, b );
		GU.spacer( ret );
		UIUtils.setJButton( b );
		return ret;
	}
	
	private JPanel getMacroViewerPanel() {
		JPanel ret = new JPanel();
		ret.setLayout( new BoxLayout( ret, BoxLayout.Y_AXIS ) );
		GU.hp( ret, new JLabel( "Macro: ", JLabel.RIGHT ), macros );
		GU.spacer( ret );
		GU.hp( ret, new JLabel( "Category: " ), macroCategory );
		macroCategory.addKeyListener( new KeyAdapter() {
			@Override
			public void keyReleased( KeyEvent e ) {
				if ( macros.getSelectedIndex() != -1 ) {
					( (Macro)macros.getSelectedItem() ).setCategory( macroCategory.getText() );
					
				}
			}
		} );
		macros.addActionListener( e -> reloadCommands() );
		JPanel com = new JPanel( new BorderLayout() );
		com.add( commands, BorderLayout.CENTER );
		ret.add( new JScrollPane( com ) );
		
		JButton add = new JButton( "Add" );
		JButton remove = new JButton( "Remove" );
		add.addActionListener( e -> {
			if ( macros.getSelectedIndex() != -1 ) {
				int i = commands.getSelectedIndex();
				i = i == -1 ? commands.getModel().getSize() : i + 1;
				NewMacroCommandDialog d = new NewMacroCommandDialog( provider );
				d.setLocation( MouseInfo.getPointerInfo().getLocation() );
				d.setVisible( true );
				if ( d.getResult() == OKCancelDialog.OK ) {
					macros.getItemAt( macros.getSelectedIndex() ).addMacroChild( d.getMacroChild(), i );
					reloadCommands();
				}
			}
		} );
		remove.addActionListener( e -> {
			int i = commands.getSelectedIndex();
			if ( i != -1 ) {
				if ( macros.getSelectedIndex() != -1 ) {
					macros.getItemAt( macros.getSelectedIndex() ).removeMacroChild( i );
					reloadCommands();
				}
			}
		} );
		Arrays.asList( add, remove ).forEach( b -> UIUtils.setJButton( b ) );
		GU.spacer( ret );
		GU.hp( ret, add, remove );
		return ret;
	}
	
	private void reloadCommands() {
		if ( macros.getSelectedIndex() != -1 ) {
			Macro selected = ( (Macro)macros.getSelectedItem() );
			model.removeAllElements();
			selected.getMacroChildren().forEach( c -> model.addElement( c ) );
			commands.revalidate();
			macroCategory.setText( selected.getCategory() );
		}
	}

	@Override
	public String getTitle() {
		return RemoteConstants.WD_MACRO_MANAGER;
	}
	
	@Override
	public void closed() {
		Arrays.asList( RemoteModule.REMOTE_DATA, RemoteModule.MACRO_DATA ).forEach( s -> provider.getMonitorManager().getDataByName( s ).deleteObserver( this ) );
	}

	@Override
	public void update( Observable o, Object arg ) {
		reload();
	}
	
	private void reload() {
		macros.removeAllItems();
		MacroData md = ( (MacroData)this.provider.getMonitorManager().getDataByName( RemoteModule.MACRO_DATA ) );
		md.getMacros().forEach( i -> macros.addItem( i ) );
		if ( macros.getItemCount() != 0 ) {
			macros.setSelectedItem( 0 );
		}
		main.repaint();
	}
}