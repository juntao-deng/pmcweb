wdefine(function(){
	$app.component('top_menu').on('click', function(options){
		if(options.trigger.id == "add"){
			options.eventCtx.width = "400";
			options.eventCtx.height = "250";
			options.eventCtx.title = "Add Server";
		}
	});
});