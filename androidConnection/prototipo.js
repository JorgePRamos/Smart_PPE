WIDGETS = {};
(() => {
  
  // activity reporting
  var currentSteps = 0, lastSentSteps=0;
  var activityInterval;
  var hrmTimeout;

  function settings() {
    let settings = require('Storage').readJSON("gbridge.json", true) || {};
    if (!("showIcon" in settings)) {
      settings.showIcon = true;
    }
    return settings;
  }

  //Send to screen
  function gbSend(message) {
    Bluetooth.println("");
    Bluetooth.println(JSON.stringify(message));
  }
  
  //HRM Reading
  function handleActivityEvent(event) {
    var s = settings();
    // handle setting activity interval
    if (s.activityInterval===undefined ||
        s.activityInterval<30)
      s.activityInterval = 3*60; // 3 minutes default
    if (event.int) {
      if (event.int<30) event.int = 30; // min 30 secs
      s.activityInterval = event.int;
      require('Storage').writeJSON("gbridge.json", s);
    }
    // set up interval/HRM to handle activity data
    var interval = s.activityInterval;
    var realtime = event.hrm || event.stp;
    if (activityInterval)
      clearInterval(activityInterval);
    activityInterval = undefined;
    if (s.hrm) Bangle.setHRMPower(1);
    if (s.hrm) {
      if (realtime) {
        // if realtime reporting, leave HRM on and use that to trigger events
        hrmTimeout = undefined;
      } else {
        // else trigger it manually every so often
        hrmTimeout = 5;
        activityInterval = setInterval(function() {
          hrmTimeout = 5;
          Bangle.setHRMPower(1);
        }, interval*1000);
      }
    } else {
      // no HRM - manually push data
      if (realtime) interval=10;
      activityInterval = setInterval(function() {
        sendActivity(-1);
      }, interval*1000);
    }
  }
 var _GB = global.GB;
  global.GB = (event) => {
    switch (event.t) {
      case "notify":
      case "notify-":
        if (event.t === "notify") {
          require("notify").show(prettifyNotificationEvent(event));
          Bangle.buzz();
        } else { // notify-
          require("notify").hide(event);
        }
        break;
      case "musicinfo":
        state.musicInfo = event;
        updateMusic({on: false});
        break;
      case "musicstate":
        if (state.music !== event.state) {
          state.music = event.state;
          updateMusic({on: true});
        }
        break;
      case "call":
        var note = { size: 55, title: event.name, id: "call",
                     body: event.number, icon:require("heatshrink").decompress(atob("jEYwIMJj4CCwACJh4CCCIMOAQMGAQMHAQMDAQMBCIMB4PwgHz/EAn4CBj4CBg4CBgACCAAw="))};
        if (event.cmd === "incoming") {
          require("notify").show(note);
          Bangle.buzz();
        } else if (event.cmd === "start") {
          require("notify").show(Object.assign(note, {
            bgColor : "#008000", titleBgColor : "#00C000",
            body: "In progress: "+event.number}));
        } else if (event.cmd === "end") {
          require("notify").show(Object.assign(note, {
            bgColor : "#800000", titleBgColor : "#C00000",
            body: "Ended: "+event.number}));
          setTimeout(function() {
            require("notify").hide({ id: "call" });
          }, 2000);
        }
        break;
      case "find":
        if (state.find) {
          clearInterval(state.find);
          delete state.find;
        }
        if (event.n)
          state.find = setInterval(_=>{
            Bangle.buzz();
            setTimeout(_=>Bangle.beep(), 1000);
          },2000);
        break;
      case "act":
        handleActivityEvent(event);
        break;
    }
    if(_GB)setTimeout(_GB,0,event);
  };

  function draw() {
    g.setColor(-1);
    if (NRF.getSecurityStatus().connected)
      g.drawImage(require("heatshrink").decompress(atob("i0WwgHExAABCIwJCBYwJEBYkIBQ2ACgvzCwoECx/z/AKDD4WD+YLBEIYKCx//+cvnAKCBwU/mc4/8/HYv//Ev+Y4EEAePn43DBQkzn4rCEIoABBIwKHO4cjmczK42I6mqlqEEBQeIBQaDED4IgDUhi6KaBbmIA==")), this.x + 1, this.y + 1);
    else
      g.drawImage(require("heatshrink").decompress(atob("i0WwQFC1WgAgYFDAgIFClQFCwEK1W/AoIPB1f+CAMq1f7/WqwQPB/fq1Gq1/+/4dC/2/CAIaB/YbBAAO///qAoX/B4QbBDQQ7BDQQrBAAWoIIIACIIIVC0ECB4cACAZiBAoRtCAoIDBA")), this.x + 1, this.y + 1);
  }

  function changedConnectionState() {
    WIDGETS["gbridgew"].draw();
    g.flip(); // turns screen on
  }

  function reload() {
    //NRF.removeListener("connect", changedConnectionState);
    //NRF.removeListener("disconnect", changedConnectionState);
    if (settings().showIcon) {
      WIDGETS["gbridgew"].width = 24;
      WIDGETS["gbridgew"].draw = draw;
      //NRF.on("connect", changedConnectionState);
     // NRF.on("disconnect", changedConnectionState);
    } else {
      WIDGETS["gbridgew"].width = 0;
      WIDGETS["gbridgew"].draw = ()=>{};
    }
  }

  function sendBattery() {
    gbSend({ t: "status", bat: E.getBattery() });
  }

  // Send a summary of activity to Gadgetbridge
  function sendActivity(hrm) {
    var steps = currentSteps - lastSentSteps;
    lastSentSteps = 0;
    gbSend({ t: "act", stp: steps, hrm:hrm });
  }

  
  // Battery monitor
  NRF.on("connect", () => setTimeout(sendBattery, 2000));
  setInterval(sendBattery, 10*60*1000);
  sendBattery();
  
  // Activity monitor
  Bangle.on("step", s => {
    if (!lastSentSteps)
      lastSentSteps = s-1;
    currentSteps = s;
  });
  Bangle.on('HRM',function(hrm) {
    var ok = hrm.confidence>80;
    if (hrmTimeout!==undefined) hrmTimeout--;
    if (ok || hrmTimeout<=0) {
      if (hrmTimeout!==undefined)
        Bangle.setHRMPower(0);
      sendActivity(hrm.confidence>20 ? hrm.bpm : -1);
    }
  });
  handleActivityEvent({}); // kicks off activity reporting

  // Finally add widget
  WIDGETS["gbridgew"] = {area: "tl", width: 24, draw:draw, reload: reload};
  reload();
  
  
  Terminal.println("Hello World");
})();