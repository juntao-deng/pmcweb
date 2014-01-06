wdefine(function(){
	$app.model('center_model', {url: 'serverinfos'});
	$app.metadata('center_grid', {model: 'center_model', height:430, multiSelect: true,
		pagination: {rowNum : 30, rowList: [10, 15, 30]},
		columns:[
                      {name: 'address', text:'Address', width:80},
                      {name: 'port', text:'Port', width:40},
                      {name: 'alive', text:'Server State', width:40},
                      {name: 'nodeAlive', text:'Node State', width:40},
                      {name: 'occupiedBy', text:'Occupied By', width:40}
                 ]}
	);
	$app.metadata('top_menu', {groups: [
	                                         {menus : [{id:'add',tip:'Add', icon:'icon-plus'}, 
	                                                   {id:'edit',tip:'Edit', icon: 'icon-edit', statemgr: FwBase.Wtf.View.S_StateMgr}, 
	                                                   {id:'del',tip:'Delete', icon:'icon-remove', statemgr: FwBase.Wtf.View.M_StateMgr}
	                                                  ]},
	                                         {menus : [{id:'occupy', name:'Occupy', statemgr: FwBase.Wtf.View.M_StateMgr}, {id:'release', name:'Release', statemgr: FwBase.Wtf.View.M_StateMgr}]},
	                                         {menus : [{id:'tagsitem',tip:'Show/Hide Tags', icon:'icon-tags'}, {id:'summaryitem',tip:'Show/Hide Summary', icon: 'icon-tasks'}]},
	                                         {menus : [{id:'actions',name:'Actions', 
	                                        	 menus:[{id:'export', name:'Export', icon:'icon-arrow-down'}, {id:'import', name:'Import', icon:'icon-arrow-up'}, {divider:true}, {id:'more', name:'More', icon: 'icon-list', 
	                                        		 																					menus:[{id:'summaryitem2', name:'Summary', icon: 'icon-tasks'}, {id:'help', name:'Help', icon: 'icon-question-sign'}]}
	                                        	 ]}]
	                                         }
									  ]
	                               }
	);
});