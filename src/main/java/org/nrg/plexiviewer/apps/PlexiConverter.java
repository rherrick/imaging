/*
 * org.nrg.plexiViewer.apps.PlexiConverter
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.apps;

/**
 * @author Mohana
 */

import org.nrg.plexiviewer.converter.ConverterUtils;
import org.nrg.plexiviewer.converter.NonXnatConverter;
import org.nrg.plexiviewer.lite.UserSelection;
import org.nrg.plexiviewer.lite.io.PlexiImageFile;
import org.nrg.plexiviewer.lite.xml.LoRes;
import org.nrg.plexiviewer.lite.xml.ViewableItem;
import org.nrg.plexiviewer.manager.PlexiSpecDocReader;
import org.nrg.plexiviewer.utils.FileUtils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

public class PlexiConverter {
    Hashtable inputArgsHash;
    String  sessionListFile    = null;
    boolean debug              = false;
    boolean all                = false;
    //String file=null;
    String  session            = null;
    String  xnatSchemaLocation = null;
    boolean overwrite          = false;
    boolean radiologic = false;
    float minIntensity = -1, maxIntensity = -1;
    boolean onlyThumbnail = false;

    public PlexiConverter(String args[]) {
        if (args.length < 1 || args.length > 14) {
            showUsage();
            return;
        }
        inputArgsHash = new Hashtable();
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-all")) {
                all = true;
            } else if (args[i].equalsIgnoreCase("-file")) {
                if (i + 1 < args.length) {
                    sessionListFile = args[i + 1];
                }
            } else if (args[i].equalsIgnoreCase("-session")) {
                if (i + 1 < args.length) {
                    session = args[i + 1];
                }
            }
            if (args[i].equalsIgnoreCase("-thumbnail")) {
                onlyThumbnail = true;
            }

