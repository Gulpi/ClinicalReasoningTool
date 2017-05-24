package beans.xAPI;

import java.io.Serializable;

public interface AddActionStatement extends Serializable{

	/**
	 * type of action statement, e.g. problem identification,...
	 * @return
	 */
	public int getType();
	public boolean hasBeenSent();
	public String getResponse();
	//public JsonElement serialize();
	public void addResponseToResult(String s);
	public void deleteResponseFromResult(String s);
}
