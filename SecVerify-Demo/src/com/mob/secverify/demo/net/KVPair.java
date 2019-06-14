package com.mob.secverify.demo.net;

public class KVPair<T> {
	final public String name;
	final public T value;

	public KVPair(String name, T value) {
		this.name = name;
		this.value = value;
	}

	public String toString() {
		return name + " = " + value;
	}
}
