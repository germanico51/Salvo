

var posiblesTiros = [];
$(function () {

loadData();

drawTable("ships");
drawTable("salvos");

});

function getParameterByName(name) {
  var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
  return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
}
var urlParams = new URLSearchParams(window.location.search);
var gamePlayerId = Number(urlParams.get('gp'));

function loadData() {
  $.get('/api/game_view/' + getParameterByName('gp'))
    .done(function (data) {
      var playerInfo;
      if (data.gamePlayers.length == 2) {
      if (data.gamePlayers[0].id == getParameterByName('gp'))
        playerInfo = [data.gamePlayers[0].player, data.gamePlayers[1].player];
      else
        playerInfo = [data.gamePlayers[1].player, data.gamePlayers[0].player];

      $('#playerInfo').text(playerInfo[0].username + '(you) vs ' + playerInfo[1].username);
        } else if (data.gamePlayers.length == 1) {
                playerInfo = data.gamePlayers[0].player.username;
                console.log(playerInfo);

                $('#playerInfo').text(playerInfo + '(you) vs "Waiting player...."')
              };

      data.ships.forEach(function (shipPiece) {
        var tipoColor= shipPiece.type;

        shipPiece.shipLocations.forEach(function (shipLocation) {

         $('#ships' + shipLocation.toLowerCase()).addClass(tipoColor);

        });
      });
       if (data.ships.length > 0) {

              $("#mostrarBarcos").hide();
            } else {

              $("#ships").hide();
              $("#salvos").hide();
              $("#gameBoard").hide();
               $("#bshoot").hide();

            }
                  const logueado = data.gamePlayers.filter(player => gamePlayerId === player.id)[0].player.id


                  let salvosDisparados = data.salvoes.filter(jugador => (jugador.player === logueado))

                  salvosDisparados.forEach(shoot => {

                    shoot.salvoLocations.forEach(hit => {

                      let shootLocation = $('#salvos' + hit.toLowerCase());

                      shootLocation.addClass('hit-cell').append(shoot.turn);

                      if (shootLocation.hasClass("ship-piece")) {
                        console.log("hiteado");
                        /* shootLocation.addClass('ship-piece-hited') */
                      }

                      console.log(shoot.turn);


                    })
                  })

    })

    .fail(function (jqXHR, textStatus) {
      alert('Failed: ' + textStatus);
    });
}

function drawTable(id) {

  var tabla = document.createElement("table");
  tabla.classList.add("table");
  var tblBody = document.createElement("tbody");
  for (var i = 0; i < 11; i++) {

    var columns = document.createElement("tr");
    for (var j = 0; j <= 10; j++) {

      var cell = document.createElement("td");
      if (i === 0) {
        var columnText = document.createTextNode(j)
        cell.appendChild(columnText);
      };
      if (j === 0) {
        var rowText = document.createTextNode(String.fromCharCode(i + 64));
        cell.appendChild(rowText);
      };

      if (j === 0 && i === 0) {
        cell.textContent = "";
      }
      columns.appendChild(cell);

      cell.id = id + String.fromCharCode(i + 64).toLowerCase() + j;

      cell.classList.add("cellBorder");

        if (id == "salvos") {
              cell.addEventListener("click", function () {
                let shot = this.id.slice(6).toLocaleUpperCase();

                if (this.classList.contains("hit-cell")) {
                  alert("ya has disparado acá")
                }
                if (!posiblesTiros.includes(shot) && !this.classList.contains("hit-cell")) {
                  this.classList.add("yellow"),
                    posiblesTiros.push(shot),
                    console.log(posiblesTiros);
                } else if (posiblesTiros.includes(shot)) {
                  this.classList.remove("yellow");
                  let i = posiblesTiros.indexOf(shot)
                  posiblesTiros.splice(i, 1);
                  console.log(posiblesTiros)
                }
              })
            }

    }

    tblBody.appendChild(columns);


    tabla.appendChild(tblBody);
    document.getElementById(id).append(tabla);
    document.getElementById(id).append(tblBody);

  }
}

const obtenerPosicion = function (shipType) {
    var ship = new Object();
    ship["name"] = $("#" + shipType).attr('id');
    ship["x"] = $("#" + shipType).attr('data-gs-x');
    ship["y"] = $("#" + shipType).attr('data-gs-y');
    ship["width"] = $("#" + shipType).attr('data-gs-width');
    ship["height"] = $("#" + shipType).attr('data-gs-height');
    ship["positions"] = [];
    if (ship.height == 1) {
        for (i = 1; i <= ship.width; i++) {
            ship.positions.push(String.fromCharCode(parseInt(ship.y) + 65) + (parseInt(ship.x) + i))
        }
    } else {
        for (i = 0; i < ship.height; i++) {
            ship.positions.push(String.fromCharCode(parseInt(ship.y) + 65 + i) + (parseInt(ship.x) + 1))
        }
    }
    var objShip = new Object();
    objShip["shipType"] = ship.name;
    objShip["locations"] = ship.positions;
    return objShip;
}
function addShips() {
    var carrier = obtenerPosicion("carrier")
    var patrol = obtenerPosicion("patrol_boat")
    var battleship = obtenerPosicion("battleship")
    var submarine = obtenerPosicion("submarine")
    var destroyer = obtenerPosicion("destroyer")

    $.post({
            url: "/api/games/players/" + getParameterByName('gp') + "/ships",
            data: JSON.stringify([carrier, patrol, battleship, submarine, destroyer]),
            dataType: "text",
            contentType: "application/json"
        })
        .done(function (response, status, jqXHR) {
            alert("ships guardados: " + response);
            setTimeout(function () {
                 window.location.href = '/web/game.html?gp='+getParameterByName('gp');
            }, 2000);
        })
        .fail(function (jqXHR, textStatus, httpError) {
            console.log("ships no guardados: " + textStatus + " " + httpError);
        })
}
function shoot() {
  shoots= posiblesTiros;
  if (posiblesTiros.length != 5){
    alert ("solo puedes hacer hasta 5 disparos, revisa tus posibles disparos")
  } else {
    console.log(getParameterByName('gp'));

    $.post({
      url: "/api/games/players/" + getParameterByName('gp') + "/salvoes",
     data: JSON.stringify({
       salvoLocations: shoots
          }),
      dataType: "text",
      contentType: "application/json"
    })
    .done(function (response, status, jqXHR) {

      alert("Shoots added: " + response);
      location.reload();
      posiblesTiros = [];
      console.log("var posiblesTiros es: "+ posiblesTiros);

    })
    .fail(function (jqXHR, status, httpError) {
      alert("Failed to add salvoes: " + status + " " + httpError);
    })

}}

