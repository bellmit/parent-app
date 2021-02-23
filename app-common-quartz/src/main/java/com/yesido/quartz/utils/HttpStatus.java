package com.yesido.quartz.utils;

public enum HttpStatus {

	SUCCESS(200), // 成功
	FAIL(400), // 失败
	VALIDATOR_ERROR(504), // 参数不合法
	INTERNAL_SERVER_ERROR(500); // 服务器错误
	private int value;

	HttpStatus(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}
}
