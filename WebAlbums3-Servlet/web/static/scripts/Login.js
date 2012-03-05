function init() {
    $("#submit_login").click(function () {
        /*
        url = "Users?action=LOGIN&dontRedirect=true&userName="+$("#userName").val()+"&userPass="+$("#userPass").val()
        $.ajax({
          url:url,
          success:function(xml){
              //check that access is not denied 
              if($(xml).find("denied").text() == "true")
                  alert("access denied, try again")
              
              //and refresh the page
              alert("test")
              if (window.location.indexOf("/Index") != -1)
                  return true
              alert("load albums")
              loadSinglePage("Album")
              return false
          },
          async:false
         });
         */
        return false
    }) ;
}
$(init)