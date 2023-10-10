function collaboratorService($http, API_HOST) {

    const API_PATH =  API_HOST + '/api/v1/collaborator';

    this.create = function(collaborator) {
        console.log(collaborator);
        return $http.post(API_PATH, collaborator);
    };


    this.list = function() {
        return $http.get(API_PATH);
    };
};