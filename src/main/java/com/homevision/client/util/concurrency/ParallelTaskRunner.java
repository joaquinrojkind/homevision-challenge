package com.homevision.client.util.parallelism;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ParallelTaskRunner {
	@Value("${client.concurrency.maxThreadNumber}")
	private int maxThreadNumber;

	public void consumeAll(List<Runnable> runnables) {
		Assert.notEmpty(runnables, "Must be at least one task to run");
		ExecutorService executorService = getExecutorWithSize(runnables.size());

		final List<CompletableFuture> futures =
				runnables.stream()
						.map(runnable -> CompletableFuture.runAsync(runnable, executorService))
						.collect(Collectors.toList());
		try {
			futures.stream()
					.forEach(CompletableFuture::join);
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
