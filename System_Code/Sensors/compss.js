//240x240px


/*
x/y/z raw x,y,z magnetometer readings
dx/dy/dz readings based on calibration since magnetometer turned on
heading in degrees based on calibrated readings (will be NaN if magnetometer hasn't been rotated around 360 degrees)

*/
g.setFont("Vector", 14);
g.clear();

let com;
console.log("Empezando....");
g.drawString(("Empezando...."), 10, 120);
g.setColor(0, 1, 1);



Bangle.setCompassPower(1);
Bangle.on('mag', function (mag) {
    // mag = {x,y,z,dx,dy,dz,heading}
    com = mag;
});


setInterval(function () {
    g.clear();


    console.log(("A: " + com.x + " ## " + com.y + " ## " + com.z));
    console.log(("B: " + com.dx + " ## " + com.dy + " ## " + com.dz + " ## " + com.heading));

    console.log(("C: " + com.heading));
    console.log("------------------------------------");


    g.drawString((com.x + " ## " + com.y + " ## " + com.z), 10, 120);

    g.drawString((com.dx + " ## " + com.dy + " ## " + com.dz + " ## " + com.heading), 10, 140);
    g.drawString((com.heading), 10, 160);




}, 2 * 1000);