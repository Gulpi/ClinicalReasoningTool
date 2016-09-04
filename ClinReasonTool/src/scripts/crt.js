/* deprecated /*
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
	//here we have to trigger an ajax call... 
	//var problemId = $("#problems").val();
	var prefix = $("#fdg_prefix").val();
	if(name!="") sendAjax(problemId, problemCallBack, "addProblem", prefix);
}

/* if the answer was correct, but there is a better choice we offer the learner to change it by clicking on the checkmark*/
function chgProblem(id, allowed, expterm){
	if(allowed!=true) return;
	var confirmMsg = confirm("The term you have chosen is correct, however, the expert has chosen "+expterm+". Do you want to change your choice?");
	if(confirmMsg){
		sendAjax(id, problemCallBack, "changeProblem", "");
	}
}

function chgProblem(id, type){
	sendAjax(id, problemCallBack, "changeProblem", type);
	
}

/*function toggleProblem(id){
	sendAjax(id, problemCallBack, "toggleProblem", "");
}*/

function delProblem(id){
	sendAjax(id, problemCallBack, "delProblem", "");
}

function delProblemCM(id){
	sendAjax(id, problemCallBackCM, "delProblem", "");
}

function problemCallBack(problemId, selProblem){
	$("#problems").val("");	
	//we update the problems list and the json string
	$("[id='probform:hiddenProbButton']").click();	
	//initLists();
	//$("[id='probform:hiddenGraphButton']").click();
}

function problemCallBackCM(problemId, selProblem){
	//we update the problems list and the json string
	$("[id='probform:hiddenProbButton']").click();	
	$("[id='graphform:hiddenGraphButton']").click();
}


function reOrderProblems(newOrder, id){
	sendAjax(id, problemCallBack, "reorderProblems", newOrder);
}

/******************** diagnoses *******************************/

/*
 * a diagnosis is added to the list of the already added diagnoses:
 */
function addDiagnosis(diagnId, name){
	if(name!="") sendAjax(diagnId, diagnosisCallBack, "addDiagnosis", name);
}

function delDiagnosis(id){
	sendAjax(id, diagnosisCallBack, "delDiagnosis", "");
}

function delDiagnosisCM(id){
	sendAjax(id, diagnosisCallBackCM, "delDiagnosis", "");
}

function diagnosisCallBack(ddxId, selDDX){
	$("#ddx").val("");	
	//we update the problems list and the json string
	$("[id='ddxform:hiddenDDXButton']").click();	
	//$("[id='probform:hiddenGraphButton']").click();
}

function diagnosisCallBackCM(ddxId, selDDX){
	//we update the problems list and the json string
	$("[id='ddxform:hiddenDDXButton']").click();	
	$("[id='graphform:hiddenGraphButton']").click();
}

function reOrderDiagnoses(newOrder, id){
	sendAjax(id, diagnosisCallBack, "reorderDiagnoses", newOrder);
}

function hasAFinalDiagnosis(){
	//tiericon_1
	var numFinDiagn = $(".icon-circle-tier4").length;
	if(numFinDiagn>0) return true;
	return false;
}

/** a diagnosis is changed to must not missed -red icon or toggled back*/
function toggleMnM(id){
	var id2 = "mnmicon_"+id;
	var actClass = $("#"+id2).attr("class");
	var newClass = "icon-attention0";
	$("#"+id2).removeClass(actClass);
	if(actClass=="icon-attention0") 
			newClass = "icon-attention1";
	
	$("#"+id2).addClass(newClass);
	id = id.substring(7);
	var newClass = newClass.substring(14);
	sendAjax(id, toggleMnMCallback, "changeMnM",  "");
}

function toggleMnMCallback(){
	//TODO: we would have to update all resources...
}

/* we submit the DDX (or only final diagnoses?) and ask for certainity*/
function doSubmitDDX(id){
	//we check whether there is a final diagnosis, if so we submit, else we display a hint
	//if()
	//$("#jdialog").dialog( "option", "position", ['center',50] );
	/*$("#jdialog").dialog( "option", "width", ['200'] );
	$("#jdialog").dialog( "option", "height", ['200'] );
	$("#jdialog").dialog( "option", "title", "Submit final diagnoses" );
	$("#jdialog").dialog( "option", "buttons", [ ] );
	$("#jdialog" ).dialog( "open" );*/
	sendAjax(id, submitDDXConfirmedCallBack, "submitDDX",  "");
	
}

/* user has confirmed that he wants to submit disgnoses/-is*/
/*function submitDDXConfirmed(){
	$("[id='ddxsubmitform:hiddenDDXSubmitButton']").click();	

	//sendAjax("", submitDDXConfirmedCallBack, "submitDDX",  "");
	//submitDDXConfirmedCallBack();
}*/

