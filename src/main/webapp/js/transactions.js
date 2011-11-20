$(document).ready(function() {

    $("#facet-tabs").bind("change", function (e) {
		alert(e.target);
    });
	
	$("#button-edit-close").click(function() {
		$('#create-edit-modal').modal('hide')
	});
	
	$("#button-edit-save").click(function() {
		$('#form-edit').submit()
	});
	
	
	$("#abcdef").click(function() {
		var test = angular.scope().$become(TransactionEditCtrl);	
		var jshfdjsdhfjsdh ="222";
	});
});