/**
 * @author Jorge Perez Ramos
 * @description Biometric collection and integrated hardware control.
 * @module Widget
 */
/**
 * @file Widget.js is the root file for this wearable app
 * @author Jorge Perez Ramos
 */

/**
 * @description Sets SmartWatch face font
 * @param  {String} "Vector"
 * @param  {Int} 20
 */
g.setFont("Vector", 20);

/**
 * @description Sensorss String variables
 * @param {String}
 */
let acclS, bttS, compssS, gpsS, hrmS, stepS; //Strings
/**
 * @description Sensorss  variables
 * @param {Number}
 */
let accelN, compssN, gpsN, hrmN, stepN; //Num
/**
 * @description Final messurement output array.
 * @param {Array}
 */
let data = [0, 0, 0, 0, 0, 0];
/**
 * @description Constants for readable.
 * 
 */
/**@description Asset access
 * @param  {String} 'Storage'
 */
let storage = require('Storage');
/**
 * @description Asset decompression
 * @param  {String} 'heatshrink'
 */
let deCom = require('heatshrink');
/**
 * @description  Sets signal for the integrated accelerometer getting {x, y, z, diff, mag}
 * @param  {State} Bangle.on'accel'
 * @param  {accelerometer} function acc
 * @param  {Num} accelN=acc
 * @param  {Num} data[3]=accelN
 * @param  {Num} setInterval
 * @param  {String} acclS
 */
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
/**
 * @description Gets battery state and charge
 * @param  {Num} setInterval
 * @param  {String} bttS
 * @param  {Num} bttN
 * @param  {Num} data[2]=bttN
 */
function btt() {

    setInterval(function () {
        bttS = E.getBattery(); //return String
        data[2] = E.getBattery();
    }, 15 * 1000);

}


/**
 * @description Sets signal for the integrated compass getting {x, y, z, dx, dy, dz, heading}.
 * @param  {State} Bangle.setCompassPower
 * @param  {Int} Bangle.on
 * @param  {Interval} function
 * @param  {Number} compssN=mag
 * @param  {Number} data[4]=compssN
 */
function compss() {

    Bangle.setCompassPower(1);
    Bangle.on('mag', function (mag) {
        // mag = {x,y,z,dx,dy,dz,heading}
        compssN = mag;
    });


    setInterval(function () {

        compssS = "A: " + compssN.x + " ## " + compssN.y + " ## " + compssN.z + "\n" +
            "B: " + compssN.dx + " ## " + compssN.dy + " ## " + compssN.dz + " ## " + "\n" +
            "C: " + compssN.heading; //return String
        data[4] = compssN;
    }, 2 * 1000);

}


/**
 * @description Sets signal for the integrated GPS getting {lat, lon, alt, speed, course, time, satellites, fix}.
 * @param  {Int} Bangle.setGPSPower
 * @param  {State} Bangle.on
 * @param  {gps} function
 * @param  {Int} gpsN
 * @param  {Number} data[5]=gpsN
 */
function gps() {

    Bangle.setGPSPower(1);
    Bangle.on('GPS', function (gps) {
        // gps = {lat,lon,alt,speed,etc}
        gpsN = gps;

    });

    setInterval(function () {

        gpsS = "A: " + gpsN.lat + " ## " + gpsN.lon + " ## " + gpsN.alt + "\n" + "B: " + gpsN.speed + " ## " + gpsN.course + " ## " + gpsN.time + "\n" +
            "C: " + gpsN.satellites + " ## " + gpsN.fix; //return String
        // work out how to display the current time
        var d = new Date();
        var year = d.getFullYear();

        var month = d.getMonth() + 1;
        var finalMonth = 0;
        if (month < 10) {
            finalMonth = "0" + month;
        } else {
            finalMonth = month;
        }
        var day = d.getDate();
        var finalDay = 0;
        if (day < 10) {
            finalDay = "0" + day;
        } else {
            finalDay = day;
        }
        var h = d.getHours(),
            m = d.getMinutes();
        var finalh = 0;
        if (h < 10) {
            finalh = "0" + h;
        } else {
            finalh = h;
        }
        var finalM = 0;
        if (m < 10) {
            finalM = "0" + m;
        } else {
            finalM = m;
        }

        var s = d.getSeconds();
        var finalS = 0;
        if (s < 10) {
            finalS = "0" + s;
        } else {
            finalS = s;
        }
        var z = d.getMilliseconds();
        var zFinal = new String(z);
        zFinal = zFinal.replace('.', '');
        var completeTime = year + "-" + finalMonth + "-" + finalDay + "T" + finalh + ":" + finalM + ":" + finalS + "." + z + "Z";
        var time = h + ":" + ("0" + m).substr(-2);
        gpsN.time = completeTime;
        data[5] = gpsN;
    }, 2 * 1000);
}

/**
 * @description Sets signal for the integrated HRM monitor getting HRM.
 * @param  {Int} Bangle.setHRMPower
 * @param  {Number} data[0]=roundedRate
 * @param  {State} Bangle.on
 */
function hrm() {

    let msr = [0, 0, 0, 0, 0];
    let lastInsert = -1;
    /**
     * @description Round insert in HRM filter.
     * @param  {HRM} nueva
     * @param  {Number} letindexFinal=lastInsert+1
     */
    function roundInsert(nueva) {
        let indexFinal = (lastInsert + 1) % (msr.length);
        msr[indexFinal] = nueva;

        item = nueva;
        lastInsert = indexFinal;

    }
    /**
     * @description Normalization of HRM filter.
     * @param  {HRMFilter} nueva
     * @param  {Number} normalize
     * @param  {Bumber} {normalize+=number;}
     */
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


/**
 * @description Sets signal for the integrated Step sensor getting back the number of steps.
 * @param  {Int} Bangle.on
 * @param  {State} Bangle.setStepPower
 * @param  {Number} s=stepN
 * @param  {Number} setIntervalfunction
 * @param  {String} stepS=String.valueOf(stepN)
 * @param  {Number} data[1]=stepN
 */
function steps() {

    Bangle.on('step', s => {

        stepN = s;
    });


    setInterval(function () {

        stepS = String.valueOf(stepN); //return String
        data[1] = stepN;
    }, 2 * 1000);


}
/**
 * @description Initialize all wearable sensors.
 * @param  {State} Bangle.Init
 * @param  {function} btt()
 * @param  {function} compss()
 * @param  {function} gps()
 * @param  {function} hrm()
 * @param  {function} steps()
 */
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


/**
 * @description Draw widget icon.
 * @param  {Int} g.setColor
 * @param  {xAxes} this.x+1
 * @param  {yAxes} this.y+1
 */
function draw() {
    g.setColor(-1);
    g.drawImage(storage.read("conectionIcon.icon"), this.x + 1, this.y + 1);

}



/**
 * @description Widget addition.
 */
WIDGETS["bangle.Sensors"] = {
    area: "tl",
    width: 10,
    draw: draw,
};

initSensors();
// Bangle.drawWidgets();
Terminal.println("Running Bangle-Widget");
/**
 * @description Convert array of measurements to Json and send via BLE.
 * @param  {Num} hrm
 * @param  {Num} step
 * @param  {Num} batt
 * @param  {Num} acc
 * @param  {Num} com
 * @param  {Num} gps
 */
setInterval(function () {
    var measurement = {
        hrm: data[0],
        step: data[1],
        batt: data[2],
        acc: data[3],
        com: data[4],
        gps: data[5]
    };

    Bluetooth.println(JSON.stringify(measurement) + "#");

}, 5 * 1000);