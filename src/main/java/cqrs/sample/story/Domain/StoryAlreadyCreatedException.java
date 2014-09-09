package cqrs.sample.story.Domain;

public class StoryAlreadyCreatedException extends Exception {

	public StoryAlreadyCreatedException(String arg0) {
		super(arg0);
	}

	public StoryAlreadyCreatedException() {
		// TODO Auto-generated constructor stub
	}

}
