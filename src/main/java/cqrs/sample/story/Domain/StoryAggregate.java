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

	public StoryAggregate(UUID guid, String stroryText) {
		//TODO: Fail if repository already exists
		setId(guid);
		setStoryText(stroryText);
		_events = new ArrayList<Event>();
	}

	public StoryCreated handle(CreateStory createStory) throws StoryAlreadyCreatedException {
		System.out.println("Handling Event with content: " +createStory.getStoryText() + " With Version: " + getVersion());
		if (getVersion() != null && getVersion() > 0 ) {
			throw new StoryAlreadyCreatedException();
		}
		return new StoryCreated(getId(), createStory.getStoryText(), getVersion());
	}
	
	public StoryUpdated handle(UpdateStoryText updateStoryText) {
		System.out.println("Handling update Event with content: " +updateStoryText.getStoryText() + " With Version: " + getVersion());
		return new StoryUpdated(getId(), updateStoryText.getStoryText(), getVersion());
	}
	
	
	public void apply(StoryCreated event){
		setStoryText(event.getStoryText());
		_events.add(event);
	}


	public void apply(StoryUpdated event) {
		setStoryText(event.getStoryText());
		_events.add(event);
	}

	
	public void commit() {
		for (Event event: _events) {
			//TODO: potentially need to handle optimistic lock here.
			event.setVersion(getVersion());
			setVersion(StoriesRepository.addEvents(getId(), event));
			System.out.println("Saving events with ID:" + event.getEventSourceId() + " and version " + event.getVersion());
		}
		_events.clear();
	}

	public void setId(UUID _guid_story) {
		this._guid_story = _guid_story;
	}

	public UUID getId() {
		return _guid_story;
	}

	public static StoryAggregate getAggregateFromHistory(UUID _guid) throws NoSuchStoryException {
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
