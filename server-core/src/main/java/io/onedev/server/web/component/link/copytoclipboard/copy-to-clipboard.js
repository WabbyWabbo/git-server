onedev.server.copyToClipboard = {
	onDomReady: function(buttonId, text) {
		var $button = $("#" + buttonId);
		new Clipboard("#"+buttonId, {text: function() {return text;}});
		$button.attr("title", "复制到剪贴板");
		$button.addClass("pressable");
	}
};