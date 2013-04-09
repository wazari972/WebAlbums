var select = true;
function selectAll() {
    $(".massedit_chkbox").each(function() {
        $(this).prop('checked', select); 
    });
    
    var bt = $(this) ;
    if (select) bt.attr('value',"Aucune");
    else bt.attr('value',"Toutes");
    select = !select ;
    check_massedit();
}

function validMass() {
    if ($(".massedit_action:checked").size() === 0) {
        alert("Pas d'action à effectuer / tag selectionné ...");
        return;
    }
        
    var size_photos = $(".massedit_chkbox:checked").size();
    if (size_photos === 0) {
        alert("Pas de photo selectionnée ...");
        return;
    }
    var size_tags = $("#massTagList option:selected").size();
    if (size_tags === 0 && $(".massedit_tag").is(":checked")) {
        alert("Pas de tag selectionné...");
        return;
    }
    
    if (($("#turnAuthor").is(":checked") || $("#turnMove").is(":checked")) && size_tags !== 1) {
        alert("Selectionnez seulement un tag ...");
        return;
    }
    
    document.forms[0].submit();
}

function check_massedit() {
    var mode = get_editionMode();
    var checked = false;
    
    if (mode === 'INTENSIVE EDIT') {
        $(".massedit_chk").addClass("edit");
        return;
    }
    
    $(".massedit_chkbox").each(function () {
        if (!checked && $(this).prop("checked")) {
            checked = true;
        }
    });
    
    if (checked) {
        $(".massedit_box").css('visibility', 'visible');
        $(".massedit_chk").removeClass("edit").css('visibility', 'visible');
    } else {
        $(".massedit_box").css('visibility', 'hidden');
        $(".massedit_chk").css('visibility', 'hidden').addClass("edit");
        $(this).parent().css('visibility', 'visibility');
        $(this).css('visibility', 'visible')
    }
}

function init_mass() {
    $(".massedit_valid").click(validMass);
    $(".massedit_box").css('visibility', 'hidden');
    $(".massedit_selectall").click(selectAll);
    $(".massedit_chkbox").change(check_massedit);
}

$(function() {
    init_mass();
})