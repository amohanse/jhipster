'use strict';

angular.module('springappApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


