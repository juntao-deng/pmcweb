wdefine(function(){
	$app.component('operationmenu').on('click', function(options){
		if(options.trigger.id == 'back'){
			AppUtil.popStack();
		}
		else if(options.trigger.id == 'stages'){
			var row = this.ctx.model('stagesmodel').select().rows[0];
			AppUtil.navigateToStack("monitor/stageinfo", {navId: row.id}, {title: "Stage Information"});
		}
		else if(options.trigger.id == 'export'){
			alert("TODO");
		}
	});
	
	$app.component('stagegrid').on('dblclick', function(options){
		AppUtil.navigateToDialog("monitor/stagesummary", {navId: $app.reqData('navId'), itemId: options.rowId}, {title: "Stage Summary", width: "800", height: "500"});
	});
});