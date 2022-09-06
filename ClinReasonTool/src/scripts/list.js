
/******************** list init *******************************/
/*
	new init method
	
	collect all possible urls -> either default listUrl or attr "listUrl" in search inpout text field
	 to make a new list possible -> add listUrl attribute to search input text field in boxes page!
	 default listUrl in template can also be "", then we scan for search input text filed with set url, otherwise we initialize
	 the text fields with genericCreateAutocompleteWithoutList!
 */

var map_autocomplete_instance = null;
var minLengthTypeAhead = 3;

var isSuccess = false;
var list_js_cosole_log_master = true;

function list_js_console_log(msg) {
	if (list_js_cosole_log_master) {
		try { console.log("list.js: " + msg); } catch(x) {};
	}
	
}

/** init the lists for selecting problems, diagnoses etc.*/
$(function() {
	function log( message ) {
		$( "<div>" ).text( message ).prependTo( "#log" );
		$( "#log" ).scrollTop( 0 );
	}
	
	/* collect all possible urls */
	var my_listUrl = listUrl; // should be listUrl
	var inputUrls = new Array();
	inputUrls[my_listUrl] =  new Array();
	$(".search input.f_text").each(function(index, value) {
  		var my_id = $(this).attr("id");
    	var my_url = $(this).attr("listUrl");
    	list_js_console_log("my_id:<" + my_id + ">; my_url:<" + my_url + ">");
    	if (my_url || my_url=="") {
			if (!inputUrls[my_url]) {
				inputUrls[my_url] = new Array();
			}
			inputUrls[my_url].push(my_id);
		}
		else {
			inputUrls[my_listUrl].push(my_id);
		}
	});
	
	/* no loop over all urls and the assigned list of ids (search input text fields for autocomplete */
	for (var key in inputUrls) {
		loadListAndAssign(key,inputUrls[key]);
	}
	 
	/*$.ajax({ //list for problems (list view)
		url: listUrl, // url: listNursingUrl,
		dataType: "json",
		success: function( data ) {
			genericCreateAutocomplete("problems", data);
			genericCreateAutocomplete("ddx", data);
			genericCreateAutocomplete("tests", data);
			genericCreateAutocomplete("patho", data, 6, false);
			genericCreateAutocomplete("mng", data);
			genericCreateAutocomplete("nddx", data);
			genericCreateAutocomplete("nmng", data);
			genericCreateAutocomplete("info", data);
			genericCreateAutocomplete("naim", data);
			genericCreateAutocomplete("act_search");
			genericCreateAutocomplete("ctxt_search");
		}
	});
	
	genericCreateAutocompleteWithoutList("problems", listUrl);
	genericCreateAutocompleteWithoutList("patho", listUrl);
	genericCreateAutocompleteWithoutList("ddx", listUrl);
	genericCreateAutocompleteWithoutList("tests", listUrl);
	genericCreateAutocompleteWithoutList("mng", listUrl);
	genericCreateAutocompleteWithoutList("act_search",listUrl);
	genericCreateAutocompleteWithoutList("ctxt_search", listUrl);
	*/
});

var exact_item_label = "";
var exact_item_value = "";

/**
	key = url or empty
	list of id of seach input text field
	go thru and init autocomplete
 */
function loadListAndAssign(key, list) {
	if (key && key != "" && key != "#") {
		$.ajax({ //list for problems (list view)
			url: key, // url: listNursingUrl,
			dataType: "json",
			success: function( data ) {
				for (let i = 0; i < list.length; i++) {
					loop = list[i];
					list_js_console_log(key + " -> " + loop);
					genericCreateAutocomplete(loop, data);
				}
			},
			error: function (jqXHR) {
    			for (let i = 0; i < list.length; i++) {
					loop = list[i];
					list_js_console_log(key + " -> " + loop);
					genericCreateAutocompleteWithoutList(loop, key);
				}
			}
		});
	}
	else {
		for (let i = 0; i < list.length; i++) {
			loop = list[i];
			list_js_console_log(key + " -> " + loop);
			genericCreateAutocompleteWithoutList(loop, key);
		}
	}
}

/**
	add item by id -> uses either specific add<> method or generic addItem
 */
function genericAddItem(ui, id) {
	// choose correct add??? function by id!
	genericAddItemByValueAndLabel(ui.item.value, ui.item.label, id);
}

/**
	add item by id -> uses either specific add<> method or generic addItem
 */
function genericAddItemByValueAndLabel(item_value,item_label, id) {
	// choose correct add??? function by id!
	if (id=="problems") { 	addProblem(item_value, item_label, $("#" + id).val());}
	else if (id=="ddx") { 	addDiagnosis(item_value,item_label, $("#" + id).val()); }
	else if (id=="tests") { addTest(item_value, item_label, $("#" + id).val()); }
	else if (id=="patho") { addPatho(item_value, item_label, $("#" + id).val()); }
	else if (id=="mng") { 	addManagement(item_value, item_label, $("#" + id).val()); }
	else if (id=="nddx") { 	addItem(item_value, item_label,  $("#" + id).val(), "Nddx"); }
	else if (id=="nmng") { 	addItem(item_value, item_label,  $("#" + id).val(), "Nmng"); }
	else if (id=="info") { 	addItem(item_value, item_label,  $("#" + id).val(), "Info"); }
	else if (id=="naim") { 	addItem(item_value, item_label,  $("#" + id).val(), "Naim"); }
	else if (id=="act_search") { addActor(item_value, item_label,  $("#" + id).val()); }
	else if (id=="ctxt_search") { addContext(item_value, item_label,  $("#" + id).val()); }
}

/**
	init search input text filed with empty list
 */
function genericCreateAutocompleteWithoutList(in_id, in_listUrl) {
	// lookup parameters by id!
	if (typeof in_id !== 'string') {
		in_id = "" + in_id;
	}
	var in_bind = "enter" + ((in_id && in_id.length>0) ? in_id.charAt(0).toUpperCase() + in_id.slice(1) : in_id);
	// define new event, that's why name (in_bind) is not as crocial'
	$("#" + in_id).bind(in_bind, function(e) {
		genericAddItemByValueAndLabel(-999, "-999", in_id);	 
    });
    
    // now trigge the event!
    $("#" + in_id).keyup(function(e){
		if(e.keyCode == 13 && in_listUrl=="") {
			$(this).trigger(in_bind);
		}
    });	
}
	
/**
	init autocomplete search input text field for given id and data (loaded in advance by url)
 */
function genericCreateAutocomplete(in_id, in_data) {
	// lookup parameters by id!
	var in_num = 0;
	var in_fdg_prefix_handling = false;
	if (in_id=="problems") {
		in_num = 1;
		in_fdg_prefix_handling = true;
	}
	else if (in_id=="ddx") 		{ in_num = 2; }
	else if (in_id=="tests") 	{ in_num = 3; }
	else if (in_id=="patho") 	{ in_num = 6; }
	else if (in_id=="mng") 		{ in_num = 4; }
	else if (in_id=="nddx") 	{ in_num = 7; }
	else if (in_id=="nmng") 	{ in_num = 9; }
	else if (in_id=="info") 	{ in_num = 10; }
	else if (in_id=="naim") 	{ in_num = 8; }
	else if (in_id=="act_search") { in_num = 4; }
	else if (in_id=="ctxt_search") { in_num = 4; }
	
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

/** 
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


