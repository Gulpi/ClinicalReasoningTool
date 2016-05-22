/**
 * javascript for displaying the illness script tabs representation
 */

/******************** patient tab*******************************/
/**
 * user changes the course of time, we trigger an ajax call to save change.
 */
function changeCourseOfTime(){
	var courseTime = $("#courseTime").val(); 
	sendAjax(courseTime, doNothing, "chgCourseOfTime", "");
}

/******************** epi tab*******************************/

/**
 * a problem is added to the list of the already added problems:
 **/
function addEpi(problemId, name){
	//here we have to trigger an ajax call... 
	//var problemId = $("#problems").val();
	if(name!="") sendAjax(problemId, epiCallBack, "addEpi", name);
}


/* if the answer was correct, but there is a better choice we offer the learner to change it by clicking on the checkmark*/
/*function chgEpi(orgId, toChgId){
	var confirmMsg = confirm("The term you have chosen is correct, however, a better choice would have been Dyspnea. Do you want to change your choice?");
	if(confirmMsg){
		$("#selprob_"+orgId).html("Dyspnea<i class=\"icon-ok2\"></i>");
		$("#selprob_"+orgId).attr("id","selprob_"+toChgId);
		//also trigger an ajax call to store the change....
	}
}*/

function delEpi(id){
	sendAjax(id, epiCallBack, "delEpi", "");
}
function delEpiCM(id){
	sendAjax(id, epiCallBackCM, "delEpi", "");
}

function epiCallBack(problemId, selProblem){
	$("#epi").val("");	
	//we update the problems list and the json string
	$("[id='epiform:hiddenEpiButton']").click();	
}

function epiCallBackCM(epiId, selEpi){
	//we update the problems list and the json string
	$("[id='epiform:hiddenEpibButton']").click();	
	$("[id='graphform:hiddenGraphButton']").click();
}


function reOrderEpi(newOrder, id){
	sendAjax(id, epiCallBack, "reorderEpi", newOrder);
}

/******************** problems tab*******************************/

/**
 * a problem is added to the list of the already added problems:
 **/
function addProblem(problemId, name){
	clearErrorMsgs();
	var prefix = $("#fdg_prefix").val();
	if(name!="") sendAjax(problemId, problemCallBack, "addProblem", prefix);
}


function chgProblem(id, type){
	clearErrorMsgs();
	sendAjax(id, problemCallBack, "changeProblem", type);
	
}

/*function toggleProblem(id){
	sendAjax(id, problemCallBack, "toggleProblem", "");
}*/

function delProblem(id){
	clearErrorMsgs();
	sendAjax(id, problemCallBack, "delProblem", "");
}


function problemCallBack(problemId, selProblem){
	$("#problems").val("");	
	
	$(".fdgs").remove(); 
	//we update the problems list and the json string
	$("[id='probform:hiddenProbButton']").click();	
	//initLists();
	//$("[id='probform:hiddenGraphButton']").click();
}

function addJokerFdg(){
	clearErrorMsgs();
	sendAjax("", problemCallBack, "addJoker", 1);
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
}

function delDiagnosis(id){
	clearErrorMsgs();
	sendAjax(id, diagnosisCallBack, "delDiagnosis", "");
}


function diagnosisCallBack(ddxId, selDDX){
	$("#ddx").val("");	
	$(".ddxs").remove();
	//we update the problems list and the json string
	$("[id='ddxform:hiddenDDXButton']").click();	
		
}


/*function hasAFinalDiagnosis(){
	//tiericon_1
	var numFinDiagn = $(".icon-circle-tier4").length;
	if(numFinDiagn>0) return true;
	return false;
}*/

/** a diagnosis is changed to must not missed -red icon or toggled back*/
function toggleMnM(id){
	clearErrorMsgs();
	var id2 = "mnmicon_"+id;
	if ($("#"+id2).hasClass("icon-attention1")){
		$("#"+id2).removeClass("icon-attention1");
		$("#"+id2).addClass("icon-attention0");
	}
	else{
		$("#"+id2).removeClass("icon-attention0");
		$("#"+id2).addClass("icon-attention1");
	}
	sendAjax(id, doNothing, "changeMnM",  "");
}


function addJokerDDX(){
	clearErrorMsgs();
	sendAjax("", diagnosisCallBack, "addJoker", 2);
}

function toggleDDXFeedback(){
	clearErrorMsgs();
	toggleExpBoxFeedback("expFeedbackDDX", "ddxs", 2);
}

function togglePeersDDX(){
	clearErrorMsgs();
	togglePeerBoxFeedback("peerFeedbackDDX", "peer_score_ddx", 2);
}

