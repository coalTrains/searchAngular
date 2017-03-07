
angular.module('Ricerca', [])

.controller('control', function($scope, $http){	
	
	$http.getUsersList('localhost:8080/RicercaAngular/usersList').
		
		$http({
			method: 'GET',
			url: 'ServletUser',
			
			data:{ action : "search", nome : $('#formName').val(), cognome : $('#formSurname').val(), data : $('#formDate').val() }	
		}).success(function(users){
			angular.forEach(users, function (index , userBean) {
//			$timeout('5000');	
			});
			
		}).error(function(){
			alert("Si è verificato un errore nella connessione con il server");
			
		});
	
	$http.getUsersList('localhost:8080/RicercaAngular/usersList').

	$scope.deleteUser = function(){
		$http({
			method: 'POST',
			url:'ServletUser',
			data:{ action : "delete", id : $('').val()}
		
		}).success(function(users){
			
		
		}).error(function(){
			alert("Si è verificato un errore nella connessione con il server");
		});
	
	};
	
	
	$http.getUsersList('localhost:8080/RicercaAngular/usersList').

	$scope.add = function(){
		$http({
			method: 'POST',
			url:'ServletUser',
			data:{ action : "add", nome : $('#formName').val(), cognome : $('#formSurname').val(), data : $('#formDate').val()}	
		}).success(function(users){
			
		
		}).error(function(){
			alert("Si è verificato un errore nella connessione con il server");
		});
	};

	$http.getUsersList('localhost:8080/RicercaAngular/usersList').

	$scope.updateUser = function(){
		$http({
			method: 'POST',
			url:'ServletUser',
			data:{ action : "update", nome : $('#formName').val(), cognome : $('#formSurname').val(), data : $('#formDate').val()}	
		}).success(function(users){
			
		
		}).error(function(){
			alert("Si è verificato un errore nella connessione con il server");
		});
//		var nome = angular.element('');
	};
	
});
angular.module('Ricerca', ['ngResource']);
	var users = $resource('ServletUser', {nome: '', cognome: '', data: ''}, {action: 'search'});
	


