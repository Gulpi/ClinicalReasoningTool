
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
	            	addProblem(ui.item.value, ui.item.label);	            	
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
		            	addDiagnosis(ui.item.value, ui.item.label);
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
		            	addTest(ui.item.value, ui.item.label);
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
		            	addManagement(ui.item.value, ui.item.label);
		            },
		  	      	close: function(ui) {
		  	      		$("#mng").val("");
		  	      		handleClose(4);
		  	      	}
		        });
	        }
	      });
	   /* $.ajax({ //list for epi (list view)
	        url: listUrl,
	        dataType: "json",
	        success: function( data ) {
	        	item_data = data;
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
	      });	  */  
	  });
  
  /*
   * matches the user input with the list labels. Also considers multiple terms and negations
   */
function doMatch(request,response){
	if (request.term == "") {
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
			var matcher;
			var user_input = request.term;
			var isNegStart = checkStartUserInput(user_input); //Then user started with something like "No ..."
			var user_input_arr = user_input.split(" ");
			

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
						
							if(!matcher.test(item.label)){
								arr_match = false;
								break;
							}
						}
					}
				}
				else{ //only one search term:
					matcher = new RegExp( $.ui.autocomplete.escapeRegex(user_input), "i" );
					arr_match = matcher.test(item.label)
				}
							
				if (!user_input || arr_match ||  item.label == user_input) {
					var tmpLabel = item.label;
					if(isNegStart) tmpLabel = user_input_arr[0]+ " " +item.label;
					return {
						value: item.value,
						label: tmpLabel
					};
				}
			}
		});
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
var start_en_arr=["no"];
function checkStartUserInput(user_input){
	var my_arr = start_en_arr;
	if(scriptlang=="de") my_arr = start_de_arr;
	
	for(var i=0; i<my_arr.length; i++){
	if(user_input.toLowerCase().startsWith(my_arr[i]))
		return true;
	}
	return false;
}