package model;

import java.io.Serializable;

import javax.faces.bean.*;
import javax.faces.bean.SessionScoped;

/**
 * @author ingahege
 *
 */
@ManagedBean(name = "error", eager = true)
@SessionScoped
public class Error implements Serializable{
	private static final long serialVersionUID = 1L;
	private int id = -1;
	private String name ="hallo"; //maybe get from a list
	private String description;
	
	public int getId() {return id;}
	public void setId(int id) {this.id = id;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public String getDescription() {return description;}
	public void setDescription(String description) {this.description = description;} 	
	
}
