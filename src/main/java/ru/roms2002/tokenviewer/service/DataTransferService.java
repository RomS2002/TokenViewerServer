package ru.roms2002.tokenviewer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import ru.roms2002.tokenviewer.dto.SendMailDTO;

@Service
public class DataTransferService {

	@Value("${infoserver.url}")
	private String adminpanelURI;

	private final Logger LOGGER = LoggerFactory
			.getLogger(DataTransferService.class);

	private final RestClient restClient;

	public DataTransferService(RestClient.Builder restClientBuilder) {
		this.restClient = restClientBuilder.build();
	}

	public boolean sendMail(SendMailDTO sendMailDTO) {
		ResponseEntity<Void> response = restClient.post()
				.uri(adminpanelURI + "/mail/send")
				.contentType(MediaType.APPLICATION_JSON).body(sendMailDTO)
				.retrieve().toBodilessEntity();

		if (response.getStatusCode() == HttpStatus.OK) {
			LOGGER.info("Mail send request was completed. {}", sendMailDTO);
			return true;
		} else {
			LOGGER.error("InfoServer error while processing {}", sendMailDTO);
			return false;
		}
	}
}
