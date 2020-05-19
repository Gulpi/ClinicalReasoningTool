package beans.relation.summary;

import java.io.Serializable;

/**
 * storing of the json creation of summary statement 
 * @author ingahege
 *
 */
public class JsonTest  implements Serializable, Comparable, Cloneable {

	private long id = -1;
	private String json = null;
	
	public JsonTest(){
	}
	
	public JsonTest(long summStId){
		this.id = summStId;
	}
	
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}
	public String getJson() {return json;}
	public void setJson(String json) {this.json = json;} 
	
	public boolean equals(Object o){
		if(o==null) return false;
		if(o instanceof JsonTest){
			if(this.id == ((JsonTest)o).getId()) return true;
		}
		return false;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		if (this.equals(o)) {
			return 0;
		}
		
		return 1;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
	
	
	
}
