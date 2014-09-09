package cqrs.sample.story.Domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cqrs.sample.story.Command.CreateStory;
import cqrs.sample.story.Command.NoSuchStoryException;
import cqrs.sample.story.Command.UpdateStoryText;
import cqrs.sample.story.Events.Event;
import cqrs.sample.story.Events.StoryCreated;
import cqrs.sample.story.Events.StoryUpdated;
import cqrs.sample.story.Storage.StoriesRepository;

public class StoryAggregate {

	private UUID _guid_story;
	private String _storyText;
	private List<Event> _events;
	private Integer _version;
	public static class StoryBuilder {


		private String _storyText;
		private UUID _guid;

		public StoryBuilder() {
		}

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
	public static StoryAggregate build(UUID guid) throws NoSuchStoryException {
		if (StoriesRepository.hasStory(guid)) {
			return StoriesRepository.getStory(guid);
		} else {
			return new StoryAggregate(guid, "");
		}
	}

	public static StoryAggregate getNewInstance(UUID guid) {
		return new StoryAggregate(guid, "");
	}

	private StoryAggregate(UUID guid, String stroryText) {
		// TODO: Fail if repository already exists
		setId(guid);
		setStoryText(stroryText);
		_events = new ArrayList<Event>();
	}

	public StoryCreated handle(CreateStory createStory)
			throws StoryAlreadyCreatedException {
		System.out
				.println("Handling Event with content: "
						+ createStory.getStoryText() + " With Version: "
						+ getVersion());
		if (getVersion() != null && getVersion() > 0) {
			throw new StoryAlreadyCreatedException("The Story already created with the same GUID. Please create a new story with new GUID instead.");
		}
		return new StoryCreated(getId(), createStory.getStoryText(),
				getVersion());
	}

	public StoryUpdated handle(UpdateStoryText updateStoryText) {
		System.out.println("Handling update Event with content: "
				+ updateStoryText.getStoryText() + " With Version: "
				+ getVersion());
		return new StoryUpdated(getId(), updateStoryText.getStoryText(),
				getVersion());
	}

	public void apply(StoryCreated event) {
		setStoryText(event.getStoryText());
		_events.add(event);
	}

	public void apply(StoryUpdated event) {
		setStoryText(event.getStoryText());
		_events.add(event);
	}

	public void commit() {
		for (Event event : _events) {
			if (!event.isApplied()) {
				// TODO: potentially need to handle optimistic lock here.
				event.setVersion(getVersion());
				setVersion(StoriesRepository.addEvents(getId(), event));
				event.applied();
				System.out.println("Saving events with ID:"
						+ event.getEventSourceId() + " and version "
						+ event.getVersion());
			}
		}
		_events.clear();
	}

	public void setId(UUID _guid_story) {
		this._guid_story = _guid_story;
	}

	public UUID getId() {
		return _guid_story;
	}

	public static StoryAggregate getAggregateFromHistory(UUID _guid)
			throws NoSuchStoryException {
		return StoriesRepository.getStory(_guid);
	}

	public void setVersion(Integer version) {
		this._version = version;
	}

	public Integer getVersion() {
		return _version;
	}

	public void setStoryText(String _storyText) {
		this._storyText = _storyText;
	}

	public String getStoryText() {
		return _storyText;
	}

}
