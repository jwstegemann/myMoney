/* Analyze Controller */

function AnalyzeCtrl($xhr) {
}

AnalyzeCtrl.$inject = ['$xhr'];

/*
 * init document
 */
$(document).ready(function() {
	g = new Dygraph(

    // containing div
    document.getElementById("graphdiv"),

    // CSV or path to a CSV file.
    "rest/analyze"

  );
});
