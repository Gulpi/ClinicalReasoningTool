/**
 * javascript for displaying the illness script tabs representation
 */

/**
 * we start an ajax call with changed params. We also include always the session id!
 */
function sendAjax(id, callback, type, name){
	$.ajax({
		  method: "POST",
		  url: "tabs_ajax.xhtml",
		  data: { type: type, id: id, session_id: sessId, name: name }
		})
	  .done(function( response ) {
		 var id2 =  $(response).find('id').text();
		// alert(id2);
		 callback(id2, name);
	  });
	
}

/******************** patient tab*******************************/
/**
 * user changes the course of time, we trigger an ajax call to save change.
 */
function changeCourseOfTime(){
	var courseTime = $("#courseTime").val(); 
	//alert(courseTime);
	sendAjax(courseTime, changeCourseOfTimeCallBack, "chgCourseOfTime", "");
}

function changeCourseOfTimeCallBack(id, name){
	//nothing to do here....
}

/******************** problems tab*******************************/

/**
 * a problem is added to the list of the already added problems:
 **/
function addProblem(problemId, name){
	//here we have to trigger an ajax call... 
	//var problemId = $("#problems").val();
	if(problemId>-1) sendAjax(problemId, addProblemCallBack, "addProblem", name);
}

function addProblemCallBack(problemId, selProblem){
	$("#problems").val("");
	//var selProblem = $("#problems option:selected").text();
	var isCorr = tmpHelperGetCorrect(problemId);
	var y = (numFinds * 30) +10;
	if(isCorr==2) $("#list_problems").append("<li id=\"selProb="+problemId+"\">"+selProblem+"<i class=\"icon-ok2\"></i></li>");
	else if(isCorr==1) $("#list_problems").append("<li id=\"selProb="+problemId+"\">"+selProblem+"<i class=\"icon-ok1\"></i></li>");
	else $("#list_problems").append("<li id=\"selprob_"+problemId+"\">"+selProblem+"</li>");
	
	//alert(problemId);
	createAndAddFind(selProblem,5, y, "cmprob_"+problemId);
	numFinds++;
		
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


/******************** concept map *******************************/
function initAddHyp(){
  $( "#addhyp" ).draggable({ //add a new hypothesis
	  start: function( event, ui ) {
		 $("#addhyp").clone().prependTo($("#hypcontainer")); 
	  } , 
	  stop: function( event, ui ) {	
		  this.remove();	  
		  var canvasX = ui.offset.left- my_canvas.html.offset().left;
 		  var canvasY = ui.offset.top - my_canvas.html.offset().top;
	  	createAndAddHyp("neue hyp", canvasX, canvasY,"cmddx_-1");	  	
	  	updatePreview();
	  }
  });	
}

function initAddFind(){
	$( "#addfind" ).draggable({ //add a new hypothesis
	  start: function( event, ui ) {
			 $("#addfind").clone().prependTo($("#findcontainer")); 
		  } , 
		  stop: function( event, ui ) {	
			  this.remove();	
			  var canvasX = ui.offset.left- my_canvas.html.offset().left;
	 		  var canvasY = ui.offset.top - my_canvas.html.offset().top;

		  	createAndAddFind("new find", canvasX, canvasY, "cmprob_-1");	  	
		  	updatePreview();
		  }
	  });	
}

function initAddMng(){
	 $( "#addmng" ).draggable({ //add a new hypothesis
		  start: function( event, ui ) {
			 $("#addmng").clone().prependTo($("#mngcontainer")); 
		  } , 
		  stop: function( event, ui ) {	
			  this.remove();	
			  var canvasX = ui.offset.left- my_canvas.html.offset().left;
	 		  var canvasY = ui.offset.top - my_canvas.html.offset().top;
		  	  createAndAddMng("new mng", canvasX, canvasY, "cmmng_-1");	  	
		      updatePreview();
		  }
	 });	
}

function initAddDiagnStep(){
	 $( "#adddiagnstep" ).draggable({ //add a new hypothesis
		  start: function( event, ui ) {
			 $("#adddiagnstep").clone().prependTo($("#dscontainer")); 
		  } , 
		  stop: function( event, ui ) {	
			  this.remove();	
			  var canvasX = ui.offset.left- my_canvas.html.offset().left;
	 		  var canvasY = ui.offset.top - my_canvas.html.offset().top;
			  createAndAddDiagnStep("new test", canvasX, canvasY,"cmds_-1");	  	
		  	updatePreview();
		  }
	}); 
}
/*an item has been dragged onto the trash bin, so, we remove it from the list*/
function removeItemFromList(draggable){
	var itemIdToDel = draggable.attr("id"); //e.g. problems_2, ddx_67
	//trigger ajax call here....
	removeItemFromListCallback(draggable);
}
/* back from ajax call, remoce item from list and the concept map*/
function removeItemFromListCallback(draggable){
	
	var id_prefix = draggable.attr("id");
	alert(id_prefix);
	draggable.remove();
	//we also remove the item from the canvas:
	var idInCM = "cm"+id_prefix.substring(3);	
	var rect  = my_canvas.getFigure(idInCM);
	deleteItemFromCM(rect); //removes the ite from the cm		
}


/* this is no longer needed with ajax */
function tmpHelperGetCorrect(problemId){
	//alert(problemId);
	var exp_problems_ids = [2,5]; //this comes then from ajax, rating can be 0 (wrong), 1(partly correct), 2 (correct)
	for(i=0; i<exp_problems_ids.length; i++){
		if(problemId==exp_problems_ids[i]) return 2;
	}
	return 0;
}

/*
 * a diagnosis is added to the list of the already added diagnoses:
 */
function addDiagnosis(diagnId, selDiagnosis){
	//here we have to trigger an ajax call... 
	//var diagnId = $("#ddx").val();
	if(diagnId>-1){
		addDiagnosisCallBack(diagnId, selDiagnosis);	
	}
}

function addDiagnosisCallBack(diagnId, selDiagnosis){
	$("#ddx").val("");
	//var selDiagnosis = $("#ddx option:selected").text();
	var liItem = "<li id='selddx_"+diagnId+"'>";
	//$("#list_diagnoses").append("<li id='selddx_"+diagnId+"'>");
	liItem+="<a href=\"javascript:toggleMnM('"+diagnId+"');\" title=\"Is this a Must-Not-Miss diagnosis? E.g. because it is lethal?\"><i id=\"mnmicon_"+diagnId+"\" class=\"icon-attention0\"></i></a>";
	liItem+=" "+selDiagnosis+" ";
	liItem +="<a href=\"javascript:changeTier('"+diagnId+"');\" id=\"tiera_"+diagnId+"\" title=\"How likely is this diagnosis?\"><i id=\"tiericon_"+diagnId+"\" class=\"icon-circle-tier0\"></i></a>";
	liItem+="</li>";
	$("#list_diagnoses").append(liItem);
	//$("#ddx").val("-1");
	//var x = numHyps*50;
	var y = (numHyps * 30) +5;
	createAndAddHyp(selDiagnosis,90, y, "cmddx_"+diagnId);
	numHyps++;
}

/*
 * a test is added to the list of the already added tests:
 */
function addTest(testId, testname){
	//here we have to trigger an ajax call... 
	//var testId = $("#tests").val();
	if(testId>-1) addTestCallBack(testId, testname);
}

function addTestCallBack(testId,selTest){
	//we get the problemId and name via ajax...
	//var selTest = $("#tests option:selected").text();
	$("#tests").val("");
	var y = (numDiagnSteps * 30) +10;
	$("#list_tests").append("<li id='selds_"+testId+"'>"+selTest+"</li>");
	//$("#tests").val("-1");
	createAndAddDiagnStep(selTest,190, y, "cmds_"+testId);
	numDiagnSteps++;
}

/*
 * a management item is added to the list of the already added items:
 */
function addManagement(mngId, selMng){
	//here we have to trigger an ajax call... 
	//var mngId = $("#mng").val();
	if(mngId>-1) addManagementCallBack(mngId, selMng);
}

function addManagementCallBack(mngId, selMng){
	//we get the problemId and name via ajax...
	//var selMng = $("#mng option:selected").text();
	$("#mng").val("");
	$("#list_mngs").append("<li id='selmng_"+mngId+"'>"+selMng+"</li>");	
	var y = (numMng * 30) +10;
	createAndAddMng(selMng,250, y, "cmmng_"+mngId);
	numMng++;
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

/* We open a jdialog for the concept map*/
function openCanvas(){
	$("#canvascontainer").show();
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


function hasAFinalDiagnosis(){
	//tiericon_1
	var numFinDiagn = $(".icon-circle-tier4").length;
	if(numFinDiagn>0) return true;
	return false;
}

/* a diagnosis is changed to must not missed -red icon or toggled back*/
function toggleMnM(id){
	var id = "mnmicon_"+id;
	var actClass = $("#"+id).attr("class");
	$("#"+id).removeClass(actClass);
	if(actClass=="icon-attention0") $("#"+id).addClass("icon-attention1");
	else  $("#"+id).addClass("icon-attention0");
}
var active = $( "#tabs" ).tabs( "option", "active" ); //we have to determine the active tab, to be able to adapt the nav icons!
  
  $(function() {
	    function log( message ) {
	      $( "<div>" ).text( message ).prependTo( "#log" );
	      $( "#log" ).scrollTop( 0 );
	    }
	 
	    $.ajax({
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
	    
	    $.ajax({
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
	    $.ajax({
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
	  	      }
	          });
	        }
	      });
	    $.ajax({
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
	  	        $("#mng").val("");
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