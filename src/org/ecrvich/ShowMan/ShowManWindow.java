package org.ecrvich.ShowMan;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.Vector;
import java.io.File;

public class ShowManWindow
      extends Window
      implements Runnable
{
   private static final int FONT_SIZE = 12;
   private int ROWS, COLS, THUMB_WIDTH, THUMB_HEIGHT;
   private Frame frame;
   private Vector canvases;
   private Toolkit toolkit;
   private Vector images, filenames;
   private Thread thread;
   private ShowManOptions options;
   private boolean pause, skipnext, finished;

   public ShowManWindow( Frame frame, ShowManOptions options )
   {
      this( frame, new Vector(), options );
   }

   public ShowManWindow( Frame frame, Vector images, ShowManOptions options )
   {
      this( frame, images, null, options );
   }

   public ShowManWindow( Frame frame, Vector images, Vector filenames, ShowManOptions options )
   {
      super( frame );
      this.frame = frame;
      this.toolkit = Toolkit.getDefaultToolkit();
      this.thread = null;
      this.pause = false;
      this.skipnext = false;
      this.finished = false;
      this.options = options;
      canvases = new Vector();
      setSize( toolkit.getScreenSize() );
      setBackground( Color.black );
      setForeground( Color.yellow );
      enableEvents( AWTEvent.KEY_EVENT_MASK );
      loadImages( images, filenames );
   }

   public void loadImages( Vector images )
   {
      loadImages( images, null );
   }

   public void loadImages( Vector images, Vector filenames )
   {
      if (thread == null)
      {
         removeAll();
         validate();
         this.images = images;
         this.filenames = filenames;
         THUMB_WIDTH = 100;
         THUMB_HEIGHT = 100 + ((filenames != null) ? FONT_SIZE : 0);
         if (options.thumbs)
         {
            COLS = (int)Math.floor( getSize().width / THUMB_WIDTH );
            ROWS = (int)Math.floor( getSize().height / THUMB_HEIGHT );
         }
         else
         {
            COLS = ROWS = 1;
         }
         setLayout( new GridLayout( ROWS, COLS ) );
      }
   }

   public void processKeyEvent( KeyEvent event )
   {
      if (event.getID() == KeyEvent.KEY_PRESSED)
      {
         if (options.debugging)
            System.out.println( "ShowManWindow got keyPressed event." );
         switch (event.getKeyCode())
         {
            case KeyEvent.VK_ESCAPE:
               finished = true;
               thread.interrupt();
               break;
            case KeyEvent.VK_SPACE:
               pause = !pause;
               thread.interrupt();
               break;
            case KeyEvent.VK_ENTER:
               skipnext = true;
               thread.interrupt();
               break;
            default:
               if (options.debugging)
                  System.err.println( "You pressed " + event.getKeyCode() );
               break;
         }
      }
   }

   private void clearImages()
   {
      while (!canvases.isEmpty())
      {
         ((ShowManCanvas)canvases.firstElement()).unloadImage();
         canvases.removeElementAt( 0 );
      }
   }

   private int showImages( int index )
   {
      boolean good_image = false;

      clearImages();
      if (options.thumbs)
      {
         int cell = 0, cells = ROWS * COLS;
         // Math.min( ROWS * COLS, images.size() - index );

         for (int i = 0; i < cells; ++i)
            addCanvas(
                  new ShowManCanvas( THUMB_WIDTH, THUMB_HEIGHT, (filenames != null) ? FONT_SIZE : 0, true ),
                  (i == 0) );
         validate();
         String fname;
         while (cell < cells && index < images.size() && !finished)
         {
            fname = (filenames == null) ? null : (String)filenames.elementAt( index );
            if (updateCanvas( (ShowManCanvas)canvases.elementAt( cell ),
                  ((Image)images.elementAt( index )).getScaledInstance( THUMB_WIDTH, THUMB_HEIGHT,
                        Image.SCALE_DEFAULT ),
                  (fname != null) ? fname.substring( fname.lastIndexOf( File.separator ) + 1 ) : null ))
            {
               good_image = true;
               ++cell;
               ++index;
               // repaint();
            }
            else
            {
               images.removeElementAt( index );
               System.err.println( "The file '" + filenames.elementAt( index ) + "' did not load properly." );
               filenames.removeElementAt( index );
            }
         }
      }
      else
      {
         String fname = (filenames == null) ? null : (String)filenames.elementAt( index );
         if (addCanvas( new ShowManCanvas( (Image)images.elementAt( index ),
               (fname != null) ? fname.substring( fname.lastIndexOf( File.separator ) + 1 ) : null, FONT_SIZE,
               false ), true ))
         {
            good_image = true;
            ++index;
            validate();
            repaint();
         }
         else
         {
            images.removeElementAt( index );
            System.err.println( "The file '" + filenames.elementAt( index ) + "' did not load properly." );
            filenames.removeElementAt( index );
         }
      }
      if (good_image)
         return (index);
      return (-1);
   }

   private boolean updateCanvas( ShowManCanvas canvas, Image image, String filename )
   {
      if (!canvas.loadImage( image, filename, FONT_SIZE, options.thumbs ))
         return (false);
      return (true);
   }

   private boolean addCanvas( ShowManCanvas canvas, boolean clear )
   {
      if (canvas.imageValid())
      {
         if (clear)
            removeAll();
         add( canvas );
         canvases.addElement( canvas );
         return (true);
      }
      return (false);
   }

   public void run()
   {
      int image_index = 0, next_index;
      boolean got_good_image = false;
      Cursor norm_cursor = new Cursor( Cursor.DEFAULT_CURSOR );
      Cursor wait_cursor = new Cursor( Cursor.WAIT_CURSOR );

      if (images.size() < 1)
         return;
      pause = false;
      show();

      finished = false;
      while (!finished)
      {
         requestFocus();
         if ((next_index = showImages( image_index )) < 0)
            break;
         requestFocus();
         sleep( !options.nocycle && image_index == 0 && next_index >= images.size() );
         if (next_index >= images.size())
         {
            image_index = 0; // start from beginning
            if (options.nocycle) // user doesn't want to start over again
               break;
         }
         else
            image_index = next_index;
      }
      dispose();
      thread = null;
      frame.dispatchEvent( new KeyEvent( frame, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ENTER ) );
   }

   public void paint( Graphics gfx )
   {
      if (canvases.size() > 0)
         ((ShowManCanvas)canvases.elementAt( canvases.size() - 1 )).repaint();
   }

   private void sleep( boolean no_wake )
   {
      long sleep_time;

      while (!finished && (!skipnext || no_wake))
      {
         if (pause)
            sleep_time = Long.MAX_VALUE;
         else
            sleep_time = options.interval * 1000;
         try
         {
            Thread.sleep( sleep_time );
         }
         catch (InterruptedException e)
         {
         }
         if (!pause)
            skipnext = true;
      }

      skipnext = false;
   }

   public void waitFor()
   {
      while (thread != null)
      {
         try
         {
            thread.join();
            thread = null;
         }
         catch (InterruptedException e)
         {
         }
      }
   }

   private void startThread()
   {
      thread = new Thread( this );
      thread.start();
   }

   private void stopThread()
   {
      thread.stop();
      thread = null;
   }

   public void start()
   {
      if (thread == null)
      {
         startThread();
      }
   }

   public void stop()
   {
      if (thread != null)
      {
         finished = true;
         thread.interrupt();
         waitFor();
      }
   }

   public void dispose()
   {
      clearImages();
      removeAll();
      System.gc();
      super.dispose();
   }
}
