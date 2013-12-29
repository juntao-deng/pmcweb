wdefine(function(){
	$app.stateManager().connectState([$app.model('center_model')], [$app.component('top_menu')]);
	$app.component('top_menu').on('click', function(options){
		if(options.trigger.id == "add"){
			options.eventCtx.width = "400";
			options.eventCtx.height = "250";
			options.eventCtx.title = "Add Server";
		}
		else if(options.trigger.id == "edit"){
			var model = this.ctx.model("center_model");
			var id = model.selections().ids[0];
			AppUtil.navigateToDialog("monitor/servers/editform", {itemId: id}, {width: 400, height: 250, title: "Add Server"});
		}
	});
});