wdefine(function(){
	function stageFormatter(cellValue, opts, rowdata, act) {
		return rowdata['stages'] + ' / ' + cellValue;
	}
	
	function sqlFormatter(cellValue, opts, rowdata, act) {
		return rowdata['sqls'] + ' / ' + cellValue;
	}
	
	$app.metadata('autorefresh', {label: 'Refresh:', labelWidth: '50', defaultValue: false});
	$app.metadata('refreshinterval', {label: '&nbsp;&nbsp;Every:', labelWidth: '40', hint: 'Sec(s)', width: 20, defaultValue: 10, editable: false});
	$app.metadata('operationmenu', {groups: [
	                                         {menus : [{id:'refresh', icon:'icon-refresh', tip: 'Refresh'},
	                                                   {id:'detail', icon:'icon-list-alt', tip: 'Details', statemgr: FwBase.Wtf.View.S_StateMgr},
	                                                   {id:'export', icon: 'icon-download', tip: 'Export', statemgr: FwBase.Wtf.View.M_StateMgr},
	                                                   {id:'stages', icon: 'icon-edit', tip: 'Stages', statemgr: FwBase.Wtf.View.S_StateMgr}
	                                         		  ]
	                                         }
	                                        ]
	                               });
	$app.metadata('detailstab', {items: [{id: 'item3', text: 'Memory Usage'}, {id: 'item4', text: 'Cpu Usage'}, {id: 'item1', text: 'Method Stack'}, {id: 'item2', text: 'Sql Information'}]});
	$app.model('threadmodel', {url: 'threadinfos', lazyInit: true, idAttribute: 'callId'});
	$app.metadata('threadgrid', {model: 'threadmodel', height:230, pagination: null,
						columns:[
	                                  {name: 'callId', text:'Id', width:80},
	                                  {name: 'duration', text:'Duration', width:40},
	                                  {name: 'stageName', text:'Name', width:70},
	                                  {name: 'clientIp', text:'Client Ip', width:90},
	                                  {name: 'userId', text:'User Name', width:50},
	                                  {name: 'stagePath', text:'Req Path', width:90},
	                                  {name: 'stageMethod', text:'Req Method', width:60},
	                                  //{name: 'requestBytes', text:'Up Bytes', width:40, align:"right", formatter:"int"},
	                                  //{name: 'responseBytes', text:'Down Bytes', width:40, align:"right", formatter:"int"},
	                                  {name: 'sumStageCount', text:'Stages / Total', width:40, align:"right", formatter: stageFormatter},        
	                                  {name: 'sumSqlCount', text:'Sqls / Total', width:40, align:"right", formatter: sqlFormatter},
	                                  {name: 'conns', text:'Db Conns', width:40, align:"right"},
	                                  {name: 'async', text:'Async', width:40}
	                          	]
						}
				);
	$app.metadata('methodForm', {model: 'threadmodel', rows: 1, elements:[{name:'detachedMethod', width: '100%', height: '200', editable: false, theme: 'default', editorType: 'input_highlight'}]});
	$app.metadata('sqlsForm', {model: 'threadmodel', rows: 1, elements:[{name:'detachedSql', width: '100%', height: '200', editable: false, theme: 'default', editorType: 'input_highlight'}]});
	$app.metadata("serverinput", {label: "Nodes:", labelWidth:'40', multiple: true, width: 400});
	
	$app.model('memmodel', {url:'meminfos', lazyInit: true});
	$app.model('cpumodel', {url:'cpuinfos', lazyInit: true});
	var categories = [];
	for(var i = 29; i > 0; i --){
		categories.push('-' + i);
	}
	categories.push('Now');
	
	$app.metadata("memarea", {model: 'memmodel', binding: {seriesField: 'series', valueField: 'values', mapping: ['max', 'total', 'used']}, width: '100%', height: '300', title: 'Memory Usage Summary', subtitle: 'Server: Unknown',
		categories: categories, ytitle: 'Usage', valueSuffix: ' mb', series: [{name: 'Maximized'}, {name: 'Assigned'}, {name: 'Used'}]});
	
	$app.metadata("cpuarea", {model: 'cpumodel', binding: {seriesField: 'series', valueField: 'values'}, width: '100%', height: '300', title: 'Cpu Usage Summary', subtitle: 'Server: Unknown',
		categories: categories, ytitle: 'Usage', valueSuffix: ' %'});
});