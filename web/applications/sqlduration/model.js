wdefine(function(){
	$app.metadata('startts', {label: 'Time From', labelWidth: '80', hint: '', width: 100});
	$app.metadata('operationmenu', {groups: [
	                                         {menus : [{id:'fetch', icon:'icon-refresh', tip: 'Fetch'}, 
	                                                   {id:'export', icon: 'icon-download', tip: 'Export', statemgr: FwBase.Wtf.View.M_StateMgr},
	                                                   {id:'stage', icon: 'icon-edit', tip: 'View Stage', statemgr: FwBase.Wtf.View.S_StateMgr}
	                                         		  ]
	                                         }
	                                        ]
	                               });
	$app.metadata('detailstab', {items: [{id: 'item1', text: 'Sql Information'}]});
	$app.model('threadmodel', {url: 'sqlinfos', lazyInit: true});
	$app.metadata('threadgrid', {model: 'threadmodel', height:230,
						columns:[
	                                  {name: 'callId', text:'Id', width:80},
	                                  {name: 'duration', text:'Duration', width:40},
	                                  {name: 'stageName', text:'Name', width:70},
	                                  {name: 'stagePath', text:'Req Path', width:90},
	                                  {name: 'stageMethod', text:'Req Method', width:60},
	                                  {name: 'resultCount', text:'Result Count', width:40, align:"right"},        
	                                  {name: 'sql', text:'Sql', width:100, align:"right"},
	                                  {name: 'async', text:'Async', width:40}
	                          	]
						}
				);
	$app.metadata('sqlsForm', {model: 'threadmodel', rows: 1, elements:[{name:'sql', width: '100%', height: '200', editable: false, theme: 'default', editorType: 'input_highlight'}]});
	$app.metadata("serverinput", {label: "Nodes:", labelWidth:'40', multiple: true, width: 380});
});