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


function reload_page_cb(data, photoid) {
    if (loadSinglePage != undefined) {
        loadSinglePage(getCurrentPage()+"#"+photoid, true)
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

function inc_dec_stars(photoid, inc_dec) {
    stars = $("#fastedit_div_stars_"+photoid).attr('rel')
    
    n = parseInt(stars)
    n += inc_dec
    
    set_stars(photoid, n)
    
}

function set_stars(photoid, stars) {
    if (stars < 0 || stars > 5) {
        alert("Le nombre d'étoiles doit être compris entre 0 et 5 ("+n+")")
        return
    }    
    
    $.post("Photos?special=FASTEDIT", 
        {id : photoid, stars:stars},
        function(data) {
            reload_page_cb(data, photoid);
        }
     );
}

function init_fastedit() {
    $("#fastedit_stars_bt").toggle()
    
    $(".fastedit_stars_inc").click(function () {
        photoid = $(this).attr('rel');
        inc_dec_stars(photoid, 1)
    }) ;

    $(".fastedit_stars_dec").click(function () {
        photoid = $(this).attr('rel');
        inc_dec_stars(photoid, -1)
    }) ;
    //buttons to toggle TAG and DESC fast edit
    $(".fastedit_stars_bt").click(function () {
        id = $(this).attr('rel');
        $("#fastedit_div_stars_"+id).toggle("fast")
    }) ;
    $(".fastedit_tag_bt").click(function () {
        id = $(this).attr('rel');
        $("#fastedit_div_tag_"+id).toggle("fast")
    }) ;
    $(".fastedit_desc_bt").click(function () {
        id = $(this).attr('rel');
        $("#desc_"+id).toggle("fast")
        $("#fastedit_div_desc_"+id).toggle("fast")
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

function setup_shadowbox() {
    Shadowbox.setup(".photo", {});
}

$(function() {
    init_massedit()
    init_fastedit()
    setup_shadowbox()
})
