/*****
 * functions for the expert script creation process.
 * 
 ******/

function chgStage(chg, loc){
	if(chg==-1 && currentStage==1) return;
	if(chg==1 && currentStage==maxStage){
		alert("last stage");
		return;
	}
	var oldStage = currentStage;
	currentStage += chg;
	//window.location.href = loc+"?stage="+currentStage;
	var s = location.href.replace("stage="+oldStage, "stage="+currentStage);
	window.location.href = s;
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

function chgStageEdge(obj){
	var id = obj.id;
	var realId = id.substring(9);
	var newStage = $("#"+id).val();
	sendAjax(realId, chgStageCallback, "chgStateOfEdge", newStage);
}

function chgStageCallback(){}


/**
 * open the jdialog to display the editor to create a new script 
 **/
function createNewScript(){
	$("#jdialog").dialog( "option", "width", ['350'] );
	$("#jdialog").dialog( "option", "height", ['250'] );	
	$("#jdialog").dialog( "option", "title", "Create new script" );
	$("#jdialog").dialog( "option", "buttons", [ ] );
	$("#jdialog").dialog( "open" );	
	$("#jdialog").dialog( "option", "position", [0,0] );
	$("#jdialog").html();
	$("#jdialog").show();
}
