function zoom(cmpid, column, value){
	sendAdempiereAu(cmpid, 'onZoom', {columnName: column, code: value});
}

function drillAcross(cmpid, column, value){
	sendAdempiereAu(cmpid, 'onDrillAcross', {columnName: column, code: value});
}

function drillDown(cmpid, column, value){
	sendAdempiereAu(cmpid, 'onDrillDown', {columnName: column, code: value});
}

function sendAdempiereAu(cmpid, cmd, data) {
	var widget = zk.Widget.$(cmpid);
	if (widget) {
		widget.fire(cmd, data, {toServer: true});
	}
}

function showColumnMenu(e, columnName, row) {
	var d = document.getElementById(columnName + "_" + row + "_d");
	
	var posx = 0;
	var posy = 0;
	if (!e) var e = window.event;
	if (e.pageX || e.pageY) 	{
		posx = e.pageX;
		posy = e.pageY;
	}
	else if (e.clientX || e.clientY) 	{
		posx = e.clientX + document.body.scrollLeft
			+ document.documentElement.scrollLeft;
		posy = e.clientY + document.body.scrollTop
			+ document.documentElement.scrollTop;
	}
	
	d.style.top = posy;	
	d.style.left = posx;
	d.style.display = "block";
	
	setTimeout("document.getElementById('" + columnName + "_" + row + "_d" + "').style.display='none'", 3000);
}
