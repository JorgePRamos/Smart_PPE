//240x240px


//  var remainder = index % array.length;
var rate = 0;

g.setFont("Vector", 20);
g.clear();


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





console.log("Empezando....");
g.drawString(("Empezando...."), 10, 120);
g.setColor(0, 1, 1);
Bangle.setHRMPower(1);

Bangle.on('HRM', function (hrm) {
  rate = hrm.bpm;

});


setInterval(function () {
  g.clear();
  rate = normalize(rate);
  console.log(rate);
  var x = parseFloat(rate).toFixed(2);
  g.drawString(("RATE-----> " + x), 10, 120);
}, 2 * 1000);