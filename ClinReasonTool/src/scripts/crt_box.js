
/**
 * javascript for displaying the illness script tabs representation
 */

/******************** general add/del*******************************/

/**
 * user changes the course of time, we trigger an ajax call to save change.
 */
/*function changeCourseOfTime(){
	var courseTime = $("#courseTime").val(); 
	sendAjax(courseTime, doNothing, "chgCourseOfTime", "");
}*/


/******************** problems tab*******************************/

/**
 * a problem is added to the list of the already added problems:
 **/
function addProblem(problemId, name){
	clearErrorMsgs();
	var prefix = $("#fdg_prefix").val();
	if(name!="") sendAjax(problemId, problemCallBack, "addProblem", prefix, name);
}


function chgProblem(id, type){
	clearErrorMsgs();
	sendAjax(id, problemCallBack, "changeProblem", type);
	
}

function delProblem(id){
	clearErrorMsgs();
	sendAjax(id, delProbCallBack, "delProblem", "");
}


function delProbCallBack(probId, selProb){
	deleteEndpoints("fdg_"+probId);
	problemCallBack(probId, selProb);
}

function problemCallBack(testId, selTest){
	$("#problems").val("");	
	$("#fdg_prefix").val("");
	$(".fdgs").remove();
	//we update the problems list and the json string
	$("[id='probform:hiddenProbButton']").click();		
	$("[id='cnxsform:hiddenCnxButton']").click();
}

function updateProbCallback(data){
	if(isCallbackStatusSuccess(data)){
		updateItemCallback(data, "fdgs", "fdg_box");
		if(isOverallExpertOn()) //re-display expert items:
			turnOverallExpFeedbackOn('expFeedback', 'icon-user-md');
	}
}


function addJokerFdg(){
	clearErrorMsgs();
	sendAjax("", problemCallBack, "addJoker", 1);
	//sendAjaxUrlHtml("", problemAddCallBack, "addJoker", 1, "probbox2.xhtml");
}

function toggleFdgFeedback(){
	clearErrorMsgs();
	toggleExpBoxFeedback("expFeedbackFdg", "fdgs", 1);
}

function togglePeersFdg(){
	clearErrorMsgs();
	togglePeerBoxFeedback("peerFeedbackFdg", "peer_score_fdg", 1);
}


/******************** diagnoses *******************************/

/*
 * a diagnosis is added to the list of the already added diagnoses:
 */
function addDiagnosis(diagnId, name){
	clearErrorMsgs();
	if(name!="") sendAjax(diagnId, diagnosisCallBack, "addDiagnosis", name);
		//sendAjaxUrlHtml(diagnId, diagnosisAddCallBack, "addDiagnosis", name, "ddxbox2.xhtml");
}

function delDiagnosis(id){
	clearErrorMsgs();
	sendAjax(id, delDiagnosisCallBack, "delDiagnosis", "");
}


function delDiagnosisCallBack(ddxId, selDDX){
	//we have to delete the endpoint separately, otherwise they persist....
	deleteEndpoints("ddx_"+ddxId);
	diagnosisCallBack();
}


function diagnosisCallBack(){
	$("#ddx").val("");		
	$(".ddxs").remove();	
	//we update the ddx boxes (re-printing all boxes)
	$("[id='ddxform:hiddenDDXButton']").click();	
	$("[id='cnxsform:hiddenCnxButton']").click();	
}

function updateDDXCallback(data){
	if(isCallbackStatusSuccess(data)){
		checkSubmitBtn();
		updateItemCallback(data, "ddxs", "ddx_box");
		if(isOverallExpertOn()) //re-display expert items:
			turnOverallExpFeedbackOn('expFeedback', 'icon-user-md');
	}
}

