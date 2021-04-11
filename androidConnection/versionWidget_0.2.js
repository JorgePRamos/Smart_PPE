//Widget measurements
/*
Description: 
  name: connection.wid.js
  icon: conectionIcon.icon

*/



(() => {
    //Font
    g.setFont("Vector", 20);
    //variabangle.Sensorss
    let acclS, bttS, compssS, gpsS, hrmS, stepS; //Strings
    let accelN, compssN, gpsN, hrmN, stepN; //Num

    let data = [0, 0, 0, 0, 0, 0];
    //Constants for redabangle.Sensors code
    let storage = require('Storage');
    let deCom = require('heatshrink');




    //Sensors code

    function accel() {

        Bangle.on('accel', function (acc) {
            // acc = {x,y,z,diff,mag}
            accelN = acc;
        });

        setInterval(function () {

            acclS = accelN.x + "##" + accelN.y + "##" + accelN.z + "\n" + accelN.diff + "##" + accelN.mag;
            data[3] = accelN;
        }, 2 * 1000);

    }

    function btt() {

        setInterval(function () {

            bttS = E.getBattery(); //return String
            data[2] = E.getBattery();
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
            data[4] = compssN;
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
            data[5] = gpsN;
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




        setInterval(function () {

            if (!isNaN(hrmN)) {


                hrmN = normalize(hrmN);
                var roundedRate = parseFloat(hrmN).toFixed(2);
                hrmS = String.valueOf(roundedRate); //return String
                //console.log("array----->" + msr);
                data[0] = roundedRate;

            }





        }, 2 * 1000);

    }


    function steps() {

        Bangle.on('step', s => {

            stepN = s;
        });


        setInterval(function () {

            stepS = String.valueOf(stepN); //return String
            data[1] = stepN;
        }, 2 * 1000);


    }

    function initSensors() {

        //need power control
        Bangle.setHRMPower(1);

        Bangle.on('HRM', function (hrm) {
            hrmN = hrm.bpm;


        });
        console.log("Sensors are being Init....");
        accel();
        btt();
        compss();
        gps();
        hrm();
        steps();

    }



    function draw() {
        g.setColor(-1);
        g.drawImage(storage.read("conectionIcon.icon"), this.x + 1, this.y + 1);

    }


    // Finally add widget
    WIDGETS["bangle.Sensors"] = {
        area: "tl",
        width: 10,
        draw: draw,
    };

    initSensors();
    // Bangle.drawWidgets();
    Terminal.println("Running Bangle-Widget");

    setInterval(function () {
        //console.log("---------------------------------------------------------------");
        //console.log(data);
        //Bluetooth.println(data[0]);
        var measurement = {hrm: data[0],step: data[1],batt: data[2],acc: data[3],com: data[4],gps: data[5]}
        Bluetooth.println(JSON.stringify(measurement)+"#");
    }, 5 * 1000);
})(); //End of Widget