            if (args[i].equalsIgnoreCase("-o")) {
                overwrite = true;
            }
            if (args[i].equalsIgnoreCase("-r")) {
                radiologic = true;
            }
            if (args[i].equalsIgnoreCase("-x")) {
                xnatSchemaLocation = args[i + 1];
            }
            if (args[i].equalsIgnoreCase("-minIntensity")) {
                if (i + 1 < args.length) {
                    minIntensity = Float.parseFloat(args[i + 1]);
                }
            }
            if (args[i].equalsIgnoreCase("-maxIntensity")) {
                if (i + 1 < args.length) {
                    maxIntensity = Float.parseFloat(args[i + 1]);
                }
            }
            if (args[i].equalsIgnoreCase("-debug")) {
                debug = true;
            }
            if (args[i].equalsIgnoreCase("-version")) {
                Package p = this.getClass().getPackage();
                System.out.println("PlexiConverter Implementation Version : "
                                   + p.getImplementationVersion());
                System.exit(1);
            }

        }
    }


    public int create() {
        int exitStatus = 1;
        if (xnatSchemaLocation == null) {
            System.out.println("Will try the standalone mode");
            if (sessionListFile == null) {
                System.out.println("Unable to proceed....missing file info");
                exitStatus = 1;
                return exitStatus;
            }
            parseFileAndCallConverter(sessionListFile);
            return exitStatus;
        }
        /*
        try {
            XFTManager.GetInstance();
        } catch (XFTInitException xftInitE) {
            try {
                XDAT.init(xnatSchemaLocation);
            } catch (Exception e) {
                exitStatus = 1;
                e.printStackTrace();
                return exitStatus;
            }
        }
        */
        try {
            if (all) {
                createForAllSessions();
            } else if (sessionListFile != null) {
                exitStatus = createForSessionsInList();
            } else if (session != null) {
                exitStatus = createForSession();
            }
        } catch (Exception e) {
            exitStatus = 1;
            e.printStackTrace();
        }
        return exitStatus;
    }

    void parseFileAndCallConverter(String sessionFile) {
        ArrayList list = FileUtils.parseFile(sessionListFile);
        Iterator  iter = list.iterator();
        while (iter.hasNext()) {
            String           lineInFile     = (String) iter.next();
            String[]         lineCols       = lineInFile.split("#");
            String           fromPath       = lineCols[0];
            String           fromFile       = lineCols[1];
            String           toPath         = lineCols[2];
            String           format         = lineCols[3];
            String           outOrientation = lineCols[4];
            int              sliceNo        = Integer.parseInt(lineCols[5]);
            NonXnatConverter converter      = new NonXnatConverter(fromPath, fromFile, toPath);
            converter.setFormat(format);
            if (minIntensity != -1 && maxIntensity != -1) {
                converter.setMinIntensity(minIntensity);
                converter.setMaxIntensity(maxIntensity);
            }
            converter.createThumbnail(outOrientation, sliceNo);
        }
    }

    private void createForAllSessions() {
        try {
            Hashtable   list = org.nrg.xdat.security.ElementSecurity.GetDistinctIdValuesFor("xnat:mrSessionData", "xnat:mrSessionData.ID", null);
            Enumeration e    = list.elements();
            while (e.hasMoreElements()) {
                convert((String) e.nextElement());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int createForSessionsInList() {
        int exitStatus = 0;
        try {
            ArrayList list = FileUtils.parseFile(sessionListFile);
            Iterator  iter = list.iterator();
            while (iter.hasNext()) {
                exitStatus = convert((String) iter.next());
                if (exitStatus != 0) {
                    break;
                }
            }
            return exitStatus;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    private int createForSession() {
        try {
            return convert(session);
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }


    private int convert(String sessionId) {
        int exitStatus = 0;
        return exitStatus;
    }


    private int createLoRes(UserSelection opts) {
        int           rtn = 0;
        UserSelection opt = (UserSelection) opts.clone();
        ViewableItem  vi  = PlexiSpecDocReader.GetInstance().getSpecDoc(opts.getProject()).getViewableItem(opt.getDataType());
        if (vi.getLoResHash() != null && vi.getLoResHash().size() > 0) {
            if (opt.getLoResType() == null) {
                if (vi.getLoResHash().size() == 1) {
                    Object uniqueKey = vi.getLoResHash().keys().nextElement();
                    opt.setLoResType(((LoRes) vi.getLoResHash().get(uniqueKey)).getType());
                } else {
                    opt.setLoResType(PlexiSpecDocReader.GetInstance().getSpecDoc(opts.getProject()).getDefaultLoResType());
                }
            }
            PlexiImageFile pf = new PlexiImageFile();
            PlexiImageFile hpf;
            if (opt.hasFile()) {
                hpf = opt.getFile();
            } else {
                hpf = FileUtils.getHiResFilePath(opt.getSessionId(), opt.getProject(), opt.getDataType(), opt.getHiResLayerNum(), opt.getScanNo());
            }
            if (hpf == null) {
                System.out.println("Pf is null!! " + opt);
                return 2;
            }
            opt.setFile(hpf);
            pf = ConverterUtils.convert(opt);
            logBuild("LoRes " + opt.getLoResType(), (pf == null) ? 1 : 0, opt.getSessionId());
            if (pf == null) {
                System.out.println("Couldnt launch the conveter");
                return 3;
            }

        } else {
            System.out.println("No Low Resolution node found in the PlexiViewerSpec.xml");
        }
        return rtn;
    }

    private int createThumbnail(UserSelection opts) {
        //-BEGIN-CHANGE
        System.out.println("PlexiConverter.java::createThumbnail()");
        System.out.println(" Arguments passed:");
        System.out.println("  1) UserSelection: sessionID=" + opts.getSessionId() + "  dataType=" + opts.getDataType() + "  HiResLayerNum=" + opts.getHiResLayerNum() + "  scanNo=" + opts.getScanNo() + "  display=" + opts.getDisplay() + "  exptType=" + opts.getProject() + "  ");
        //-END-CHANGE
        int           rtn = 0;
        UserSelection opt = (UserSelection) opts.clone();
        //--->System.out.println("Creating Thumbnail....." );
        ViewableItem vi = PlexiSpecDocReader.GetInstance().getSpecDoc(opt.getProject()).getViewableItem(opt.getDataType());
        //-BEGIN-CHANGE
        System.out.println("PlexiConverter.java::createThumbnail()");
        System.out.println(" ViewableItem:  HiResFromat=" + vi.getHiRes().getFormat() + "  convertToformat=" + vi.getThumbnail().getFormat() + "  Slices=" + vi.getThumbnail().getSlices().toString());
        //-END-CHANGE
        if (vi.getThumbnail() != null) {
            PlexiImageFile pf = new PlexiImageFile();
            PlexiImageFile hpf;
            if (opt.hasFile()) {
                hpf = opt.getFile();
            } else {
                hpf = FileUtils.getHiResFilePath(opt.getSessionId(), opt.getProject(), opt.getDataType(), opt.getHiResLayerNum(), opt.getScanNo());
            }
            if (hpf == null) {
                System.out.println("Pf is null!! " + opt);
                return 2;
            }
            //-BEGIN-CHANGE
            System.out.println("PlexiConverter.java::createThumbnail() - Setting HiResFilePath to " + hpf.getPath());
            //-END-CHANGE
            opt.setFile(hpf);
            //-BEGIN-CHANGE
            System.out.println("PlexiConverter.java::createThumbnail() - Calling ConverterUtils.convert()");
            pf = ConverterUtils.convert(opt);
            System.out.println("PlexiConverter.java::createThumbnail() - Done.");
            logBuild("Thumbnail ", (pf == null) ? 1 : 0, opt.getSessionId());
            if (pf == null) {
                System.out.println("Couldnt launch the thumbnail converter");
                return 3;
            }
        } else {
            System.out.println("No Thumbnail Node found in the PlexiViewerSpec.xml file");
        }
        return rtn;
    }

    private void logBuild(String buildType, int exitStatus, String sessionId) {
        if (exitStatus == 0) {
            System.out.println("Built " + buildType + " for " + sessionId);
        } else {
            System.out.println("Couldnt Build " + buildType + " for " + sessionId);
        }
    }

    private void showError(String msg) {
        System.out.println(msg);
        System.exit(1);
    }

    private void showUsage() {
        System.out.println(getUsage());
    }

    String getUsage() {
        String usage = "Usage: plexiConverter -all [-file <path to file containing session ids>] [-session <session id>] \n";
        usage += "-o <overwrite existing files [Default: false]>\n -r <radiologic convention [Default non-radiologic]>\n";
        usage += "-x <path to XNAT project>\n";
        usage += "-minIntensity <min intensity> [used only with -file]\n";
        usage += "-maxIntensity <max intensity> [used only with -file]\n";
        usage += "-thumbnail [this option will result in only thumbnail files being generated\n";
        usage += "If -x flag is not passed, the file passed should have # separated information\n";
        usage += "<path to file>#<name of input file>#<path to write to>#<Image Format>#<orientation>#<slice no>\n";
        return usage;
    }

    public static void main(String args[]) {
        try {
            int status = new PlexiConverter(args).create();
            System.exit(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
