import commands.Constants;
import entity.Command;
import entity.Item;
import entity.Room;
import util.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private final Parser parser;
    private Room currentRoom;
    private final Room entry;
    private final Room toilet;
    private final Room livingroom;
    private final Room bedroom;
    private final Room office;
    private final Room kitchen;
    private Room roomToGoBack;
    private final List<Item> pickedUpItems;
    private int totalWeight;

    public Game() {
        pickedUpItems = new ArrayList<>();
        totalWeight = 0;
        parser = new Parser(System.in);

        entry = new Room("in the entryroom");
        toilet = new Room("in the toilet");
        livingroom = new Room("in the livingroom");
        bedroom = new Room("in the bedroom");
        office = new Room("in the office");
        kitchen = new Room("in the kitchen");

        entry.setExits(null, toilet, bedroom, livingroom);
        toilet.setExits(null, null, null, entry);
        livingroom.setExits(null, entry, null, null);
        bedroom.setExits(entry, office, null, null);
        office.setExits(null, null, null, bedroom);
        kitchen.setExits(livingroom, null,null,null);

        currentRoom = entry;
        roomToGoBack = entry;
    }

    public void play() {
        generateItems();
        printWelcome();

        boolean finished = false;
        while (!finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    private void printWelcome() {
        System.out.println();
        System.out.println("Welcome to Zork Game!");
        System.out.println("Zork is a simple adventure game.");
        System.out.println("Pick up Items and when you think they're worth enough,");
        System.out.println("tpye 'quit' and see if you completed the challenge");
        System.out.println();
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println(currentRoom.longDescription());
    }

    private boolean processCommand(Command command) {
        if (command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord();

        switch (commandWord) {
            case "help" -> printHelp();
            case "go" -> goRoom(command);
            case "quit" -> quitGame();
            case "back" -> {
                currentRoom = roomToGoBack;
                System.out.println(currentRoom.longDescription());
            }
            case "pickup" -> pickUpItem();
            case "drop" -> dropItem();
        }

        return false;
    }

    private void printHelp() {
        System.out.println("Your command words are:");
        System.out.println(parser.showCommands());
    }

    private void goRoom(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Go where?");
        } else {

            String direction = command.getSecondWord();

            Room nextRoom = currentRoom.nextRoom(direction);

            if (nextRoom == null)
                System.out.println("There is no door!");
            else {
                roomToGoBack = currentRoom;
                currentRoom = nextRoom;
                System.out.println(currentRoom.longDescription());
            }
        }
    }

    private void generateItems() {
        List<Item> items = new ArrayList<>();
        items.add(new Item("Beer", 5, 1));
        items.add(new Item("Baseball bat", 30, 5));
        items.add(new Item("Wine", 20, 3));
        items.add(new Item("Apples", 10, 10));
        items.add(new Item("Jacket", 100, 15));
        items.add(new Item("Laptop", 400, 2));
        items.add(new Item("Pen", 30, 1));
        items.add(new Item("Monitor", 200, 7));
        items.add(new Item("Keyboard", 20, 1));
        items.add(new Item("Basketball", 50, 3));
        items.add(new Item("Shoe", 100, 1));
        items.add(new Item("Stick", 1, 1));
        items.add(new Item("T-shirt", 200, 1));
        items.add(new Item("Phone", 200, 2));
        items.add(new Item("Rock", 1000, 20));
        addItemsToRooms(items);
    }

    public void addItemsToRooms(List<Item> items) {
        Random rand = new Random();
        for (Item item : items) {
            int randNum = rand.nextInt(6);
            switch (randNum) {
                case 1 -> entry.addItemToRoom(item);
                case 2 -> toilet.addItemToRoom(item);
                case 3 -> livingroom.addItemToRoom(item);
                case 4 -> bedroom.addItemToRoom(item);
                case 5 -> office.addItemToRoom(item);
            }
        }
    }

    private void dropItem() {
        boolean found = false;
        Item itemToRemove = null;
        Scanner keyBoard = new Scanner(System.in);
        System.out.println("Inventory ");
        for (Item item : pickedUpItems) {
            System.out.println("Item: " + item.getName() + " Weight: " + item.getWeight());
        }
        System.out.println("Enter the name of item to drop: ");
        String itemName = keyBoard.nextLine();
        for (Item item : pickedUpItems) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                currentRoom.addItemToRoom(item);
                totalWeight -= item.getWeight();
                System.out.println("Item dropped");
                itemToRemove = item;
                found = true;
            }
        }
        if (found) {
            pickedUpItems.remove(itemToRemove);
            System.out.println(currentRoom.longDescription());
        } else {
            System.out.println("Couldn't find item. Please retry with drop command");
        }
    }

    private void pickUpItem() {
        boolean found = false;
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Which item? ");
        System.out.println("If you don't want to pickup an item then enter: continue");
        while (!found) {
            String itemName = keyboard.nextLine();
            if (itemName.equalsIgnoreCase("continue")) {
                found = true;
                System.out.println(currentRoom.longDescription());
            }
            List<Item> items = currentRoom.getItems();
            for (Item item : items) {
                if (item.getName().equalsIgnoreCase(itemName)) {
                    if (item.getWeight() + totalWeight <= Constants.MAX_WEIGHT) {
                        pickedUpItems.add(item);
                        totalWeight += item.getWeight();
                        currentRoom.removeItemFromRoom(itemName);
                        System.out.println(itemName + " added to inventory");
                    } else {
                        System.out.println("You cannot pickup this item. It's exceeding your total weight");
                        System.out.println("If you want to pickup this item you first have to drop another item");
                    }
                    found = true;
                    System.out.println(currentRoom.longDescription());
                }
                if (found) {
                    break;
                }
            }
            if (!found) {
                System.out.println("Could not find this item. Please reenter the name of it or continue");
            }
        }
    }

    private void quitGame() {
        int sum = 0;
        for (Item item : pickedUpItems) {
            sum += item.getValue();
        }
        System.out.println("Your total money: " + sum);
        if (sum >= 1000) {
            System.out.println("You successfully completed the challenge! Congrats");
        } else {
            System.out.println("Sadly, you did not have enough money together to complete the challenge");
        }
        System.out.println("Thanks for playing!");
        System.exit(0);
    }

    public Room getEntry() {
        return entry;
    }

    public Room getToilet() {
        return toilet;
    }

    public Room getTavern() {
        return livingroom;
    }

    public Room getBedroom() {
        return bedroom;
    }

    public Room getOffice() {
        return office;
    }
    
    public Room getKitchen(){
        return kitchen;
    }
}
