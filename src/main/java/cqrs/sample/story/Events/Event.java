package cqrs.sample.story.Events;

import java.util.UUID;

public class Event implements IEvent {
	private UUID _eventIdentifier;
	private UUID _eventSourceId;
	private Integer _version;

	public Event() {
		_eventIdentifier = UUID.randomUUID();
	};

	public void setEventIdentifier(UUID _eventIdentifier) {
		this._eventIdentifier = _eventIdentifier;
	}

	public UUID getEventIdentifier() {
		return _eventIdentifier;
	}

	public UUID getEventSourceId() {
		return _eventSourceId;
	}

	public void setEventSourceId(UUID guid) {
		_eventSourceId = guid;
	}

	public void setVersion(Integer version) {
		_version = version;
	}

	public Integer getVersion() {
		return _version;
	}

}
