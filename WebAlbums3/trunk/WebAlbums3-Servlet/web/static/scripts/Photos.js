$("#selectAllBt").click(function () {
    selectAll() ;
}) ;

$(".photo").ezpz_tooltip();

//set correctly the action address of the MASSEDIT form
if (typeof getCurrentPage == 'function') {
    if($("#massEditForm").length != 0) {
        $("#massEditForm").get(0).setAttribute("action", getCurrentPage())
    }
}

