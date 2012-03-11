function init_index() {
    url = ""+window.location+""
    
    unsafe = false
    
    if (url.lastIndexOf("/") == url.length - 1) {
        unsafe = true
    }
    if (url.indexOf("/Index") != -1) {
        unsafe = true
    }
    
    
    if (!unsafe) {
        $(".themeForm").attr("action", window.location)
    }
}


$(init_index)