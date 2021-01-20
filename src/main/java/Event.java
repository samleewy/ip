package main.java;

public class Event extends Task {
    protected String at;

    Event(String name, String at) {
        super(name, "Event coming right up!");
        this.at = at;
    }

    public String toString() {
        return "[E]" + super.toString() + " (at: " + at + ")";
    }
}