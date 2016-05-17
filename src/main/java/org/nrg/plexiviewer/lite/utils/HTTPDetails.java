//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.utils;

import org.nrg.framework.net.JSESSIONIDCookie;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class HTTPDetails {
	public static String host="";
	public static int port=8080;
	public static String protocol="http";
	public static String webAppName="";
	public static JSESSIONIDCookie jsessionidCookie;
		
	public static void setPort(int p) {
		port = p;
	}
	
	public static int getPort() {
			return 	port;		
	}

	public static void setProtocol(String p) {
		protocol =p;
	}

	public static String getProtocol() {
		return protocol;
	}

	public static String getSuffix(String servlet) {
        String suffix = "/";
        if (webAppName != "") {
            suffix += webAppName + "/servlet" + "/" + servlet;
        }else 
            suffix +=  "servlet" + "/" + servlet;
		return suffix;
	}

	public static URL getURL(String host, String urlRequest) throws MalformedURLException{
		URL url = new URL(getProtocol(), host, getPort(), urlRequest);
		return url;
	}

	/**
	 * @return
	 */
	public static String getHost() {
		return host;
	}

	/**
	 * @param string
	 */
	public static void setHost(String string) {
		host = string;
	}

	/**
	 * @return
	 */
	public static String getWebAppName() {
		return webAppName;
	}

	/**
	 * @param string
	 */
	public static void setWebAppName(String string) {
        //System.out.println("Recd " + string);
		string = string.substring(1);
        if (string.endsWith("/")) string = string.substring(0, string.length()-1);
        //System.out.println("Recd 1" + string);
		int indexOfSlash = string.indexOf("/");
        if (indexOfSlash != -1)
            webAppName = string.substring(0,indexOfSlash);
        //System.out.println("HTTDETAILS::Set the webapp:" + webAppName +":");
	}
    
	public static JSESSIONIDCookie getJSESSIONIDCookie() {
		return jsessionidCookie;
	}

	public static void setJSESSIONIDCookie(JSESSIONIDCookie js) {
		jsessionidCookie = js;
	}
    
    public static URLConnection openConnection(URL url) throws IOException {
    	URLConnection connection = url.openConnection();
    	getJSESSIONIDCookie().setInRequestHeader(connection);
    	return connection;
    }

    public static void main(String args[]) {
        try {
            URL path = new URL("http://www.oasis-brains.org:80/applet");
            System.out.println("Host is " + path.getHost());
            HTTPDetails.setHost(path.getHost());
            HTTPDetails.setPort(path.getPort());
            HTTPDetails.setWebAppName(path.getPath());
            HTTPDetails.setProtocol(path.getProtocol());
            System.out.println("Webappname:" + HTTPDetails.getWebAppName() +":");
            String suffix = HTTPDetails.getSuffix("PopulateServlet");
            System.out.println("Suffix is " + suffix);
            URL populateServlet = HTTPDetails.getURL(HTTPDetails.getHost(),suffix);
            System.out.println("URL is " + populateServlet);
        }catch(Exception e){e.printStackTrace();}
    }

}
