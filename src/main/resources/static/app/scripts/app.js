(function intApplication(){
    const app = angular.module('app', []);
    app.controller('list-control', listController);
})();


function listController($scope, $http) {
    $scope.titulo = 'Page Title';
    $http.get('http://localhost:8080/api/v1/collaborator')
        .then(function(response) {
            // Atribuir a resposta (lista de itens) ao escopo
            $scope.lista = response.data;
        })
        .catch(function(error) {
            alert('Erro ao carregar a lista de itens.');
            console.log(error);
        });
}
