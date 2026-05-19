function ad_getWidgetByUuid(uuid) {
	if (!uuid) {
		return null;
	}

	var w = zk.Widget.$('$' + uuid);
	if (w) {
		return w;
	}

	var n = document.getElementById(uuid);
	if (n) {
		return zk.Widget.$(n);
	}

	return null;
}

function ad_getNodeByUuid(uuid) {
	var w = ad_getWidgetByUuid(uuid);
	if (w && w.$n()) {
		return w.$n();
	}

	return document.getElementById(uuid);
}

function ad_deferRenderBorderLayout(uuid, timeout) {
	var w = ad_getWidgetByUuid(uuid);

	if (w) {
		setTimeout(function() {
			_ad_deferBDL(uuid);
		}, timeout);
	}
}

function _ad_deferBDL(uuid) {
	var w = ad_getWidgetByUuid(uuid);

	if (!w) {
		return;
	}

	if (zk.beforeSizeAt) {
		zk.beforeSizeAt();
	}

	if (zk.onSizeAt) {
		zk.onSizeAt();
	}

	if (w.render) {
		w.render();
	} else if (w.onSize) {
		w.onSize();
	} else if (w.resize) {
		w.resize();
	}
}

function ad_closeBuble(uuid) {
	var n = ad_getNodeByUuid(uuid);

	if (!n || !n.bandInfos || !n.instance) {
		return;
	}

	for (var i = 0; i < n.bandInfos.length; i++) {
		if (n.instance.getBand && n.instance.getBand(i)) {
			n.instance.getBand(i).closeBubble();
		}
	}
}

function scrollToRow(uuid) {
	var n = ad_getNodeByUuid(uuid);

	if (n) {
		n.style.display = "inline";
		n.focus();
		n.style.display = "none";
	}
}
 