(function intApplication(){
    const app = angular.module('app', []);
    app.controller('list-control', listController);
    app.directive('collabTreeNode', collabTreeNode);
})();


function listController($scope, $http) {
    $scope.title = 'Listing ';
    $http.get('http://localhost:8080/api/v1/collaborator')
        .then(function (response) {
            // Atribuir a resposta (lista de itens) ao escopo
            $scope.list = response.data;
        })
        .catch(function (error) {
            alert('Erro ao carregar a lista de itens.');
            console.log(error);
        });
}

function collabTreeNode() {
    return {
        restrict: 'E',
        scope: {
            node: '='
        },
        template:
        `<li>
                {{ node.name }}  <button ng-show="node.managedCollaborators.length" ng-click="toggleChildren()">+/-</button>
                <span class="percent-box">{{ node.passwordScore }}%</span> 
                <span class="complexity-box" ng-class="'cb-'+node.passComplexity.id">{{ node.passComplexity.label}}</span> 
                <ul ng-show="node.childrenVisible">
                    <collab-tree-node ng-repeat="child in node.managedCollaborators" node="child"></collab-tree-node>
                </ul>
        </li>`,
        link: function (scope) {
            scope.node.childrenVisible = false;
            scope.toggleChildren = function () {
                scope.node.childrenVisible = !scope.node.childrenVisible;
            };
        }
    };
}
