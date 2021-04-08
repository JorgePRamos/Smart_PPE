//console.log("Running..545445454454...");
//src="https://www.puck-js.com/puck.js";
console.log("Updated back2");
let input;
// When we click the connect button...
var connection;
//let d = document.getElementById("btnConnect")
document.getElementById("btnConnect").addEventListener("click", function() {
  // disconnect if connected already
  if (connection) {
    connection.close();
    connection = undefined;
  }
  // Connect
  Puck.connect(function(c) {
    
  

    var buf = "";
    c.on("data", function(d) {
      buf += d;
      var l = buf.split("\n");
      buf = l.pop();
      l.forEach(onLine);
    });
 

    });
  });
  function onLine(line) {
    input = line;
    /*
    console.log("################");
    console.log("RECEIVED:"+line);*/
    console.log(line);
    console.log("################ ----->");
    

  }
