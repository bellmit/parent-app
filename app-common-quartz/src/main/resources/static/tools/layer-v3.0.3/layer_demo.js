jQuery.prompt = {
	percent_h: 0.80,
	percent_w: 0.75,
	ok: "ok",
	msg: "msg",
	warn: "warn",
	error: "error",
	/**
	 * 提示窗口
	 * 使用方式：$.prompt.message("注册成功！", $.prompt.msg);
	 * @param title
	 * @param type 不传默认$.prompt.msg
	 * @param time 多少毫秒后关闭
	 */
	message: function(title, type, options) {
		if(options == undefined) {
			options = {};
		}
		if(options.time == undefined) {
			options.time = 3000;
		}
		if(type == jQuery.prompt.ok) {
			layer.msg(title, {
				time : options.time,
				icon : 1
			});
		} else if(type == jQuery.prompt.error) {
			layer.msg(title, {
				time : options.time,
				icon : 2
			});
		} else if(type == jQuery.prompt.warn) {
			layer.msg(title, {
				time : options.time,
				icon : 0
			});
		} else if(type == jQuery.prompt.msg) {
			layer.msg(title, {time : options.time});
		} else {
			layer.msg(title, {time : options.time});
		}
	}
}