package test;

/**
 * Level of confidence, e.g. with the DDX, stored as a value between 1-100. 
 * 1-25: not at all confident, 26-50: somewhat confident , 51-75: confident, 76-100:very confident
 * @author ingahege
 * @deprecated
 */
public class Confidence {
	public static final int NOT_CONFIDENT = 1;
	public static final int SOMEWHAT_CONFIDENT = 2;
	public static final int CONFIDENT = 3;
	public static final int VERY_CONFIDENT = 4;
	
	private int levelOfConfidence = -1;
	
	
	public int getLevelOfConfidence() {return levelOfConfidence;}
	public void setLevelOfConfidence(int levelOfConfidence) {this.levelOfConfidence = levelOfConfidence;}

	public int getFuzzyLevelOfConfidenc(){
		if(levelOfConfidence<=0) return -1; //not set
		if(levelOfConfidence<=25) return NOT_CONFIDENT;
		if(levelOfConfidence>25 && levelOfConfidence<=50) return SOMEWHAT_CONFIDENT;
		if(levelOfConfidence>50 && levelOfConfidence<=75) return CONFIDENT;
		else return VERY_CONFIDENT;
	}
	
}
