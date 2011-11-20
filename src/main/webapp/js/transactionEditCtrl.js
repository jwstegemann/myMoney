/* TransactionList Controllers */



function TransactionEditCtrl($xhr) {
	var scope = this;
	
	this.dateRegExp = /^\d\d\d\d-\d\d-\d\d$/;
	
	//TODO: move to a main script
	$xhr.defaults.headers.put['Content-Type']='application/json'

	scope.createTransaction = function() {
		
		if (scope.tx.to == "") scope.tx.to = null;
	
		$xhr(
			"PUT",
			"rest/expense",
			scope.tx,
			function(code, response) {
				$("#create-edit-modal").modal("hide")
				//TODO: Dafür sorgen, dass das auch wieder verschwindet!
				showMsg("#save-success-msg");
				
				// reset model
				scope.tx = undefined;
			},
			function(code, msg) {
				$("#save-error-details").html(msg);
				showMsg("#save-error-msg");
			}
		);
	}
}

TransactionEditCtrl.$inject = ['$xhr'];