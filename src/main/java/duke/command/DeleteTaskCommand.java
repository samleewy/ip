package main.java.duke.command;

import main.java.duke.DukeException;
import main.java.duke.Storage;
import main.java.duke.TaskList;
import main.java.duke.Ui;
import main.java.duke.task.Task;

public class DeleteTaskCommand extends Command {
    private int index;

    public DeleteTaskCommand(int index) {
        super();
        this.index = index - 1;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        if (index < 0 || index >= tasks.size()) {
            throw new DukeException("The task number does not work, try again?");
        }
        Task deleteTask = tasks.find(index);
        tasks.delete(deleteTask);
        ui.speak("Aww yes! I've removed this task:");
        System.out.println(deleteTask);
        ui.speak("Now you have " + tasks.size() + " tasks left.");
        storage.writeToFile(tasks);
    }
}