function loadPersons() {
    loadExernals('personsLoader', 'Tags?special=PERSONS', 'persons') ;
}

function loadPlaces() {
    loadExernals('placesLoader', 'Tags?special=PLACES', 'places') ;
}

function loadAlbums() {
    loadExernals('albumsLoader', 'Albums?special=TOP5', 'albums') ;
}

function loadYears() {
    loadExernals('yearsLoader', 'Albums?special=YEARS', 'years') ;
}

function loadSelect() {
    loadExernals('selectLoader', 'Albums?special=SELECT', 'select') ;
}

//addLoadEvent(loadYears) ;
//addLoadEvent(loadAlbums) ;
//addLoadEvent(loadPersons) ;
//addLoadEvent(loadPlaces) ;
//addLoadEvent(loadGoogleMap) ;
addLoadEvent(loadSelect) ;

function printDate(strDate) {
    var dDate = new Date(parseInt(strDate)) ;
    var dateOut = dDate.toString() ;
    return dateOut.substring(0, dateOut.length - 24) ;
}

function trimAlbums(min, max) {
    $('.selectAlbum').each(function(index) {
        if (parseInt($(this).attr( 'rel'))  < min ) {
           $(this).hide() ;
        } else if (parseInt($(this).attr( 'rel'))  > max) {
            $(this).hide() ;
        } else {
            $(this).show() ;
        }
    });

}

var NOW = new Date().getTime() ;
var sliderOption = {
  range: true,
  min: 0,
  max: NOW,
  step: 100000000,
  slide: function(event, ui) {
      $("#fromDate").text(printDate(ui.values[0]));
      $("#toDate").text(printDate(ui.values[1]));
      trimAlbums(ui.values[0], ui.values[1]) ;
  }
} ;