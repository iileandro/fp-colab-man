function collabNewFormDirective() {
    return {
        restrict: 'E',
        scope: { },
        template:
            `
            <button ng-if="!creating" ng-click="openForm()">NEW COLLABORATOR</button>
            <form class="form-collab" ng-if="creating" >
                    <div class="float-left">
                         <label for="name">Name</label>
                         <br/>
                        <input name="name" ng-class="(loading) ? 'disabled':''" autocomplete="new-password" ng-model="formData.name" />
                    </div>
                    <div class="float-left">
                        <label for="plainPassword">Password</label>
                         <br/>
                        <input name="plainPassword" ng-class="(loading) ? 'disabled':''" class="plain-password" autocomplete="new-password" type="password" ng-model="formData.plainPassword"/>
                         <img class="icon over-input" title="Hide/Show Password" src="images/hide.png" ng-click="togglePasswordView()"/>
                    </div>
                    <div class="float-left">
                        &nbsp;
                        <br/>
                            <button ng-click="seveForm()" ng-class="(loading || (!formData.name || !formData.plainPassword))? 'disabled':''">{{loading? 'Loading...' : 'SAVE'}}</button>
                        &nbsp;
                        &nbsp;
                        <a ng-click="cancel()">cancel</a>
                    </div>
                    <div class="clear-both"></div>
            </form>`,
        link: function (scope, element, attrs) {
            scope.formData = {};
            scope.creating = false;
            scope.loading = false;

            scope.openForm = function () {
                scope.creating = true;
            };
            scope.seveForm = function () {
                scope.loading = true;
                scope.$parent.CollaboratorService.create(scope.formData).then(function(result) {
                    scope.$parent.list.push(result.data);
                    scope.cancel();
                }).catch(function (error) {
                    scope.loading = false;
                    alert('Error trying to create new collaborator.');
                    console.log(error);
                });
            };
            scope.togglePasswordView = function () {
                const inputPass = element.find('input')[1];
                if(inputPass.type === 'password') {
                    inputPass.type = 'text';
                }else{
                    inputPass.type = 'password';
                }
            };
            scope.cancel = function () {
                scope.creating = false;
                scope.loading = false;
                scope.formData = {};
            };
        }
    };
}

function collabNewSubFormDirective() {
    return {
        restrict: 'E',
        scope: {
            manager: '='
        },
        template:
            `
            <button ng-if="!creating" ng-click="openForm()">NEW SUBORDINATE</button>
            <form class="form-collab" ng-if="creating" >
                    <div class="float-left">
                         <label for="name">Name</label>
                         <br/>
                        <input name="name" ng-class="(loading) ? 'disabled':''" autocomplete="new-password" ng-model="formData.name" />
                    </div>
                    <div class="float-left">
                        <label for="plainPassword">Password</label>
                         <br/>
                        <input name="plainPassword" ng-class="(loading) ? 'disabled':''" class="plain-password" autocomplete="new-password" type="password" ng-model="formData.plainPassword"/>
                         <img class="icon over-input" title="Hide/Show Password" src="images/hide.png" ng-click="togglePasswordView()"/>
                    </div>
                    <div class="float-left">
                        &nbsp;
                        <br/>
                            <button ng-click="seveForm()" ng-class="(loading || (!formData.name || !formData.plainPassword))? 'disabled':''">{{loading? 'Loading...' : 'SAVE'}}</button>
                        &nbsp;
                        &nbsp;
                        <a ng-click="cancel()">cancel</a>
                    </div>
                    <div class="clear-both"></div>
            </form>`,
        link: function (scope, element, attrs) {
            scope.formData = {};
            scope.creating = false;
            scope.loading = false;

            scope.openForm = function () {
                scope.creating = true;
            };
            scope.seveForm = function () {
                scope.loading = true;
                scope.formData.managerId = scope.manager.id;
                scope.$parent.$parent.CollaboratorService.create(scope.formData).then(function(result) {
                    if(!scope.manager.managedCollaborators){
                        scope.manager.managedCollaborators = [];
                    }
                    scope.manager.managedCollaborators.push(result.data);
                    scope.cancel();
                }).catch(function (error) {
                    scope.loading = false;
                    alert('Error trying to create new collaborator.');
                    console.log(error);
                });
            };
            scope.togglePasswordView = function () {
                const inputPass = element.find('input')[1];
                if(inputPass.type === 'password') {
                    inputPass.type = 'text';
                }else{
                    inputPass.type = 'password';
                }
            };
            scope.cancel = function () {
                scope.creating = false;
                scope.loading = false;
                scope.formData = {};
            };
        }
    };
}

function collabTreeNodeDirective() {
    return {
        restrict: 'E',
        scope: {
            node: '='
        },
        template:
            `<li>
                <div class="collab-name" ng-class="node.managedCollaborators && node.managedCollaborators.length ? 'with-children' : ''">
                    <button class="btn-action-item children-view-btn" title="{{!node.childrenVisible? 'Expand':'Collapse'}} subordinates." ng-click="toggleChildren()">{{!node.childrenVisible? '+':'-'}}</button>
                    {{ node.name }} 
                </div>
                
                <div class="complexity-box" ng-class="'cb-'+node.passComplexity.id">{{ node.passComplexity.label}}</div> 
                <div class="percent-box">{{ node.passwordScore }}%</div> 
                <div class="btns-container">
                
                </div>
                <ul ng-show="node.childrenVisible">
                    <collab-tree-node ng-repeat="child in node.managedCollaborators" node="child"></collab-tree-node>
                    <li>
                        <collab-new-sub-form manager="node"></collab-new-sub-form>
                    </li>
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