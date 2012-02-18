
function updateFullImage(id) {
    var img = document.getElementById("largeImg");
    img.src = "Images?id="+id+"&mode=GRAND" ;
}
function init_fullscreen () {
    $(".visio_img").click(function () {
        updateFullImage($(this).attr("rel"))
    })
}

/****************************************************/

function init_tree() {
    $("#tree_expand").click(function () {
        ddtreemenu.flatten('cloudTree', 'expand')
    })
    $("#tree_contract").click(function () {
        ddtreemenu.flatten('cloudTree', 'contract')
    })

    add_callback("treemenu", function(){ddtreemenu.createTree("cloudTree", false)})
}

/****************************************************/

$(function() {
    init_fullscreen()
    init_tree()
})