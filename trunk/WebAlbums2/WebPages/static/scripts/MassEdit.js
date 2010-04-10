var select = true;
function selectAll() {
    var chks = document.getElementsByTagName("input");
    for(var i = 0; i < chks.length; i++) {
	if (chks[i].type == 'checkbox' && chks[i].name.match("chk")) {
	    chks[i].checked = select ;
	}
    }
    var bt = document.getElementById("selectAllBt") ;
    if (select) bt.value = "Aucune";
    else bt.value = "Toutes";
    select = !select ;
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
    for(var i = 0; i < turn.length && !selected; i++) {
	if (turn[i].checked) {
	    if (turn[i].value.match("tag")) {
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