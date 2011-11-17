/* TransactionList Controllers */



function TransactionEditCtrl($xhr) {
	var scope = this;
	
	//TODO: move to a main script
	$xhr.defaults.headers.put['Content-Type']='application/json'

	scope.createTransaction = function() {
		var transaction = {
				name: scope.name,
				value: parseFloat(scope.value),
				from: scope.from,
				description: scope.description,
				recurrence: parseInt(scope.recurrence)
			}
			
		if (scope.to != "") transaction["to"] = scope.to;
	
		$xhr(
			"PUT",
			"rest/expense",
			transaction,
			function(code, response) {
				alert("done");
			},
			function(code, msg) {
				alert("something went wrong");
			}
		);
	}
}

TransactionEditCtrl.$inject = ['$xhr'];