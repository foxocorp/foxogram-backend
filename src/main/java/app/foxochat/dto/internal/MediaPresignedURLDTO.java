package app.foxochat.dto.internal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MediaPresignedURLDTO {

	private String url;

	private String uuid;

	private Class<?> media;

	public MediaPresignedURLDTO(String url, String uuid, Class<?> media) {
		this.url = url;
		this.uuid = uuid;
		this.media = media;
	}
}
