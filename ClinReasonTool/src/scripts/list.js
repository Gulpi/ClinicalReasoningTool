
/******************** list init *******************************/

var map_autocomplete_instance = null;
var minLengthTypeAhead = 3;

var item_data = null;
var isSuccess = false;
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
	        	item_data = data;
	          $( "#problems" ).autocomplete({
	           /* source: data,*/
	        	source: doMatch,
	            minLength: minLengthTypeAhead,
	            select: function( event, ui ) {	        		
	            	isSuccess = true;
	            	addProblem(ui.item.value, ui.item.label, $("#problems").val());	 
	            	ui.item.value = ""; //necessary if action is cancelled
	            },
	  	      	close: function(ui) {
	  	      		$("#problems").val("");
	  	      		$("#fdg_prefix").val("");
	  	      		handleClose('1');
	  	      	}
	          });
	          $( "#ddx" ).autocomplete({
		            source: doMatch,
		            minLength: minLengthTypeAhead,
		            select: function( event, ui ) {
		            	isSuccess = true;
		            	addDiagnosis(ui.item.value, ui.item.label, $("#ddx").val());
		            	ui.item.value = ""; //necessary if action is cancelled
		            },
		  	      	close: function(ui) {
		  	      		$("#ddx").val("");
		  	      		handleClose(2);
		  	      	}
		      });
	          $( "#tests" ).autocomplete({
		            source: doMatch,
		            minLength: minLengthTypeAhead,
		            select: function( event, ui ) {
		            	isSuccess = true;
		            	addTest(ui.item.value, ui.item.label, $("#tests").val());
		            	ui.item.value = ""; //necessary if action is cancelled
		            },
		  	      	close: function(ui) {
		  	      		$("#tests").val("");
		  	      		handleClose(3);
		  	      	}
		      });
	          $( "#mng" ).autocomplete({
		            source: doMatch,
		            minLength: minLengthTypeAhead,
		            select: function( event, ui ) {
		            	isSuccess = true;
		            	addManagement(ui.item.value, ui.item.label, $("#mng").val());
		            	ui.item.value = ""; //necessary if action is cancelled
		            },
		  	      	close: function(ui) {
		  	      		$("#mng").val("");
		  	      		handleClose(4);
		  	      	}
		        });
	        }
	      });
	    /* alternative handling if we do not have a list: */
	    $("#problems").bind("enterProb",function(e){
	    	addProblem(-999, "-999", $("#problems").val());	 
	    	});
	    	$("#problems").keyup(function(e){
	    	    if(e.keyCode == 13 && listUrl=="")
	    	    {
	    	        $(this).trigger("enterProb");
	    	    }
	    	});
	    	
		 $("#ddx").bind("enterDDX",function(e){
		    	addDiagnosis(-999, "-999", $("#ddx").val());	 
		    	});
		    	$("#ddx").keyup(function(e){
		    	    if(e.keyCode == 13 && listUrl=="")
		    	    {
		    	        $(this).trigger("enterDDX");
		    	    }
		    });
			$("#tests").bind("enterTest",function(e){
			    	addTest(-999, "-999", $("#tests").val());	 
			    	});
			    	$("#tests").keyup(function(e){
			    	    if(e.keyCode == 13 && listUrl=="")
			    	    {
			    	        $(this).trigger("enterTest");
			    	    }
			    });
			$("#mng").bind("enterMng",function(e){
				addManagement(-999, "-999", $("#mng").val());	 
		    	});
		    	$("#mng").keyup(function(e){
		    	    if(e.keyCode == 13 && listUrl=="")
		    	    {
		    	        $(this).trigger("enterMng");
		    	    }
		    });
			
	  });
  
  /*
   * matches the user input with the list labels. Also considers multiple terms and negations
   */
