/* TransactionList Controllers */

function TransactionListCtrl($xhr) {
	//FIXME: Move to main script
	$xhr.defaults.headers.put['Content-Type']='application/json'
	$xhr.defaults.headers.post['Content-Type']='application/json'

	var scope = this;
	scope.itemList = {};
	scope.itemList.sortProperty = "name";
	scope.itemList.sortReverse = false;
	
	/*
	 * Validation expressions
	 */ 
	this.dateRegExp = /^\d\d\d\d-\d\d-\d\d$/;
	
	scope.recurrences = [
      {value:0, key: "just once"},
      {value:52, key: "weekly"},
      {value:12, key: "monthly"},
      {value:4, key: "quarterly"},
      {value:1, key: "yearly"}
    ];

	scope.update = function(query) {
		$("table.#itemList").fadeOut(function() {
	
		if (query == undefined) query = scope.query
		else scope.query = query
		$xhr("GET", "rest/expenses/" + query, function(code, response) {
			scope.transactions = response;
			$("table.#itemList").fadeIn();
		});
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
		scope.tx = {recurrence: 0, description: "", value: 0.0};
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
	
	scope.save = function() {
		
		console.log(scope.tx);
		
		// optional value
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
	
	scope.sort = function(table,property) {
		sortTable(scope, table, property);
	}
	
	/*
	 * init
	 */
	scope.query = "singleton";
	scope.update();
}

TransactionListCtrl.$inject = ['$xhr'];

/*
 * init document
 */
$(document).ready(function() {
	$("body").css("display", "none");

	$("#button-edit-close").click(function() {
		$('#create-edit-modal').modal('hide')
	});
	
	$("#button-edit-save").click(function() {
		if ($(this).attr("disabled") == "disabled") {
			showMsg("#invalid-error-msg");
		}
		else {
			$('#form-edit').submit()
		}
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
	
	$("body").fadeIn();
});
