
/******************** list init *******************************/

var map_autocomplete_instance = null;
var minLengthTypeAhead = 3;

var isSuccess = false;



/** init the lists for selecting problems, diagnoses etc.*/
$(function() {
	function log( message ) {
		$( "<div>" ).text( message ).prependTo( "#log" );
		$( "#log" ).scrollTop( 0 );
	}
	 
	$.ajax({ //list for problems (list view)
		url: listUrl, // url: listNursingUrl,
		dataType: "json",
		success: function( data ) {
			genericCreateAutocomplete("problems", data, 1, true);
			genericCreateAutocomplete("ddx", data, 2, false);
			genericCreateAutocomplete("tests", data, 3, false);
			genericCreateAutocomplete("patho", data, 6, false);
			genericCreateAutocomplete("mng", data, 4, false);
			genericCreateAutocomplete("nddx", data, 7, false);
			genericCreateAutocomplete("nmng", data, 9, false);
			genericCreateAutocomplete("info", data, 10, false);
			genericCreateAutocomplete("naim", data, 8, false);
			genericCreateAutocomplete("act_search", data, 4, false);
			genericCreateAutocomplete("ctxt_search", data, 4, false);
		}
	});
	
	genericCreateAutocompleteWithoutList("problems", "enterProb", listUrl);
	genericCreateAutocompleteWithoutList("patho", "enterPatho", listUrl);
	genericCreateAutocompleteWithoutList("ddx", "enterDDX", listUrl);
	genericCreateAutocompleteWithoutList("tests", "enterTest", listUrl);
	genericCreateAutocompleteWithoutList("mng", "enterMng", listUrl);
	genericCreateAutocompleteWithoutList("act_search", "enterActor", listUrl);
	genericCreateAutocompleteWithoutList("ctxt_search", "enterContext", listUrl);
});

var exact_item_label = "";
var exact_item_value = "";

function genericAddItem(ui, id) {
	if (id=="problems") {
		addProblem(ui.item.value, ui.item.label, $("#" + id).val());	 
	}
	else if (id=="ddx") {
		addDiagnosis(ui.item.value, ui.item.label, $("#" + id).val());	 
	}
	else if (id=="tests") {
		addTest(ui.item.value, ui.item.label, $("#" + id).val());	 
	}
	else if (id=="patho") {
		addPatho(ui.item.value, ui.item.label, $("#" + id).val());	 
	}
	else if (id=="mng") {
		addManagement(ui.item.value, ui.item.label, $("#" + id).val());	 
	}
	else if (id=="nddx") {
		addItem(ui.item.value, ui.item.label,  $("#" + id).val(), "Nddx");
	}
	else if (id=="nmng") {
		addItem(ui.item.value, ui.item.label,  $("#" + id).val(), "Mmng");
	}
	else if (id=="info") {
		addItem(ui.item.value, ui.item.label,  $("#" + id).val(), "Info");
	}
	else if (id=="naim") {
		addItem(ui.item.value, ui.item.label,  $("#" + id).val(), "Naim");
	}
	else if (id=="act_search") {
		addActor(ui.item.value, ui.item.label,  $("#" + id).val());
	}
	else if (id=="ctxt_search") {
		addContext(ui.item.value, ui.item.label,  $("#" + id).val());
	}
}

function genericCreateAutocompleteWithoutList(in_id, in_bind, in_listUrl) {
	$("#" + in_id).bind(in_bind, function(e) {
		addContext(-999, "-999", $("#" + in_id).val());	 
    });
    
    $("#" + in_id).keyup(function(e){
		if(e.keyCode == 13 && in_listUrl=="") {
			$(this).trigger(in_bind);
		}
    });	
}
	

function genericCreateAutocomplete(in_id, in_data, in_num, in_fdg_prefix_handling) {
	$( "#" + in_id).autocomplete({
       	/* source: data,*/
    	source: function (request, response) {
			doMatch(request, response, in_data);
		},
        minLength: minLengthTypeAhead,
        select: function( event, ui ) {	        		
        	isSuccess = true;
        	genericAddItem(ui, in_id)
        	ui.item.value = ""; //necessary if action is cancelled
        },
      	close: function(ui) {
      		$("#" + in_id).val("");
      		if (in_fdg_prefix_handling) {
				$("#fdg_prefix").val("");
			}
      		
      		handleClose(in_num);
      	}
	});
}


  /*
   * matches the user input with the list labels. Also considers multiple terms, negations, and
   * a typo tolerance
   */
