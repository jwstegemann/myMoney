$(document).ready(function() {

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
	
	$("#create-edit-modal").modal({
		keyboard: false,
		backdrop: "static"
    })
	
	initAngular();
});