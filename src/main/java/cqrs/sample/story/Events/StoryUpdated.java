package cqrs.sample.story.Events;

import java.util.UUID;

public class StoryUpdated extends Event {
	private String _storyText;

	public StoryUpdated(UUID id, String storyText, Integer version) {
		_storyText = storyText;
		super.setEventSourceId(id);
		super.setVersion(version);
	}

	public void setStoryText(String _storyText) {
		this._storyText = _storyText;
	}

	public String getStoryText() {
		return _storyText;
	}

}
