package cqrs.sample.story.Storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import cqrs.sample.story.Command.NoSuchStoryException;
import cqrs.sample.story.Domain.StoryAggregate;
import cqrs.sample.story.Events.Event;
import cqrs.sample.story.Events.IEvent;
import cqrs.sample.story.Events.StoryCreated;
import cqrs.sample.story.Events.StoryUpdated;

public class StoriesRepository {

	private static Map<UUID, LinkedBlockingQueue<Event>> _eventSources = new ConcurrentHashMap<UUID, LinkedBlockingQueue<Event>>();

	public static int addEvents(UUID aggregateId, Event event) {
		synchronized (_eventSources) {
			// Check if the even Source exists
			LinkedBlockingQueue<Event> events = _eventSources.get(aggregateId);
			if (events == null || events.isEmpty()) {
				events = new LinkedBlockingQueue<Event>();
				_eventSources.put(aggregateId, events);
			}
			Integer version = event.getVersion();
			Event latest_event = events.peek();
			// if (latest_event == null && version == null) {
			// version = 1;
			// System.out.println("RESET ONE");
			// } else if (version == null) {
			// version = latest_event.getVersion() + 1;
			// System.out.println("RESET TWO");
			// } else if (latest_event == null || latest_event.getVersion() ==
			// null
			// || latest_event.getVersion() < version) {
			// version++;
			// System.out.println("RESET THREE");
			// } else {
			// version = latest_event.getVersion() + 1;
			// System.out.println("RESET FOUR");
			// }
			if (version == null) {
				version = 1;
			} else {
				version++;
			}

			if (latest_event != null && latest_event.getVersion() == version) {
				version++;
			}

			Integer lastest_version = -1;
			if (latest_event != null) {
				lastest_version = latest_event.getVersion();
			}
			event.setVersion(version);
			// Append the event to the source
			events.add(event);
			System.out.println("Latest event Version:" + lastest_version
					+ " vs " + version + " and Event Added with " + aggregateId
					+ ". with events size:" + events.size());
			return version;
		}
	}

	public static StoryAggregate getStory(UUID guid)
			throws NoSuchStoryException {
		// Check if the even Source exists
		Queue<Event> events = _eventSources.get(guid);
		System.out
				.println("Search for Event with Source ID " + guid.toString());
		if (events == null || events.isEmpty()) {
			System.out.println("ERROR: Search for Event with Source ID "
					+ guid.toString());
			throw new NoSuchStoryException();
		}
		StoryAggregate result = StoryAggregate.getNewInstance(guid);
		for (IEvent event : events) {
			if (event instanceof StoryCreated)
				result.apply((StoryCreated) event);
			if (event instanceof StoryUpdated)
				result.apply((StoryUpdated) event);
			result.setVersion(event.getVersion());
		}
		return result;
	}

	public static Queue<Event> getEvents(UUID aggregateId)
			throws NoSuchStoryException {
		// Check if the even Source exists
		Queue<Event> events = _eventSources.get(aggregateId);
		System.out.println("Search for Event with Source ID "
				+ aggregateId.toString());
		if (events == null || events.isEmpty()) {
			throw new NoSuchStoryException();
		}
		return events;
	}

	public static boolean hasStory(UUID aggregateId) {
		Queue<Event> events = _eventSources.get(aggregateId);
		return events != null;
	}

}
