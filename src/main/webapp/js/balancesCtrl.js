/* Balances Controller */

function BalancesCtrl($xhr) {
	//FIXME: Move to main script
	$xhr.defaults.headers.put['Content-Type']='application/json'
	$xhr.defaults.headers.post['Content-Type']='application/json'

	var scope = this;
	scope.itemList = {};
	scope.itemList.sortProperty = "date";
	scope.itemList.sortReverse = false;
	
	/*
	 * Validation expressions
	 */ 
	this.dateRegExp = /^\d\d\d\d-\d\d-\d\d$/;

	scope.update = function() {
		$xhr("GET", "rest/balances", function(code, response) {
			scope.balances = response;
		});
	};
   
	scope.deleteSelected = function() {
		$("input.balanceSelect:checked").each(function(index,value) {
			var id = $(value).attr("id");
			
			$xhr( "DELETE", 
				"rest/balance/" + id, 
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
		scope.bal = undefined;
		//TODO: insert date from today
		scope.bal = {description: ""};
		$("#create-edit-modal").modal("show");
	}
		
	scope.save = function() {
		
		if (scope.bal.to == "") scope.bal.to = null;
	
		$xhr(
			"PUT",
			"rest/balance",
			scope.bal,
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
	scope.update();
}

BalancesCtrl.$inject = ['$xhr'];

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
	
	$("#create-edit-modal").modal({
		keyboard: false,
		backdrop: "static"
    })
	
	initAngular();
	
    $("body").fadeIn();
});
