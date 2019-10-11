var app = new Vue ({
    el:"#app",
    data:{
    players:[],
    games: [],
    currentUser: ""
    }
})

$(function () {
  loadData();
    cargarUsuario();
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
 var table= "";
     table = '<thead class="thead-dark"><tr><th>Date of creation</th><th>Player 1</th><th>Player 2 </th></tr></thead>';
    table +='<tbody>';
    games.games.forEach (g => {
    table += '<tr>';
   table += '<td>'+ new Date(g.created).toLocaleString()+'</td>';
    table += '<td>'+g.gamePlayers[0].player.userName+'</td>';
    table += '<td>'+g.gamePlayers[1].player.userName+'</td>';

    });
    table +='</tbody>';

    gameList.innerHTML= table;
    }
function cargarUsuario() {
  $.get("/api/games")
    .done(function (data) {
      app.games = data.games;
      app.currentUser = data.player.email;
      console.log(data.player.email)
    })
    .fail(function (jqXHR, textStatus) {
      alert('Failed: ' + textStatus);
    })
}

function register(){
    var form = document.getElementById("register-form");
    $.post("/api/players", {
        email: form["username"].value,
        password: form["password"].value
    })
    .done(function () {
            alert('Success');
          })
    .fail(function (jqXHR, textStatus) {
            alert('Failed: ' + jqXHR.status);
          });
}

 function login() {
      if (app.currentUser == "Guest") {
         var form = document.getElementById('login-form')
         $.post("/api/login", {
             username: form["username"].value,
             password: form["password"].value
           })
           .done(setTimeout(function(){ cargarUsuario(); }, 1000))
           .fail(function (jqXHR, textStatus) {
             alert('Failed: ' + jqXHR.status);
           });
       } else {
         console.log("Ya existe un usuario")
       }
     }

function logout() {
  $.post("/api/logout")
    .done(window.location.replace("games.html"))
    .fail(function (jqXHR, textStatus) {
      alert('Failed: ' + textStatus);
    });
}