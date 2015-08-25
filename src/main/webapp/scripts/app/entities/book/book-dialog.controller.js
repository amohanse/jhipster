'use strict';

angular.module('springappApp').controller('BookDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Book',
        function($scope, $stateParams, $modalInstance, entity, Book) {

        $scope.book = entity;
        $scope.load = function(id) {
            Book.get({id : id}, function(result) {
                $scope.book = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('springappApp:bookUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.book.id != null) {
                Book.update($scope.book, onSaveFinished);
            } else {
                Book.save($scope.book, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
