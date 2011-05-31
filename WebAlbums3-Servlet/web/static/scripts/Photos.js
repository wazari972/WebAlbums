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

$("#selectAllBt").click(function () {
    selectAll() ;
}) ;

$(".fastedit_bt").click(function () {
    id = $(this).attr('rel');
    $("#fastedit_"+id).toggle("fast")
}) ;

$(".fastedit_addtag").click(function () {
    id = $(this).attr('rel');
    
    alert("add "+$("#fastedit_tag_"+id).val())
}) ;
$(".fastedit_rmtag").click(function () {
    id = $(this).attr('rel');
    alert("rm "+$("#fastedit_tag_"+id).val())
}) ;
$(".fastedit_desc").click(function () {
    id = $(this).attr('rel');
    alert("desc "+$("#fastedit_desc_"+id).val())
}) ;