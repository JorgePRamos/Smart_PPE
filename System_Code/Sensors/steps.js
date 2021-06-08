var rate = 0;

g.setFont("Vector", 20);
g.clear();




var currentSteps = 0,
    lastSentSteps = 0;


console.log("Empezando....");
g.drawString(("Empezando...."), 10, 120);
g.setColor(0, 1, 1);
Bangle.setHRMPower(1);

Bangle.on('step', s => {

    currentSteps = s;
});


setInterval(function () {
    g.clear();

    console.log(currentSteps);

    g.drawString(("STEPS-----> " + currentSteps), 10, 120);
}, 2 * 1000);