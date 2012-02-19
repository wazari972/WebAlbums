function init() {
    $("#submit_login").click(function () {
        url = "Users?action=LOGIN&dontRedirect=true&userName="+$("#userName").val()+"&userPass="+$("#userPass").val()
        /*
        $.ajax({
          url:url,
          success:function(html){
              //check that HTML is null 
              //and refresh the page
          },
          async:false
         });*/
        return true
    }) ;
}

$(init)