/** a diagnosis is changed to must not missed -red icon or toggled back*/
function toggleMnM(id){
	clearErrorMsgs();
	hideDropDown("ddddx_"+id);
	var id2 = "mnmicon_"+id;
	if ($("#"+id2).hasClass("fa-exclamation-circle1")){
		$("#"+id2).removeClass("fa-exclamation-circle1");
		$("#"+id2).addClass("fa-exclamation-circle0");
	}
	else{
		$("#"+id2).removeClass("fa-exclamation-circle0");
		$("#"+id2).addClass("fa-exclamation-circle1");
	}
	sendAjax(id, doNothing, "changeMnM",  "");
}


function addJokerDDX(){
	clearErrorMsgs();
	//sendAjax("", diagnosisCallBack, "addJoker", 2);
	sendAjaxUrlHtml("", diagnosisCallBack, "addJoker", 2, "ddxbox2.xhtml");

}

function toggleDDXFeedback(){
	clearErrorMsgs();
	toggleExpBoxFeedback("expFeedbackDDX", "ddxs", 2);
}

function togglePeersDDX(){
	clearErrorMsgs();
	togglePeerBoxFeedback("peerFeedbackDDX", "peer_score_ddx", 2);
}


/* 1. we show the submit ddx dialog and ask for confidence */
function doSubmitDDXDialog(){
	//we check whether there are diagnoses, if so we open a dialog, else we display a hint
	var ddxNum = $( ".ddxs" ).length;
	$('.ui-tooltip').remove();
	if(ddxNum>0){ //then open jdialog:
		
		$("#jdialog").dialog( "option", "width", ['300'] );
		$("#jdialog").dialog( "option", "height", ['350'] );
		
		$("#jdialog").dialog( "option", "title", submitDialogTitle );
		$("#jdialog").dialog( "option", "buttons", [ ] );
		//$("#jdialog").dialog( "option", "position", [20,20] );
		
		//loadFile();
		if(submitted=="true" || presubmitted=="true") $("#jdialog").load("submitteddialog.xhtml"); // $("#jdialog").load("submitteddialog.xhtml");
		else $("#jdialog").load("submitdialog.xhtml"); //$("#jdialog").load("submitdialog.xhtml");	
		
		$("#jdialog" ).dialog( "open" );
		
		$("#jdialog").show();
		
	}
	else{
		alert(submitDDXOne);
		$('.ui-tooltip').remove();
	}
}

/**
 * successful submission -> continue case = just close dialog...
 */
function continueCase(){
	$("#jdialog" ).dialog( "close" );
}

/* 2. user has selected ddxs and submits final ddxs */
function submitDDXConfirmed(){
	var checks = $(".chb_ddx:checked");
	if(checks.length<=0){
		alert("Please select your final diagnosis/-es.");
		return;
	}
	var ids="";
	for(i=0; i<checks.length;i++){
		ids += checks[i].value +"#";
	}
	ids = ids.substring(0, ids.length-1);
	var confVal = $( "#confidence_slider" ).slider( "value" );
	sendAjax(ids, submitDDXConfirmedCallBack, "submitDDXAndConf",  confVal);
}

/* 3. we come back after the submission and have to reload ddxs once again show feedback for submitted diagnoses */
function submitDDXConfirmedCallBack(data){
	$("#jdialog").load("submitteddialog.xhtml");
	
}

/* 5. show feedback for submitted diagnoses */
function doScoreDDXDialogCallback(){
	$(".tier_4").prop( "checked", true );
	$(".chb_ddx").attr("disabled","disabled");
}

/*
 * close the dialog and user continues with case....
 */
function backToCase(){	
	$("#jdialog" ).dialog( "close" );	
	sendAjax("", revertSubmissionCallback, "resetFinalDDX","");
}

function revertSubmissionCallback(data){
	//if(isCallbackStatusSuccess(data)){
		$(".ddxs").remove();
		presubmitted = "false";
		checkSubmitBtn();
		postEnforceFinalDDXSubmission("false");
		$("[id='ddxform:hiddenDDXButton']").click();
	//}
}

/* 
 * dialog remains open and user can choose again from list? -> what if correct diagnosis is not included 
 */
function tryAgain(){
	sendAjax("", tryAgainCallback, "resetFinalDDX","");
}

