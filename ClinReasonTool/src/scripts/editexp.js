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
	var winloc = window.location.href;
	
	if(winloc.indexOf("stage")<0) winloc = loc+"?stage="+currentStage;
	else {
		winloc = location.href.replace("stage="+oldStage, "stage="+currentStage);		
	}	
	window.location.href = winloc;
}

/**
 * the display of the individual map is either as a step-thru (0) or complete map display (1)
 * @param url
 * @returns
 */
function toggleShowAll(){
	var winloc = window.location.href;
	if(winloc.indexOf("repdm")<0) winloc = winloc+"&repdm=0";

	if (displayMode==1){
		winloc = winloc.replace("repdm=1", "repdm=0");
	}
	else
		winloc = winloc.replace("repdm=0", "repdm=1");
	
	//alert ( winloc);
	window.location.href = winloc;
}

/*
 * expert has chosen a diagnosis to be a final one...
 */
function expFinalDiagnosis(id){
	sendAjax(id, expFinalDiagnosisCallback, "expSetFinalDiagnosis", "");
}

/*
 * expert has chosen a diagnosis to be a final one...
 */
function expNoFinalDiagnosis(id){
	sendAjax(id, expFinalDiagnosisCallback, "expSetNoFinalDiagnosis", "");
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

/* changes the stage when the final diagnosis is made */
function chgStageFinal(obj){
	var id = obj.id;
	var realId = id.substring(14);
	var newStage = $("#"+id).val();
	sendAjax(realId, chgStageCallback, "chgFinalState", newStage);
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

function toggleStageDisplay(){
    $(".stagedisplay").hide();
    $(".stagestepon").show();
    $(".stagestepoff").hide();
    if(displayMode==1){
    	$(".stagedisplay").show(); //display of stages
    	$(".stagestepon").hide(); //the navigation
    	$(".stagestepoff").show();
    }
}
