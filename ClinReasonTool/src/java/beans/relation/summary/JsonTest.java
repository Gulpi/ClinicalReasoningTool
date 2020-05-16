package beans.relation.summary;

/**
 * storing of the json creation of summary statement 
 * @author ingahege
 *
 */
public class JsonTest {

	private long id;
	private String json;
	
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
	
}
