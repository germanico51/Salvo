
$(function () {
  loadData();
});

function getParameterByName(name) {
  var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
  return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
}

function loadData() {
  $.get('/api/game_view/' + getParameterByName('gp'))
    .done(function (data) {
      console.log(data);
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

                $('#playerInfo').text(playerInfo + '(you) vs "Esperando Jugador"')
              };

      data.ships.forEach(function (shipPiece) {
        shipPiece.shipLocations.forEach(function (shipLocation) {
         $('#B_' + shipLocation).addClass('ship-piece');
          let turnHitted = isHit(shipLocation,data.salvoes,playerInfo[0].id)
          if(turnHitted >0){
            $('#B_' + shipLocation).addClass('ship-piece-hited');
            $('#B_' + shipLocation).text(turnHitted);

          }
          else
            $('#B_' + shipLocation).addClass('ship-piece');
        });
      });
      data.salvoes.forEach(function (salvo) {
        console.log(salvo);
        if (playerInfo[0].id === salvo.player) {
          salvo.salvoLocations.forEach(function (location) {

            $('#S_' + location).addClass('salvo');
          });
        } else {
          salvo.salvoLocations.forEach(function (location) {
            $('#_' + location).addClass('salvo');
          });
        }
      });
    })
    .fail(function (jqXHR, textStatus) {
      alert('Failed: ' + textStatus);
    });
}


function isHit(shipLocation,salvoes,playerId) {
  var hit = 0;
  salvoes.forEach(function (salvo) {
    if(salvo.player != playerId)
      salvo.salvoLocations.forEach(function (location) {
        if(shipLocation === location)
          hit = salvo.turn;
      });
  });
  return hit;
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
      console.log(carrier);
         console.log(getParameterByName('gp'));
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