'use strict';

angular.module('springappApp')
    .controller('AuthorDetailController', function ($scope, $rootScope, $stateParams, entity, Author) {
        $scope.author = entity;
        $scope.load = function (id) {
            Author.get({id: id}, function(result) {
                $scope.author = result;
            });
        };
        $rootScope.$on('springappApp:authorUpdate', function(event, result) {
            $scope.author = result;
        });
    });
