package jkml.client;

import java.util.Objects;
import java.util.UUID;

public class Message {

	public static final Message CORRUPTED =  new Message("CORRUPTED");

	private final UUID id;

	private final String content;

	public Message(String content) {
		this.id = UUID.randomUUID();
		this.content = content;
	}

	public UUID getId() {
		return id;
	}

	public String getContent() {
		return content;
	}

	@Override
	public int hashCode() {
		return Objects.hash(content, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Message)) {
			return false;
		}
		Message other = (Message) obj;
		return Objects.equals(content, other.content) && Objects.equals(id, other.id);
	}

}

