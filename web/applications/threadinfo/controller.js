wdefine(function(){
	$app.stateManager().connectState([$app.model('threadmodel')], [$app.component('operationmenu')]);
	var ctx = $app;
	$app.on('closing', function(options){
		if(cpuAndMemRt)
			clearTimeout(cpuAndMemRt);
		if(refreshRt)
			clearTimeout(refreshRt);
	});
	
	var refreshRt = null;
	$app.component('autorefresh').on('valuechange', function() {
		if(this.value()){
			this.ctx.component('refreshinterval').editable(true);
			startRefresh();
		}
		else{
			this.ctx.component('refreshinterval').editable(false);
			if(refreshRt)
				clearTimeout(refreshRt);
		}
	});
	
	$app.component('operationmenu').on('click', function(options){
		if(options.trigger.id == 'refresh'){
			var servers = this.ctx.component('serverinput').value();
			if(servers == null || servers.length == 0){
				alert("Please select active node(s) first.");
				return;
			}
			doRefresh();
		}
		else if(options.trigger.id == 'export'){
			alert("TODO");
		}
		else if(options.trigger.id == "stages"){
			var row = this.ctx.model('threadmodel').selections().rows[0];
			AppUtil.navigateToStack("monitor/stageinfo", {navId: row.id, urlBase: 'threadinfos'}, {title: "Stage Information"});
		}
		else if(options.trigger.id == 'detail'){
			var row = this.ctx.model('threadmodel').selections().rows[0];
			openSummary(row.id);
		}
	});
	
	$app.component('threadgrid').on('dblclick', function(options){
		openSummary(options.rowId);
	});
	
	function openSummary(itemId){
		AppUtil.navigateToDialog("monitor/threadsummary", {itemId: itemId, urlBase: 'threadinfos'}, {title: "Thread Summary", width: "800", height: "500"});
	}
	
	$app.component('serverinput').on('valuechange', function(options){
		var servers = options.value;
		var series = [];
		if(servers == null || servers.length == 0){
			if(cpuAndMemRt != null)
				clearTimeout(cpuAndMemRt);
		}
		else{
			doRefresh();
			for(var i = 0; i < servers.length; i ++){
				series.push({name: servers[i]});
			}
			this.ctx.component('cpuarea').series(series);
			startCpuAndMem();
		}
	});
	
	var cpuAndMemRt = null;
	function startCpuAndMem() {
		var serverInput = ctx.component('serverinput');
		if(serverInput == null)
			return;
		var servers = serverInput.value();
		if(servers == null || servers.length == 0)
			return;
		var model = ctx.model('cpumodel');
		model.reqParam("ips", servers.join(","));
		model.reload();
		
		model = ctx.model('memmodel');
		model.reqParam("ips", servers.join(","));
		model.reload();
		
		cpuAndMemRt = setTimeout(startCpuAndMem, 5000);
	}
	
	function startRefresh(){
		var refreshInput = $app.component('autorefresh');
		if(refreshInput == null)
			return;
		var refreshable = refreshInput.value();
		if(!refreshable)
			return;
		var interval = $app.component('refreshinterval').value();
		if(interval <= 0)
			return;
		refreshRt = setTimeout(doRefreshWithRestart, interval * 1000);
	}
	function doRefreshWithRestart() {
		doRefresh();
		startRefresh();
	}
	
	function doRefresh() {
		var servers = ctx.component('serverinput').value();
		if(servers == null || servers.length == 0)
			return;
		var model = ctx.model('threadmodel');
		model.reqParam("ips", servers.join(","));
		model.reload();
	}
	
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
				var values = [];
				for(var i = 0; i < data.length; i ++){
					if(data[i].sessionId != null)
						values.push(data[i].address);
				}
				$app.component('serverinput').value(values);
			}
		}
	});
});