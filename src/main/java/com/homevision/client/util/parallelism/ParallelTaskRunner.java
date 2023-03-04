package com.homevision.client.util.parallelism;

import com.homevision.client.util.parallelism.response.SupplyAllResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

@Component
@Slf4j
public class ParallelTaskRunner {
	@Value("${client.globalTimeout}")
	private long globalTimeoutMillis;
	@Value("${client.maxThreadNumber}")
	private int maxThreadNumber;

	public <T> SupplyAllResponse<T> supplyAllAndGetExceptions(List<Supplier<T>> suppliers) {
		Assert.notEmpty(suppliers, "Must be at least one task to run");
		ExecutorService executorService = getExecutorWithSize(suppliers.size());

		final List<CompletableFuture<T>> futures = suppliers.stream()
				.map(supplier -> CompletableFuture.supplyAsync(supplier, executorService))
				.collect(toList());

		try {
			SupplyAllResponse<T> response = new SupplyAllResponse<>();
			futures.forEach( future -> {
				try {
					response.getSuccessResponses().add(future.get(globalTimeoutMillis, TimeUnit.MILLISECONDS));
				} catch (Exception e) {
					log.error("Error while executing API client calls in parallel", e);
					response.getExceptions().add(e.getCause());
				}
			});
			return response;
		} finally {
			executorService.shutdown();
		}
	}

	private ExecutorService getExecutorWithSize(int poolSize) {
		return Executors.newFixedThreadPool(getSanitizedPoolSize(poolSize), Executors.defaultThreadFactory());
	}

	private int getSanitizedPoolSize(int poolSize) {
		return poolSize > maxThreadNumber ? maxThreadNumber : poolSize;
	}
}
