package api.impl;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import api.ApiInterface;
import database.HibernateSession;
import database.HibernateUtil;
import net.casus.util.StringUtilities;
import net.casus.util.Utility;

/**
 * Sample ApiInterface implemenation for demo
 * 
 * hqlQuery6 & 
http://m7.instruct.de:8080/crt/src/html/api/api.xhtml?impl=la&in_case=1151758_2&in_ext_user=222112,208121,223276,200091,209297,222264,222369,222114,222116,225227,221513,210188,220797,237601,222181,224097,220937,246452,15739,237514,239659,221464,222205,247015,207402,226450,246352,225702,205557,208856,245105,239713,227639,240699,238449,244817,207979,210603,221674,210179,221353,182021,225339,210892,226187,222321,194601,226526,210660,232640,222224,194435,207496,223647,206186,207938,221599,226266,248619,208803,210575,210577,225513,224764,207747,210403,226461,223316,198792,226430,223352,223373,246143,223572,219310,236537,237454,202436,241491,241493,209244,160784,71500,177661,227408,234415,151796,234478,7875,156696,7900,200233,219986,232473,231054,231203,95846,231329) *
 * 
 * @author Gulpi (=Martin Adler)
 */
public class LearningAnalytics1 implements ApiInterface {

	@Override
	public String handle() {
 		String result = "";
		try {
			String in_case = (String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("in_case");
			String in_ext_user = (String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("in_ext_user");
			
			String hqlQuery6 = "select listItemId,count(*) from beans.relation.RelationDiagnosis where destId in ( " + 
					"select id from beans.scripts.PatientIllnessScript where vp_id = :in_case and userId in (\n" + 
					"select userId from beans.user.User where extUserId2 in ( :in_ext_user ) ) )\n" + 
					"group by listItemId";
			
			String hqlQuery = "select sourceId,count,(select name from beans.list.ListItem li where li.item_id = sourceId) from ( " + 
					"select listItemId as sourceId,count(*) as count from beans.relation.RelationDiagnosis where destId in ( " + 
					"select id from beans.scripts.PatientIllnessScript where  userId in ( " + 
					"select userId from beans.user.User where extUserId in ( :in_ext_user) " + 
					"and vp_id = :in_case\n" + 
					")\n" + 
					")\n" + 
					"group by listItemId;";
			
			String hql = hqlQuery6;
			Session session = HibernateUtil.impl.getHibernateSession();
			Query query = session.createQuery(hql);
			if (hql.contains(":in_case")) {
				query.setString("in_case", in_case);
			}
			List<String> myList = StringUtilities.getStringListFromString(in_ext_user, ",");
			if (hql.contains(":in_ext_user")) {
				query.setParameterList("in_ext_user", myList);
			}
			
			StringWriter sw = new StringWriter();
			List l = query.list();
			if (l != null) {
				Iterator it = l.iterator();
				while (it.hasNext()) {
					Object[] loop = (Object[]) it.next();
					for (int i=0;i<loop.length;i++) {
						if (i>0) {
							sw.append("\t");
						}
						sw.append(loop[i].toString());
					}
					sw.append("\n");
				}
			}
			result = sw.toString();
			
		} catch (HibernateException e) {
			result = Utility.stackTraceToString(e);
		}
		return result;
	}

}
