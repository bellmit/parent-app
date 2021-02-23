var ajaxConfig = {
	/**----post：发送post异步请求----**/
	post: function(url, param, successFun, errorFun) {
		ajaxConfig.ajax("post", url, param, true, successFun, errorFun);
	},
	/**----get：发送get异步请求----**/
	get: function(url, param, successFun, errorFun) {
		ajaxConfig.ajax("get", url, param, true, successFun, errorFun);
	},
	/**----syncPost：发送post同步请求----**/
	syncPost: function(url, param, successFun, errorFun) {
		ajaxConfig.ajax("post", url, param, false, successFun, errorFun);
	},
	/**----syncGet：发送get同步请求----**/
	syncGet: function(url, param, successFun, errorFun) {
		ajaxConfig.ajax("get", url, param, false, successFun, errorFun);
	},
	ajax: function(type, url, param, async, successFun, errorFun) {
		if(!errorFun) {
			errorFun = ajaxConfig.errorFun;
		}
		$.ajax({  
			type: type,
	        url: url,
	        data: param,
	        async: async,
	        dataType: "json",
	        success: successFun,
	        error: errorFun
	    });
	},
	errorFun: function(xmlHttp, e1, e2) {
		alert("系统异常，请稍后再试！");
	}
};