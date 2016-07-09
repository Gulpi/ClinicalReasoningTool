package test;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;

import actions.scoringActions.ScoringSummStAction;
import util.CRTLogger;
import util.StringUtilities;

public class TextSimilarityComparing {

	private static final String testfile ="/Users/ingahege/ownCloud/documents/Inga/marie_curie/WP2_concept/SummSt/summStSimple24.txt";
	private static final String expText = "Mrs. Cole is a 52-year-old female s/p treatment with broad-spectrum antibiotics and mechanical ventilation in the ICU for Strep pneumoniae meningitis complicated by septic shock and ARDS who developed new onset fever with associated diarrhea and cough on hospital day seven. Exam reveals crackles at the posterior lung bases, mild generalized abdominal tenderness without peritoneal signs, and the presence of a urinary catheter and subclavian line.";

	
	public void compareTestData(){
		FileReader fileReader = null;
		LineNumberReader lineReader = null;
		ScoringSummStAction scsa = new ScoringSummStAction();
		try {
			File f = new File(testfile);
			fileReader = new FileReader(f);
			lineReader = new LineNumberReader(fileReader);
			String line = null;
			while((line=lineReader.readLine()) != null) {
				scsa.calculateSimilarityADW(line, expText);
			}
		}
		catch(Exception e){
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
		}
		finally {
			if (lineReader != null) {
				try {
					lineReader.close();
				} catch (Exception e) {}
			}
			lineReader = null;
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (Exception e) {}
			}
			fileReader = null;
		}
	}
}
