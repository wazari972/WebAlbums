var select = true;
function selectAll() {
    $(".massedit_chkbox").each(function() {
        $(this).prop('checked', select); 
    });
    var bt = $(this) ;
    if (select) bt.attr('value',"Aucune");
    else bt.attr('value',"Toutes");
    select = !select ;
    check_massedit()
}

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

function check_massedit() {
    checked = false
    $(".massedit_chkbox").each(function () {
        if (!checked && $(this).prop("checked"))
            checked = true
    })
    if (checked) {
        $(".massedit_box").show()
        $(".massedit_chk").removeClass("edit")
        $(".massedit_chk").show()
    } else {
        $(".massedit_box").hide()
        $(".massedit_chk").hide()
        $(".massedit_chk").addClass("edit")
        $(this).parent().show()
    }
    
}

function init_mass() {
    $(".massedit_valid").click(validMass)
    $(".massedit_box").hide()
    $(".massedit_selectall").click(selectAll)
    $(".massedit_chkbox").change(check_massedit)
}

$(function() {
    init_mass()
})