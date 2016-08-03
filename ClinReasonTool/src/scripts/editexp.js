/*****
 * functions for the expert script creation process.
 * 
 ******/

function chgStage(chg){
	if(chg==-1 && currentStage==1) return;
	currentStage += chg;
	window.location.href = "exp_boxes.xhtml?stage="+currentStage;
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