function submitDDXConfirmedCallBack(){
	$("#jdialog" ).dialog( "close" );
	$("[id='ddxform:hiddenDDXButton']").click();	

	//now we have to display feedback concerning the ddx submission: 	
}

//var tiermsg=["I do not now","Clinically high likelyhood","Clinically moderate likelyhood","Clinically low likelyhood","My final diagnosis"];

/* we change the tier-color of the corresponding diagnosis*/
/*function changeTier(id){
	var id2 = "tiericon_"+id;
	var actClass = $("#"+id2).attr("class");
	var num = Number(actClass.charAt( actClass.length-1 )) +1;
	if(num>=5) num=0;
	$("#"+id2).removeClass(actClass);
	$("#"+id2).addClass("icon-circle-tier"+num);
	$("#tiera_"+id).prop("title",tiermsg[num]);
	toggleSubmit();	
	sendAjax(id, doNothing, "changeTier",  num);
}*/

/* change tier from dropdown menu*/
/*function changeTier2(id, tier){
	toggleSubmit();	
	sendAjax(id, diagnosisCallBack, "changeTier",  tier);
}*/

/* we activate the upload button to submit DDX*/ 
/*function toggleSubmit(){
	var hasFinalDiagnosis = hasAFinalDiagnosis();

	if(hasFinalDiagnosis && submitted!="true"){
		//if($("#uploadddx").attr("class")=="icon-upload-on") return;
		$("#uploadddx").removeClass("icon-upload-off");
		$("#uploadddx").addClass("icon-upload-on");
		$("#submitDDXHref").title="Submit your final diagnoses";
	}
	else{
		//if($("#uploadddx").attr("class")=="icon-upload-off") return;
		$("#uploadddx").removeClass("icon-upload-on");
		$("#uploadddx").addClass("icon-upload-off");
		$("#is_icon").removeClass("icon-list-on");
		$("#is_icon").addClass("icon-list-off");
		if(submitted =="true")
			$("#submitDDXHref").title="Final diagnoses already submitted.";
		else 
			$("#submitDDXHref").title="Select a final diagnosis in order to submit.";

	}
}*/

/*
 * user has changed the confidence slider, so, we send the new value via ajax.
 */
function submitSliderChange(){
	var confVal = $( "#confidence_slider" ).slider( "value" );
	sendAjax(-1, doNothing, "changeConfidence",  confVal);
}

/******************** management *******************************/

/*
 * a management item is added to the list of the already added items:
 */
function addManagement(mngId, name){
	if(name!="") sendAjax(mngId, managementCallBack, "addMng", name);
	//if(mngId>-1) addManagementCallBack(mngId, selMng);
}

function delManagement(id){
	sendAjax(id, managementCallBack, "delMng", "");
}

function delManagementCM(id){
	sendAjax(id, managementCallBackCM, "delMng", "");
}

function managementCallBack(mngId, selMng){
	$("#mng").val("");	
	//we update the problems list and the json string
	$("[id='mngform:hiddenMngButton']").click();		
}

function managementCallBackCM(mngId, selMng){
	//we update the problems list and the json string
	$("[id='mngform:hiddenMngButton']").click();	
	$("[id='graphform:hiddenGraphButton']").click();
}

function reOrderMngs(newOrder, id){
	sendAjax(id, managementCallBack, "reorderMngs", newOrder);
}


/******************** diagnostic steps *******************************/


/*
 * a test is added to the list of the already added tests:
 */
function addTest(testId, name){
	if(name!="") sendAjax(testId, testCallBack, "addTest", name);
}

function delTest(id){
	sendAjax(id, testCallBack, "delTest", "");
}

function delTestCM(id){
	sendAjax(id, testCallBackCM, "delTest", "");
}

function testCallBack(testId, selTest){
	$("#test").val("");	
	//we update the problems list and the json string
	$("[id='testform:hiddenTestButton']").click();		
}

function testCallBackCM(testId, selTest){
	//we update the problems list and the json string
	$("[id='testform:hiddenTestButton']").click();	
	$("[id='graphform:hiddenGraphButton']").click();
}

function reOrderTests(newOrder, id){
	sendAjax(id, testCallBack, "reorderTests", newOrder);
}

/******************** summary statement *******************************/

function saveSummSt(id){
	var text =  $("#summStText").val();
	sendAjax(id, doNothing, "saveSummStatement",text);	
	//TODO let user know that was saved
}

function saveNote(id){
	var text =  $("#notes").val();
	sendAjax(id, doNothing, "saveNote",text);
	//TODO let user know that was saved
}

var map_autocomplete_instance = null;

