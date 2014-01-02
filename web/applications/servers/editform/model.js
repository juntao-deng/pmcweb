wdefine(function(){
	$app.model('editmodel', {url: 'serverinfos', lazyInit: true});
	$app.metadata('editform', {model: 'editmodel', rows : 1, labelWidth : 100, elements : [
	                                      {name: 'address', label: 'Ip Address:', nextrow : false, rowSpan : 1, editorType:'input_ip'},
	                                      {name: 'port', label: 'Port:', nextrow : false, rowSpan : 1, editorType:'input_integer', defaultValue: '7878'}
	                                   ]}
	             );
});