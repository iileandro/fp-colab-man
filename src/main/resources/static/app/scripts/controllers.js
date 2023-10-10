function listController($scope, $http, CollaboratorService) {
    $scope.list = [];
    $scope.CollaboratorService = CollaboratorService;


    $scope.toggleAll = function(expanded) {
        $scope.toggleNode($scope.list, expanded);
    };

    $scope.toggleNode = function(list, expanded) {
        list.forEach(function (item){
            item.childrenVisible = expanded;
            if(item.managedCollaborators && item.managedCollaborators.length){
                $scope.toggleNode(item.managedCollaborators, expanded);
            }
        });
    }

    CollaboratorService.list().then(function (response) {
        $scope.list = response.data;
    }).catch(function (error) {
        alert('Error trying to load list. Verify database and/or connection.');
        console.log(error);
    });
}