wdefine(function(){
	$app.component("viewstageslink").on("click", function(){
		AppUtil.navigateToStack("monitor/stageinfo", {navId: this.ctx.reqData('itemId'), urlBase: $app.reqData("urlBase")}, {title: "Stages Information", callback: callback});
		var ctx = this.ctx;
		function callback(){
			ctx.close();
		}
	});
	
	$app.component('okbt').on('click', function(){
		this.ctx.close();
	});
});