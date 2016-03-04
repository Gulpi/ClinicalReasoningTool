package model;
/**
 * All items that can be part of the clinical reasoning concept map. Includes items of the meta map, such as 
 * IllnessScript or items of specific concepts maps such as Problem, Diagnosis,... 
 * @author ingahege
 *
 */
public abstract class Node {

	private long nodeId = -1;
	private int type = -1; //e.g. Problem, Diagnosis,... (do we need this?)
	private String name;
	/**
	 * for most of the nodes we have a kind of hierarchy (e.g. back pain is child of pain or atypical pneumonia
	 * is child of pneumonia)
	 */
	private long parentId;
}
