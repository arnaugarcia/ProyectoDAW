(function() {
    'use strict';

    angular
        .module('proyectoApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {




        $stateProvider.state('register', {
            parent: 'account',
            url: '/register',
            data: {
                authorities: [],
                pageTitle: 'register.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/account/register/register.html',
                    controller: 'RegisterController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('register');
                    return $translate.refresh();
                }]
            }
        });
        $stateProvider.state('register-2', {
            parent: 'register-extra',
            url: '/register2',
            data: {
                authorities: [],
                pageTitle: 'register.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/account/register/register2.html',
                    controller: 'RegisterController',
                    controllerAs: 'vm'
                },
                'navbar@': {

                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('register');
                    $translatePartialLoader.addPart('');
                    return $translate.refresh();
                }]
            }
        });
    }
})();
