package org.ecrvich.ShowMan;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class ShowManFrame extends Frame
{
	private Toolkit toolkit;
	private ShowManWindow window;
	private ShowManMenu menu;
	private ShowManFile filer;
	private ShowManOptions options;
	private Vector images, filenames;
	
	public ShowManFrame( ShowManOptions options )
	{
		super( "ShowMan" );
		this.toolkit = Toolkit.getDefaultToolkit();
		this.options = options;
		this.images = new Vector();
		this.filenames = new Vector();
		setBackground( Color.blue );
		setBounds( new Rectangle( 0, 0, 320, 240 ) );
		enableEvents( AWTEvent.KEY_EVENT_MASK + AWTEvent.WINDOW_EVENT_MASK );
		filer = new ShowManFile( this, options.startdir, options.files );
		window = new ShowManWindow( this, options );
		menu = new ShowManMenu( this, options );
		show();
	}

	private void run()
	{
		window.stop(); // just in case
		images.removeAllElements();
		filenames.removeAllElements();
		filer.run();
		images = filer.getImages();
		filenames = filer.getFilenames();
		if (images.size() > 0)
		{
			window.loadImages( images, (options.names) ? filenames : null );
			window.start();
		}
	}

	public void dispose()
	{
		window.dispose();
		super.dispose();
		System.exit( 0 );
	}
	
	protected void processWindowEvent( WindowEvent event )
	{
		if (event.getID() == WindowEvent.WINDOW_CLOSING)
			dispose();
	}

	protected void processKeyEvent( KeyEvent event )
	{
		// some platforms don't give key events to Windows,
		// so just pass it on if the Window is up.
		if (window.isVisible())
			window.processKeyEvent( event );
		else if (event.getID() == KeyEvent.KEY_PRESSED)
		{
			if (options.debugging)
				System.out.println( "ShowManFrame got keyPressed event." );
			switch (event.getKeyCode())
			{
				case KeyEvent.VK_ESCAPE:
					window.stop();
					dispose();
					break;
				case KeyEvent.VK_SPACE:
				case KeyEvent.VK_ENTER:
					run();
					break;
				default:
					if (options.debugging)
						System.err.println( "You pressed " + event.getKeyCode() );
					break;
			}
		}
	}
}
