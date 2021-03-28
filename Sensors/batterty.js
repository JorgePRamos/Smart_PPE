//240x240px

g.setFont("Vector", 14);
g.clear();


console.log("Empezando....");
g.drawString(("Empezando...."), 10, 120);
g.setColor(0, 1, 1);


setInterval(function () {
    g.clear();


    console.log(E.getBattery());

    g.drawString((E.getBattery()), 10, 160);

}, 15 * 1000);