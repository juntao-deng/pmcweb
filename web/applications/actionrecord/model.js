wdefine(function(){
	$app.metadata('useridinput', {label: 'User Id:', labelWidth: '50', hint: '', width: 60, defaultValue: 'super'});
	$app.metadata('operationmenu', {groups: [
	                                         {menus : [{id:'play', icon:'icon-play', name: ''}, 
	                                                   {id:'stop', icon: 'icon-stop', name: ''},
	                                                   {id:'export', icon: 'icon-download', name: 'Export'}
	                                         		  ]
	                                         }
	                                        ]
	                               });
	$app.metadata('detailstab', {items: [{id: 'item1', text: 'Method Stack'}, {id: 'item2', text: 'Sql Information'}]});
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
	                                  {name: 'stages', text:'Stages', width:40, align:"right"},        
	                                  {name: 'sqls', text:'Sqls', width:40, align:"right"},
	                                  {name: 'conns', text:'Db Conns', width:40, align:"right"}
	                          	]
						}
				);
	$app.metadata('methodForm', {model: 'threadmodel', rows: 1, elements:[{name:'methodStack.method', width: '100%', height: '200', editable: false, theme: 'default', editorType: 'input_highlight'}]});
	$app.metadata('sqlsForm', {model: 'threadmodel', rows: 1, elements:[{name:'detachedSql', width: '100%', height: '200', editable: false, theme: 'default', editorType: 'input_highlight'}]});
	$app.metadata("serverinput", {label: "Active Nodes:", labelWidth:'90', multiple: true, width: 300});
});