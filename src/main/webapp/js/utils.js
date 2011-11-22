function showMsg(selector, duration) {
	if (duration == undefined) duration = 4000;

	$(selector).show();
	setTimeout(function() {
		$(selector).fadeOut("slow");
	},duration);
}

function initAngular() {
	/*
	 * by STE
	 */
/*	angular.directive("ste:click", function(expression, element){
		return angular.extend(function($updateView, element){
			var self = this;
			element.bind('click', function(event){
			self.$tryEval(expression, element);
			$updateView();
			// continue with event-propagation
			});
		}, {$inject: ['$updateView']}); 
	});
*/
	angular.directive("ste:click", function(expression, element){
		return function(element){
			var self = this;
			element.bind('click', function(event){
				self.$apply(expression);
	//			event.stopPropagation();
			});
		};
	});
	
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
}

function initHighlightTableRow(selector) {
	$(selector).change(function(event) {
		alert($(this).attr("checked"));
	});
}

