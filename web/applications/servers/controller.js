wdefine(function(){
	$app.stateManager().connectState([$app.model('center_model')], [$app.component('top_menu')]);
	$app.component('top_menu').on('click', function(options){
//		if(options.trigger.id == "add"){
//			options.eventCtx.width = "400";
//			options.eventCtx.height = "250";
//			options.eventCtx.title = "Add Server";
//		}
//		else if(options.trigger.id == "edit"){
//			var model = this.ctx.model("center_model");
//			var id = model.selections().ids[0];
//			AppUtil.navigateToDialog("monitor/servers/editform", {itemId: id}, {width: 400, height: 250, title: "Add Server"});
//		}
		if(options.trigger.id == "occupy"){
			var model = this.ctx.model('center_model');
			var sels = model.selections();
			var addrs = [];
			for(var i = 0; i < sels.ids.length; i ++){
				addrs.push(sels.rows[i].get('address'));
			}
			RestApi.action({
				restUrl: 'serverinfos',
				actionName: 'occupy',
				params: {serveriplist: addrs.join(",")},
				callback: function(data, status) {
					model.reload();
				}
			});
		}
		else if(options.trigger.id == "release"){
			var model = this.ctx.model('center_model');
			var sels = model.selections();
			var addrs = [];
			for(var i = 0; i < sels.ids.length; i ++){
				addrs.push(sels.rows[i].get('address'));
			}
			RestApi.action({
				restUrl: 'serverinfos',
				actionName: 'release',
				params: {serveriplist: addrs.join(",")},
				callback: function(data, status) {
					model.reload();
				}
			});
		}
	});
});