function tryAgainCallback(data){
	//if(isCallbackStatusSuccess(data)){
		//$(".ddxs").remove();
		presubmitted = "false";
		postEnforceFinalDDXSubmission("false");
		$("#jdialog").load("submitdialog.xhtml");
		//$("[id='ddxform:hiddenDDXButton']").click();
	//}
}

function showSolution(){
	sendAjax("", showSolutionCallBack, "showSolution",  "");
}

function showSolutionCallBack(data){
	//if(isCallbackStatusSuccess(data)){
		showSolutionStage = currentStageScriptNoUPdate;
		$("#jdialog" ).dialog( "close" );
		clickExpFeedbackOn();
		presubmitted = "false";
		submitted = "true";
		postEnforceFinalDDXSubmission("true");
		//make changes visible in the ddx box:
		$(".ddxs").remove();
		$("[id='ddxform:hiddenDDXButton']").click();
	//}
}

/**
 * called when user clicks on close button of the submit dialog
 */
function closeSubmitDialog(){
	//just close jdialog if diagnosis already submitted OR only submitdialog is opened.
	if(submitted=="true" || presubmitted=="false") return true; 
	if(parseInt(currentStage) < parseInt(maxSubmittedStage)){ //user can continue the case:
		sendAjax("", revertSubmissionCallback, "resetFinalDDX","");
		return true;
	}
	else{ //user has already reached max number of cards and has to decide about final diagnosis, so we do NOT close the dialog
		//but display a message:
		$("#enforceSubmitMsg").show();
		return false;
	}
}

/* change tier from dropdown menu*/
function changeTier2(id, tier){
	clearErrorMsgs();	
	var item = $("#ddx_"+id);
	var color = getRectColor(tier);
	item.css("border-color", color);
	//item.css("background-color", color);
	item.css("color", color);
	sendAjax(id, doNothing, "changeTier",  tier);
}

function ruleOut(id){
	clearErrorMsgs();
	hideDropDown("ddddx_"+id);
	var item = $("#ddx_"+id);	
	item.css("border-color", "#cccccc"); 
	item.css("color", "#cccccc");
	$("#ri_a_"+id).removeClass();
	$("#ro_a_"+id).removeClass();
	$("#ro_a_"+id).hide();
	$("#ri_a_"+id).show();
	sendAjax(id, doNothing, "changeTier",  5);
}

function ruleIn(id){
	clearErrorMsgs();
	hideDropDown("ddddx_"+id);
	var item = $("#ddx_"+id);	
	item.css("border-color", "#000000"); 
	item.css("color", "#000000");
	$("#ri_a_"+id).removeClass();
	$("#ro_a_"+id).removeClass();
	$("#ri_a_"+id).hide();
	$("#ro_a_"+id).show();
	sendAjax(id, doNothing, "changeTier",  5);
}

function workingDDXOn(id){
	clearErrorMsgs();
	hideDropDown("ddddx_"+id);
	var item = $("#ddx_"+id);
	item.css("background-color", getRectColor(6))
	//item.css("color", "#c00815");
	$("#wdon_a_"+id).removeClass();
	$("#wdoff_a_"+id).removeClass();
	$("#wdon_a_"+id).hide();
	$("#wdoff_a_"+id).show();
	sendAjax(id, doNothing, "changeTier",  6);
}

function workingDDXOff(id){
	clearErrorMsgs();
	hideDropDown("ddddx_"+id);
	var item = $("#ddx_"+id);
	item.css("background-color", getRectColor(-1))
	//item.css("color", "#000000");
	$("#wdoff_a_"+id).removeClass();
	$("#wdon_a_"+id).removeClass();
	$("#wdoff_a_"+id).hide();
	$("#wdon_a_"+id).show();
	sendAjax(id, doNothing, "changeTier",  6);
}

 function getRectColor(tier){
	if(tier==5) return "#cccccc"; //ruled-out
	if(tier==6) return "#cce6ff"; //working diagnosis
	if(tier==4) return "#80bfff"; //final diagnosis
	//if(tier==7) return "#f3546a"; //MnM
	return "#ffffff";
 }

