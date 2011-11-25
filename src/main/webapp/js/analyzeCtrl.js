/* Analyze Controller */

function AnalyzeCtrl($xhr) {
}

AnalyzeCtrl.$inject = ['$xhr'];

/*
 * init document
 */
$(document).ready(function() {
	$("body").css("display", "none");
	$("body").fadeIn();

	var start = new Date().getTime();
	g = new Dygraph(

		// containing div
		document.getElementById("graphdiv"),

		// CSV or path to a CSV file.
		"rest/analyze", 
		
		// Options
		{
			highlightCircleSize: 6,
			strokeWidth: 3,
			drawPoints: true,
			labelsDivWidth: 120,
			labelsDivStyles: {
					'backgroundColor': 'white',
			},
			labelsSeparateLines: true,
			showRangeSelector: true,
			includeZero: true,
			digitsAfterDecimal: 2,
			drawCallback: function() {
				if (start != undefined) {
					var elapsed = new Date().getTime() - start;
					$("#renderedIn").html(elapsed);
					start = undefined
				}
			}
		}
	);
	
});
