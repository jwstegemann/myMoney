/* TransactionList Controllers */

function TransactionListCtrl($xhr) {
	var scope = this;

	angular.filter('recurrence', function(input) {
		switch(input) {
			case 0 : return "just once";
			case 52 : return "weekly";
			case 12 : return "monthly";
			case 4 : return "quarterly";
			case 1 : return "yearly";
			default : return "unknown";
		}
	});

   $xhr('GET', 'rest/expenses', function(code, response) {
	    scope.transactions = response;
	  });
	  
	  scope.deleteSelected = function() {
		$("input.transactionSelect:checked").each(function(index,value) {
			var id = $(value).attr("id");
			$xhr( "DELETE", 
				"rest/expense/" + id, 
				function(code, response) {
				},
				function(code, message) {
					console.log("error: " + message);
				});
		});
	  };
	  
	  scope.mapRecurrence = function(value) {
		switch(value) {
			case 0 : return "just once";
			case 52 : return "weekly";
			case 12 : return "monthly";
			case 4 : return "quarterly";
			case 1 : return "yearly";
			default : return "unknown";
		}
	  };
}

TransactionListCtrl.$inject = ['$xhr'];
