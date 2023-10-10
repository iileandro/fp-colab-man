(function intApplication(){
    const app = angular.module('app', []);

    app.constant('API_HOST', 'http://localhost:8080');

    // Services...
    app.service('CollaboratorService', collaboratorService);

    // Controllers...
    app.controller('list-control', listController);

    // Directives...
    app.directive('collabTreeNode', collabTreeNodeDirective);
    app.directive('collabNewForm',  collabNewFormDirective);
    app.directive('collabNewSubForm',  collabNewSubFormDirective);

})();