/** init the lists for selecting problems, diagnoses etc.*/
var active = $( "#tabs" ).tabs( "option", "active" ); //we have to determine the active tab, to be able to adapt the nav icons!
  
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
	  	        //$("#dialogCMMng" ).hide();
	  	        //$("#cm_mng_sel").autocomplete( "close" );
	  	        //if we had opened a new one, we have to remove it from the canvas:
	  	       // delTempRect();
	  	      }
	          });
	        }
	      });
	    $.ajax({ //list for problems from concept map
	        url: listUrl,
	        dataType: "json",
	        success: function( data ) {
	        	map_autocomplete_instance = $( "#cm_prob_sel" ).autocomplete({
	            source: data,
	            minLength: minLengthTypeAhead,
	            select: function( event, ui ) {
	            	editOrAddProblemCM(ui.item.value, ui.item.label);
	            },
	  	      close: function(ui) {
		  	    	cancelCMList("cm_prob_sel", "cmprob");

	  	    	  /*$("#cm_prob_sel").val("");	  	      
	  	    	  $("#cm_prob" ).hide();
	  	    	  //  $("#dialogCMProb" ).hide();
	  	    	  $("#cm_prob_sel").autocomplete( "close" );
	  	    	  delTempRect("cmprob_-1");*/
	  	      }
	          });
	        }
	      });
	    $.ajax({ //list for epi from concept map
	        url: listUrl,
	        dataType: "json",
	        success: function( data ) {
	          $( "#cm_epi_sel" ).autocomplete({
	            source: data,
	            minLength: minLengthTypeAhead,
	            select: function( event, ui ) {
	            	editOrAddEpiCM(ui.item.value, ui.item.label);
	            },
	  	      close: function(ui) {
	  	    	cancelCMList("cm_epi_sel", "cmepi");
	  	      /*  $("#cm_epi_sel").val("");
	  	        $("#cm_epi_sel" ).hide();
	  	        $("#cm_epi_sel").autocomplete( "close" );
	  	        delTempRect("cmepi_-1");*/
	  	      }
	          });
	        }
	      });
	    $.ajax({ //list for diagnoses from concept map
	        url: listUrl,
	        dataType: "json",
	        success: function( data ) {
	          $( "#cm_ddx_sel" ).autocomplete({
	            source: data,
	            minLength: minLengthTypeAhead,
	            select: function( event, ui ) {
	            	editOrAddDDXCM(ui.item.value, ui.item.label);
	            },
	  	      close: function(ui) {
	  	    	cancelCMList("cm_ddx_sel", "cmddx");
	  	      /*  $("#cm_ddx_sel").val("");
	  	        $("#cm_ddx_sel" ).hide();
	  	        $("#cm_ddx_sel").autocomplete( "close" );
	  	        //if we had opened a new one, we have to remove it from the canvas:
	  	        delTempRect("cmddx_-1");*/
	  	      }
	          });
	        }
	      });
	    $.ajax({ //list for tests from concept map
	        url: listUrl,
	        dataType: "json",
	        success: function( data ) {
	          $( "#cm_ds_sel" ).autocomplete({
	            source: data,
	            minLength: minLengthTypeAhead,
	            select: function( event, ui ) {
	            	editOrAddTestCM(ui.item.value, ui.item.label);
	            },
	  	      close: function(ui) {
	  	    	cancelCMList("cm_ds_sel", "cmds");
	  	       /* $("#cm_ds_sel").val("");
	  	        $("#cm_ds_sel" ).hide();
	  	        $("#cm_ds_sel").autocomplete( "close" );
	  	        //if we had opened a new one, we have to remove it from the canvas:
	  	        delTempRect("cmds_-1");*/
	  	      }
	          });
	        }
	      });
	    $.ajax({ //list for management item from concept map
	        url: listUrl,
	        dataType: "json",
	        success: function( data ) {
	          $( "#cm_mng_sel" ).autocomplete({
	            source: data,
	            minLength: minLengthTypeAhead,
	            select: function( event, ui ) {
	            	editOrAddMngCM(ui.item.value, ui.item.label);
	            },
	  	      close: function(ui) {
	  	    	cancelCMList("cm_mng_sel", "cmmng");
	  	        /*$("#cm_mng_sel").val("");
	  	        $("#cm_mng_sel" ).hide();
	  	        $("#cm_mng_sel").autocomplete( "close" );
	  	        //if we had opened a new one, we have to remove it from the canvas:
	  	        delTempRect("cmmng_-1");*/
	  	      }
	          });
	        }
	      });
	  });

  /*
   * user does not enter anything into the textfield and clicks on cancel, so, we have to remove all the dialog stuff
   */
  function cancelCMList(id, rectPrefix){
	  $("#"+id).val("");
	  $(".listdialog" ).hide();
	  $("#"+id).autocomplete( "close" );
      //if we had opened a new one, we have to remove it from the canvas:
      delTempRect(rectPrefix+"_-1");
  	  initAddHyp();
  	  initAddFind();
  	  initAddMng();
  	  initAddTest();
  	  initAddEpi();  
  }
  
  
  function initLists(){
	    $( "#list_diagnoses" ).sortable({
	 		stop: function( event, ui ) {
	 			var sorted = $( "#list_diagnoses" ).sortable( "serialize", { key: "selddx" } );
	 			reOrderDiagnoses(sorted, ui.item.attr("id"));
	 		}
	    });
	    $( "#list_diagnoses" ).disableSelection();
	    $( "#list_tests" ).sortable({
	 		stop: function( event, ui ) {
	 			var sorted = $( "#list_tests" ).sortable( "serialize", { key: "selds" } );
	 			reOrderTests(sorted, ui.item.attr("id"));
	 		}
	    });
	    $( "#list_tests" ).disableSelection();
	    $( "#list_mngs" ).sortable({
	 		stop: function( event, ui ) {
	 			var sorted = $( "#list_mngs" ).sortable( "serialize", { key: "selmng" } );
	 			reOrderMngs(sorted, ui.item.attr("id"));
	 		}
	    });
	    $( "#list_mngs" ).disableSelection();
	    $( "#list_problems" ).sortable({
	 		stop: function( event, ui ) {
	 			var sorted = $( "#list_problems" ).sortable( "serialize", { key: "selProb" } );
	 			reOrderProblems(sorted, ui.item.attr("id"));
	 		}
	    });
	    $( "#list_problems" ).disableSelection();
	    $( "#list_epi" ).sortable({
	 		stop: function( event, ui ) {
	 			var sorted = $( "#list_epi" ).sortable( "serialize", { key: "selEpi" } );
	 			reOrderEpi(sorted, ui.item.attr("id"));
	 		}
	    });
	    $( "#list_epi" ).disableSelection();
  }
  