/* 1. we update the DDX before opening the dialog*/
function doSubmitDDXDialog(){
	clearErrorMsgs();
	$("[id='ddx_submit_form:hiddenDDXSubmitButton']").click();	//update the ddxs to submit!
	//doSubmitDDXDialogCallback();
}

/* 2. we show the submit ddx dialog and ask for confidence */
function doSubmitDDXDialogCallback(){
	//we check whether there are diagnoses, if so we open a dialog, else we display a hint
	var ddxNum = $( ".ddxs" ).length;
	
	if(ddxNum>0){
		$(".tier_4").prop( "checked", true );
		$("#jdialog").dialog( "option", "width", ['300'] );
		$("#jdialog").dialog( "option", "height", ['350'] );
		$("#jdialog").dialog( "option", "title", "Submit final diagnoses" );
		$("#jdialog").dialog( "option", "buttons", [ ] );
		$("#jdialog" ).dialog( "open" );
	}
	else{
		alert("In order to submit your final diagnoses you have at least to add one differential diagnosis.");
	}
}

/* 3. user has selected ddxs and submits final ddxs */
function submitDDXConfirmed(){
	var checks = $(".chb_ddx:checked");
	if(checks.length<=0){
		alert("nothing selected.");
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

/* 4. we come back after the submission and have to reload ddxs once again show feedback for submitted diagnoses */
function submitDDXConfirmedCallBack(){
	$("[id='ddx_submit_form:hiddenDDXSubmitScoreButton']").click();	

}

/* 5. show feedback for submitted diagnoses */
function doScoreDDXDialogCallback(){
	$(".tier_4").prop( "checked", true );
	$(".chb_ddx").attr("disabled","disabled");
	$("#ddx_submit_btn").hide();
	$(".aftersubmit").show();	
	$(".ddxsubmit_score").show();
	//$("[id='ddxform:hiddenDDXButton']").click(); //update ddx in background	
}



/*
 * close the dialog and user continues with case....
 */
function backToCase(){
	$("#ddx_submit_btn").show();
	$(".aftersubmit").hide();
	$("#jdialog" ).dialog( "close" );
	sendAjax("", revertSubmissionCallback, "resetFinalDDX","");
}

function revertSubmissionCallback(){
	$("[id='ddxform:hiddenDDXButton']").click();
}

/* 
 * dialog remains open and user can choose again from list? -> what if correct diagnosis is not included 
 */
function tryAgain(){
	$("#ddx_submit_btn").show();
	$(".aftersubmit").hide();
	sendAjax("", revertSubmissionCallback, "resetFinalDDX","");
}

function showSolution(){
	$("#ddx_submit_btn").show();
	$(".aftersubmit").hide();
	$("#jdialog" ).dialog( "close" );
	//todo turn feedback for ddx on and show complete expert solution....
}


//var tiermsg=["I do not now","Clinically high likelyhood","Clinically moderate likelyhood","Clinically low likelyhood","My final diagnosis"];

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
	var item = $("#ddx_"+id);	
	item.css("border-color", "#000000"); 
	item.css("color", "#000000");
	$("#ri_a_"+id).removeClass();
	$("#ro_a_"+id).removeClass();
	$("#ri_a_"+id).hide();
	$("#ro_a_"+id).show();
	sendAjax(id, doNothing, "changeTier",  5);
}

 function getRectColor(tier){
	if(tier==5) return "#cccccc"; //ruled-out
	if(tier==7) return "#f3546a"; //MnM
	return "#000000";
 }


/*
 * user has changed the confidence slider, so, we send the new value via ajax.
 * -> now we submit it together with the ddx submission.
 */
function submitSliderChange(){
	var confVal = $( "#confidence_slider" ).slider( "value" );
	//sendAjax(-1, doNothing, "changeConfidence",  confVal);
}

/******************** management *******************************/

/*
 * a management item is added to the list of the already added items:
 */
function addManagement(mngId, name){
	clearErrorMsgs();
	if(name!="") sendAjax(mngId, managementCallBack, "addMng", name);
	//if(mngId>-1) addManagementCallBack(mngId, selMng);
}

function delManagement(id){
	clearErrorMsgs();
	sendAjax(id, managementCallBack, "delMng", "");
}

function chgManagement(id, type){
	clearErrorMsgs();
	sendAjax(id, managementCallBack, "changeMng", type);
	
}
function managementCallBack(mngId, selMng){
	$("#mng").val("");	
	$(".mngs").remove();

	//we update the problems list and the json string
	$("[id='mngform:hiddenMngButton']").click();	
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
	sendAjax(id, testCallBack, "delTest", "");
}

function testCallBack(testId, selTest){
	$("#test").val("");	
	$(".tests").remove();
	//we update the problems list and the json string
	$("[id='testform:hiddenTestButton']").click();		
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

function saveSummStatementCallBack(){
	$("#msg_sumstform").html("Changes have been saved.");	
}


/******************** list init *******************************/

var map_autocomplete_instance = null;

/** init the lists for selecting problems, diagnoses etc.*/
  $(function() {
	    function log( message ) {
	      $( "<div>" ).text( message ).prependTo( "#log" );
	      $( "#log" ).scrollTop( 0 );
	    }
	 
	    $.ajax({ //list for problems (list view)
	        url: listUrl,
	        dataType: "json",
	        success: function( data ) {
	          $( "#problems" ).autocomplete({
	            source: data,
	            minLength: minLengthTypeAhead,
	            select: function( event, ui ) {
	            	addProblem(ui.item.value, ui.item.label);
	            },
	  	      close: function(ui) {
	  	        $("#problems").val("");
	  	      }
	          });
	        }
	      });
	    $.ajax({ //list for epi (list view)
	        url: listUrl,
	        dataType: "json",
	        success: function( data ) {
	          $( "#epi" ).autocomplete({
	            source: data,
	            minLength: minLengthTypeAhead,
	            select: function( event, ui ) {
	            	addEpi(ui.item.value, ui.item.label);
	            },
	  	      close: function(ui) {
	  	        $("#epi").val("");
	  	      }
	          });
	        }
	      });	    
	    $.ajax({ //list for diagnoses (list view)
	        url: listUrl,
	        dataType: "json",
	        success: function( data ) {
	          $( "#ddx" ).autocomplete({
	            source: data,
	            minLength: minLengthTypeAhead,
	            select: function( event, ui ) {
	            	addDiagnosis(ui.item.value, ui.item.label);
	            },
	  	      close: function(ui) {
	  	        $("#ddx").val("");
	  	        
	  	      }
	          });
	        }
	      });    
	    
	    $.ajax({ //list for diagnostic steps (list view)
	        url: listUrl,
	        dataType: "json",
	        success: function( data ) {
	          $( "#tests" ).autocomplete({
	            source: data,
	            minLength: minLengthTypeAhead,
	            select: function( event, ui ) {
	            	addTest(ui.item.value, ui.item.label);
	            },
	  	      close: function(ui) {
	  	        $("#tests").val("");
	  	       
	  	        //$("#tests").autocomplete( "destroy" );
	  	      }
	            
	          });
	        }
	      });
	    $.ajax({ //list for management item (list view)
	        url: listUrl,
	        dataType: "json",
	        success: function( data ) {
	          $( "#mng" ).autocomplete({
	            source: data,
	            minLength: minLengthTypeAhead,
	            select: function( event, ui ) {
	            	addManagement(ui.item.value, ui.item.label);
	            },
	  	      close: function(ui) {
	  	        $("#mng").val("");
	  	      }
	          });
	        }
	      });
	  });
  
  


/** when coming back from adding items, we empty the map and init it again from the updated json string*/
/*function updateCallback(data){
	switch (data.status) {
    case "begin": // This is called right before ajax request is been sent.
        //button.disabled = true;
        break;

    case "complete": // This is called right after ajax response is received.
        // We don't want to enable it yet here, right?
        break;

    case "success": // This is called right after update of HTML DO, we do no longer update map here, this is only done when clicking on tab.
    	//my_canvas.clear();   	
    	//alert("updateCallback");
    	//initConceptMap();
    	//initLists();
    	alert("end");
        break;
	}
}
*/
/******************** Feedback *******************************/

  /*
   * display exp feedback (green/gray borders for boxes) for given box/type.
   * Does not include missing items!
   */
function toggleExpBoxFeedback(iconId, itemClass, type){
	if($("#"+iconId).hasClass("icon-user-md_on")){ //turn feedback off
		turnExpBoxFeedbackOff(iconId, itemClass);
		sendAjaxContext(0, doNothing, "toogleExpBoxFeedback", type);
	}
	else{ //turn feedback on
		turnExpBoxFeedbackOn(iconId, itemClass);
		sendAjaxContext(1, doNothing, "toogleExpBoxFeedback", type);
	}
}

function turnExpBoxFeedbackOn(iconId, itemClass){
	if($("#"+iconId).hasClass("icon-user-md_on")) return; //already on
	
	$("#"+iconId).attr("class","icon-user-md_on");
	$("#"+iconId).attr("title", "click to hide expert feedback");
	var items = $("."+itemClass);
	if(items!=null){
		for (i=0; i<items.length; i++){
			var score = $("#"+items[i].id).attr("score");
			$("#"+items[i].id).addClass("box_"+score);
		}
	}
}

function turnExpBoxFeedbackOff(iconId, itemClass){
	if($("#"+iconId).hasClass("icon-user-md")) return; //already off
	
	$("#"+iconId).attr("class","icon-user-md");
	$("#"+iconId).attr("title", "click to show expert feedback");
	var items = $("."+itemClass);
	if(items!=null){
		for (i=0; i<items.length; i++){
			var score = $("#"+items[i].id).attr("score");
			$("#"+items[i].id).removeClass("box_"+score);
		}
	}
}


/*
 * display or hide the expert's summary statement
 */
function toggleSumFeedback(iconId, type){
	clearErrorMsgs();
	if($("#"+iconId).hasClass("icon-user-md_on")){ //turn feedback off
		$("#"+iconId).attr("class","icon-user-md");
		$("#"+iconId).attr("title", "click to show expert feedback");
		$("#list_score_sum").hide();
		sendAjaxContext(0, doNothing, "toogleExpBoxFeedback", type);

	}
	else{ //turn feedback on
		$("#"+iconId).attr("class","icon-user-md_on");
		$("#"+iconId).attr("title", "click to hide expert feedback");
		$("#list_score_sum").show();
		sendAjaxContext(1, doNothing, "toogleExpBoxFeedback", type);
	}
}
/*
 * we display the peer feedback for the given box/type
 */
function togglePeerBoxFeedback(iconId, itemClass, type){
	if($("#"+iconId).hasClass("icon-users_on")){ //turn off
		$("#"+iconId).addClass("icon-users");
		$("#"+iconId).removeClass("icon-users_on");
		$("#"+iconId).attr("title", "click to hide expert feedback");
		$("."+itemClass).hide();
		sendAjaxContext(0, doNothing, "tooglePeerBoxFeedback",  type);
	
	}	
	else{ //turn it on
		$("#"+iconId).addClass("icon-users_on");
		$("#"+iconId).removeClass("icon-users");
		$("#"+iconId).attr("title", "click to show expert feedback");
		$("."+itemClass).show();
		sendAjaxContext(1, doNothing, "tooglePeerBoxFeedback",  type);
	}
}


/*
 * display/hide overall expert feedback (including missing items)
 */
function toggleExpFeedback(iconId, itemClass){
	if($("#"+iconId).hasClass("icon-user-md_on")){ //turn feedback off
		$("#"+iconId).attr("class","icon-user-md");
		$("#"+iconId).attr("title", "click to show expert feedback");
		$(".expbox").removeClass("expboxstatus_show");
		$(".expbox").addClass("expboxstatus");
		turnExpBoxFeedbackOff("expFeedbackFdg", "fdgs");
		turnExpBoxFeedbackOff("expFeedbackDDX", "ddxs");
		$(".jsplumb-exp-connector").addClass("jsplumb-exp-connector-hide");
		$(".jsplumb-exp-connector").removeClass("jsplumb-exp-connector-show");
		sendAjaxContext(0, doNothing, "toogleExpFeedback", "");

	}
	else{ //turn feedback on
		$("#"+iconId).attr("class","icon-user-md_on");
		$("#"+iconId).attr("title", "click to hide expert feedback");
		$(".expbox").addClass("expboxstatus_show");
		$(".expbox").removeClass("expboxstatus");
		turnExpBoxFeedbackOn("expFeedbackFdg", "fdgs");
		turnExpBoxFeedbackOn("expFeedbackDDX", "ddxs");
		$(".jsplumb-exp-connector").addClass("jsplumb-exp-connector-show");
		$(".jsplumb-exp-connector").removeClass("jsplumb-exp-connector-hide");
		sendAjaxContext(1, doNothing, "toogleExpFeedback", "");
	}
}

function openErrorDialog(){
	$("#jdialogError").dialog( "option", "width", ['200'] );
	$("#jdialogError").dialog( "option", "height", ['200'] );
	$("#jdialogError").dialog( "option", "title", "Potential errors" );
	$("#jdialogError").dialog( "option", "buttons", [ ] );
	$("#jdialogError" ).dialog( "open" );
	$("#jdialogError").show();
}



function showDropDown(id){
	$("#"+id).show();
}

function hideDropDown(id){
	$("#"+id).hide();
}

function clearErrorMsgs(){
	$(".errormsg").html("");
}
