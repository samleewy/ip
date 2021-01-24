package main.java;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import java.util.*;
import java.util.Scanner;

public class Duke {

    private static List<Task> tasks = new ArrayList<Task>();

    /**
     * Returns message with additional header 'Olly', the chat bot name
     * @param message: Message to be made by Olly
     */
    public static void ollySpeak(String message) {
        System.out.println("Olly: " + message);
    }

    /**
     * Main engine which handles the input from the user
     * Currently supported inputs: list, todo, deadline, event, done, delete.
     * Throws DukeException upon incorrect command from user
     * @param input: Input message from user
     * @throws DukeException
     */
    private static void inputHandler(String input) throws DukeException {
        if (input.equals("bye")) {
            ollySpeak("Goodbye for now, we will meet again.");
            System.exit(0);
        }

        if (input.contains("list")) {
            String[] args = input.split(" ");

            if (args.length == 1) {
                printTasks();
            } else if (args.length == 2) {
                String criteria = args[1];
                if (criteria.equals("today")) {
                    printTasks(LocalDate.now());
                } else if (criteria.equals("tomorrow") || criteria.equals("tmr")) {
                    printTasks(LocalDate.now().plus(1, ChronoUnit.DAYS));
                } else {
                    try {
                        LocalDate date = LocalDate.parse(criteria);
                        printTasks(date);
                    } catch (DateTimeParseException dtEx) {
                        ollySpeak("Your date/time must be in the yyyy-mm-dd format. Please try again!");
                    }
                }
            }
        } else if (input.startsWith("todo")) {
            String[] command = input.split("todo ");
            if (command.length == 1) throw new DukeException("The description of a todo cannot be empty.");
            Todo todo = new Todo(command[1]);
            addTask(todo);
        } else if (input.startsWith("deadline")) {
            String[] command = input.split("deadline ");
            if (command.length == 1) throw new DukeException("The description of a deadline cannot be empty.");
            String deadlineArg = command[1];

            String[] byArgs = deadlineArg.split(" /by ");
            if (byArgs.length < 2) throw new DukeException("There must be a date for deadline.");

            try {
                String dateString = byArgs[1];
                LocalDate date = LocalDate.parse(dateString);
                Deadline deadline = new Deadline(byArgs[0], date);
                addTask(deadline);
            } catch (DateTimeParseException dtEx) {
                ollySpeak("Your date/time must be in the yyyy-mm-dd format. Please try again!");
            }

        } else if (input.startsWith("event")) {
            String[] command = input.split("event ");
            if (command.length == 1) throw new DukeException("The description of a event cannot be empty.");
            String eventArg = command[1];

            String[] atArgs = eventArg.split(" /at ");
            if (atArgs.length < 2) throw new DukeException("There must be a date for event.");

            try {
                String dateString = atArgs[1];
                LocalDate date = LocalDate.parse(dateString);
                Event event = new Event(atArgs[0], date);
                addTask(event);
            } catch (DateTimeParseException dtEx) {
                ollySpeak("Your date/time must be in the yyyy-mm-dd format. Please try again!");
            }
        } else if (input.startsWith("done")) {
            String[] command = input.split(" ");
            int index = Integer.parseInt(command[1]);
            if (index > 0 && index <= tasks.size()) {
                Task doneTask = tasks.get(index - 1);
                doneTask.setDone();
                ollySpeak("Swee! This task is done:");
                System.out.println(doneTask);
            } else {
                ollySpeak("The task number does not work, try again?");
            }
        } else if (input.startsWith("delete")) {
            String[] command = input.split(" ");
            int index = Integer.parseInt(command[1]);
            if (index > 0 && index <= tasks.size()) {
                Task  deletedTask = tasks.get(index - 1);
                deleteTask(deletedTask);
            } else {
                ollySpeak("The task number does not work, try again?");
            }
        } else {
            throw new DukeException("I don't understand your language leh. Speak singlish?");
        }
    }

    /**
     * Wrapper for getting the total task count of Olly (Duke)
     * @return number of tasks
     */
    private static int getTaskCount() {
        return tasks.size();
    }

    /**
     * Add task to the current list of tasks that Olly is handling
     * @param task: Supports Event, Todo, Deadline tasks (any child class inheriting from Task)
     */
    private static void addTask(Task task) {
        tasks.add(task);
        ollySpeak(task.addMessage + (task.addMessage == null ? "" : " ") + "I've added:");
        System.out.println(task);
        ollySpeak("You now have " + getTaskCount() + " tasks at hand.");
    }

    /**
     * Deletes task from task list and informs user of the task that has been removed
     * @param task: Task to be removed
     */
    private static void deleteTask(Task task) {
        tasks.remove(task);
        ollySpeak("Aww man.. I've removed this task:");
        System.out.println(task);
        ollySpeak("Now you have " + getTaskCount() + " tasks left.");
    }

    /**
     * Prints out the list of task that the user currently has. The tasks are ordered numerically in the sequence
     * in which it was inserted.
     */
    private static void printTasks() {
        if (getTaskCount() == 0) {
            ollySpeak("You currently have no tasks! Use todo, deadline or event.");
        } else {
            ollySpeak("Here you go! Your list of items:");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println(i+1 + ". " + tasks.get(i));
            }
        }
    }

    private static void printTasks(LocalDate date) {
        if (getTaskCount() == 0) {
            ollySpeak("You currently have no tasks! Use todo, deadline or event.");
        } else {
            ollySpeak("Here you go! Your list of items:");
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).date != null && tasks.get(i).date.isEqual(date)) {
                    System.out.println(i+1 + ". " + tasks.get(i));
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ollySpeak("Hey! Welcome to the chatbot. What can I do for you today?");

        while (sc.hasNext()) {
            String input = sc.nextLine();
            try {
                inputHandler(input);
            } catch (DukeException dukeEx) {
                System.out.println(dukeEx);
            }
        }
    }
}
