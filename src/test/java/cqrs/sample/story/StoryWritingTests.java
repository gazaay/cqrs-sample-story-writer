package cqrs.sample.story;

import static org.easymock.EasyMock.createMockBuilder;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;

import org.hamcrest.Matchers;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import com.carrotsearch.junitbenchmarks.*;
import com.carrotsearch.junitbenchmarks.annotation.*;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import cqrs.sample.story.Command.CommandBase;
import cqrs.sample.story.Command.CreateStory;
import cqrs.sample.story.Command.ICommand;
import cqrs.sample.story.Command.NoSuchStoryException;
import cqrs.sample.story.Command.UpdateStoryText;
import cqrs.sample.story.Domain.StoryAggregate;
import cqrs.sample.story.Domain.StoryAlreadyCreatedException;
import cqrs.sample.story.Domain.StoryBuilder;
import cqrs.sample.story.Events.Event;
import cqrs.sample.story.Storage.StoriesRepository;

public class StoryWritingTests extends AbstractBenchmark {

	private StoryAggregate _story;
	private final UUID _guid = UUID.randomUUID();
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup() {
		_story = createMockBuilder(StoryAggregate.class)
				.withConstructor(UUID.class, String.class)
				.withArgs(_guid, "This is a story.").createMock();
	}

	@Test
	public void should_write_story_to_repository() throws Exception {
		UUID guid = UUID.randomUUID();
		String storyText_ = "This is a story.";
		StoryAggregate story_ = new StoryBuilder().setStory(storyText_)
				.setId(guid).create();
		CommandBase<CreateStory> command = new CommandBase<CreateStory>(
				new CreateStory(guid, storyText_, story_));
		command.execute();
		assertThat(StoriesRepository.getStory(guid).getId(), is(equalTo(guid)));
	}

	@Test(expected = NoSuchStoryException.class)
	public void should_throw_exception_for_null_story() throws Exception {
		StoriesRepository.getStory(_guid).getId();
		fail("My method didn't throw when I expected it to");
	}

	@Test
	public void should_event_triggered_by_command() throws Exception {
		String storyText_ = "This is a story.";
		StoryAggregate story_ = new StoryAggregate(_guid, "a");
		CommandBase<CreateStory> command = new CommandBase<CreateStory>(
				new CreateStory(_guid, storyText_, story_));
		command.execute();
		assertThat(StoriesRepository.getEvents(_guid),
				hasItems(Matchers.<Event> hasProperty("version", is(1))));
		thrown.expect(StoryAlreadyCreatedException.class);
		command.execute();
		fail("It should throw exception story already created.");
	}

	@Test
	public void should_story_written_with_two_versions() throws Exception {
		UUID guid = UUID.randomUUID();
		StoryAggregate story_ = new StoryAggregate(guid, "a");
		String storyText_ = "Nothing to add";
		CommandBase<CreateStory> command = new CommandBase<CreateStory>(
				new CreateStory(guid, storyText_, story_));
		command.execute();

		storyText_ = "Here is the Story begins";
		CommandBase<UpdateStoryText> updateCommand = new CommandBase<UpdateStoryText>(
				new UpdateStoryText(guid, storyText_, story_));
		updateCommand.execute();
		storyText_ = "I want to change the beginning";
		updateCommand = new CommandBase<UpdateStoryText>(new UpdateStoryText(
				guid, storyText_, story_));
		updateCommand.execute();
		Queue<Event> events = StoriesRepository.getEvents(guid);
		assertThat(events, not(nullValue()));
		assertThat(events,
				hasItems(Matchers.<Event> hasProperty("version", is(3))));
	}

	@Test
	public void should_build_from_history_without_version_change()
			throws Exception {
		UUID guid = UUID.randomUUID();
		// build object
		String storyText_ = "This is a story.";
		StoryAggregate story_ = new StoryAggregate(guid, "a");
		CommandBase command = new CommandBase<CreateStory>(new CreateStory(
				guid, storyText_, story_));
		command.execute();
		for (int i = 0; i < 50; i++) {
			storyText_ = "Adding new story" + i;
			command = new CommandBase<UpdateStoryText>(new UpdateStoryText(
					guid, storyText_, story_));
			command.execute();
		}

		StoryAggregate testStory_ = StoryAggregate
				.getAggregateFromHistory(guid);
		assertThat(testStory_, hasProperty("version", is(51)));
	}

	@Test
//	@BenchmarkOptions(benchmarkRounds = 5000, warmupRounds = 10, concurrency = 3)
	public void should_hit_optimistic_lock() {
//		System.out.println("test");
	}

	@Test
	public void should_not_create_duplication_story_with_same_guid()
			throws Exception {
		String storyText_ = "This is a story.";
		StoryAggregate story_ = new StoryAggregate(_guid, "a");
		CommandBase command = new CommandBase<CreateStory>(new CreateStory(
				_guid, storyText_, story_));
		command.execute();
		story_ = new StoryAggregate(_guid, "a");
		command = new CommandBase<CreateStory>(new CreateStory(_guid,
				storyText_, story_));
		command.execute();
		assertThat(story_, hasProperty("version", is(2)));
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 5000, warmupRounds = 10, concurrency = 3)
	public void should_handle_concurrent_event_updates() throws Exception {
		String storyText_ = "This is a story.";
		StoryAggregate story_ = new StoryAggregate(_guid, "a");
		CommandBase command = new CommandBase<CreateStory>(new CreateStory(
				_guid, storyText_, story_));
		command.execute();
		Random random = new Random();
		int seed = random.nextInt();
		storyText_ = "Adding new story" + seed;
		command = new CommandBase<UpdateStoryText>(new UpdateStoryText(_guid,
				storyText_, story_));
		command.execute();
		StoryAggregate testStory_ = null;
		int version = story_.getVersion();
		testStory_ = StoryAggregate.getAggregateFromHistory(_guid);
		if (version == testStory_.getVersion()) {
			assertThat(
					testStory_,
					hasProperty("storyText",
							containsString(String.valueOf(seed))));
		} else {
			assertThat(testStory_.getVersion(), greaterThan(version));
		}
	}

	public void should_story_updated_by_two_users() {

	}

	public void should_read_the_current_state_of_story() {

	}
	
	public void should_not_add_new_events_with_same_version(){
		
	}
}