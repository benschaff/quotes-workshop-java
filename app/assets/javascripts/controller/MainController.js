define(['angular'], function(angular) {

    angular.module('app.controllers').controller('MainController', [ '$rootScope', '$scope', '$filter', 'Last30DaysService', function($rootScope, $scope, $filter, Last30DaysService) {
        $rootScope.action = 'home';

        $scope.demo = [
            { 'name': 'Google Inc', 'symbol': 'GOOG' }
        ];

        $scope.changeSymbol = function(symbol) {
            Last30DaysService.get(
                { symbol: symbol.symbol },
                function(chartData) {
                    var categories = [];
                    $.each(chartData.Dates, function(index, date) {
                        categories.push($filter('date')(date, 'dd/MM'));
                    });

                    $scope.currentSymbolChartData = {
                        "title": {
                            "text": "Price history of " + symbol.name
                        },
                        "subtitle": {
                            "text": "30 days period"
                        },
                        "xAxis": {
                            "categories": categories
                        },
                        "tooltip": {},
                        "plotOptions": {
                            "area": {
                                "pointStart": chartData.Elements[0].DataSeries.close.values[0]
                            }
                        },
                        "series": [
                            {
                                "name": "Close price",
                                "data": chartData.Elements[0].DataSeries.close.values
                            }
                        ]
                    };

                    $scope.currentSymbol = symbol;
                },
                function(error) {
                    console.log(error);

                    $rootScope.setMessage({ type: 'error', text: 'An error occurred. Please try again later' });
                }
            );
        };
    }]);

});
