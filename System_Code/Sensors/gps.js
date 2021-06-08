//240x240px


/*
{ "lat": number,      // Latitude in degrees
  "lon": number,      // Longitude in degrees
  "alt": number,      // altitude in M
  "speed": number,    // Speed in kph
  "course": number,   // Course in degrees
  "time": Date,       // Current Time (or undefined if not known)
  "satellites": 7,    // Number of satellites
  "fix": 1            // NMEA Fix state - 0 is no fix
}

*/
g.setFont("Vector", 14);
g.clear();

let loc;
console.log("Empezando....");
g.drawString(("Empezando...."), 10, 120);
g.setColor(0, 1, 1);



Bangle.setGPSPower(1);
Bangle.on('GPS', function (gps) {
    // gps = {lat,lon,alt,speed,etc}
    loc = gps;

});


setInterval(function () {
    g.clear();


    console.log(("A: " + loc.lat + " ## " + loc.lon + " ## " + loc.alt));
    console.log(("B: " + loc.speed + " ## " + loc.course + " ## " + loc.time));

    console.log(("C: " + loc.satellites + " ## " + loc.fix));
    console.log("------------------------------------");

    g.drawString(("A: " + loc.lat + " ## " + loc.lon + " ## " + loc.alt), 10, 120);
    g.drawString(("B: " + loc.speed + " ## " + loc.course + " ## " + loc.time), 10, 140);
    g.drawString(("C: " + loc.satellites + " ## " + loc.fix), 10, 160);

}, 2 * 1000);