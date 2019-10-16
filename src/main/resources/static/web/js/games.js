var app = new Vue ({
    el:"#app",
    data:{
    games: [],
    players:[],
    user: ""
    }
})

$(function () {
  loadData();
  getUser();
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
function getUser() {
  $.get("/api/games")
    .done(function (data) {

      app.user = data.player.email;
      console.log(data.player.email)
    })
    .fail(function (jqXHR, textStatus) {
      alert('Failed: ' + textStatus);
    })
}
 function login() {
      if (app.user == "Guest") {
         var form = document.getElementById('login-form')
         let user = form["username"].value;
         let pass = form["password"].value;
         $.post("/api/login", {
             username:user,
             password:pass
           })
           .done(setTimeout(function(){ getUser(); }, 1000))
           .fail(function (jqXHR, textStatus) {
             alert('Failed: ' + jqXHR.status);
           });
       }
     }

function logout() {
  $.post("/api/logout")
    .done(window.location.replace("games.html"))
    .fail(function (jqXHR, textStatus) {
      alert('Failed: ' + textStatus);
    });
}

function signUp(){
    var form = document.getElementById("register-form");
     let user = form["username"].value;
     let pass = form["password"].value;

    $.post("/api/players", {
        email: user,
        password: pass
    }).done(function () {
            alert('Success');
            location.reload();
          })
    .fail(function (jqXHR, textStatus) {
            alert('Failed: ' + jqXHR.status);
          });
}

