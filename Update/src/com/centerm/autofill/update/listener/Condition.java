package com.centerm.autofill.update.listener;

public interface Condition {
	boolean reach(byte[] buf, int len);
}
