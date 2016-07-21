package org.ecrvich.ShowMan;

import java.util.Vector;

public class ShowMan
{
   private ShowManOptions options;

   public ShowMan()
   {
      options = new ShowManOptions();
      options.debugging = false;
      options.nocycle = false;
      options.thumbs = false;
      options.names = false;
      options.interval = 10;
      options.startdir = "D:\\";
      options.files = new String[0];
   }

   public boolean debugOn()
   {
      return (options.debugging);
   }

   private void parseArgs( String[] args )
   {
      Vector filevec = new Vector();

      for (int i = 0; i < args.length; ++i)
      {
         if (args[i].startsWith( "-" )) // a flag
         {
            if (parseFlag( args[i], (i >= args.length - 1) ? null : args[i + 1] ))
               ++i;
         }
         else
            filevec.addElement( args[i] );
      }
      options.files = new String[filevec.size()];
      filevec.copyInto( options.files );
   }

   private boolean parseFlag( String flag, String arg )
   {
      boolean used_arg = false; // usually not
      flag = flag.toLowerCase();
      if (flag.equals( "-debug" ))
         options.debugging = true;
      else if (flag.equals( "-nocycle" ))
         options.nocycle = true;
      else if (flag.equals( "-thumbs" ))
         options.thumbs = true;
      else if (flag.equals( "-names" ))
         options.names = true;
      else if (flag.equals( "-version" ) || flag.equals( "-fullversion" ))
         showVersion();
      else if (flag.equals( "-interval" ))
      {
         if (arg == null || arg.length() < 1)
            showUsage();
         try
         {
            options.interval = Integer.parseInt( arg );
         }
         catch (NumberFormatException e)
         {
            showUsage();
         }
         if (options.interval < 0)
            showUsage();
         used_arg = true;
      }
      else if (flag.equals( "-dir" ))
      {
         if (arg == null || arg.length() < 1)
            showUsage();
         options.startdir = arg;
         used_arg = true;
      }
      else
         showUsage();

      return (used_arg);
   }

   private void runShowMan()
   {
      ShowManFrame frame = new ShowManFrame( options );
   }

   public void showVersion()
   {
      System.out.println( "ShowMan v1.0.7  05-NOV-1998  (C) Ernest M. Crvich" );
      System.exit( 0 );
   }

   public void showUsage()
   {
      System.out.println( "USAGE: java ShowMan [flags] [files | dirs ...]" );
      System.out.println( "   available flags:" );
      System.out.println( "       [-help | -version] |" );
      System.out.println( "       [-debug] [-nocycle] [-thumbs] [-names]" );
      System.out.println( "       [-interval <secs>] [-dir <startdir>]" );
      System.exit( 0 );
   }

   public static void main( String[] args )
   {
      ShowMan showman = new ShowMan();
      showman.parseArgs( args );
      if (showman.debugOn())
         showman.runShowMan();
      else
      {
         try
         {
            showman.runShowMan();
         }
         catch (Throwable e)
         {
         }
      }
   }
}
