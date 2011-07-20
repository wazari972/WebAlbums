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

$(".fastedit_tag_bt").click(function () {
    id = $(this).attr('rel');
    $("#fastedit_tag_"+id).toggle("fast")
}) ;
$(".fastedit_desc_bt").click(function () {
    id = $(this).attr('rel');
    $("#desc_"+id).toggle("fast")
    $("#fastedit_desc_"+id).toggle("fast")
}) ;

function reload_page_cb(data, photoid) {
    //$(data).find('fastedit').each(function(){});
    if (loadSinglePage != undefined) {
        loadSinglePage(getCurrentPage()+"#"+photoid, true)
    } else
        alert("Please reload the page to refresh")
}

function add_rm_tag(photoid, tagact) {
    tagid = $("#fastedit_tag_"+id).val()
    if (tagid == -1) {
        alert("No tag selected ...")
        return
    }
    $.post("Photos?special=FASTEDIT", 
        {id : photoid, tagAction:tagact, tag:tagid},
        function(data) {
            reload_page_cb(data, photoid);
        }
     );
}

$(".fastedit_addtag").click(function () {
    photoid = $(this).attr('rel');
    add_rm_tag(photoid, "ADD")
}) ;

$(".fastedit_rmtag").click(function () {
    photoid = $(this).attr('rel');
    add_rm_tag(photoid, "RM")
}) ;

$(".fastedit_desc").click(function () {
    photoid = $(this).attr('rel');
    photodesc = $("#fastedit_desc_"+photoid).val()
    
    $.post("Photos?special=FASTEDIT", 
        {id : photoid, desc:photodesc},
        function(data) {
            reload_page_cb(data, photoid);
        }
     );
}) ;

massedit_enabled = false
function massedit_toggle(state) {
    if (state == true || state == false)
        massedit_enabled = state
    else
        massedit_enabled = !massedit_enabled;
    
    $("#massedit_item").toggle(massedit_enabled);
    $(".massedit_chk").toggle(massedit_enabled);

    if (massedit_enabled)
        $(".massedit_toggle").text("Mass edit V")
    else
        $(".massedit_toggle").text("Mass edit X")
}
massedit_toggle(true)

$(".massedit_toggle").unbind('click');
$(".massedit_toggle").click(massedit_toggle) ;