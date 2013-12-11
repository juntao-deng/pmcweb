wdefine(function(){
	$app.on('loaded', function(){
		var model = this.model('editmodel');
		model.row();
		model.select(0);
	});
	
	$app.component('okbt').on('click', function(){
		var model = this.ctx.model('editmodel');
		var ctx = this.ctx;
		model.save({success: function(){
			ctx.parent.model('center_model').reload();
			ctx.close();
		}});
	});
});