/****************************************************/

function init_tree() {
    $("#tree_expand").click(function () {
        ddtreemenu.flatten('cloudTree', 'expand');
    });
    $("#tree_contract").click(function () {
        ddtreemenu.flatten('cloudTree', 'contract');
    });

    add_callback("treemenu", function(){ddtreemenu.createTree("cloudTree", false);});
}

/****************************************************/

$(function() {
    try {
        if (dontinittree){
            return;
        }
    } catch(e){}
    
    init_tree();
});