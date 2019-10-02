package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Properties;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionListener;

/**
 * View to display open sessions...
 * @author gulpi
 *
 */
@ManagedBean(name = "version", eager = true)
@ApplicationScoped
public  class Version  extends Observable implements Serializable {
	
	private static final Properties version_props = new Properties();
	
	String version = "";
	String versionDate = "";
	String longcommit = "";
	String container = "";
	String containerVersion = "";
	String javaVersion = "";
	String runtimeName = "";
	String osName = "";
	String osVersion = "";
	String serverStartTime = "";
	String freeMemoryPercent = "";
	String freeMemory = "";
	String totalMemory = "";
	String systemMillis = "";

	public Version(){
	    try {
			FacesContext fc = FacesContext.getCurrentInstance();
			ExternalContext ec = fc.getExternalContext();
			ServletContext servletContext = (ServletContext) FacesContext
				    .getCurrentInstance().getExternalContext().getContext();
			
			if (version_props.size() == 0) {
				try{
			    	//load properties for the application(file is in WEB-INF/classes:
			    	InputStream input =  Thread.currentThread().getContextClassLoader().getResourceAsStream("properties/version.properties");
			    	version_props.load(input);
			    }
			    catch(Exception e){}
			}
			
			Date mydate = new Date();
			serverStartTime = mydate.toString();
			version = version_props.getProperty("commit", "?");
			longcommit = version_props.getProperty("longcommit", "?");
			versionDate = version_props.getProperty("date", "?");
			container = servletContext.getServerInfo();
			containerVersion = Integer.toString(servletContext.getMajorVersion()) + "." + Integer.toString(servletContext.getMinorVersion());
			javaVersion = System.getProperty("java.version");
			runtimeName  = System.getProperty("java.runtime.name");
			osName = System.getProperty("os.name");
			osVersion = System.getProperty("os.version");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getContainer() {
		return container;
	}

	public void setContainer(String container) {
		this.container = container;
	}

	public String getContainerVersion() {
		return containerVersion;
	}

	public void setContainerVersion(String containerVersion) {
		this.containerVersion = containerVersion;
	}

	public String getJavaVersion() {
		return javaVersion;
	}

	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}

	public String getRuntimeName() {
		return runtimeName;
	}

	public void setRuntimeName(String runtimeName) {
		this.runtimeName = runtimeName;
	}

	public String getOsName() {
		return osName;
	}

	public void setOsName(String osName) {
		this.osName = osName;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public static Properties getVersionProps() {
		return version_props;
	}

	public String getLongcommit() {
		return longcommit;
	}

	public void setLongcommit(String longcommit) {
		this.longcommit = longcommit;
	}

	public String getVersionDate() {
		return versionDate;
	}

	public void setVersionDate(String versionDate) {
		this.versionDate = versionDate;
	}

	public String getServerStartTime() {
		return serverStartTime;
	}

	public void setServerStartTime(String serverStartTime) {
		this.serverStartTime = serverStartTime;
	}

	public String getFreeMemory() {
		long freeMem = Runtime.getRuntime().freeMemory();
		freeMemory = Long.toString(freeMem);
		return freeMemory;
	}

	public void setFreeMemory(String freeMemory) {
		this.freeMemory = freeMemory;
	}

	public String getTotalMemory() {
		long totalMem =  Runtime.getRuntime().totalMemory();
		totalMemory = Long.toString(totalMem);
		return totalMemory;
	}

	public void setTotalMemory(String totalMemory) {
		this.totalMemory = totalMemory;
	}

	public String getSystemMillis() {
		long actMillis = System.currentTimeMillis();
		systemMillis = Long.toString(actMillis);
		return systemMillis;
	}

	public void setSystemMillis(String systemMillis) {
		this.systemMillis = systemMillis;
	}

	public String getFreeMemoryPercent() {
		long freeMem = Runtime.getRuntime().freeMemory();
	    long totalMem = Runtime.getRuntime().totalMemory();
	    freeMemoryPercent = Long.toString(freeMem*100/totalMem); 
		return freeMemoryPercent;
	}

	public void setFreeMemoryPercent(String freeMemoryPercent) {
		this.freeMemoryPercent = freeMemoryPercent;
	}

	
    
}