function doMatch(request,response, in_data){
	exact_item_label = "";
	exact_item_value = "";

	$('.ui-tooltip').remove();
	
	if (request.term == "") { //user has entered nothing, so we return here....
		response( $.map( in_data, function( item ) {
			return {
				value: item.value,
				/*text: item.label*/
				label: item.label
			}
		}));
		return;
	}
	else {
		
		var my_map = createMatchedMap(request, response, in_data);
		//if we have an exact match, we display it at the top with a delimiter:
		if(exact_item_label!="" && exact_item_value!=""){
			var del = "-----------";
			my_map.unshift(del);
			var obj = {label:exact_item_label, value:exact_item_value};
			my_map.unshift(obj);
		}
		//if the only entry is the "add own entry" we change it in expert mode to "no entries found"
		//(-> we could also hide it in player mode if there is an exact match -> just set label to "") 
		if(my_map.length==1 && isExp){ 
			my_map[0].label = noEntryFound;
		}
		//we have found no match, so we are checking for typos (currently not for expert edit, but could be done, too?)
		if(my_map.length==1 && !isExp){
			my_map = createMatchedMapWithTypos(request, response, 2, in_data);
			//if we have a match here, we add something saying "Did you mean..."
			if(my_map.length>1){
				var obj = {label: didYouMean, value:"IGNORE", category:"hallo"};
				my_map.unshift(obj);
			}
		}
		response( my_map );
	}
}


function createMatchedMap(request, response, in_data){
	return $.map( in_data, function( item ) {
		 
		if(item.label=="") return;
		if(item.value=="-99"){
			var myid = item.id;
		}
		var matcher;
		//We replace blanks and commas to improve the comparison mechanisms
		var user_input = replaceChars(request.term);
		var listEntry = replaceChars(item.label); 
		
		if(user_input==listEntry){ //then we have an exact match
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
				//display it empty if in edit exp mode:      
				if(item.value=="-99" && isExp){
					tmpLabel = "";
				}
				return {
					
					value: item.value,
					label: tmpLabel
				};
			}
		}
	});
}

/**
 * return matching entries that have a Levenshtein distance <= maxDist
 * (currently only calculated if user enters ONE term)
 * @param request
 * @param response
 * @param maxDist
 * @returns
 */
function createMatchedMapWithTypos(request, response, maxDist, in_data){
	return $.map( in_data, function( item ) {
		 
		/*if(item.label=="") return;*/
		if(item.value=="-99"){
			var myid = item.id;
		}
		//var matcher;
		//We replace blanks and commas to improve the comparison mechanisms
		var user_input = replaceChars(request.term);
		var listEntry = replaceChars(item.label); 
		
		var isNegStart = checkStartUserInput(user_input); //Then user started with something like "No ..."
		var user_input_arr = user_input.split(" ");
		var listentry_arr = listEntry.split(" ");
		
		var arr_match = false;
		/*if(isNegStart && (user_input_arr.length==1 || user_input_arr.length>1 && user_input_arr[1].length<minLengthTypeAhead)){
			//then we should not display the list, we have to wait until user starts the second search term
		}
		else{*/
			if(user_input_arr.length>1){ //then we have more than one search term.
				
				for(var i=0; i<user_input_arr.length; i++){
					if(isNegStart && i==0){
						$("#fdg_prefix").val(user_input_arr[0]);
						//no matching
					}
					else{
						if(getEditDistance(user_input_arr[i], listEntry)>maxDist) break;
						
					}
				}
			}
			else{ //only one search term:

				//now look whether there is a typo					
				var leven = getEditDistance(user_input, listEntry);
				if(leven<=maxDist){
					return {						
						value: item.value,
						label: item.label
					};
				}
			}
						
			if (item.value=="-99") {
				var tmpLabel = item.label;
				//if(isNegStart) tmpLabel = user_input_arr[0]+ " " +item.label;
				//display it empty if in edit exp mode:      
				if(item.value=="-99" && isExp){
					tmpLabel = "";
				}
				return {
					
					value: item.value,
					label: tmpLabel
				};
			//}
		}
	});
}

var inputhistory = "";
/* we store what the user has typed in */ 
function storeHistory(inputId){
	var txt = $("#"+inputId).val();
	inputhistory +=txt+"#";
}

/*we remove some ugly stuff from the string before comparing it */
function replaceChars(str){
	str = str.replace(",","");
	str = str.replace("-","");
	str = str.trim();
	str = str.replace("ß","ss");
	str = str.toLowerCase();
	return str;
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

var start_de_arr=["kein ", "keine "];
var start_en_arr=["no "];
var start_es_arr=["ningun"];
var start_pl_arr=["brak ", "bez ", "nie "];

function checkStartUserInput(user_input){
	var my_arr = start_en_arr;
	if(scriptlang=="de") my_arr = start_de_arr;
	else if(scriptlang=="es") my_arr = start_es_arr;
	else if(scriptlang=="pl") my_arr = start_pl_arr;
	//else if(scriptlang=="fr") my_arr = start_fr_arr;
	//else if(scriptlang=="pt") my_arr = start_pt_arr;
	
	for(var i=0; i<my_arr.length; i++){
	if(user_input.toLowerCase().startsWith(my_arr[i]))
		return true;
	}
	return false;
}


