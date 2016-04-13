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
public  abstract class MyError implements Serializable{
	private static final long serialVersionUID = 1L;

	public static final int TYPE_PREMATURE_CLOUSRE = 1;
	//....
	
	private long id; 
	private String description; //get from a general source based on the error type... 
	private int type; 
	private String discr;
	private Timestamp creationDate;
	private long patIllScriptId;
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}
	public abstract String getDescription();
	public abstract String getName();// {return description;} //needs to be in subclasses? 
	public void setDescription(String description) {this.description = description;}
	public abstract int getType();
	public long getPatIllScriptId() {return patIllScriptId;}
	public void setPatIllScriptId(long patIllScriptId) {this.patIllScriptId = patIllScriptId;}
	public void setType(int type) {this.type = type;}	
	
	public String getDiscr() {return discr;}
	public void setDiscr(String discr) {this.discr = discr;}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o instanceof MyError){
			MyError e = (MyError) o;
			if (e.getId()== this.getId()) return true;
		}
		return false;
	}

}
