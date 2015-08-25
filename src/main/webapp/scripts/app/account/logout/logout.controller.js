'use strict';

angular.module('springappApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
