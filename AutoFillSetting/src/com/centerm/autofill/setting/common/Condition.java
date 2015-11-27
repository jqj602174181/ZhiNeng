package com.centerm.autofill.setting.common;

public interface Condition {
	boolean reach(byte[] buf, int len);
}
