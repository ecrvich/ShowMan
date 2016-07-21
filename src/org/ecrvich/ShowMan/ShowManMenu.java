package org.ecrvich.ShowMan;
import java.awt.*;
import java.awt.event.*;

public class ShowManMenu extends MenuBar
{
	private Frame frame;
	private Font font;
	private ShowManOptions options;
	private Menu filemenu, opmenu;
	private MenuItem open, quit, interval;
	private CheckboxMenuItem nocycle, thumbs, names;

	public ShowManMenu( Frame frame, ShowManOptions options )
	{
		this.frame = frame;
		this.options = options;
		font = new Font( "Serif", Font.BOLD, 14 );
		initMenus();
		frame.setMenuBar( this );
	}

	private void initMenus()
	{
		filemenu = new Menu( "File" );
		filemenu.setFont( font );
//		filemenu.setShortcut( new MenuShortcut( KeyEvent.VK_F ) );
		open = new MenuItem( "Open..." );
		open.setFont( font );
		open.addActionListener( new ActionListener()
				{ public void actionPerformed( ActionEvent e )
				{ frame.dispatchEvent( new KeyEvent( frame, KeyEvent.KEY_PRESSED,
				0, 0, KeyEvent.VK_ENTER ) ); } } );
		quit = new MenuItem( "Exit" );
		quit.setFont( font );
		quit.addActionListener( new ActionListener()
				{ public void actionPerformed( ActionEvent e )
				{ frame.dispatchEvent( new WindowEvent( frame,
				WindowEvent.WINDOW_CLOSING ) ); } } );
		filemenu.add( open );
		filemenu.add( new MenuItem( "-" ) );
		filemenu.add( quit );

		opmenu = new Menu( "Options" );
		opmenu.setFont( font );
//		opmenu.setShortcut( new MenuShortcut( KeyEvent.VK_O ) );
		interval = new MenuItem( "Interval..." );
		interval.setFont( font );
		interval.addActionListener( new ActionListener()
				{ public void actionPerformed( ActionEvent e )
				{ new ShowManTextDialog( frame, "Interval",
					new Integer( options.interval ).toString(), 6, options ); } } );
		nocycle = new CheckboxMenuItem( "NoCycle" );
		nocycle.setFont( font );
		nocycle.setState( options.nocycle );
		nocycle.addItemListener( new ItemListener()
				{ public void itemStateChanged( ItemEvent e )
				{ options.nocycle = !options.nocycle; } } );
		thumbs = new CheckboxMenuItem( "Thumbnails" );
		thumbs.setFont( font );
		thumbs.setState( options.thumbs );
		thumbs.addItemListener(	new ItemListener()
				{ public void itemStateChanged( ItemEvent e )
				{ options.thumbs = !options.thumbs; } } );
		names = new CheckboxMenuItem( "ShowNames" );
		names.setFont( font );
		names.setState( options.names );
		names.addItemListener( new ItemListener()
				{ public void itemStateChanged( ItemEvent e )
				{ options.names = !options.names; } } );
		opmenu.add( interval );
		opmenu.add( thumbs );
		opmenu.add( names );
		opmenu.add( nocycle );

		add( filemenu );
		add( opmenu );
	}
}
