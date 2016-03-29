package beans;
public interface IllnessScriptInterface {

	public static final int TYPE_LEARNER_CREATED = 1; //patientIllnessScript created by learner
	public static final int TYPE_EXPERT_CREATED = 2; //patientIllnessScript created by expert
	public static final int TYPE_ILLNESSSCRIPT = 3; //IllnessScript 

	public int getType();
}
