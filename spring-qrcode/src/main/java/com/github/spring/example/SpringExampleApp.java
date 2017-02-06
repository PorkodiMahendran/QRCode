package com.github.spring.example;

import net.rossillo.spring.web.mvc.CacheControl;
import net.rossillo.spring.web.mvc.CacheControlHandlerInterceptor;
import net.rossillo.spring.web.mvc.CachePolicy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.*;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@EnableAsync
@EnableCaching
@RestController
@SpringBootApplication
public class SpringExampleApp extends WebMvcConfigurerAdapter {

	public static final String QRCODE_ENDPOINT = "/qrcode";
	
	@Autowired
	ImageService imageService;

	public static void main(String[] args) {
		SpringApplication.run(SpringExampleApp.class, args);
	}

	@RequestMapping(value = QRCODE_ENDPOINT, method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	@CacheControl(maxAge = 3600, policy = { CachePolicy.PUBLIC } )
	public ListenableFuture<byte[]> getQRCode(@RequestParam(value = "guid", required = true) String guid) {
		try {
			return imageService.generateQRCodeAsync(guid, 256, 256);
		} catch (Exception ex) {
			throw new InternalServerError("Error while generating QR code image.", ex);
		}
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new CacheControlHandlerInterceptor());
	}	
	
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public class InternalServerError extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public InternalServerError(final String message, final Throwable cause) {
			super(message);
		}

	}

}