package irpi.module.remote.ui;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

import statics.GU;
import statics.UIUtils;
import ui.theme.ThemeConstants;

public class IRPIUIUtils {
	
	public static JButton createButton( String text, ActionListener l ) {
		JButton b = GU.createButton( text, l );
		GU.setSizes( b, new Dimension( 100, 100 ) );
		UIUtils.setJButton( b );
		b.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseEntered( MouseEvent e ) {
				b.setForeground( ThemeConstants.BACKGROUND );
				b.setBackground( ThemeConstants.FOREGROUND );
			}
			
			@Override
			public void mouseExited( MouseEvent e ) {
				b.setForeground( ThemeConstants.FOREGROUND );
				b.setBackground( ThemeConstants.BACKGROUND );
			}
		});
		return b;
	}

}
