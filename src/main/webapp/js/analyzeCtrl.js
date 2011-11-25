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

	var rawData = [];
	
	var start = new Date().getTime();
	var sectionsToMark = undefined;
	
	var g = new Dygraph(

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
					start = undefined;
				}
			},
			underlayCallback: function(canvas, area, g) {
			  if (sectionsToMark == undefined) sectionsToMark = analyzeValues(g);
			
			  for (i=0; i < sectionsToMark.length; i++) {
				var bottom_left = g.toDomCoords(sectionsToMark[i][0], -20);
				var top_right = g.toDomCoords(sectionsToMark[i][1], +20);

				var left = bottom_left[0];
				var right = top_right[0];

				canvas.fillStyle = "rgba(255, 50, 50, 1.0)";
				canvas.fillRect(left, area.y, right - left, area.h);
			  }
            }
		}
	);	
});

function analyzeValues(g) {
	var sectionsToMark = [];
	var inSection = false;
	var section = undefined;

	var date = undefined;
	var value = undefined;
	
	for (i=0; i < g.rawData_.length; i++) {
		date = g.rawData_[i][0];
		value = g.rawData_[i][1];
		
//		console.log(date + " -> " + value);
		
		if (!inSection && value < 0) {
			// start of a new section
			section = [date,undefined];
			inSection = true;
		}
		else if (inSection && value > 0) {
			section[1] = date;
			sectionsToMark.push(section);
			inSection = false;
		}
	}
	
	// close open section at the end if necessary
	if (inSection) {
		section[1] = date;
		sectionsToMark.push(section);
	}
	
	return sectionsToMark;
}
