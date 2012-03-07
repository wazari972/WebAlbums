function init() {
    $("#submit_login").click(function () {
        url = "Users?action=LOGIN&dontRedirect=true&userName="+$("#userName").val()+"&userPass="+$("#userPass").val()
        $.ajax({
          url:url,
          success:function(xml){
              //check that access is not denied 
              if($(xml).find("denied").text() == "true")
                  alert("access denied, try again")
              
              //and refresh the page
              /*
              if (window.location.indexOf("Logout") != -1)
                  return true
              if (window.location.indexOf("/Users") != -1)
                  return true
              */
             
             
              saved_themeId = $.cookie("themeId")
              if (saved_themeId != undefined) {
                  $.get("Choix?action=JUST_THEME&themeId="+saved_themeId+"")
              }
              window.location = window.location
              return false
          },
          async:false
         });
         
        return false
    }) ;
}
$(init)