package cqrs.sample.story.Events;

import java.util.UUID;

public class StoryCreated extends Event{

	private String _storyText;
	
	public StoryCreated(UUID _guid, String storyText, Integer version) {
		super.setEventSourceId(_guid);
		_storyText = storyText;
		super.setVersion(version);
	}

	public void setStoryText(String storyText) {
		this._storyText = storyText;
	}

	public String getStoryText() {
		return _storyText;
	}

}
