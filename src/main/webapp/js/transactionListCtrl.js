/* TransactionList Controllers */

function TransactionListCtrl($xhr) {
	//FIXME: Move to main script
	$xhr.defaults.headers.put['Content-Type']='application/json'
	$xhr.defaults.headers.post['Content-Type']='application/json'

	var scope = this;
	
	/*
	 * Validation expressions
	 */ 
	this.dateRegExp = /^\d\d\d\d-\d\d-\d\d$/;

	scope.update = function(query) {
		if (query == undefined) query = scope.query
		else scope.query = query
		$xhr("GET", "rest/expenses/" + query, function(code, response) {
			scope.transactions = response;
		});
	};
   
	scope.deleteSelected = function() {
		$("input.transactionSelect:checked").each(function(index,value) {
			var id = $(value).attr("id");
			
			$xhr( "DELETE", 
				"rest/expense/" + id, 
				function(code, response) {
					//FIXME: this should be done only once
					//FIXME: show success-msg
					scope.update();
				},
				function(code, message) {
					showMsg("#delete-error-msg");
				});
		});
	};
	
	scope.create = function() {
		scope.tx = undefined;
		$("#create-edit-modal").modal("show");
	}
	
	scope.edit = function(id) {
		$xhr(
			"GET",
			"rest/expense/" + id,
			function(code, response) {
				scope.tx = response;
				$("#create-edit-modal").modal("show");
			},
			function(code, msg) {
				$("#edit-error-details").html(msg);
				showMsg("#edit-error-msg");
			}
		);
	}
	
	scope.createTransaction = function() {
		
		if (scope.tx.to == "") scope.tx.to = null;
	
		// define if create or update
		var method = (scope.tx.id != null)?"POST":"PUT";
		var url = (scope.tx.id != null)?("rest/expense/" + scope.tx.id):"rest/expense";
	
		$xhr(
			method,
			url,
			scope.tx,
			function(code, response) {
				$("#create-edit-modal").modal("hide");
				showMsg("#save-success-msg");
				scope.update();
			},
			function(code, msg) {
				$("#save-error-details").html(msg);
				showMsg("#save-error-msg");
			}
		);
	}
	
	/*
	 * init
	 */
	scope.query = "singleton";
	scope.update();
}

TransactionListCtrl.$inject = ['$xhr'];
