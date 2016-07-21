package org.ecrvich.ShowMan;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class ShowManFile extends FileDialog implements FilenameFilter
{
	private final static String ALL_FILES_STRING = "all files";
	private Frame frame;
	private Vector images, filenames;
	private Toolkit toolkit;
	private String dir;
	private String[] first_files;

	public ShowManFile( Frame frame, String startdir )
	{
		this( frame, startdir, new String[0] );
	}

	public ShowManFile( Frame frame, String startdir, String[] files )
	{
		super( frame, "Open Images", FileDialog.LOAD );
		this.frame = frame;
		this.images = new Vector();
		this.filenames = new Vector();
		this.toolkit = Toolkit.getDefaultToolkit();
		this.dir = startdir;
		this.first_files = files;
		if (!new File( this.dir ).exists())
			this.dir = System.getProperty( "user.dir" );
		setBackground( Color.blue );
		setForeground( Color.yellow );
		setLocation( 20, 20 );
	}

	public void run()
	{
		images.removeAllElements();
		
		if (first_files == null || first_files.length < 1)
			first_files = getFilesFromDialog();
		getFilesInDirs( first_files );
		first_files = null;
		filterFiles();
		loadImages();
	}
	
	public boolean accept( File dir, String name )
	{
		name = name.toUpperCase();
		if (name.endsWith( ".JPG" ) || name.endsWith( ".JPEG" ) ||
		    name.endsWith( ".JPE" ) || name.endsWith( ".GIF" ))
			return (true);
		return (false);
	}
	
	public Vector getImages()
	{
		return (images);
	}

	public Vector getFilenames()
	{
		return (filenames);
	}

	private void filterFiles()
	{
		String file;

		for (int i = 0; i < filenames.size(); ++i)
		{
			file = ((String)filenames.elementAt( i )).toUpperCase();
			if (!accept( null, file ))
			{
				filenames.removeElementAt( i );
				--i;  // so the for-loop doesn't move the index
			}
		}
	}

	private void loadImages()
	{
		for (int i = 0; i < filenames.size(); ++i)
			images.addElement( toolkit.getImage( (String)filenames.elementAt( i ) ) );
	}

	private String[] getFilesFromDialog()
	{
		String file, path = null;
		String[] results = new String[0];
		
		setDirectory( dir );
		setFile( ALL_FILES_STRING );
		show();
		dispose();
		try
		{
			dir = getDirectory().trim();
			file = getFile().trim();
		} catch (RuntimeException e) { return (results); }
		if (file.equals( ALL_FILES_STRING ))
			file = "";
		if ((path = canonicalize( dir + File.separator + file )) == null)
			return (results);
		results = new String[1];
		results[0] = path;
		return (results);
	}

	private String canonicalize( String path )
	{
		return (canonicalize( path, true ));
	}
	
	private String canonicalize( String path, boolean check_exists )
	{
		return (canonicalize( new File( path ), check_exists ));
	}
	
	private String canonicalize( File fd )
	{
		return (canonicalize( fd, true ));
	}
	
	private String canonicalize( File fd, boolean check_exists )
	{
		String path;
		
		try
		{
			if (check_exists && !fd.exists())
				path = null;
			else
				path = fd.getCanonicalPath();
		} catch (IOException e) { path = null; }
		return (path);
	}

	private void getFilesInDirs( String dir )
	{
		String[] dirs = new String[1];
		dirs[0] = dir;
		getFilesInDirs( dirs );
	}
	
	private void getFilesInDirs( String[] dirs )
	{
		File fd = null;
		
		for (int i = 0; i < dirs.length; ++i)
		{
			fd = new File( dirs[i] );
			if (fd.exists())
			{
				if (fd.isDirectory())
				{
					getFilesInDir( fd );
				}
				else
				{
					addFilename( canonicalize( fd, false ) );
				}
			}
		}
	}

	private void addFilename( String pathname )
	{
		int j;
		String lhs, rhs; // for comparisons

		if (pathname == null || pathname.length() < 1)
			return;
		if (File.separatorChar != '/') // not Unix, so case insensitive
			lhs = pathname.toUpperCase();
		else
			lhs = pathname; // Unix, compare case sensitively
		for (j = 0; j < filenames.size(); ++j)
		{
			if (File.separatorChar != '/') // not Unix, so case insensitive
				rhs = ((String)filenames.elementAt( j )).toUpperCase();
			else
				rhs = (String)filenames.elementAt( j );
			if (lhs.compareTo( rhs ) < 0)
				break;
		}
		filenames.insertElementAt( pathname, j );
	}
	
	private void getFilesInDir( File dir )
	{
		String[] filelist;
		String dirname, dirsep;
		
		dirname = canonicalize( dir );
		if (dirname == null)
			return;
		filelist = dir.list();
		if (filelist == null)
			return;
		if (dirname.endsWith( File.separator ))
			dirsep = "";
		else
			dirsep = File.separator;
		for (int i = 0; i < filelist.length; ++i)
			addFilename( dirname + dirsep + filelist[i] );
	}
}
