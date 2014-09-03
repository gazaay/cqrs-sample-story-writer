package cqrs.sample.story.Domain;

import java.util.UUID;

import cqrs.sample.story.Command.NoSuchStoryException;
import cqrs.sample.story.Storage.StoriesRepository;

public class StoryBuilder {

	private String _storyText;
	private UUID _guid;

	public StoryAggregate create() throws NoSuchStoryException,
			StoryAlreadyCreatedException {
		if (StoriesRepository.hasStory(_guid)) {
			throw new StoryAlreadyCreatedException();
		}
		return new StoryAggregate(_guid, _storyText);
	}

	public StoryBuilder setStory(String storyText) {
		_storyText = storyText;
		return this;
	}

	public StoryBuilder setId(UUID guid) {
		_guid = guid;
		return this;
	}


}
