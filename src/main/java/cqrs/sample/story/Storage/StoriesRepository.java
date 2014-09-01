package cqrs.sample.story.Storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cqrs.sample.story.Command.NoSuchStoryException;
import cqrs.sample.story.Domain.StoryAggregate;
import cqrs.sample.story.Events.Event;
import cqrs.sample.story.Events.IEvent;
import cqrs.sample.story.Events.StoryCreated;
import cqrs.sample.story.Events.StoryUpdated;

public class StoriesRepository {

	private static Map<UUID, List<Event>> _eventSources = new HashMap<UUID, List<Event>>();
	
	public static void addEvents(UUID aggregateId, Event event) {
		// Check if the even Source exists
		List<Event> events = _eventSources.get(aggregateId);
		if (events == null || events.isEmpty()){
			events = new ArrayList<Event>();
			_eventSources.put(aggregateId, events);
		}
		int version = event.getVersion();
		event.setVersion(version++);
		// Append the event to the source
		events.add(event);
		System.out.println("Event Added. with events size:" + events.size());
	}

	public static StoryAggregate getStory(UUID guid) throws NoSuchStoryException {
		// Check if the even Source exists
		List<Event> events = _eventSources.get(guid);
		System.out.println("Search for Event with Source ID " + guid.toString());
		if (events==null || events.isEmpty()){
			throw new NoSuchStoryException();
		}
		StoryAggregate result = new StoryAggregate(guid, "");
		for (IEvent event : events) {
			if (event instanceof StoryCreated ) result.apply((StoryCreated) event);
			if (event instanceof StoryUpdated ) result.apply((StoryUpdated) event);
			result.setVersion(event.getVersion());
		}
		return result;
		
	}

	public static List<Event> getEvents(UUID aggregateId) throws NoSuchStoryException {
		// Check if the even Source exists
		List<Event> events = _eventSources.get(aggregateId);
		System.out.println("Search for Event with Source ID " + aggregateId.toString());
		if (events==null || events.isEmpty()){
			throw new NoSuchStoryException();
		}
		return events;
	}

	public static boolean hasStory(UUID aggregateId) {
		List<Event> events = _eventSources.get(aggregateId);
		return events!=null;
	}

}
