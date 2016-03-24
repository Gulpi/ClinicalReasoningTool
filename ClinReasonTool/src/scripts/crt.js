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


/******************** problems tab*******************************/

/**
 * a problem is added to the list of the already added problems:
 **/
function addProblem(problemId, name){
	//here we have to trigger an ajax call... 
	//var problemId = $("#problems").val();
	if(problemId>-1) sendAjax(problemId, problemCallBack, "addProblem", name);
}


/* if the answer was correct, but there is a better choice we offer the learner to change it by clicking on the checkmark*/
function chgProblem(orgId, toChgId){
	var confirmMsg = confirm("The term you have chosen is correct, however, a better choice would have been Dyspnea. Do you want to change your choice?");
	if(confirmMsg){
		$("#selprob_"+orgId).html("Dyspnea<i class=\"icon-ok2\"></i>");
		$("#selprob_"+orgId).attr("id","selprob_"+toChgId);
		//also trigger an ajax call to store the change....
	}
}

function delProblem(id){
	sendAjax(id, problemCallBack, "delProblem", "");
}

function problemCallBack(problemId, selProblem){
	$("#problems").val("");	
	//we update the problems list and the json string
	$("[id='probform:hiddenProbButton']").click();		
}


function reOrderProblems(newOrder, id){
	sendAjax(id, problemCallBack, "reorderProblems", newOrder);
}

/******************** diagnoses *******************************/

/*
 * a diagnosis is added to the list of the already added diagnoses:
 */
function addDiagnosis(diagnId, name){
	if(diagnId>-1) sendAjax(diagnId, diagnosisCallBack, "addDiagnosis", name);
}

function delDiagnosis(id){
	sendAjax(id, diagnosisCallBack, "delDiagnosis", "");
}

function diagnosisCallBack(ddxId, selDDX){
	$("#ddx").val("");	
	//we update the problems list and the json string
	$("[id='ddxform:hiddenDDXButton']").click();		
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
	sendAjax(id, toggleMnMCallback, "changeMnM",  newClass);
}

function toggleMnMCallback(){
	//TODO: we would have to update all resources...
}

/* we submit the DDX (or only final diagnoses?) and ask for certainity*/
function doSubmitDDX(){
	//we check whether there is a final diagnosis, if so we submit, else we display a hint
	//if()
	//$("#jdialog").dialog( "option", "position", ['center',50] );
	$("#jdialog").dialog( "option", "width", ['200'] );
	$("#jdialog").dialog( "option", "height", ['200'] );
	$("#jdialog").dialog( "option", "title", "Submit final diagnoses" );
	$("#jdialog").dialog( "option", "buttons", [ ] );
	$("#jdialog" ).dialog( "open" );
}

/* user has confirmed that he wants to submit disgnoses/-is*/
function submitDDXConfirmed(){
	
	submitDDXConfirmedCallBack();
}

function submitDDXConfirmedCallBack(){
	//we retrieve scoring of diagnoses and display it here, also whether an error has occured. 
	$("div[id=jdialog]").html("Correct! Final diagnosis of expert is Bronchopneumonia. Differentials include acute bronchits.");
	$("#is_icon").removeClass("icon-list-off");
	$("#is_icon").addClass("icon-list-on");	
}

var tiermsg=["I do not now","Clinically high likelyhood","Clinically moderate likelyhood","Clinically low likelyhood","My final diagnosis"];

/* we change the tier-color of the corresponding diagnosis*/
function changeTier(id){
	var id2 = "tiericon_"+id;
	var actClass = $("#"+id2).attr("class");
	var num = Number(actClass.charAt( actClass.length-1 )) +1;
	if(num>=5) num=0;
	$("#"+id2).removeClass(actClass);
	$("#"+id2).addClass("icon-circle-tier"+num);
	$("#tiera_"+id).prop("title",tiermsg[num]);
	toggleSubmit();	
}

/* we activate the upload button to submit DDX*/ 
function toggleSubmit(){
	var hasFinalDiagnosis = hasAFinalDiagnosis();
	if(hasFinalDiagnosis){
		//if($("#uploadddx").attr("class")=="icon-upload-on") return;
		$("#uploadddx").removeClass("icon-upload-off");
		$("#uploadddx").addClass("icon-upload-on");
	}
	else{
		//if($("#uploadddx").attr("class")=="icon-upload-off") return;
		$("#uploadddx").removeClass("icon-upload-on");
		$("#uploadddx").addClass("icon-upload-off");
		$("#is_icon").removeClass("icon-list-on");
		$("#is_icon").addClass("icon-list-off");
	}
}

/******************** management *******************************/

/*
 * a management item is added to the list of the already added items:
 */
function addManagement(mngId, name){
	if(mngId>-1) sendAjax(mngId, managementCallBack, "addMng", name);
	//if(mngId>-1) addManagementCallBack(mngId, selMng);
}

function delManagement(id){
	sendAjax(id, managementCallBack, "delMng", "");
}

function managementCallBack(testId, selTest){
	$("#mng").val("");	
	//we update the problems list and the json string
	$("[id='mngform:hiddenMngButton']").click();		
}

function reOrderMngs(newOrder, id){
	sendAjax(id, managementCallBack, "reorderMngs", newOrder);
}


/******************** diagnostic steps *******************************/


/*
 * a test is added to the list of the already added tests:
 */
function addTest(testId, name){
	if(testId>-1) sendAjax(testId, testCallBack, "addTest", name);
}