function checkSubmitBtn(){
	var ddxNum = $( ".ddxs" ).length;
	if(ddxNum>0){
		$("#submitBtnSpan").removeClass("submitBtnOff");
	}
	else if(ddxNum<=0 && !$("#submitBtnSpan").hasClass("submitBtnOff"))
		$("#submitBtnSpan").addClass("submitBtnOff");		
}

/*
 * user has changed the confidence slider, so, we send the new value via ajax.
 * -> now we submit it together with the ddx submission.
 */
function submitSliderChange(){
	var confVal = $( "#confidence_slider" ).slider( "value" );
	//sendAjax(-1, doNothing, "changeConfidence",  confVal);
}
/**
 * we init the display of the dialog shown after a diagnosis has been submitted.
 * depending on the score we display different dialogs. If showSolution or score >=0.5 we als trigger 
 * the update of the ddx box to show the final diagnosis in the appropriate color.
 */
function initSubmittedDialog(){
	if(minScoreCorrect=="") minScoreCorrect = "0.5";
	presubmitted = "true";
	$(".tier_4").prop( "checked", true );
	var ddxsFBIcons = $(".expfb");
	if(ddxsFBIcons!=null){
		for(i=0; i<ddxsFBIcons.length;i++){
			var iItem =  ddxsFBIcons[i];
			if($(iItem).attr("mytooltip")=="") $(iItem).hide();
		}
	}
	//learner has the correct or pretty correct solution:
	if($("#score").val()>=parseFloat(minScoreCorrect)){ // 50 - 100% correct solution:
		submitted = "true";
		presubmitted = "false";
		$(".aftersubmit_succ").show();
		$(".aftersubmit_fail").hide();
		$(".errors").hide();
		postEnforceFinalDDXSubmission(submitted/*, myStage, maxSubmittedStage*/);
		//update ddx box:
		$(".ddxs").remove();
		$("[id='ddxform:hiddenDDXButton']").click();

		return;
	}
	 //show solution has been selected
	if(showSolutionStage!="" && showSolutionStage!="-1" && showSolutionStage!="0"){
		submitted = "true";
		$(".aftersubmit_succ").show();
		$(".aftersubmit_fail").hide();
		postEnforceFinalDDXSubmission(submitted/*, myStage, maxSubmittedStage*/);
		//update ddx box:
		$(".ddxs").remove();
		$("[id='ddxform:hiddenDDXButton']").click();

		return;
	}
	else{
		$(".aftersubmit_fail").show();	
		$(".aftersubmit_succ").hide();
		if($("#errors").html().trim()=="") 
			$(".errors").hide();
	}

}

/******************** management *******************************/

/*
 * a management item is added to the list of the already added items:
 */
function addManagement(mngId, name){
	clearErrorMsgs();
	if(name!="") sendAjax(mngId, managementCallBack, "addMng", name);
}

function delManagement(id){
	clearErrorMsgs();
	sendAjax(id, delManagementCallBack, "delMng", "");
}

function chgManagement(id, type){
	clearErrorMsgs();
	sendAjax(id, managementCallBack, "changeMng", type);
	
}

function delManagementCallBack(mngId, selMng){
	//if(isCallbackStatusSuccess(data)){
		deleteEndpoints("mng_"+mngId);
		managementCallBack(mngId, selMng);
	//}
}

function managementCallBack(mngId, selMng){
	//if(isCallbackStatusSuccess(data)){
		$("#mng").val("");	
		$(".mngs").remove();
	
		//we update the problems list and the json string
		$("[id='mngform:hiddenMngButton']").click();	
		$("[id='cnxsform:hiddenCnxButton']").click();
	//}
}

