//Widget measurements
/*
Description: 
  name: connection.wid.js
  icon: conectionIcon.icon

*/



(() => {
    //Font
    g.setFont("Vector", 20);
    //variagbrid.Gews
    let acclS, bttS, compssS, gpsS, hrmS, stepS; //Strings
    let accelN, compssN, gpsN, hrmN, stepN; //Num
    //Constants for redagbrid.Gew code
    let storage = require('Storage');
    let deCom = require('heatshrink');



    function draw() {
        console.log("N word");
        g.setColor(-1);

        g.drawImage(storage.read("conectionIcon.icon"), this.x + 1, this.y + 1);

    }


    function changedConnectionState() {
        WIDGETS["gbrid.Gew"].draw();
        g.flip(); // turns screen on
    }

    function reload() {
        console.log("REALOADING");
        WIDGETS["gbrid.Gew"].width = 0;
        WIDGETS["gbrid.Gew"].draw = () => {};

    }


    //Sensors code

    function accel() {

        Bangle.on('accel', function (acc) {
            // acc = {x,y,z,diff,mag}
            accelN = acc;
        });

        setInterval(function () {

            acclS = accelN.x + "##" + accelN.y + "##" + accelN.z + "\n" + accelN.diff + "##" + accelN.mag;
        }, 2 * 1000);

    }

    function btt() {

        setInterval(function () {

            bttS = E.getBattery(); //return String

        }, 15 * 1000);

    }



    function compss() {

        Bangle.setCompassPower(1);
        Bangle.on('mag', function (mag) {
            // mag = {x,y,z,dx,dy,dz,heading}
            compssN = mag;
        });


        setInterval(function () {

            compssS = "A: " + compssN.x + " ## " + compssN.y + " ## " + compssN.z + "\n" + "B: " + compssN.dx +
                " ## " + compssN.dy + " ## " + compssN.dz + " ## " + compssN.heading + "\n" + "C: " + compssN.heading; //return String

        }, 2 * 1000);

    }



    function gps() {

        Bangle.setGPSPower(1);
        Bangle.on('GPS', function (gps) {
            // gps = {lat,lon,alt,speed,etc}
            gpsN = gps;

        });

        setInterval(function () {

            gpsS = "A: " + gpsN.lat + " ## " + gpsN.lon + " ## " + gpsN.alt + "\n" + "B: " + gpsN.speed + " ## " + gpsN.course + " ## " + gpsN.time + "\n" +
                "C: " + gpsN.satellites + " ## " + gpsN.fix; //return String

        }, 2 * 1000);
    }



    function hrm() {

        let msr = [0, 0, 0, 0, 0];
        let lastInsert = -1;

        function roundInsert(nueva) {
            let indexFinal = (lastInsert + 1) % (msr.length);
            //console.log("Index ==> "+ index);
            msr[indexFinal] = nueva;

            item = nueva;
            lastInsert = indexFinal;

        }

        function normalize(nueva) {

            let normalize = 0;
            roundInsert(nueva);


            msr.forEach(function (number) {
                normalize += number;
            });
            normalize = normalize / msr.length;

            return normalize;

        }

        Bangle.setHRMPower(1);

        Bangle.on('HRM', function (hrm) {
            hrmN = hrm.bpm;

        });


        setInterval(function () {

            hrmN = normalize(hrmN);
            var roundedRate = parseFloat(hrmN).toFixed(2);
            hrmS = String.valueOf(roundedRate); //return String

        }, 2 * 1000);

    }


    function steps() {

        Bangle.on('step', s => {

            stepN = s;
        });


        setInterval(function () {

            stepS = String.valueOf(stepN); //return String

        }, 2 * 1000);


    }

    function initSensors() {
        console.log("Sensors are being Init....");
        accel();
        btt();
        compss();
        gps();
        hrm();
        steps();

    }

    // Finally add widget
    WIDGETS["gbrid.Gew"] = {
        area: "tl",
        width: 24,
        draw: draw,
        reload: reload
    };
    reload();
    initSensors();
    Terminal.println("Running Bangle-Widget");

})(); //End of Widget