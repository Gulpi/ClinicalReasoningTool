/*****
 * functions for the expert script creation process.
 * 
 ******/

function chgStage(chg, loc){
	if(chg==-1 && currentStage==1) return;
	currentStage += chg;
	window.location.href = loc+"?stage="+currentStage;
	//$("#stageSpan").html(currStage);
}

/*
 * expert has chosen a diagnosis to be a final one...
 */
function expFinalDiagnosis(id){
	sendAjax(id, expFinalDiagnosisCallback, "expSetFinalDiagnosis", "");
}

function expFinalDiagnosisCallback(){
	diagnosisCallBack();
}

/* expert changes the stage for a problem or ddx or ...*/
function chgStageItem(obj){
	var id = obj.id;
	var realId = id.substring(9);
	var newStage = $("#"+id).val();
	sendAjax(realId, chgStageCallback, "chgStateOfItem", newStage);
}

function chgStageCallback(){}