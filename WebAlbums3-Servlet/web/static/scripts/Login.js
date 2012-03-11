function init() {
    $("#submit_login").click(function () {
        url = "Users?action=LOGIN&dontRedirect=true&userName="+$("#userName").val()+"&userPass="+$("#userPass").val()
        $.ajax({
          url:url,
          success:function(xml){
              //check that access is not denied 
              if($(xml).find("denied").text() == "true")
                  alert("access denied, try again")
             
              saved_themeId = $.cookie("themeId")
              if (saved_themeId != undefined) {
                  $.get("Choix?action=JUST_THEME&themeId="+saved_themeId+"")
              }
              
                            //and refresh the page
              url = ""+window.location+""
              unsafe = false
              if (url.indexOf("logout") != -1)
                  unsafe = true
              if (url.indexOf("/Users") != -1)
                  unsafe = true
              
              if (!unsafe)
                window.location = window.location
              else
                  window.location = "Index"
              return false
          },
          async:false
         });
         
        return false
    }) ;
}
$(init)