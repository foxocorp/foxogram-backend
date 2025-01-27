package su.foxogram.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "attachments", indexes = {
		@Index(name = "idx_attachment_id", columnList = "id", unique = true)
})
public class Attachment {

	@Id
	public String id;

	@Column()
	public String filename;

	@Column()
	public String contentType;

	@Column()
	public long flags;

	public Attachment() {
	}

	public Attachment(String id, String filename, String contentType, long flags) {
		this.id = id;
		this.filename = filename;
		this.contentType = contentType;
		this.flags = flags;
	}
}
