package cqrs.sample.story.Command;

public interface ICommand {
	public void execute() throws Exception;
}
