wdefine(function(){
	
	$app.component('operationmenu').on('click', function(options){
		if(options.trigger.id == 'play'){
		}
		else if(options.trigger.id == 'stop'){
		}
	});
	
	$app.component('threadgrid').on('dblclick', function(options){
		AppUtil.navigateToDialog("monitor/threadsummary", {navId: options.rowId}, {title: "Thread Summary", width: "800", height: "500"});
	});
	
	$app.component('serverinput').on('valuechange', function(options){
		var servers = options.value;
		var series = [];
		if(servers == null || servers.length == 0){
			if(cpuAndMemRt != null)
				clearTimeout(cpuAndMemRt);
		}
		else{
			for(var i = 0; i < servers.length; i ++){
				series.push({name: servers[i]});
			}
			this.ctx.component('cpuarea').series(series);
			startCpuAndMem();
		}
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
				}
				options.push(option);
			}
			$app.component('serverinput').datas(options);
			if(options.length > 0){
				$app.component('serverinput').value(options[0].value);
			}
		}
	});
});