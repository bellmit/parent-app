$(function() {
	
});
function showText(password) {
	$.prompt.message(password, $.prompt.msg);
}
function refresh() {
	window.location.href = window.location.href;
}
function addJob(jobId) {
	ajaxConfig.post("/schedule/job/add",{id: jobId},function(data) {
		$.prompt.message(data.msg, $.prompt.msg);
		refresh();
	});
}
function pauseJob(jobId) {
	ajaxConfig.post("/schedule/job/pause",{id: jobId},function(data) {
		$.prompt.message(data.msg, $.prompt.msg);
		refresh();
	});
}
function resumeJob(jobId) {
	ajaxConfig.post("/schedule/job/resume",{id: jobId},function(data) {
		$.prompt.message(data.msg, $.prompt.msg);
		refresh();
	});
}
function removeJob(jobId) {
	ajaxConfig.post("/schedule/job/remove",{id: jobId},function(data) {
		$.prompt.message(data.msg, $.prompt.msg);
		refresh();
	});
}
function runJobOneTime(jobId) {
	ajaxConfig.post("/schedule/job/run",{id: jobId},function(data) {
		$.prompt.message(data.msg, $.prompt.msg);
		refresh();
	});
}

function status() {
	ajaxConfig.post("/schedule/job/status",{},function(data) {
		$("#status").html(JSON.stringify(data.data));
		// $.prompt.message(JSON.stringify(data.data), $.prompt.msg);
	});
}

function startScheduler() {
	ajaxConfig.post("/schedule/job/start_scheduler",{},function(data) {
		$.prompt.message(data.msg, $.prompt.msg);
	});
}

function startup() {
	ajaxConfig.post("/schedule/job/startup",{},function(data) {
		$.prompt.message(data.msg, $.prompt.msg);
	});
}
function shutdown() {
	ajaxConfig.post("/schedule/job/shutdown",{},function(data) {
		$.prompt.message(data.msg, $.prompt.msg);
	});
}
