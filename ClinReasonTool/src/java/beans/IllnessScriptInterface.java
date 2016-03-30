package beans;
public interface IllnessScriptInterface {

	public static final int TYPE_LEARNER_CREATED = 1; //patientIllnessScript created by learner
	public static final int TYPE_EXPERT_CREATED = 2; //patientIllnessScript created by expert
	public static final int TYPE_ILLNESSSCRIPT = 3; //IllnessScript 
	public static final int TYPE_PEER_CREATED = 4;
	public static final int TYPE_PEER_CREATED_EXPLICIT = 5; //we need this to distinguish between cnx made by peers explicitly (by conx) from those made implicitly (=4)
	public int getType();
}
