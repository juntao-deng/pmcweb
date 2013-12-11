wdefine(function(){
	$app.metadata("viewstageslink", {text: 'View Stages', style: 'link'});
	$app.model("threadmodel", {url: 'threadinfos/' + $app.reqData('itemId'), autoSelect: true});
	$app.metadata('summaryform', {editable: false, model: 'threadmodel', elements: [{name: 'callId', label: 'Id:', nextrow : false, rowSpan : 1, editorType:'input'},
	                                         {name: 'duration', label: 'Duration:', nextrow : false, rowSpan : 1, editorType:'input'},
	                                         {name: 'stageName', label: 'Name:', nextrow : false, rowSpan : 1, editorType:'input'},
	                                         {name: 'clientIp', label: 'Client Ip:', nextrow : false, rowSpan : 1, editorType:'input'},
	                                         {name: 'userId', label: 'User Name:', nextrow : false, rowSpan : 1, editorType:'input'},
	                                         {name: 'stagePath', label: 'Req Path:', nextrow : false, rowSpan : 1, editorType:'input'},
	                                         {name: 'stageMethod', label: 'Req Method:', nextrow : false, rowSpan : 1, editorType:'input'},
	                                         {name: 'stages', label: 'Stages:', nextrow : false, rowSpan : 1, editorType:'input'},
	                                         {name: 'sqls', label: 'Sqls:', nextrow : false, rowSpan : 1, editorType:'input'},
	                                         {name: 'conns', label: 'conns:', nextrow : false, rowSpan : 1, editorType:'input'},
	                                         {name: 'requestBytes', label: 'Up Bytes:', nextrow : false, rowSpan : 1, editorType:'input'},
	                                         {name: 'responseBytes', label: 'Down Bytes:', nextrow : false, rowSpan : 1, editorType:'input'}
	                                        ]
								}
				);
});