package cqrs.sample.story.Events;

import java.util.UUID;

public class StoryUpdated extends Event {
	private String _storyText;

	public StoryUpdated(UUID id, String storyText, int _version) {
		_storyText = storyText;
		super.setEventSourceId(id);
	}

	public void setStoryText(String _storyText) {
		this._storyText = _storyText;
	}

	public String getStoryText() {
		return _storyText;
	}

}