function doMatch(request,response){
	$('.ui-tooltip').remove();
	var exact_item_label = "";
	var exact_item_value = "";
	
	if (request.term == "") { //user has entered nothing, so we return here....
		response( $.map( item_data, function( item ) {
			return {
				value: item.value,
				/*text: item.label*/
				label: item.label
			}
		}));
		return;
	}
	else {
		
		var my_map = $.map( item_data, function( item ) {
			 
			if(item.label=="") return;
			if(item.value=="-99"){
				var myid = item.id;
			}
			var matcher;
			//We replace blanks and commas to improve the comparison mechanisms
			var user_input = request.term.replace(",","");
			user_input = user_input.replace("-","");
			user_input = user_input.trim();
			user_input = user_input.replace("ß","ss");
			
			var listEntry = item.label.replace(",","");
			listEntry = listEntry.replace("-","");
			listEntry = listEntry.trim();
			listEntry = listEntry.replace("ß","ss");
			
			if(user_input.toLowerCase()==listEntry.toLowerCase()){
				exact_item_label = item.label;
				exact_item_value = item.value;
			}
			var isNegStart = checkStartUserInput(user_input); //Then user started with something like "No ..."
			var user_input_arr = user_input.split(" ");
			var listentry_arr = listEntry.split(" ");

			var arr_match = false;
			if(isNegStart && (user_input_arr.length==1 || user_input_arr.length>1 && user_input_arr[1].length<minLengthTypeAhead)){
				//then we should not display the list, we have to wait until user starts the second search term
			}
			else{
				if(user_input_arr.length>1){ //then we have more than one search term.
					arr_match = true;
					for(var i=0; i<user_input_arr.length; i++){
						if(isNegStart && i==0){
							$("#fdg_prefix").val(user_input_arr[0]);
							//no matching
						}
						else{
							matcher = new RegExp( $.ui.autocomplete.escapeRegex(user_input_arr[i]), "i" );	
							if(!matcher.test(listEntry)){
								arr_match = false;
								break;
							}
						}
					}
				}
				else{ //only one search term:
					matcher = new RegExp( $.ui.autocomplete.escapeRegex(user_input), "i" );
					arr_match = matcher.test(listEntry);
					if(!arr_match && listentry_arr.length>1){
						//we check combinations e.g. if user enters "PneumonieAspiration" to match "Pneumonie, -Aspirations"
						var listEntryOne = listEntry.replace(" ","");
						arr_match = matcher.test(listEntryOne);
						if(!arr_match && listentry_arr.length==2){ //now check Aspirationspneumonie vs "Pneumonie, -Aspirations"
							var listEntryReverse = listentry_arr[1].trim() + listentry_arr[0].trim();
							listEntryReverse = listEntryReverse.trim();
							arr_match = matcher.test(listEntryReverse);
						}
					}
				}
							
				if (item.value=="-99" || !user_input || arr_match ||  listEntry == user_input) {
					var tmpLabel = item.label;
					if(isNegStart) tmpLabel = user_input_arr[0]+ " " +item.label;
					return {
						value: item.value,
						label: tmpLabel
					};
				}
			}
		});
		//if we have an exact match, we display it at the top with a delimiter:
		if(exact_item_label!="" && exact_item_value!=""){
			var del = "-----------";
			my_map.unshift(del);
			var obj = {label:exact_item_label, value:exact_item_value};
			my_map.unshift(obj);
		}
		response( my_map );
	}
}


var inputhistory = "";

function storeHistory(inputId){
	var txt = $("#"+inputId).val();
	inputhistory +=txt+"#";
}
/**
 * closing list - either after something has been selected or the action has been canceled (nothing found
 * or similar).
 */
function handleClose(type){
	//alert(isSuccess);
	if(!isSuccess){ //only send in case of nothing has been selected. If something has been selected 
					//the history is send together with saving the new item
		sendAjax(type, doNothing, "addTypeAheadBean", "");
	}
	isSuccess = false;
	inputhistory = "";
}

var start_de_arr=["kei", "kein", "keine"];
var start_en_arr=["no "];
function checkStartUserInput(user_input){
	var my_arr = start_en_arr;
	if(scriptlang=="de") my_arr = start_de_arr;
	
	for(var i=0; i<my_arr.length; i++){
	if(user_input.toLowerCase().startsWith(my_arr[i]))
		return true;
	}
	return false;
}


