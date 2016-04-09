package beans.error;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.faces.bean.SessionScoped;

/**
 * Abstract class for all types of errors. 
 * @author ingahege
 *
 */
@SessionScoped
public  abstract class Error implements Serializable{
	private static final long serialVersionUID = 1L;

	public static final int TYPE_PREMATURE_CLOUSRE = 1;
	//....
	
	private long id; 
	private String description; //get from a general source based on the error type... 
	private int type; 
	private Timestamp creationDate;
	private long patIllScriptId;
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}
	public String getDescription() {return description;} //needs to be in subclasses? 
	public void setDescription(String description) {this.description = description;}
	public abstract long getType();
	public long getPatIllScriptId() {return patIllScriptId;}
	public void setPatIllScriptId(long patIllScriptId) {this.patIllScriptId = patIllScriptId;}
	public void setType(int type) {this.type = type;}	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o instanceof Error){
			Error e = (Error) o;
			if (e.getId()== this.getId()) return true;
		}
		return false;
	}

}