function updatMngCallback(data){
	if(isCallbackStatusSuccess(data)){
		updateItemCallback(data, "mngs", "mng_box");
		if(isOverallExpertOn()) //re-display expert items:
			turnOverallExpFeedbackOn('expFeedback', 'icon-user-md');
	}
}

function addJokerMng(){
	clearErrorMsgs();
	sendAjax("", managementCallBack, "addJoker", 4);
}

function toggleMngFeedback(){
	clearErrorMsgs();
	toggleExpBoxFeedback("expFeedbackMng", "mngs", 4);
}

function togglePeersMng(){
	clearErrorMsgs();
	togglePeerBoxFeedback("peerFeedbackMng", "peer_score_mng", 4);
}

/******************** diagnostic steps *******************************/

/*
 * a test is added to the list of the already added tests:
 */
function addTest(testId, name){
	clearErrorMsgs();
	if(name!="") sendAjax(testId, testCallBack, "addTest", name);
}

function delTest(id){
	clearErrorMsgs();
	sendAjax(id, delTestCallBack, "delTest", "");
}

function delTestCallBack(testId, selTest){
	deleteEndpoints("tst_"+testId);
	testCallBack(testId, selTest);
}

function testCallBack(testId, selTest){
	$("#test").val("");	
	$(".tests").remove();
	//we update the problems list and the json string
	$("[id='testform:hiddenTestButton']").click();		
	$("[id='cnxsform:hiddenCnxButton']").click();
}

function updateTestCallback(data){
	if(isCallbackStatusSuccess(data)){
		updateItemCallback(data, "tests","tst_box");
		if(isOverallExpertOn()) //re-display expert items:
			turnOverallExpFeedbackOn('expFeedback', 'icon-user-md');
	}
}

function chgTest(id, type){
	clearErrorMsgs();
	sendAjax(id, testCallBack, "changeTest", type);
	
}

function addJokerTest(){
	clearErrorMsgs();
	sendAjax("", testCallBack, "addJoker", 3);
}

function toggleTestFeedback(){
	clearErrorMsgs();
	toggleExpBoxFeedback("expFeedbackTest", "tests", 3);
}

function togglePeersTest(){
	clearErrorMsgs();
	togglePeerBoxFeedback("peerFeedbackTest", "peer_score_test", 3);
}

/******************** summary statement *******************************/


function saveSummSt(id){
	clearErrorMsgs();
	var text =  $("#summStText").val();		
	sendAjax(id, saveSummStatementCallBack, "saveSummStatement",text);	
}

function saveSummStatementCallBack(data){
	if(isCallbackStatusSuccess(data)){
		$("#msg_sumstform").html("Changes have been saved.");	
	}
}



 
/******************** Feedback *******************************/

  /*
   * display exp feedback (green/gray borders for boxes) for given box/type.
   * Does not include missing items!
   */
function toggleExpBoxFeedback(iconId, itemClass, type){
	if($("#"+iconId).hasClass("fa-user-md_on")){ //turn feedback off
		turnExpBoxFeedbackOff(iconId, itemClass);
		sendAjaxContext(0, doNothing, "toogleExpBoxFeedback", type);
	}
	else{ //turn feedback on
		turnExpBoxFeedbackOn(iconId, itemClass);
		sendAjaxContext(1, doNothing, "toogleExpBoxFeedback", type);
	}
}

function turnExpBoxFeedbackOn(iconId, itemClass){
	//if($("#"+iconId).hasClass("fa-user-md_on")) return; //already on

	$("#"+iconId).removeClass("fa-user-md_off");
	$("#"+iconId).addClass("fa-user-md_on");
	$("#"+iconId).attr("title", hideExpTitle);
	$("."+itemClass+"_score").show();
	if(itemClass!="ddxs") return;
	var items = $("."+itemClass);
	if(items!=null){
		for (i=0; i<items.length; i++){
			var suffix = $("#"+items[i].id).attr("suffix");
			var expSuffix = $("#expstyle_"+items[i].id).attr("suffix");
			if(suffix!=expSuffix && expSuffix>=0){
				$("#"+items[i].id).removeClass("ddxclass_"+suffix);
				$("#"+items[i].id).addClass("ddxclass_"+expSuffix);
			}
		}
	}
}

