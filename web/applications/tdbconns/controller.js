wdefine(function(){
	$app.stateManager().connectState([$app.model('threadmodel')], [$app.component('operationmenu')]);
	var ctx = $app;
	$app.component('operationmenu').on('click', function(options){
		if(options.trigger.id == 'fetch'){
			var servers = this.ctx.component('serverinput').value();
			if(servers == null || servers.length == 0){
				alert("Please select active node(s) first.");
				return;
			}
			doFetch();
		}
		else if(options.trigger.id == 'export'){
			alert("TODO");
		}
		else if(options.trigger.id == "stages"){
			var row = this.ctx.model('threadmodel').selections().rows[0];
			AppUtil.navigateToStack("monitor/stageinfo", {navId: row.id, urlBase: 'threadinfoshis'}, {title: "Stage Information"});
		}
	});
	
	$app.component('threadgrid').on('dblclick', function(options){
		AppUtil.navigateToDialog("monitor/threadsummary", {itemId: options.rowId, urlBase: 'threadinfoshis'}, {title: "Thread Summary", width: "800", height: "500"});
	});
	
	function doFetch() {
		var server = ctx.component('serverinput').value();
		if(server == null || server == "")
			return;
		var startTs = ctx.component('startts').value();
		if(startTs == null || startTs == ""){
			alert("Start Time can not be empty");
			return;
		}
		var model = ctx.model('threadmodel');
		model.reqParam('startTs', startTs);
		model.reqParam('fetchType', "conns");
		model.reqParam("ips", server);
		model.reload();
	}
	
	$app.on('loaded', function(){
		this.component('startts').value(new Date().format("MM/dd/yyyy 00:00"));
	});
	
	RestApi.action({
		restUrl: 'serverinfos',
		actionName: 'getServerList',
		params: {aliveNode: 'true'},
		callback: function(data, status) {
			var options = [];
			if(data != null){
				for(var i = 0; i < data.length; i ++){
					var option = {value: data[i].address, text: data[i].address};
					options.push(option);
				}
			}
			$app.component('serverinput').datas(options);
			if(options.length > 0){
				$app.component('serverinput').value(options[0].value);
			}
		}
	});
});