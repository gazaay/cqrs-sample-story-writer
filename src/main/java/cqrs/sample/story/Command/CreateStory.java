package cqrs.sample.story.Command;

import java.util.UUID;

import cqrs.sample.story.Domain.StoryAggregate;
import cqrs.sample.story.Domain.StoryAlreadyCreatedException;
import cqrs.sample.story.Events.StoryCreated;

public class CreateStory implements ICommand {

	private UUID _guid;
	private String _storyText;
	private int _version;
	private StoryAggregate _story;
	
	/**
	 * Command To Create a New Story
	 * @param guid unique identifier
	 * @param storyText the story context
	 */
	public CreateStory(UUID guid, String storyText, StoryAggregate story) {
		//TODO: validation
		//TODO: build the object
		_guid = guid;
		setStoryText(storyText);
		if (_story == null) {
			try {
				_story = StoryAggregate.build(_guid);
			} catch (NoSuchStoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		_story = story; 
	}

	public void setStoryText(String _storyText) {
		this._storyText = _storyText;
	}

	public String getStoryText() {
		return _storyText;
	}

	@Override
	public void execute() throws NoSuchStoryException, StoryAlreadyCreatedException  {
		StoryCreated event = _story.handle(this);
        _story.apply(event);
        _story.commit();
	}



}
