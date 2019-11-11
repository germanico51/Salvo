var app = new Vue ({
    el:"#app",
    data:{
    players:[],
    user: ""
    }
})

function loadData() {
  $.get('/api/leaderboard')
  .done(function (data){ app.players = data; })
  .fail(function (jqXHR, textStatus) {
        alert('Failed: ' + textStatus);
      });
      }

window.addEventListener('load', function () {
    $.get("/api/games")
    .done(function (data) {
         loadGameList(data);
         loadData();
         getUser();
        })
});
var gameList = document.getElementById("gameList");

function loadGameList(data){
 var table= "";
     table = '<thead class="thead-dark"><tr><th>Date of creation</th><th>Player 1</th><th>Player 2 </th><th></th></tr></thead>';
     table +='<tbody>';
     let juegos = data.games;
     juegos.forEach (g => {
        let included= false;
        let gp = 0;
        g.gamePlayers.map(function(p) {
            if(p.player.username == data.player.username){
                included=true;
                gp = p.id;
            }
        });
        table += '<td>'+ new Date(g.created).toLocaleString()+'</td>';
        table +=  ' ' +  g.gamePlayers.map(function(p) { return '<td>'+ p.player.username +'</td>' }).sort().join('');
       if(included &&  !(g.score.length > 0)){
            table += '<td>'+'<button type="button" onclick="reEnter(this)" data-gameplayerid="'+gp+'"' ;
            table += 'class="btn btn-primary btn-sm ">Return Game </button>'+'</td>';

           if(g.gamePlayers.length == 1){
                 table += '<td></td>';
            }
        }else{
            if(g.gamePlayers.length == 2){
                table += '<td></td>';
            }else{
                table +='<td>'+ '<button type="button" onclick="joinGame(this)" data-gameid="'+g.id +'"';
                table += 'class=" btn btn-primary btn-sm ">Join Game </button>'+'</td>';
                table += '<td></td>';
                }
            }
        table +='</tbody>';
    });
    gameList.innerHTML= table;
}
function getUser() {
  $.get("/api/games")
    .done(function (data) {
      app.user = data.player.username;
      console.log(data.player.username);
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
         loginFunc(user, pass)
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
        username: user,
        password: pass,
    })
    .done(function (){
          alert('Registrado Exitosamente');
           loginFunc(user, pass)
           })
           .fail(function (jqXHR, textStatus) {
           alert('Failed: ' + jqXHR.status);
           });
}

function reEnter(enterButton){
  var gameplayerdataid = enterButton.getAttribute('data-gameplayerid');
  let url = "/web/game.html?gp=" + gameplayerdataid + "";
  location.href = url;
}
function joinGame(joinButton){
 var gamedataid = joinButton.getAttribute('data-gameid');
  let url = "/api/game/" + gamedataid + "/players";
  $.post(url)
     .done(function (data) {
         gameViewUrl = "/web/game.html?gp=" + data.gpId;
         location.href = gameViewUrl;
     })
     .fail(function (data) {
        alert('Must be registered to join a game')
        console.log("game join failed");
     });
}
function createGame(){
    $.post("/api/games")
        .done(function(data){
            console.log(data);
          var gameViewUrl ="/web/game.html?gp="+ data.gpId;
          location.href=gameViewUrl;
        })
        .fail(function(data){
            console.log("game creation failed");
        });
}
 function loginFunc( nameUsu,passwordUsu) {
     $.post("/api/login", {
                     username:nameUsu,
                     password:passwordUsu
                   })
                   .done((function(){ getUser(); location.reload(); }))
                   .fail(function (jqXHR, textStatus) {
                     alert('Failed: ' + jqXHR.status);
                   });
             }



