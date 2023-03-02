package com.homevision.client.util.parallel.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class SupplyAllResponse<T> {
	private List<T> successResponses = new ArrayList<>();
	private List<Throwable> exceptions = new ArrayList<>();
}
