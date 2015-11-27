package com.centerm.autofill.appframework.listener;

public interface Condition {
	boolean reach(byte[] buf, int len);
}
