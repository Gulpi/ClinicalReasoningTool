package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Properties;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * View to display open sessions...
 * @author gulpi
 *
 */
@ManagedBean(name = "version", eager = true)
@ApplicationScoped
public  class Version implements Serializable {
	
	private static final Properties version_props = new Properties();
	
	public void rendertext() throws IOException {
	    FacesContext fc = FacesContext.getCurrentInstance();
	    ExternalContext ec = fc.getExternalContext();
	    
	    if (version_props.size() == 0) {
	    	try{
		    	//load properties for the application(file is in WEB-INF/classes:
		    	InputStream input =  Thread.currentThread().getContextClassLoader().getResourceAsStream("properties/version.properties");
		    	version_props.load(input);
		    }
		    catch(Exception e){}
	    }
	   
	    StringWriter sw = new StringWriter();
	    
	    sw.append("Build: (commit) ");
	    sw.append(version_props.getProperty("commit", "?"));
	    sw.append("\n");     
	    
	    sw.append("Java Info: ");
	    sw.append(System.getProperty("java.version"));
	    sw.append(",");
        String runtimeName = System.getProperty("java.runtime.name");
        //runtimeName = StringUtilities.replace(runtimeName, " ", "");
        sw.append(runtimeName);
        sw.append(",");
        sw.append(System.getProperty("os.name"));
        sw.append(",");
        sw.append(System.getProperty("os.version"));
		sw.append("\n"); 
	 
	    ec.setResponseContentType("text/plain");
	    ec.setResponseCharacterEncoding("UTF-8");
	    ec.getResponseOutputWriter().write(sw.toString());

	    fc.responseComplete(); // Important! Prevents JSF from proceeding to render HTML.
	}

	
    
}
