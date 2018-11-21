function draw() {

}

function evaluate(json) {
    if (json.win) {
        $("#content").html("<div class='won'>GAME WON!</div>")
    } else if (json.lose) {
        $("#content").html("<div class='lose'>GAME OVER!</div>")
    } else {
        var intro = "<div class='intro'>Hello Player.</div>"
        var tiles = json.grid.tiles
        var grid = "<div class='grid'><div>"
        for (i = 0; i < Math.sqrt(tiles.length); i++) {
            grid = grid + "<div>"
            for (j = 0; j < Math.sqrt(tiles.length); j++) {
                grid = grid + "<span class='tile'>" +
                    tiles[Math.sqrt(tiles.length) * i + j].value +
                    "</span>"
            }
            grid = grid + "</div>"
        }
        grid = grid + "</div>"

        var score = "<div class='score'>Score: " + json.score.value + "</div>"

        var html = intro + grid + score

        $("#content").html(html)
    }
}

function turn(action) {
    $.ajax({
        method: "GET",
        url: action,
        dataType: "json",

        success: function (result) {
            evaluate(result)
        }
    });
}

function clickListener() {
    $("#buttonUp").click(function () {
        turn("/up")
    });
    $("#buttonDown").click(function () {
        turn("/down")
    });
    $("#buttonLeft").click(function () {
        turn("/left")
    });
    $("#buttonRight").click(function () {
        turn("/right")
    });
    $("#buttonReset").click(function () {
        turn("/reset")
    });
    $("#buttonUndo").click(function () {
        turn("/undo")
    });
}

function init() {
    $.ajax({
        method: "GET",
        url: "/toJson",
        dataType: "json",

        success: function (result) {
            evaluate(result)
            clickListener()
        }
    });
}

$(document).ready(function () {
    init();
});