function doDisplayIS(){
	alert("Not yet implemented - related illness scripts would be displayed here...");
	/*if($("#is_icon").attr("class")=="icon-list-on")
		alert("Display of the Illness Script(s) for the (correct) final diagnoses");
	else alert("You can access the Illness Script only after submitting your final diagnoses.");*/
}


/** when coming back from adding items, we empty the map and init it again from the updated json string*/
function updateCallback(data){
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
    	initLists();
        break;
	}
}

/******************** Feedback *******************************/

/*
 * we change the display of the concept map 
 * TODO: change display of lists and include missed items!
 */
function toggleExpertFedback(){
	if(expFeedback == false){
		expFeedback = true;
		$("#expFeedbackButton").attr("class","icon-user-md_on");
		$("#expFeedbackButton").attr("title", "click to hide expert feedback");
		$(".list_score").show();
		//$(".peer_score").show();
		sendAjaxContext(1, doNothing, "toogleExpFeedback",  getCurrentTab());
		
	}
	else{
		expFeedback = false;
		$("#expFeedbackButton").attr("class", "icon-user-md");
		$("#expFeedbackButton").attr("title", "click to show expert feedback");
		$(".list_score").hide();
		//$(".peer_score").hide();
		sendAjaxContext(0, doNothing, "toogleExpFeedback", getCurrentTab());
	}
	my_canvas.clear();
	initConceptMap();
}

/*
 * we change the display of the concept map 
 * TODO: change display of lists and include missed items!
 */
function togglePeerFeedback(){
	if(peerFeedback == false){
		peerFeedback = true;
		$("#peerFeedbackButton").attr("class","icon-users_on");
		$("#peerFeedbackButton").attr("title", "click to hide expert feedback");
		$(".peer_score").show();
		sendAjaxContext(1, doNothing, "tooglePeerFeedback",  getCurrentTab());
		
	}
	else{
		peerFeedback = false;
		$("#peerFeedbackButton").attr("class", "icon-users");
		$("#peerFeedbackButton").attr("title", "click to show expert feedback");
		$(".list_score").hide();
		$(".peer_score").hide();
		sendAjaxContext(0, doNothing, "tooglePeerFeedback", getCurrentTab());
	}
	//my_canvas.clear();
	//initConceptMap();
}

function openErrorDialog(){
	$("#jdialogError").dialog( "option", "width", ['200'] );
	$("#jdialogError").dialog( "option", "height", ['200'] );
	$("#jdialogError").dialog( "option", "title", "Potential errors" );
	$("#jdialogError").dialog( "option", "buttons", [ ] );
	$("#jdialogError" ).dialog( "open" );
	$("#jdialogError").show();
}

/* this is no longer needed with ajax */
/*function tmpHelperGetCorrect(problemId){
	//alert(problemId);
	var exp_problems_ids = [2,5]; //this comes then from ajax, rating can be 0 (wrong), 1(partly correct), 2 (correct)
	for(i=0; i<exp_problems_ids.length; i++){
		if(problemId==exp_problems_ids[i]) return 2;
	}
	return 0;
}*/
