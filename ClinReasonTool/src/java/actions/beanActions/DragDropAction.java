package actions.beanActions;


import java.awt.Point;

import beans.LogEntry;
import beans.PatientIllnessScript;
import beans.relation.Relation;
import controller.GraphController;
import database.DBClinReason;

/**
 * An item has been moved in the map view (drag&drop). so, we store the new position in the map. 
 * @author ingahege
 *
 */
public class DragDropAction {
	private PatientIllnessScript patIllScript;
	
	public DragDropAction(PatientIllnessScript patIllScript){
		this.patIllScript = patIllScript;
	}
	
	public void move(String idStr, String xStr, String yStr){
		int type = GraphController.getTypeByPrefix(idStr.substring(0,6));
		long id = Long.parseLong(idStr.substring(6));
		Relation rel = patIllScript.getRelationByIdAndType(id, type);
		if(rel!=null){
			float x = Float.valueOf(xStr.trim());
			float y = Float.valueOf(yStr.trim());
			rel.setXAndY(new Point((int) x, (int) y));
		}
		save(rel);
		notifyLog(rel);
	}
	
	private void save(Relation rel){
		new DBClinReason().saveAndCommit(rel);
	}
	
	/* (non-Javadoc)
	 * @see beanActions.AddAction#notifyLog(beans.relation.Relation)
	 */
	public void notifyLog(Relation relProb){
		LogEntry le = new LogEntry(LogEntry.DRAGDROP_ACTION, patIllScript.getId(), relProb.getListItemId());
		le.save();
	}

}
