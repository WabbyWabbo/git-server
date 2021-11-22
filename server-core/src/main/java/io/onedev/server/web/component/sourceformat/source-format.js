onedev.server.sourceFormat = {
	init: function(containerId) {
		var $container = $("#" + containerId);

		$container.find(".indent-type>select").prepend("<optgroup label='缩进类型'></optgroup>");
		$container.find(".tab-size>select").prepend("<optgroup label='Tab 尺寸'></optgroup>");
		/*$container.find(".line-wrap-mode>select").prepend("<optgroup label='换行模式'></optgroup>");*/
	}
};