function turnExpBoxFeedbackOff(iconId, itemClass){
	if($("#"+iconId).hasClass("fa-user-md_off")) return; //already off
	
	$("#"+iconId).removeClass("fa-user-md_on");
	$("#"+iconId).addClass("fa-user-md_off");
	$("#"+iconId).attr("title", showExpTitle);
	$("."+itemClass+"_score").hide();
	if(itemClass!="ddxs") return;
	var items = $("."+itemClass);
	if(items!=null){
		for (i=0; i<items.length; i++){
			var suffix = $("#"+items[i].id).attr("suffix");
			var expSuffix = $("#expstyle_"+items[i].id).attr("suffix");
			if(suffix!=expSuffix && expSuffix>=0){
				$("#"+items[i].id).removeClass("ddxclass_"+expSuffix);
				$("#"+items[i].id).addClass("ddxclass_"+suffix);				
			}
			//var cssclass = $("#"+items[i].id).attr("class");
			//alert(cssclass);
			//addClass("box_"+score);
		}
	}
}

var addHeightPixSum = 0;
/*
 * display or hide the expert's summary statement
 */
function toggleSumFeedback(iconId, type){
	clearErrorMsgs();
	if($("#"+iconId).hasClass("fa-user-md_on")){ //turn feedback off
		$("#"+iconId).removeClass("fa-user-md_on");
		$("#"+iconId).addClass("fa-user-md_off");		
		$("#"+iconId).attr("title", showExpTitle);
		$("#list_score_sum").hide();
		sendAjaxContext(0, doNothing, "toogleExpBoxFeedback", type);

	}
	else{ //turn feedback on
		$("#"+iconId).removeClass("fa-user-md_off");	
		$("#"+iconId).addClass("fa-user-md_on");
		$("#"+iconId).attr("title", hideExpTitle);
		$("#list_score_sum").show();
		//$("#sum_box").height("300");
		sendAjaxContext(1, doNothing, "toogleExpBoxFeedback", type);
	}
}

function isOverallExpertOn(){
	//if($("#"+iconId).hasClass("fa-user-md_on"))
	//alert($("#expFeedback").prop("checked"));
	if($("#expFeedback").prop("checked"))
		return true;
	return false;
}



function clickExpFeedbackOn(){
	$("#expFeedback").prop("checked", "true");
}

/* when displaying the expert summSt we have to increase the height of the box.*/
function calcAddPixForExpSum(){
	
}
/*
 * we display the peer feedback for the given box/type
 */
function togglePeerBoxFeedback(iconId, itemClass, type){
	if($("#"+iconId).hasClass("fa-users_on")){ //turn off
		$("#"+iconId).removeClass("fa-users_on");
		$("#"+iconId).addClass("fa-users_off");		
		$("#"+iconId).removeClass("icon-users_on");
		$("#"+iconId).attr("title", "click to hide expert feedback");
		$("."+itemClass).hide();
		sendAjaxContext(0, doNothing, "tooglePeerBoxFeedback",  type);
	
	}	
	else{ //turn it on
		$("#"+iconId).removeClass("fa-users_off");	
		$("#"+iconId).addClass("fa-users_on");
		$("#"+iconId).attr("title", "click to show expert feedback");
		$("."+itemClass).show();
		sendAjaxContext(1, doNothing, "tooglePeerBoxFeedback",  type);
	}
}


/*
 * display/hide overall expert feedback (including missing items)
 */
function toggleExpFeedback(iconId, itemClass){
	if($("#"+iconId).hasClass("fa-user-md_on")){ //turn feedback off
		turnOverallExpFeedbackOff(iconId, itemClass);
		sendAjaxContext(0, doNothing, "toogleExpFeedback", "");
	}
	else{ 
		turnOverallExpFeedbackOn(iconId, itemClass);
		sendAjaxContext(1, doNothing, "toogleExpFeedback", "");
	}
}

