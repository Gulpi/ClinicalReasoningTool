package beans.list;

/**
 * List of physical or mathematical units we like to extract from texts (e.g. the summary statement)
 * @author ingahege
 *
 */
public class SIUnit {

	public static final int TYPE_WEIGHT = 1; //e.g. kg, oz
	public static final int TYPE_VOLUME = 2; //e.g. ml
	public static final int TYPE_LENGTH = 3; //e.g. mm, mi, yd
	public static final int TYPE_TIME = 4; //e.g. sec, min, days
	public static final int TYPE_OTHER = 5; //e.g. mmHg, mmol,...
	public static final int TYPE_TEMP = 6; //e.g. °F, degree
	
	private long id; 
	/**
	 * name of the unit (e.g. "kg", "ml", "in",...)
	 */
	private String name; 
	/**
	 * see static type definitions of this class
	 */
	private int type;
	
	/**
	 * for the moment we store here if we have identified a number that is combined with the unit (this is 
	 * not stored in the database.
	 */
	private String numeric;
	
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public int getType() {return type;}
	public void setType(int type) {this.type = type;}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o instanceof SIUnit && (((SIUnit) o).getId()==id)) return true;
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){return name;}
			
}
