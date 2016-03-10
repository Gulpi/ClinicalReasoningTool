package model;

import java.beans.Beans;
//we probably do not need it, since all this info is already in ListItem
public class Problem extends Beans{


	/**
	 * for some problems we can further classify them, e.g. fever - mild/severe
	 */
	private String name;
	private long id;
	
	/**
	 * Which organ is affected (e.g. for rash -> skin), this would enable also an organ-based search
	 */
	private int organ = -1; 
	
	 
}
