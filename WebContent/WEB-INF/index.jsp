<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import = "javax.servlet.RequestDispatcher" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html ng-app="Ricerca">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Ricerca Db</title>
		
		<script src="js/angular.js"> </script>
		<script src="js/angular-resource.js"> </script>
		<script>
			var module = angular.module('Ricerca', ['ngResource']);
			
				module.factory('ServletUser', function ($resource){
					var res = $resource('/users/:id', {id: '@id' });
					return {
						get: function() {
							return res.query();
						}
					};
					
			
				});
				module.controller('controllerRicerca', function($resource, users){		
					$scope.modUserIsHidden = true;
					$scope.newUserIsHidden = true;
				
					function search($resource){
						var usersRes = $resource('ServletUser', {nome: '', cognome: '', data: ''}, {action: 'search'});
						var users = usersRes.get( {action: 'search', nome: '', cognome: '', data: ''},
							function(){
								return users;
							},
							function(){
								alert("Si è verificato un errore nella connessione con il server");
							}
						);
					}
					// Modifica
					function update($resource){
						$scope.modUserIsHidden = !$scope.modUserIsHidden; 
						//save implem.
					}
					// Elimina
					function erase($resource){
						var usersRes = $resource('ServletUser' , {nome: '', cognome: '', data: ''}, {action: 'delete'});
						usersRes.save({action:'delete' , nome: '' , cognome: '' , data: '' },
							function(){
							//save implem.
							}),
							function(){
								
							};
					}

				
				});
				// Aggiungi
				function add(){
					$scope.newUserIsHidden = !$scope.newUserIsHidden;
				    //save	implem.
				}
				
			angular.module('Ricerca')
			.controller('controllerUpdate', function($resouce, users){
				function doUpdateUser($resource){
					
					
					$scope.modUserIsHidden = !$scope.modUserIsHidden;
				}
			});
			angular.module('Ricerca')
			.controller('controllerAdd', function($resouce, users){
				function doAdd(){
					
					
					$scope.newUserIsHidden = !$scope.newUserIsHidden;
				}
			});
					
		</script>

	</head>
	
	<body style = "background-color: #eeeebb">
	
		<table><tr><td width="600px">
	
		<div id="modUser" ng-controller="controllerUpdate" ng-hide="modUserIsHidden" align="center">  <h2>Modifica utente</h2>
			<form name="form"  id="modForm" >
			Nome:  <input type="text" id="modName" ng-model="modUser.name" name="nome" value="'+{{userBean.nome}}+'"/> <br> <br> 
			Cognome:  <input type="text" id="modSurname" ng-model="modUser.surname" name="cognome" value="'+{{userBean.cognome}}+'" /> <br> <br>
			Data di nascita(aaaa-mm-gg):  <input type="text" id="modDate" ng-model="modUser.date" name="data" value="'+{{userBean.data}}+'" /> <br> <br>
			<input type="button" ng-click="doUpdateUser(modUser)" id="doModifica" value="Invia modifica" />	 	
			</form> <br> 
		</div>
		
		<div id="newUser" ng-controller="controllerAdd" ng-hide="newUserIsHidden" align="center">  <h2>Aggiungi nuovo utente</h2>
			
			<form name="form" id="addForm" >
				Nome:  <input type="text" id="addName" ng-model="addUser.name" name="nome"/><br> <br> 
				Cognome:  <input type="text" id="addSurname" ng-model="addUser.surname" name="cognome"/><br> <br>
				Data di nascita(aaaa-mm-gg):  <input type="text" id="addDate" ng-model="addUser.date" name="data"/> <br> <br>
				<input type="button" ng-click="doAdd(addUser)" id="doAggiungi" value="Aggiungi"/>	 	
			</form> <br> 
		</div>
		
	
	
		<div class="search" ng-controller="controllerRicerca" ng-model="user" align="center">
			<h2>Ricerca utenti</h2> 
			<br><br> 
			<form  name="form" id="form"><b>
				 Nome:  <input type="text" ng-model="user.name" name="listName" ng-maxlength="40" ng-trim="true" id="formName" value=""/><br><br> 
				 Cognome:  <input type="text" ng-model="user.surname" name="listSurname" ng-maxlength="40" ng-trim="true" id="formSurname" value=""/><br><br>
				 Data di nascita (aaaa-mm-gg):  <input type="text" ng-model="user.date" name="data" ng-trim="true" ng-pattern="/^((\d{4})-(\d{2})-(\d{2}))$/" id="formDate"  value=""/><br><br>
				 <input type="hidden" name="action" value="search"/>
				 <input type="button" ng-click="search(user)" id="formSubmit" value="Ricerca"/></b>		 
				 <br><br><br><br>	
			</form> 
		</div>	
		</td>
		<td>
		
		<div class="Db-list">
		<h2>Lista utenti</h2>
		
		<table border="1" id="list" ng-controller="controllerRicerca"><thead><tr> <th>ID</th> <th>Nome</th>  <th>Cognome</th> <th>Data di nascita</th>  </tr></thead> 
			
			<tr class='rows' ng-repeat="userBean in users">
				<td class='uId' id='uId_" + userBean.id + "'> </td>
				<td class='uName' id='uName_"+userBean.id+"' value='{{userBean.nome}}'> </td>
				<td class='uSurname' id='uSurname_"+userBean.id+"' value='{{userBean.cognome}}'> </td>
				<td class='uDate' id='uDate_"+userBean.id+"' value='{{userBean.data}}'> </td>
				<td> <form action='ServletUser' method='GET'> <input type='button' ng-click="update(userBean)" value='Modifica' class='modifica' id='modifica_"+userBean.id+"'/> </form> </td>
				<td> <form action='ServletUser' method='POST'><input type='button' ng-click="erase(userBean)" value='Elimina'  class='elimina' id='elimina_"+userBean.id+"'/> </form> </td>
			</tr>
			
			<tr> <td> <form  id="linkAggiungi" >  <input type="button" ng-click="add()" value="Aggiungi nuovo utente" id="add"/> </form> </td> </tr>

		</table>		
		</div>
		</td></tr>
		</table>
		
	</body>

</html>