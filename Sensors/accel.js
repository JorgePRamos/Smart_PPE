//240x240px

/*

x is X axis (left-right) in g
y is Y axis (up-down) in g
z is Z axis (in-out) in g
diff is difference between this and the last reading in g
mag is the magnitude of the acceleration in g

*/

g.setFont("Vector", 13);
g.clear();

let accel;
console.log("Empezando....");
g.drawString(("Empezando...."), 10, 120);
g.setColor(0, 1, 1);
Bangle.on('accel', function (acc) {
        // acc = {x,y,z,diff,mag}
        accel = acc;

    }

);


setInterval(function () {
    g.clear();


    console.log("{" + accel.diff + "  ##  " + accel.mag + "}");
    console.log("");
    g.drawString(("{" + accel.diff + "  ##  " + accel.mag + "}"), 10, 120);
}, 2 * 1000);