function showMsg(selector, duration) {
	if (duration == undefined) duration = 4000;

	$(selector).show();
	setTimeout(function() {
		$(selector).fadeOut("slow");
	},duration);
}

