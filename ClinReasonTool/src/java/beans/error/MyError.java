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
	/**
	 * type of subclass
	 */
	private int type; 
	/**
	 * we have a list of errors, idx reflects the order in that the errors have occured.
	 */
	private int idx;
	/**
	 * type of subclass as string (discriminator needs to be a string).
	 */
	private String discr;
	private Timestamp creationDate;
	private long patIllScriptId;
	/**
	 * At which stage did the error occur? We need this for displaying errors when ddx submission occurs multiple times.
	 */
	private int stage;
	
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}
	public abstract String getDescription();
	public abstract String getName();// {return description;} //needs to be in subclasses? 
	public void setDescription(String description) {this.description = description;}
	public abstract int getType();
	public long getPatIllScriptId() {return patIllScriptId;}
	public void setPatIllScriptId(long patIllScriptId) {this.patIllScriptId = patIllScriptId;}
	public void setType(int type) {this.type = type;}	
	public int getStage() {return stage;}
	public void setStage(int stage) {this.stage = stage;}
	public String getDiscr() {return discr;}
	public void setDiscr(String discr) {this.discr = discr;}	
	public int getIdx() {return idx;}
	public void setIdx(int idx) {this.idx = idx;}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o instanceof MyError){
			MyError e = (MyError) o;
			if (e.getId()== this.getId() || e.getType()==getType() && e.getStage() == getStage()) return true;
		}
		return false;
	}

}
