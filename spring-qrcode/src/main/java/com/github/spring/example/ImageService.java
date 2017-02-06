package com.github.spring.example;

import java.io.*;

import org.slf4j.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;

import com.google.zxing.*;
import com.google.zxing.client.j2se.*;
import com.google.zxing.common.BitMatrix;

@Service
public class ImageService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

	@Cacheable("qr-code-cache")
	public byte[] generateQRCode(String guid, int width, int height) throws WriterException, IOException {

		Assert.hasText(guid);
		Assert.isTrue(width > 0);
		Assert.isTrue(height > 0);
		
		LOGGER.info("Will generate image  guid=[{}], width=[{}], height=[{}]", guid, width, height);

		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		BitMatrix matrix = new MultiFormatWriter().encode(guid, BarcodeFormat.QR_CODE, width, height);
		MatrixToImageWriter.writeToStream(matrix, MediaType.IMAGE_PNG.getSubtype(), baos, new MatrixToImageConfig());
		return baos.toByteArray();
	}

	@Cacheable("qr-code-cache")
	public ListenableFuture<byte[]> generateQRCodeAsync(String guid, int width, int height) throws Exception {
		return new AsyncResult<byte[]>(generateQRCode(guid, width, height));
	}

}