/*function addTestCallBack(testId,selTest){
	//we get the problemId and name via ajax...
	//var selTest = $("#tests option:selected").text();
	$("#tests").val("");
	var y = (numDiagnSteps * 30) +10;
	$("#list_tests").append("<li id='selds_"+testId+"'>"+selTest+"</li>");
	//$("#tests").val("-1");
	createAndAddDiagnStep(selTest,190, y, "cmds_"+testId);
	numDiagnSteps++;
}*/

function delTest(id){
	sendAjax(id, testCallBack, "delTest", "");
}

function testCallBack(testId, selTest){
	$("#test").val("");	
	//we update the problems list and the json string
	$("[id='ddxform:hiddenTestButton']").click();		
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

/** init the lists for selecting problems, diagnoses etc.*/
var active = $( "#tabs" ).tabs( "option", "active" ); //we have to determine the active tab, to be able to adapt the nav icons!
  
  $(function() {
	    function log( message ) {
	      $( "<div>" ).text( message ).prependTo( "#log" );
	      $( "#log" ).scrollTop( 0 );
	    }
	 
	    $.ajax({ //list for problems (list view)
	        url: "jsonp.json",
	        dataType: "json",
	        success: function( data ) {
	          $( "#problems" ).autocomplete({
	            source: data,
	            minLength: 3,
	            select: function( event, ui ) {
	            	addProblem(ui.item.value, ui.item.label);
	            },
	  	      close: function(ui) {
	  	        $("#problems").val("");
	  	      }
	          });
	        }
	      });
	    
	    $.ajax({ //list for diagnoses (list view)
	        url: "jsonp.json",
	        dataType: "json",
	        success: function( data ) {
	          $( "#ddx" ).autocomplete({
	            source: data,
	            minLength: 3,
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
	        url: "jsonp.json",
	        dataType: "json",
	        success: function( data ) {
	          $( "#tests" ).autocomplete({
	            source: data,
	            minLength: 3,
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
	        url: "jsonp.json",
	        dataType: "json",
	        success: function( data ) {
	          $( "#mng" ).autocomplete({
	            source: data,
	            minLength: 3,
	            select: function( event, ui ) {
	            	addManagement(ui.item.value, ui.item.label);
	            },
	  	      close: function(ui) {
	  	        $("#cm_mng_sel").val("");
	  	        //$("#dialogCMMng" ).hide();
	  	        //$("#cm_mng_sel").autocomplete( "close" );
	  	        //if we had opened a new one, we have to remove it from the canvas:
	  	       // delTempRect();
	  	      }
	          });
	        }
	      });
	    $.ajax({ //list for problems from concept map
	        url: "jsonp.json",
	        dataType: "json",
	        success: function( data ) {
	          $( "#cm_prob_sel" ).autocomplete({
	            source: data,
	            minLength: 3,
	            select: function( event, ui ) {
	            	editOrAddProblemCM(ui.item.value, ui.item.label);
	            },
	  	      close: function(ui) {
	  	        $("#cm_prob_sel").val("");
	  	        $("#dialogCMProb" ).hide();
	  	        $("#cm_prob_sel").autocomplete( "close" );
	  	        delTempRect();
	  	      }
	          });
	        }
	      });
	    $.ajax({ //list for diagnoses from concept map
	        url: "jsonp.json",
	        dataType: "json",
	        success: function( data ) {
	          $( "#cm_ddx_sel" ).autocomplete({
	            source: data,
	            minLength: 3,
	            select: function( event, ui ) {
	            	editOrAddDDXCM(ui.item.value, ui.item.label);
	            },
	  	      close: function(ui) {
	  	        $("#cm_ddx_sel").val("");
	  	        $("#dialogCMDDX" ).hide();
	  	        $("#cm_ddx_sel").autocomplete( "close" );
	  	        //if we had opened a new one, we have to remove it from the canvas:
	  	        delTempRect();
	  	      }
	          });
	        }
	      });
	    $.ajax({ //list for tests from concept map
	        url: "jsonp.json",
	        dataType: "json",
	        success: function( data ) {
	          $( "#cm_ds_sel" ).autocomplete({
	            source: data,
	            minLength: 3,
	            select: function( event, ui ) {
	            	editOrAddTestCM(ui.item.value, ui.item.label);
	            },
	  	      close: function(ui) {
	  	        $("#cm_ds_sel").val("");
	  	        $("#dialogCMTest" ).hide();
	  	        $("#cm_ds_sel").autocomplete( "close" );
	  	        //if we had opened a new one, we have to remove it from the canvas:
	  	        delTempRect();
	  	      }
	          });
	        }
	      });
	    $.ajax({ //list for management item from concept map
	        url: "jsonp.json",
	        dataType: "json",
	        success: function( data ) {
	          $( "#mng" ).autocomplete({
	            source: data,
	            minLength: 3,
	            select: function( event, ui ) {
	            	editOrAddMng(ui.item.value, ui.item.label);
	            },
	  	      close: function(ui) {
	  	        $("#cm_mng_sel").val("");
	  	        $("#dialogCMMng" ).hide();
	  	        $("#cm_mng_sel").autocomplete( "close" );
	  	        //if we had opened a new one, we have to remove it from the canvas:
	  	        delTempRect();
	  	      }
	          });
	        }
	      });
	  });

function doDisplayIS(){
	if($("#is_icon").attr("class")=="icon-list-on")
		alert("Display of the Illness Script(s) for the (correct) final diagnoses");
	else alert("You can access the Illness Script only after submitting your final diagnoses.");
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
