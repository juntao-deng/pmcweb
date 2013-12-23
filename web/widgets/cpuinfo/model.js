wdefine(function(){
	$widget.metadata('cpuchart', {width: '100%', height: '300', title: 'Statisics of Cpu Usage', subtitle: 'For all monitored nodes',
		categories: ['1750', '1800', '1850', '1900', '1950', '1999', '2050'], ytitle: 'Percent', valueSuffix: ' %',
        series: [{
            name: '130.184',
            data: [502, 635, 809, 947, 1402, 3634, 5268]
        }, {
            name: '3.195',
            data: [106, 107, 111, 133, 221, 767, 1766]
        }, {
            name: '3.035',
            data: [163, 203, 276, 408, 547, 729, 628]
        }, {
            name: '130.165',
            data: [18, 31, 54, 156, 339, 818, 1201]
        }, {
            name: '130.173',
            data: [2, 2, 2, 6, 13, 30, 46]
        }]});
});