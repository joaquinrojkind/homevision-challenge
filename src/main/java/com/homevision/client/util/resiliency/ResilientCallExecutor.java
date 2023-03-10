package com.homevision.client.util.resiliency;

import com.homevision.client.exception.ApiClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

@Slf4j
@Component
public class ResilientCallExecutor {

	@Value("${client.resiliency.maxRetries}")
	private Integer maxRetries;
	public <T> T executeCall(Call<T> call) {
		try {
			Response<T> response = call.execute();
			int retryCount = 1;
			while (response.code() >= 500 && retryCount <= maxRetries) {
				response = call.clone().execute();
				retryCount++;
			}
			if (response.isSuccessful()) {
				return response.body();
			}
			log.error("Error while trying to connect with url {}, http status is {}. Total retries attempted: {}", call.request().url(), response.code(), maxRetries);
			throw new ApiClientException();
		} catch (IOException e) {
			log.error("Internal error while trying to connect with url {}, exception: {}", call.request().url(), e);
			throw new ApiClientException();
		}
	}
}
