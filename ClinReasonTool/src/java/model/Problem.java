package model;
public class Problem extends Node{


	/**
	 * for some problems we can further classify them, e.g. fever - mild/severe
	 */
	private int classifier = -1;  //better in the Rel_IS_Problem class?
	/**
	 * Which organ is affected (e.g. for rash -> skin), this would enable also an organ-based search
	 */
	private int organ = -1; 
	
	 
}
