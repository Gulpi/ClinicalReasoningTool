package util.nlp;

import net.casus.util.Integer2Wrapper;
import net.casus.util.StringUtilities;
import net.casus.util.nlp.spacy.SpacyDocJson;
import net.casus.util.nlp.spacy.SpacyDocToken;
import net.casus.util.nlp.spacy.SpacyDocTokenHashKey;
import net.casus.util.nlp.spacy.SpacyDocTokenHashKey2;
import net.casus.util.nlp.spacy.SpacyStructureStats;
import net.casus.util.nlp.spacy.helper.SpacyStructureStatsDataProviderInterface;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import beans.relation.summary.SummaryStatement;
import beans.relation.summary.SummaryStatementSQ;
import database.DBClinReason;


public class SpacyStructureStatsDataProviderDB implements SpacyStructureStatsDataProviderInterface {
	SpacyStructureStats container = null;
	
	@Override
	public void init() {
		// load from DB
		List<SummaryStatement> l = new DBClinReason().readTrainingSummaryStatementsForScoring();

		
		for (int i=0;i<l.size();i++) {
			SummaryStatement loop = l.get(i);
			String text =loop.getText();
			String json = loop.getSpacy_json();

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
						sq_experts_list.add(loop2.getText().toLowerCase());
					}
						
					if (true) {
						sq_it = loop.getSqHits().iterator();
						while (sq_it.hasNext()) {
							SummaryStatementSQ loop2 = sq_it.next();
							SpacyDocToken jsqJsonObject = null;
							try {
								jsqJsonObject = loop2.getSpacyMatch();
							} catch (Exception e) {
							}
							
							if (jsqJsonObject != null) {
								int position = jsqJsonObject.getPrintpos();
								String sq = jsqJsonObject.getLabel();
								SpacyDocToken loopToken = impl.getSpacyDocToken(position);
								SpacyDocToken tree = loopToken;
								
								if (loopToken != null) {
									SpacyDocTokenHashKey hk = new SpacyDocTokenHashKey2(sq, loopToken.getToken(), loopToken).initRefs(loopToken);
									Map<String,Map<SpacyDocTokenHashKey,Integer2Wrapper>> tmp = container.getHitMap();
									container.readIn_stats(sq_experts_list, sq, loopToken, hk, tmp);
								}
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
