package beans.relation;

import beans.list.SIUnit;

public class SummaryStNumeric {

	private long id; 
	/**
	 * name of the matched item (e.g. 14mg,  128/100 mmHg,...)
	 */
	private String name; 
	
	private SIUnit unit;
	
	/**
	 * for the moment we store here if we have identified a number that is combined with the unit (this is 
	 * not stored in the database.
	 */
	private String numeric;
	
	/**
	 * position of the word in the text (0-based)
	 */
	private int pos;
	
	public SummaryStNumeric(){}
	public SummaryStNumeric(SIUnit unit, int pos){
		this.unit = unit;	
		this.pos = pos;
	}
	public SummaryStNumeric(SIUnit unit, String name, int pos){
		this.unit = unit;	
		this.name = name;
		this.pos = pos;
	}
	
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}	
	public SIUnit getUnit() {return unit;}
	public void setUnit(SIUnit unit) {this.unit = unit;}
	public int getPos() {return pos;}
	public void setPos(int pos) {this.pos = pos;}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o instanceof SummaryStNumeric && (((SummaryStNumeric) o).getId()==id)) return true;
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		if(name!=null) return name + " (" + pos + ")";
		if(unit!=null) return unit.toString() + " (" + pos + ")";
		return "";
	}
			
}
