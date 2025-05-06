package ru.roms2002.tokenviewer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import ru.roms2002.tokenviewer.dto.ChangeDepartmentDTO;
import ru.roms2002.tokenviewer.dto.ChangeRoleDTO;
import ru.roms2002.tokenviewer.dto.ChangeStudgroupDTO;
import ru.roms2002.tokenviewer.dto.SendMailDTO;

@Service
public class DataTransferService {

	@Value("${infoserver.url}")
	private String infoserverURI;

	private final Logger LOGGER = LoggerFactory.getLogger(DataTransferService.class);

	private final RestClient restClient;

	public DataTransferService(RestClient.Builder restClientBuilder) {
		this.restClient = restClientBuilder.build();
	}

	public boolean sendMail(SendMailDTO sendMailDTO) {
		ResponseEntity<Void> response = restClient.post().uri(infoserverURI + "/mail/send")
				.contentType(MediaType.APPLICATION_JSON).body(sendMailDTO).retrieve()
				.toBodilessEntity();

		if (response.getStatusCode() == HttpStatus.OK) {
			LOGGER.info("Mail send request was completed. {}", sendMailDTO);
			return true;
		} else {
			LOGGER.error("InfoServer error while processing {}", sendMailDTO);
			return false;
		}
	}

	public void sendBlocked(int userId) {
		restClient.post().uri(infoserverURI + "/notification/blocked")
				.contentType(MediaType.APPLICATION_JSON).body(userId).retrieve().toBodilessEntity();
	}

	public void sendNewStudgroup(String groupName) {
		restClient.post().uri(infoserverURI + "/notification/new-group")
				.contentType(MediaType.APPLICATION_JSON).body(groupName).retrieve()
				.toBodilessEntity();
	}

	public void sendDeleteUser(Integer userId) {
		restClient.post().uri(infoserverURI + "/notification/delete-user")
				.contentType(MediaType.APPLICATION_JSON).body(userId).retrieve().toBodilessEntity();
	}

	public void sendUserChangeStudgroup(ChangeStudgroupDTO dto) {
		restClient.post().uri(infoserverURI + "/notification/change-group")
				.contentType(MediaType.APPLICATION_JSON).body(dto).retrieve().toBodilessEntity();
	}

	public void sendUserChangeRole(ChangeRoleDTO dto) {
		restClient.post().uri(infoserverURI + "/notification/change-role")
				.contentType(MediaType.APPLICATION_JSON).body(dto).retrieve().toBodilessEntity();
	}

	public void sendUserChangeDepartment(ChangeDepartmentDTO dto) {
		restClient.post().uri(infoserverURI + "/notification/change-role")
				.contentType(MediaType.APPLICATION_JSON).body(dto).retrieve().toBodilessEntity();
	}
}
