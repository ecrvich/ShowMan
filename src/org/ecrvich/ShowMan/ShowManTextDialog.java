package org.ecrvich.ShowMan;
import java.awt.*;
import java.awt.event.*;
//import java.exception.NumberFormatException;

public class ShowManTextDialog extends Dialog
{
	private class ShowManTextField extends TextField
	{
		public ShowManTextField( String text, int maxlen )
		{
			super( text, maxlen );
			selectAll();
			enableEvents( AWTEvent.KEY_EVENT_MASK );
			setFont( new Font( "Monospaced", Font.PLAIN, 14 ) );
			setForeground( Color.yellow );
			setBackground( Color.black );
		}

		public boolean isInputValid()
		{
			int interval_test;
			
			try
			{
				interval_test = Integer.parseInt( getText() );
				if (interval_test > 0 && (interval_test * 1000) > 0)
				{
					options.interval = interval_test;
					return (true);
				}
			}
			catch (NumberFormatException e)
			{
			}
			return false;
		}
	
		protected void processKeyEvent( KeyEvent e )
		{
			if (e.getID() == KeyEvent.KEY_PRESSED)
			{
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_ENTER && isInputValid())
					dispose();
				else
				{
					switch (key)
					{
						case KeyEvent.VK_LEFT:
						case KeyEvent.VK_RIGHT:
						case KeyEvent.VK_HOME:
						case KeyEvent.VK_END:
						case KeyEvent.VK_BACK_SPACE:
						case KeyEvent.VK_DELETE:
						case KeyEvent.VK_INSERT:
							break; // allow key to behave normally
						default:
							if (!Character.isDigit( (char)key ) ||
									(getText().length() >= 6 &&
									getSelectionStart() == getSelectionEnd()))
								e.consume();
							break;
					}
				}
			}
		}
	}
	ShowManTextField text_field;
	ShowManOptions options;
	
	public ShowManTextDialog( Frame frame, String title,
			String field_text, int maxlen, ShowManOptions options )
	{
		super( frame, title, true );
		text_field = new ShowManTextField( field_text, maxlen );
		this.options = options;
		setBounds( new Rectangle( 20, 20, 140, 80 ) );
		setResizable( false );
		Panel pan = new Panel();
		pan.add( text_field );
		pan.setBackground( Color.yellow );
		pan.setSize( 120, 30 );
		add( pan );
		enableEvents( AWTEvent.WINDOW_EVENT_MASK );
		show();
	}

	protected void processWindowEvent( WindowEvent e )
	{
		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			if (text_field.isInputValid())
				dispose();
			else
				System.out.println( (char)8 );
		}
	}
}
