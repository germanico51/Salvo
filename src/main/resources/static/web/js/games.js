var app = new Vue ({
    el:"#app",
    data:{
    players:[]
    }
})

$(function () {
  loadData();
});

function getParameterByName(name) {
  var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
  return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
}
function loadData() {
  $.get('/api/leaderboard')
  .done(function (data){ app.players = data; })
  .fail(function (jqXHR, textStatus) {
        alert('Failed: ' + textStatus);
      });
      }


window.addEventListener('load', function () {
    $.get("/api/games")
    .done(function (games) {
         loadGameList(games);
        })

});
var gameList = document.getElementById("gameList");

function loadGameList(games){
    var list = "";
    games.forEach(g => {
              list +='<li>';
              list +=new Date(g.created).toLocaleString();
              list += ' ' + g.gamePlayers.map(gp => gp.player.email).join(', ');
              list +='</li>';
        });
    gameList.innerHTML = list;
}