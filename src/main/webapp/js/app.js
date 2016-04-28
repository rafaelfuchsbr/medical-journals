var app = angular.module('RafaelFuchs_SEM_Journals_Java', ['ngRoute','ngFileUpload','ngCookies']);

app.run(function($rootScope, $http, $location, $cookies, $templateCache) {

    $rootScope.$on('$viewContentLoaded', function() {
        $templateCache.removeAll();
    });

    $rootScope.loadApp = function() {
        var user = $cookies.get('MJPCO_local');
        if (user != null) {
            user = JSON.parse(atob(user));
            $rootScope.reloadLogin(user);
        }
    };

    $rootScope.goto = function(link) {
        $location.url(link);
    };

    $rootScope.doLogin = function(email, password) {

        $http.post('/users/login', { "email": email, "password": password })
        .then(function(response) {
            $rootScope.reloadLogin(response.data);
            $cookies.put('MJPCO_local', btoa(JSON.stringify($rootScope.loggedUser)));
            $location.path('/home');
        },function(response) {
            $rootScope.loginErrorMessage = response.data.message;
            $rootScope.showLoginError = true;
        });
    };

    $rootScope.$on("loginDone", function(event, message) {
        $cookies.put('MJPCO_local', btoa(JSON.stringify($rootScope.loggedUser)));
    });

    $rootScope.doLogout = function() {
        $rootScope.loggedUser = {};
        $rootScope.loggedUser.loggedIn = false;
        $rootScope.showLoginError = false;
        $cookies.remove('MJPCO_local');
        $location.url('/home');
    };

    $rootScope.reloadLogin = function(user) {
        $rootScope.loggedUser = user;
        $rootScope.loggedUser.loggedIn = true;
        delete($rootScope.loggedUser.password);
        delete($rootScope.loggedUser.createdDate);
        $rootScope.$broadcast("loginDone");
    };

    $rootScope.checkLogin = function(goHome) {
        if ($rootScope.loggedUser == null) {
            if (goHome) {
                $rootScope.goHome();
            }
            return false;
        } else {
            return true;
        }
    };

    $rootScope.checkAuthor = function(goHome) {
        if (!$rootScope.checkLogin(false)) {
            if (goHome) {
                $rootScope.goHome();
                return false;
            } else {
                return true;
            }
        } else {
            if ($rootScope.loggedUser.type != 'author') {
                $rootScope.goHome();
                return false;
            }
            return true;
        }
    };

    $rootScope.goHome = function() {
        $location.url('/home');
    };

        $rootScope.showLoginError = false;
        $rootScope.tab = 'home';

        $rootScope.loadApp();
});

app.controller('NavBarController',
    ['$scope', '$rootScope',
    function($scope, $rootScope) {

    $rootScope.tab = 'home';

    $scope.setTab = function(tab) {
        $scope.tab = tab;
    };

    $scope.isSelected = function(tab) {
        return $scope.tab === tab;
    };
}]);

app.config(['$routeProvider',
    function($routeProvider) {
        $routeProvider.
            when('/topics', {
                templateUrl: 'topicsList.html',
                controller: 'TopicController'
            }).
            when('/logout', {
                templateUrl: 'home.html',
                controller: 'HomeController'
            }).
            when('/signup', {
                templateUrl: 'signup.html',
                controller: 'SignupController'
            }).
            when('/journalList', {
                templateUrl: 'journalList.html',
                controller: 'JournalListController'
            }).
            when('/home', {
                templateUrl: 'home.html',
                controller: 'HomeController'
            }).
            when('/signupconfirmation', {
                templateUrl: 'signupconfirmation.html',
                controller: 'SignupController'
            }).
            when('/login', {
                templateUrl: 'login.html',
                controller: 'SimpleController'
            }).
            when('/editJournal', {
                templateUrl: 'editJournal.html',
                controller: 'JournalEditController'
            }).
            when('/manageJournals', {
                templateUrl: 'manageJournals.html',
                controller: 'ManageJournalController'
            }).
            when('/about', {
                templateUrl: 'about.html',
                controller: 'AboutController'
            }).
            when('/', {
                templateUrl: 'home.html',
                controller: 'HomeController'
            }).
            otherwise({
                redirectTo: '/'
            }
        );
    }
]);

app.controller('SignupController',
    ['$scope', '$http', '$location', '$rootScope',
    function($scope, $http, $location,$rootScope) {

    $scope.user = { "name" : "", "email" : "", "password" : "", "confPassword" : "" };

    $scope.save = function() {
        if ($scope.signupForm.$valid) {
            $http.post('/users', $scope.user).then(function(response) {
                $location.path('/signupconfirmation');
            }, function(response) {
                console.log(response);
            });
        };
    };

}]);

app.controller('JournalListController',
    ['$rootScope', '$scope', '$http', '$location',
    function($rootScope, $scope, $http, $location) {

    $rootScope.tab = 'journal';

    $scope.doJournalSearch = function (page) {
        var queryString = "";
        if ($scope.mainJournalSearch) {
            queryString = queryString + "&filter="+$scope.mainJournalSearch;
        }
        if (page) {
            queryString = queryString + "&page="+page;
        }
        $http.get('/journals?' + queryString).then(function(response) {
            $scope.pagedJournals = response.data;
        });
    };

    $scope.doJournalSearch(1);
}]);

