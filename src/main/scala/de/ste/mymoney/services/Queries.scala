package de.ste.mymoney.services

object AnalyzeMapReduce {

	val map = """	function() {
		emit(this.from,{saldo: this.value, expenses: [{name: this.name, value: this.value}]});
	}"""
	
	val reduce = """	function(key, values) {
		var result = {saldo : 0.0, expenses: []};

		values.forEach(function(value) {
			result.saldo += value.saldo;
			result.expenses = result.expenses.concat(value.expenses);
		});
		
		return result;
	}"""
}