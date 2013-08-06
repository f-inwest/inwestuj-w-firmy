(function($){ // izolujemy skrypt
	$(document).ready(function(){ // dokument redy zaczyna ładowac skrypt
		var center = new google.maps.LatLng(51.227756, 6.773647); // wspolrzedne polozenia
        var mapOptions = {  
          center: center, // parametrowi center przekazujemy zmienna center - dwie up
          zoom: 8, //zoom
          disableDefaultUI: true,
          mapTypeId: google.maps.MapTypeId.ROADMAP,  // typ mapy 
          styles: [ // style z josona
				{
					featureType: 'landscape',
					elementType: 'all',
					stylers: [
						{ "color": "#ffffff" },
						{ saturation: -100 },
						{ lightness: 4 },
						{ visibility: 'on' }
					]
				},{
					featureType: 'road.highway',
					elementType: 'all',
					stylers: [
						{ "color": "#4cc7df"},
						{ saturation: 100 },
						{ lightness: -7 },
						{ visibility: 'on' }
					]
				},{
					featureType: 'road.arterial',
					elementType: 'all',
					stylers: [
						{ "color": "#4cc7df" },
						{ saturation: -30 },
						{ lightness: -3 },
						{ visibility: 'on' }
					]
				},{
					featureType: 'road.local',
					elementType: 'all',
					stylers: [
						{ "color": "#ffffff" },
						{ saturation: -30 },
						{ lightness: -3 },
						{ visibility: 'on' }
					]
				},{
					featureType: 'landscape.natural',
					elementType: 'all',
					stylers: [
						{ "color": "#ffffff" },
						{ saturation: -30 },
						{ lightness: -3 },
						{ visibility: 'on' }
					]
				},{
					featureType: 'poi.park',
					elementType: 'all',
					stylers: [
						{ "color": "#ffffff" },
						{ saturation: -30 },
						{ lightness: -3 },
						{ visibility: 'on' }
					]
				}
			]
        };
        var map = new google.maps.Map($('#mapka').get(0), // zmienna map do jakiego diva ma byc przekazana 
            mapOptions);
        var markerOptions = { // opcje markera 
        	position: center, 
        	icon: {
        		anchor: new google.maps.Point(25,15), // wspolzedne poczatku na ikonie tu center statku
        		url: 'images/statek-ico.png'
        	},
        	map: map, // 
        	title: 'tu znajduje sie opis dla ikony statku' // marker tytuł 
        };
        
        var marker = new google.maps.Marker(markerOptions);
        
        google.maps.event.addListener(marker, 'click', function(){ // po kliknieciu w marker 
        	window.location.href = 'http://link do strony';//link do strony
        });
		
	});
})(jQuery);
