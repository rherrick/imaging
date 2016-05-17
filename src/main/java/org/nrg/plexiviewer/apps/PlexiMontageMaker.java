/* 
 *	Copyright Washington University in St Louis 2006
 *	All rights reserved
 * 	
 * 	@author Mohana Ramaratnam (Email: mramarat@wustl.edu)

*/

package org.nrg.plexiviewer.apps;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.util.Hashtable;


public class PlexiMontageMaker {
    private class CommandLineArguments {
        
        final String  MIN_THRESHOLD = "minThreshold";
        final String MAX_THRESHOLD = "maxThreshold";
        final String FILE = "file";
        final String STARTAT = "startAt";
        final String INCREMENT = "increment";
        final String COLOR_MAP = "colormap";
        
        Hashtable commandLineArgs = new Hashtable();
        int noOfRequiredArguments = 5;
        boolean colormapDefined = false;
        
        public CommandLineArguments(String argv[]) {
            int c;
            int foundArgs = 0;
            commandLineArgs = new Hashtable();
            LongOpt[] longopts = new LongOpt[7];
            longopts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
            longopts[1] = new LongOpt(FILE, LongOpt.REQUIRED_ARGUMENT, null, 'f'); 
            longopts[2] = new LongOpt(MIN_THRESHOLD, LongOpt.REQUIRED_ARGUMENT, null, 'm');
            longopts[3] = new LongOpt(MAX_THRESHOLD, LongOpt.REQUIRED_ARGUMENT, null, 'x');
            longopts[4] = new LongOpt(STARTAT, LongOpt.REQUIRED_ARGUMENT, null, 's');
            longopts[5] = new LongOpt(INCREMENT, LongOpt.REQUIRED_ARGUMENT, null, 'i');
            longopts[6] = new LongOpt(COLOR_MAP, LongOpt.REQUIRED_ARGUMENT, null, 'c');
            // 
            Getopt g = new Getopt("PlexiMontageMaker", argv, "f:m:x:s:i:c:h;", longopts, true);
            g.setOpterr(false); // We'll do our own error handling
            while ((c = g.getopt()) != -1) {
                switch (c)
                  {
                     case 'f':
                         commandLineArgs.put(FILE,g.getOptarg());
                         foundArgs++;
                         break;
                     case 'm':
                         commandLineArgs.put(MIN_THRESHOLD,new Double(Double.parseDouble(g.getOptarg())));
                         foundArgs++;
                         break;
                     case 'x':
                         commandLineArgs.put(MAX_THRESHOLD,new Double(Double.parseDouble(g.getOptarg())));
                         foundArgs++;
                         break;
                     case 's':
                         commandLineArgs.put(STARTAT,new Integer(Integer.parseInt(g.getOptarg())));
                         foundArgs++;
                         break;
                     case 'i':
                         commandLineArgs.put(INCREMENT,new Integer(Integer.parseInt(g.getOptarg())));
                         foundArgs++;
                         break;
                     case 'c':
                         commandLineArgs.put(COLOR_MAP,g.getOptarg());
                         colormapDefined = true;
                         break;
                  }
            }
            
            if (foundArgs < noOfRequiredArguments) {
                printUsage();
                System.exit(1);
            }
            
        }
        
        public void printUsage() {
            System.out.println("PlexiMontageMaker ");
            System.out.println("-"+FILE + "<path to image file whose montage is to be created>");
            System.out.println("-"+MIN_THRESHOLD +"<min threshold value (double)>");
            System.out.println("-"+MAX_THRESHOLD +"<max threshold value (double)>");
            System.out.println("-"+STARTAT +"<start slice for the montage (int)>");
            System.out.println("-"+INCREMENT +"<increment slices for the montage (int)>");
            System.out.println("-"+COLOR_MAP +"<path to color map file>");
        }
        
        public String getFile() {
            return (String)commandLineArgs.get(FILE);
        }
        
        public double getMinThreshold() {
            return ((Double)commandLineArgs.get(MIN_THRESHOLD)).doubleValue();
        }
        
        public double getMaxThreshold() {
            return ((Double)commandLineArgs.get(MAX_THRESHOLD)).doubleValue();
        }

        public int getStartSlice() {
            return ((Integer)commandLineArgs.get(STARTAT)).intValue();
        }

        public int getIncrement() {
            return ((Integer)commandLineArgs.get(INCREMENT)).intValue();
        }
        
    }
    
    public PlexiMontageMaker(String argv[]) {
        CommandLineArguments commandLineArgs = new CommandLineArguments(argv);
        
    }
}
