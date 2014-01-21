wdefine(function(){
	$app.component('operationmenu').on('click', function(options){
		if(options.trigger.id == 'back'){
			AppUtil.popStack();
		}
		else if(options.trigger.id == 'stages'){
			var row = this.ctx.model('stagesmodel').selections().rows[0];
			AppUtil.navigateToStack("monitor/stageinfo", {navId: row.id, urlBase: $app.reqData("urlBase")}, {title: "Stage Information"});
		}
		else if(options.trigger.id == 'export'){
			alert("TODO");
		}
	});
	
	$app.component('stagegrid').on('dblclick', function(options){
		AppUtil.navigateToDialog("monitor/stagesummary", {navId: $app.reqData('navId'), itemId: options.rowId, urlBase: $app.reqData("urlBase")}, {title: "Stage Summary", width: "800", height: "500"});
	});
});