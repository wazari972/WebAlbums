function init_massedit() {
    $("#selectAllBt").click(function () {
        selectAll() ;
    }) ;

    //set correctly the action address of the MASSEDIT form
    if (typeof getCurrentPage == 'function') {
        if($("#massEditForm").length != 0) {
            $("#massEditForm").get(0).setAttribute("action", getCurrentPage())
        }
    }

    $("#selectAllBt").click(function () {
        selectAll() ;
    }) ;
}

function reload_page_cb(data, photoid, cb) {
    if (loadSinglePage != undefined) {
        loadSinglePage(getCurrentPage()+"#"+photoid, /*dont_scoll*/true, /*force*/true, /*async*/true)
    } else
        alert("Please reload the page to refresh")
}

function add_rm_tag(photoid, tagact) {
    tagid = $("#fastedit_tag_"+id).val()
    if (!(tagid > 0)) {
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

function set_stars(photoid, stars) {
    if (stars < 0 || stars > 5) {
        alert("Le nombre d'étoiles doit être compris entre 0 et 5 ("+n+")")
        return
    }
    $("#stars_"+photoid+"_message").text("Settings the stars ...")
    $.post("Photos?special=FASTEDIT", 
        {id : photoid, stars:stars},
        function(data) {
            $("#stars_"+photoid+"_message").text("Stars set. Reloading ...")
            reload_page_cb(data, photoid);
        }
     );
}

function init_fastedit() {
    $(".fastedit_tag_bt").click(function () {
        id = $(this).attr('rel');
        $("#fastedit_div_tag_"+id).toggle("fast")
        $("#fastedit_div_tag_"+id).parent(".edit").toggleClass("edit_visible")
    }) ;
    $(".fastedit_desc_bt").click(function () {
        id = $(this).attr('rel');
        $("#desc_"+id).toggle("fast")
        $("#fastedit_div_desc_"+id).toggle("fast")
        $("#fastedit_div_desc_"+id).parent(".edit").toggleClass("edit_visible")
    }) ;
    $(".fastedit_stars").click(function () {
        photoid = $(this).attr('rel').split('/')[0];
        stars = $(this).attr('rel').split('/')[1];

        set_stars(photoid, parseInt(stars))

    }) ;

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
}

$(function() {
    $(".exif").ezpz_tooltip({stayOnContent: true});
    init_massedit()
    init_fastedit()
})
