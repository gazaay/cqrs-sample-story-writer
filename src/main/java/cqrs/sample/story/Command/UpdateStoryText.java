package cqrs.sample.story.Command;

import java.util.UUID;

import cqrs.sample.story.Domain.StoryAggregate;
import cqrs.sample.story.Events.StoryCreated;
import cqrs.sample.story.Events.StoryUpdated;

public class UpdateStoryText implements ICommand {

	private UUID _guid;
	private String _storyText;
	private StoryAggregate _story;

	public UpdateStoryText(UUID guid, String storyText, StoryAggregate story) {
		_guid=guid;
		_storyText = storyText;
		_story = story;
	}

	@Override
	public void execute() throws Exception{
		StoryUpdated event = _story.handle(this);
        _story.apply(event);
        _story.commit();		
	}

	public String getStoryText() {
		return _storyText;
	}

}
