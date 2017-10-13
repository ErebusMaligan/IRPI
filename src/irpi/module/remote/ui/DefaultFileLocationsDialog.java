package irpi.module.remote.ui;

import java.awt.BorderLayout;
import java.awt.MouseInfo;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gui.dialog.OKCancelDialog;
import irpi.app.IRPI;
import statics.GU;
import statics.UIUtils;

public class DefaultFileLocationsDialog extends OKCancelDialog {

	private static final long serialVersionUID = 1L;
	
	private JTextField macro = new JTextField();
	
	private JTextField device = new JTextField();
	
	private IRPI provider;
	
	public DefaultFileLocationsDialog( IRPI provider ) {
		super( provider.getFrame(), "Default File Locations", true );
		this.provider = provider;
		this.setLocation( MouseInfo.getPointerInfo().getLocation() );
		macro.setText( provider.getDefFileS().getMacro() );
		device.setText( provider.getDefFileS().getDevice() );
		JPanel center = new JPanel();
		center.setLayout( new BoxLayout( center, BoxLayout.Y_AXIS ) );
		GU.hp( center, new JLabel( "Device File:" ), device );
		GU.spacer( center );
		GU.hp( center, new JLabel( "Macro File:" ), macro );
		this.setLayout( new BorderLayout() );
		this.add( getButtonPanel(), BorderLayout.SOUTH );
		this.add( center, BorderLayout.CENTER );
		UIUtils.setJButton( ok );
		UIUtils.setJButton( cancel );
		UIUtils.setColorsRecursive( this );
		this.pack();
	}
	
	protected void saveParams() {
		provider.getDefFileS().setDevice( device.getText() );
		provider.getDefFileS().setMacro( macro.getText() );
	}
	
	@Override
	public void ok() {
		saveParams();
		provider.writeSettings();
		JOptionPane.showMessageDialog( this, "Default File Options Require Application Restart To Take Effect" );
		super.ok();
	}
}