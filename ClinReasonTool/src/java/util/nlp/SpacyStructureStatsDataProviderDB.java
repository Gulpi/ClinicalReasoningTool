package util.nlp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import beans.relation.summary.JsonTest;
import beans.relation.summary.SummaryStatement;
import beans.relation.summary.SummaryStatementSQ;
import database.DBClinReason;
import database.DBSummaryStatement;
import net.casus.util.Integer2Wrapper;
import net.casus.util.StringUtilities;
import net.casus.util.nlp.spacy.SpacyDocJson;
import net.casus.util.nlp.spacy.SpacyDocToken;
import net.casus.util.nlp.spacy.SpacyDocTokenHashKey;
import net.casus.util.nlp.spacy.SpacyDocTokenHashKey2;
import net.casus.util.nlp.spacy.SpacyStructureStats;
import net.casus.util.nlp.spacy.helper.SpacyStructureStatsDataProviderInterface;

public class SpacyStructureStatsDataProviderDB implements SpacyStructureStatsDataProviderInterface {
	SpacyStructureStats container = null;
	
	@Override
	public void init() {
		// load from DB
		List<SummaryStatement> l = new DBSummaryStatement().readTrainingSummaryStatementsForScoring();

		
		for (int i=0;i<l.size();i++) {
			SummaryStatement loop = l.get(i);
			String text =loop.getText();
			String json = loop.getSpacy_json();
			
			if (json == null) {
				JsonTest jt = new DBClinReason().selectJsonTestBySummStId(loop.getId()); //the json of the statement
				if (jt != null) {
					json = jt.getJson();
				}
			}

			if (StringUtilities.isValidString(text) && StringUtilities.isValidString(json) && StringUtilities.isValidString(json.trim())) {
				try {
					text = text.trim();
						
					SpacyDocJson impl = new SpacyDocJson(json);
					impl.init();
						
					List<String> sq_experts_list = new ArrayList<String>();
					Map<String,String> sq_experts_map = new HashMap();
					Iterator<SummaryStatementSQ> sq_it = loop.getSqHits().iterator();
					while (sq_it.hasNext()) {
						SummaryStatementSQ loop2 = sq_it.next();
						sq_experts_map.put(loop2.getText().toLowerCase(), loop2.getText());
						if (loop2.getManuallyApproved() == 0 || loop2.getManuallyApproved() == 1) {
							sq_experts_list.add(loop2.getText().toLowerCase());
						}
					}
						
					if (true) {
						sq_it = loop.getSqHits().iterator();
						while (sq_it.hasNext()) {
							SummaryStatementSQ loop2 = sq_it.next();
							SpacyDocToken jsqJsonObject = impl.getSpacyDocToken(loop2.getPosition());;
							if (jsqJsonObject != null) {
								String sq = loop2.getText().toLowerCase();
								SpacyDocTokenHashKey hk = new SpacyDocTokenHashKey2(sq, jsqJsonObject.getToken(), jsqJsonObject).initRefs(jsqJsonObject);
								Map<String,Map<SpacyDocTokenHashKey,Integer2Wrapper>> tmp = container.getHitMap();
								container.readIn_stats(sq_experts_list, sq, jsqJsonObject, hk, tmp);
							}
						}
					}
				} catch (Throwable th) {
					// TODO Auto-generated catch block
					th.printStackTrace();
				}
			}
		}
	}

	@Override
	public SpacyStructureStatsDataProviderInterface setContainer(SpacyStructureStats container) {
		this.container = container;
		return this;
	}

}
