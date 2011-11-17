/* TransactionList Controllers */


function TransactionListCtrl($xhr) {
	var scope = this;

	$xhr('GET', 'rest/expenses', function(code, response) {
	    scope.transactions = response;
	  });
}

TransactionListCtrl.$inject = ['$xhr'];
