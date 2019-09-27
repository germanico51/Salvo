
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