package beans.helper;

public class IllnessScriptKey {

	public static final int KEY_AGE = 1;
	
	private int key = -1;
	private long illnessScriptId = -1;
	
	public String getKeyStr(){
		return key+"_"+illnessScriptId;
	}
}
