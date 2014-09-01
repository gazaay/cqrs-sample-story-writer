package cqrs.sample.story.Command;

public class CommandBase<T extends ICommand> {

	private T _command;

	public CommandBase(T command) {
		_command = command;
	}

	public void execute() throws Exception {
		_command.execute();
	}

}
