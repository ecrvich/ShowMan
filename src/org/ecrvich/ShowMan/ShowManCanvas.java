package org.ecrvich.ShowMan;
import java.awt.*;

public class ShowManCanvas extends Canvas
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Toolkit toolkit;
	private Image image;
	private String name;
	private int x, y;
	private boolean valid;
	private MediaTracker tracker;
	
	public ShowManCanvas( int width, int height, int font_size,
			boolean thumb )
	{
		this( null, null, font_size, thumb );
		if (thumb)
			setSize( width, (font_size > 0) ? height + font_size : height );
		this.valid = true;
	}

	public ShowManCanvas( Image image, String name, int font_size,
			boolean thumb )
	{
		this.toolkit = Toolkit.getDefaultToolkit();
		this.image = image;
		this.name = name;
		this.tracker = new MediaTracker( this );
		setBackground( Color.black );
		setForeground( Color.yellow );
		if (font_size > 0)
			setFont( new Font( "Monospaced", Font.PLAIN, font_size ) );
		this.x = this.y = 0;
		loadImage( image, name, font_size, thumb );
	}

	public boolean loadImage( Image img, String imgName, int font_size,
			boolean thumb )
	{
		Cursor norm_cursor = new Cursor( Cursor.DEFAULT_CURSOR );
		Cursor wait_cursor = new Cursor( Cursor.WAIT_CURSOR );
		this.valid = false;
		if (img == null)
			return (false);
		setCursor( wait_cursor );
		if (waitForImage( img ))
		{
			this.name = imgName;
			if (thumb)
			{
				this.image = img;
				setThumbSize( font_size );
				this.valid = true;
			}
			else
			{
				this.image = scaleImage( img );
				if (waitForImage( this.image ))
				{
					Dimension scrsize = this.toolkit.getScreenSize();
					this.x = (scrsize.width - this.image.getWidth( this )) / 2;
					this.y = (scrsize.height - this.image.getHeight( this )) / 2;
					setSize( scrsize );
					this.valid = true;
				}
			}
		}
		setCursor( norm_cursor );
		return (this.valid);
	}

	public void unloadImage()
	{
		if (this.image != null)
			this.image.flush();
		this.image = null;
		this.valid = false;
	}
	
	private boolean waitForImage( Image img )
	{
		boolean rc;
		if (img == null)
			return (false);
		this.tracker.addImage( img, 5 );
		while (true)
		{
			try
			{
				this.tracker.waitForID( 5 );
				break;
			} catch (InterruptedException e)
			{
				// do nothing
			}
		}
		rc = !this.tracker.isErrorID( 5 );
		this.tracker.removeImage( img );
		return (rc);
	}
	
	private void setThumbSize( int font_size )
	{
		Dimension scrsize = this.toolkit.getScreenSize();
		int width = Math.min( scrsize.width, this.image.getWidth( this ) );
		int height = Math.min( scrsize.height, this.image.getHeight( this ) );
		if (this.name != null && font_size > 0)
			height += font_size;
		setSize( width, height );
	}
	
	private Image scaleImage( Image img )
	{
		Dimension scrsize = this.toolkit.getScreenSize();
		Image rc = img;
		int iw, ih, sw, sh;
		double hscale = 1.0, wscale = 1.0;
		
		iw = img.getWidth( this );
		ih = img.getHeight( this );
		sw = scrsize.width;
		sh = scrsize.height;
		if (sw < iw)
			wscale = (double)sw / (double)iw;
		if (sh < ih)
			hscale = (double)sh / (double)ih;
		if (hscale < 1.0)
		{
			double scale = Math.min( wscale, hscale );
			int new_width = (int)(iw * scale);
			int new_height = (int)(ih * scale);
			rc = img.getScaledInstance( new_width, new_height,
					Image.SCALE_DEFAULT );
			img.flush();
		}
		return (rc);
	}
	
	public boolean imageValid()
	{
		return (this.valid);
	}
	
	public void paint( Graphics gfx )
	{
		if (this.image != null)
		{
			gfx.drawImage( this.image, this.x, this.y, this );
			if (this.name != null)
				gfx.drawString( this.name, 0, getSize().height - 3 );
		}
	}
}
