package com.example.lendahand;

public class Recipient {
    private int id;
    private String name;
    private String bio;
    private int amountNeeded;

    public Recipient(int id, String name, String bio, int amountNeeded) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.amountNeeded = amountNeeded;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getBio() { return bio; }
    public int getAmountNeeded() { return amountNeeded; }
}