function turnOverallExpFeedbackOn(iconId, itemClass){
	$("#"+iconId).removeClass("fa-user-md_off");	
	$("#"+iconId).addClass("fa-user-md_on");
	$("#"+iconId).attr("title", hideExpTitle);
	$(".expbox").addClass("expboxstatus_show");
	$(".expbox").removeClass("expboxstatus");
	$(".expbox").removeClass("expboxinvis");
	turnExpBoxFeedbackOn("expFeedbackFdg", "fdgs");
	turnExpBoxFeedbackOn("expFeedbackDDX", "ddxs");
	turnExpBoxFeedbackOn("expFeedbackTest", "tests");
	turnExpBoxFeedbackOn("expFeedbackMng", "mngs");
	if(isOverallCnxOn()){
		$(".jsplumb-exp-connector").addClass("jsplumb-exp-connector-show");
		$(".jsplumb-exp-connector").removeClass("jsplumb-exp-connector-hide");
	}
}

function turnOverallExpFeedbackOff(iconId, itemClass){
	$("#"+iconId).removeClass("fa-user-md_on");
	$("#"+iconId).addClass("fa-user-md_off");	
	$("#"+iconId).attr("title", showExpTitle);
	$(".expbox").removeClass("expboxstatus_show");
	$(".expbox").addClass("expboxstatus");
	turnExpBoxFeedbackOff("expFeedbackFdg", "fdgs");
	turnExpBoxFeedbackOff("expFeedbackDDX", "ddxs");
	turnExpBoxFeedbackOff("expFeedbackTest", "tests");
	turnExpBoxFeedbackOff("expFeedbackMng", "mngs");
	$(".jsplumb-exp-connector").addClass("jsplumb-exp-connector-hide");
	$(".jsplumb-exp-connector").removeClass("jsplumb-exp-connector-show");
}

function openErrorDialog(){
	$("#jdialogError").dialog( "option", "width", ['300'] );
	$("#jdialogError").dialog( "option", "height", ['300'] );
	$("#jdialogError").dialog( "option", "title", errorDialogTitle);
	$("#jdialogError").dialog( "option", "buttons", [ ] );
	//$("#jdialogError").load("errors.xhtml");
	$('.ui-tooltip').remove();
	$("#jdialogError" ).dialog( "open" );
	$("#jdialogError").show();
}

/*
 * show the expert items as a step-wise process thru the stages
 */
function showExpStages(){
	
}
/*
 * We get the position of the pos element (the itembox) and position the dropdown menu close to it
 */
function showDropDown(id, pos){	
	hideAllDropDowns(); //we first hide all in case any are still open...
	clearErrorMsgs();
	$("#"+id).show();
	var x =  $("#"+pos).position().left+10;
	var y = $("#"+pos).position().top+5;
	$("#"+id).css( { left: x + "px", top: y + "px" } ) 
}
/*
 * Onmouseleave (! not onmouseout) we hide the dropdown again
 */
function hideDropDown(id){
	$("#"+id).hide();
}

function hideAllDropDowns(){
	$(".dropdown-content").hide();
}

function clearErrorMsgs(){
	$(".errormsg").html("");
}
/**
 * opens the help dialog
 */
function openHelp(){
	clearErrorMsgs();
	
	$("#jdialogHelp").dialog( "option", "width", ['350'] );
	$("#jdialogHelp").dialog( "option", "position",  {my: "center top", at: "center top", of: window}  );
	$("#jdialogHelp").dialog( "option", "height", ['400'] );
	$("#jdialogHelp").dialog( "option", "title", helpDialogTitle);
	$("#jdialogHelp").dialog( "option", "buttons", [ ] );
	$("#jdialogHelp").load("help/index_"+lang+".template");
	//$("#help" ).dialog.html(template);
	$('.ui-tooltip').remove();
	$("#jdialogHelp" ).dialog( "open" );
	$("#jdialogHelp").show();
}
