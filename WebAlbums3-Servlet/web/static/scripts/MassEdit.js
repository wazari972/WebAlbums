var select = true;
function selectAll() {
    $(".massedit_chkbox").each(function() {
        $(this).attr('checked', select); 
    });
    var bt = $("#massedit_selectall") ;
    if (select) bt.attr('value',"Aucune");
    else bt.attr('value',"Toutes");
    select = !select ;
}
$("#massedit_selectall").click(selectAll)

function validMass() {
    var selected = false ;

    var chks = document.getElementsByTagName("input");
    for(var i = 0; i < chks.length && !selected; i++) {
        if (chks[i].type == 'checkbox' && chks[i].name.match("chk")) {
            if (chks[i].checked) selected = true ;
        }
    }

    if (!selected) {
        alert("Pas de photo selectionnée...");
        return ;
    }

    selected = false ;
    var turn = document.getElementsByName("turn");
    for(var j = 0; j < turn.length && !selected; j++) {
        if (turn[j].checked) {
            if (turn[j].value.match("tag")) {
                if (document.getElementById("massTagList").value != -1) {
                    selected = true ;
                }
            }
            else {
                selected = true ;
            }
        }
    }
    if (!selected) {
        alert("Pas d'action à effectuer / tag selectionné ...") ;
        return ;
    }
    document.forms[0].submit()
}