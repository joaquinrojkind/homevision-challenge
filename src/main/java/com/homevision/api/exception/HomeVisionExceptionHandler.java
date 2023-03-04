package com.homevision.api.exception;

import com.homevision.client.exception.ApiClientException;
import com.homevision.service.exception.DownloadPhotoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class HomeVisionExceptionHandler {

	@ExceptionHandler({ ApiClientException.class })
	public ResponseEntity handleApiClientException(ApiClientException exception) {
		ApiErrorDto errorPayload = ApiErrorDto.builder()
				.status(HttpStatus.BAD_GATEWAY.value())
				.code(HttpStatus.BAD_GATEWAY.name())
				.message("Error while trying to connect with downstream services")
				.build();
		return ResponseEntity
			.status(errorPayload.getStatus())
			.body(errorPayload);
	}

	@ExceptionHandler({ DownloadPhotoException.class })
	public ResponseEntity handleDownloadPhotoException(DownloadPhotoException exception) {
		ApiErrorDto errorPayload = ApiErrorDto.builder()
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.code(HttpStatus.INTERNAL_SERVER_ERROR.name())
				.message(String.format("Error while downloading house photo with url: %s", exception.getPhotoUrl()))
				.build();
		return ResponseEntity
				.status(errorPayload.getStatus())
				.body(errorPayload);
	}
}
