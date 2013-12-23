wdefine(function(){
	$app.stateManager().connectState([$app.model('threadmodel')], [$app.component('operationmenu')]);
	var startTs = endTs = null;
	$app.component('operationmenu').on('click', function(options){
		if(options.trigger.id == 'play'){
			var servers = this.ctx.component('serverinput').value();
			if(servers == null || servers.length == 0){
				alert("Please select a node first");
				return;
			}
			RestApi.action({
				restUrl: 'threadinfosaction',
				actionName: 'startRecord',
				params: {ips: servers.join(",")},
				callback: function(data, status) {
					var recordId = data;
					if(data == null || data == ""){
						alert("not started");
						return;
					}
					options.source.item("stop").enable(true);
					options.trigger.enable(false);
					$app.data("recordId", data.recordId);
				}
			});
		}
		else if(options.trigger.id == 'stop'){
			var servers = this.ctx.component('serverinput').value();
			if(servers == null || servers.length == 0){
				alert("Please select a node first");
				return;
			}
			RestApi.action({
				restUrl: 'threadinfosaction',
				actionName: 'endRecord',
				params: {ips: servers.join(","), recordId : $app.data("recordId")},
				callback: function(data, status) {
					options.source.item("play").enable(true);
					options.trigger.enable(false);
					doFetch();
				}
			});
		}
	});
	
	function doFetch() {
		var servers = $app.component('serverinput').value();
		if(servers == null || servers.length == 0)
			return;
		var model = $app.model('threadmodel');
		model.reqParam('recordId', $app.data("recordId"));
		model.reqParam("ips", servers.join(","));
		model.reload();
	}
	
	$app.component('threadgrid').on('dblclick', function(options){
		AppUtil.navigateToDialog("monitor/threadsummary", {itemId: options.rowId, urlBase: 'threadinfosaction'}, {title: "Thread Summary", width: "800", height: "500"});
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