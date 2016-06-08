function chgStage(chg){
	if(chg==-1 && currentStage==1) return;
	currentStage += chg;
	window.location.href = "exp_boxes.xhtml?stage="+currentStage;
	//$("#stageSpan").html(currStage);
}