$(function(){
	if($(".alert").length > 0){
		setTimeout(function(){
			$(".alert").fadeOut(1000);
		},3000);
	}
	$("#modal").on("hidden.bs.modal", function() {
		$(this).removeData("bs.modal");
	});
	bootbox.setDefaults({
		locale: "zh_CN"
	});
	$(".delete").on(ace.click_event, function() {
		handler = this;
		bootbox.confirm($(handler).attr('title'), function(result) {
			if(result) {
				window.location.href=$(handler).attr('href');
			}
		});
		return false;
	});
	$(".switch").on("change", function() {
		s = $(this).val();
		$.post($(this).attr('href'),{status:s},function(req){
			window.location.reload();
		});
	});
});
function checkAll(el,cls){
	if($(el).is(':checked')){
		$("."+cls).attr("checked","checked");
		$.each($('.'+cls),function(i,item){
			item.checked = true;
		})
	}else{
		$("."+cls).removeAttr("checked");
	}
}
function model_form_submit(){
	var form = $("#modal form");
	form.bootstrapValidator('validate');
	var bv = form.data('bootstrapValidator');
	
	if(bv.$invalidFields.size() == 0) {
		$("#modal form").ajaxSubmit({
		success:function(req,status){
			req = (new Function("return " + req))();
			if(req.status == "1"){
				window.location.reload();
			}else{
				$("#modal .modal-body .alert").remove();
				$("#modal .modal-body").prepend('<div class="alert alert-block alert-danger"><button data-dismiss="alert" class="close" type="button"><i class="ace-icon fa fa-times"></i></button>'+req.msg+'</div>');
			}
		}
		});
	}
}