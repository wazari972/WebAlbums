function init_index() {
    var url = ""+window.location+"";
    var unsafe = false;
    
    if (url.lastIndexOf("/") === url.length - 1) {
        unsafe = true;
    }
    if (url.indexOf("/Index") !== -1 || url.indexOf("/index") !== -1) {
        unsafe = true;
    }
    
    if (!unsafe) {
        $(".themeForm").attr("action", window.location);
    }
}


$(init_index);