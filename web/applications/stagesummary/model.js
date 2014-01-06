wdefine(function(){
	$app.metadata("viewstageslink", {text: 'View Stages', style: 'link'});
	var restBase = $app.reqData("urlBase");
	$app.model("threadmodel", {url: restBase + '/' + $app.reqData('navId') + '/stageinfos/' + $app.reqData('itemId'), autoSelect: true});
	$app.metadata('summaryform', {editable: false, model: 'threadmodel', elements: [{name: 'callId', label: 'Id:', nextrow : false, rowSpan : 1, editorType:'input'},
	                                         {name: 'duration', label: 'Duration:', nextrow : false, rowSpan : 1, editorType:'input'},
	                                         {name: 'stageName', label: 'Name:', nextrow : false, rowSpan : 1, editorType:'input'},
	                                         {name: 'stagePath', label: 'Req Path:', nextrow : false, rowSpan : 1, editorType:'input'},
	                                         {name: 'stageMethod', label: 'Req Method:', nextrow : false, rowSpan : 1, editorType:'input'},
	                                         {name: 'sumStageCount', label: 'Sum Stages:', nextrow : false, rowSpan : 1, editorType:'input'},
	                                         {name: 'sumSqlCount', label: 'Sum Sqls:', nextrow : false, rowSpan : 1, editorType:'input'},
	                                         {name: 'stages', label: 'Stages:', nextrow : false, rowSpan : 1, editorType:'input'},
	                                         {name: 'sqls', label: 'Sqls:', nextrow : false, rowSpan : 1, editorType:'input'},
	                                         {name: 'conns', label: 'conns:', nextrow : false, rowSpan : 1, editorType:'input'},
	                                         {name: 'async', text:'Async', width:40}
	                                        ]
								}
				);
});