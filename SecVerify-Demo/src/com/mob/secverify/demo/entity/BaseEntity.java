package com.mob.secverify.demo.entity;

import com.google.gson.Gson;
import com.mob.tools.utils.Hashon;

import java.io.Serializable;

public class BaseEntity implements Serializable {
	public String toJSONString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