app.controller('TopicController',
    ['$scope', '$http', '$rootScope',
    function($scope,$http, $rootScope) {

    $rootScope.tab = 'category';

    $scope.doTopicsSearch = function (page) {
        var queryString = "";
        if ($scope.mainTopicSearch) {
            queryString = queryString + "&filter="+$scope.mainTopicSearch;
        }
        if (page) {
            queryString = queryString + "&page="+page;
        }
        $http.get('/topics?' + queryString).then(function(response) {
            $scope.pagedTopics = response.data;
            $scope.checkSubscriptions();
        });
    };

    $rootScope.$on("loginDone", function(event, message) {
        $scope.checkSubscriptions();
    });

    $scope.subscribe = function(oid) {
        $http.post('/topics/' + oid +'/user/' + $rootScope.loggedUser.oid ).then(function(response) {
            $rootScope.reloadLogin(response.data);
        });
    };

    $scope.unsubscribe = function(oid) {
        $http.delete('/topics/' + oid +'/user/' + $rootScope.loggedUser.oid ).then(function(response) {
            $rootScope.reloadLogin(response.data);
        });
    };

    $scope.checkSubscriptions = function() {
        if (!$rootScope.loggedUser) {
            return;
        }
        if (!$scope.pagedTopics) {
            return;
        }
        for (x in $scope.pagedTopics.items) {
            $scope.pagedTopics.items[x].subscribed = false;

            for(i in $rootScope.loggedUser.topics) {
                if ($scope.pagedTopics.items[x].oid == $rootScope.loggedUser.topics[i].oid) {
                    $scope.pagedTopics.items[x].subscribed = true;
                }
            }
        }
    };

    $scope.doTopicsSearch(1);
}]);

app.controller('JournalEditController',
    ['$rootScope', '$scope', 'Upload', '$timeout', '$route', '$http', '$location' ,
    function ($rootScope, $scope, Upload, $timeout, $route, $http, $location) {

    $rootScope.tab = 'manageJournals';

    $scope.editJournal = {};
    $scope.authors = [];
    $scope.categories = [];

    $scope.uploadJournalFile = function(file, oid) {
        file.upload = Upload.upload({
            url: '/journals/upload/'+oid,
            data: {file: file},
        });

        file.upload.then(
            function (response) {
                $timeout(function () {
                    file.result = response.data;
                });
            },
            function (response) {
                if (response.status > 0)
                    $scope.errorMsg = response.status + ': ' + response.data;
            },
            function (evt) {
                file.progress = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
            });
    };

    $scope.saveJournal = function(file) {
        var headers = {
            user : $rootScope.loggedUser.email
        };

        $http.post('/journals/', $scope.editJournal, { headers : headers }).success(function(response) {
            $scope.editJournal = response;
            if (file) {
                $scope.uploadJournalFile(file, $scope.editJournal.oid);
            }
            $location.url('/manageJournals');
        }).error(function(response) {
                console.log('error');
            }
        );
    };

    $scope.load = function() {
        if (!$rootScope.checkAuthor(true)) {
            return;
        }
        if ($route.current.params.oid != null) {
            $scope.loadJournal($route.current.params.oid);
        }
        $scope.loadAuthors();
        $scope.loadCategories();
    };

    $scope.loadJournal = function (oid) {
       $http.get('/journals/'+oid).success(function(response) {
           $scope.editJournal = response;
       }).error(function(response) {
           console.log('error');
       });
    };

    $scope.loadAuthors = function (oid) {
       $http.get('/users/type/author/').success(function(response) {
           $scope.authors = response;
       });
    };

    $scope.loadTopics = function (oid) {
       $http.get('/topics?items=999999999').success(function(response) {
           $scope.topics = response;
       });
    };

    $scope.load();

}]);

app.controller('ManageJournalController',
    ['$rootScope', '$location', '$scope', '$route', '$http', '$window' ,
    function ($rootScope, $location, $scope, $route, $http, $window) {

    $rootScope.tab = 'manageJournals';

    $scope.myPagedJournals = [];

    $scope.doMyJournalSearch = function (page) {
        if (!$rootScope.checkAuthor(true)) {
            return;
        }
        var queryString = "";
        if ($scope.myJournalSearch) {
            queryString = queryString + "&filter="+$scope.myJournalSearch;
        }
        if (page) {
            queryString = queryString + "&page="+page;
        }
        $http.get('/journals?' + queryString).then(function(response) {
                                                               $scope.myPagedJournals = response.data;
        });
    };

    $scope.deleteJournal = function(oid) {

        if (!$window.confirm('Are you sure you want to delete?')) {
            return;
        }
        var headers = {
            user : $rootScope.loggedUser.email
        };

        $http.delete('/journals/'+oid, { headers : headers }).success(function(response) {
            $scope.myJournalSearch = '';
            $scope.doMyJournalSearch(1);
        }).error(function(response) {
                console.log('error');
            }
        );
    };

    $scope.editJournal = function(oid) {
        $location.url('/editJournal?oid='+oid);
    };

    $scope.newJournal = function() {
        $location.url('/editJournal');
    };

    $scope.load = function() {
        if (!$rootScope.checkAuthor(true)) {
            return;
        }
        $scope.doMyJournalSearch(1);
    }

    $scope.load();
}]);

app.controller('HomeController',
    ['$rootScope',
    function ($rootScope) {

    $rootScope.tab = 'home';

}]);

app.controller('AboutController',
    ['$rootScope',
    function ($rootScope) {

    $rootScope.tab = 'about';

}]);

app.controller('SimpleController',
    ['$rootScope',
    function ($rootScope) {

    $rootScope.tab = '';

}]);
