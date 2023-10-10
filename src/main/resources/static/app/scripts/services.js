function collaboratorService($http, API_HOST) {

    const API_PATH =  API_HOST + '/api/v1/collaborator';

    this.create = function(collaborator) {
        return $http.post(API_PATH, collaborator);
    };

    this.update = function(collaborator) {
        return $http.put(API_PATH, collaborator);
    };


    this.list = function() {
        return $http.get(API_PATH);
    };

    this.delete = function(id) {
        return $http.delete(`${API_PATH}/${id}`);
    };
};