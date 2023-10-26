package lms.io;

import lms.exceptions.FileFormatException;
import lms.grid.Coordinate;
import lms.grid.GameGrid;
import lms.grid.GridComponent;
import lms.logistics.Item;
import lms.logistics.Transport;
import lms.logistics.belts.Belt;
import lms.logistics.container.Producer;
import lms.logistics.container.Receiver;

import java.io.*;
import java.util.*;
import java.util.function.IntConsumer;

/**
 *This class is responsible for loading (reading and parsing) a text file containing details
 * required for the creation of a simulated factory represented in the form of a graphical
 * hexagonal grid.
 */
public class GameLoader {

    /**
     * Given a complete list of nodes and coordinates this function
     * distinguishes them by type and sets them in the grd
     * @param grid - the grid being loaded
     * @param type - the type of tile
     * @param coordinate - The position for the tile to be set
     * @param id - Unique id of the tile (if one is applicable)
     * @param nodes - map of ids to tiles in the grid
     */
    private static void tileDecider(GameGrid grid, String type, Coordinate coordinate,
                                    Integer id, Map<Integer, Transport> nodes) {
        switch (type) {
            case "w" -> grid.setCoordinate(coordinate, () -> "w");
            case "o" -> grid.setCoordinate(coordinate, () -> "o");
            default -> grid.setCoordinate(coordinate, nodes.get(id));
        }
    }

    /**
     * Checks to make sure the current path attempting to go into the grid is allowed
     * @param first - the first node
     * @param second - the second node
     * @param direction - the direction (input or output)
     * @throws FileFormatException if an illegal path is identified
     */
    private static void validPath(Transport first, Transport second, String direction)
            throws FileFormatException {

        //Ensures that the path being added to the grid doesn't input to a producer
        if (Objects.equals(direction, "input")) {
            if (first.getClass().equals(Producer.class)
                    && second.getClass().equals(Belt.class)) {
                throw new FileFormatException();
            }

            if (first.getClass().equals(Producer.class)
                    && second.getClass().equals(Producer.class)) {
                throw new FileFormatException();
            }

            if (first.getClass().equals(Producer.class)
                    && second.getClass().equals(Receiver.class)) {
                throw new FileFormatException();
            }
        }
    }

    /**
     * Given all connection information from the file, decides each connection and passes it to
     * directionDecider to be inputted to the grid
     * @param nodes - map of ids to tiles in the grid
     * @param connections - connection information from the file
     * @throws FileFormatException - if there are more ids in the
     * connection list then unique tiles in the grid
     */
    private static void pathHelper(Map<Integer, Transport> nodes, ArrayList<String> connections)
            throws FileFormatException {
        for (String connection : connections) {
            List<String> id = new ArrayList<>(Arrays.asList(connection.split("[-,]")));
            id.removeIf(""::equals);

            //Can't have more path references to nodes than actual nodes
            for (String number : id) {
                if (Integer.parseInt(number) > nodes.size()) {
                    throw new FileFormatException();
                }
            }

            //Comma replaced with "c" to differentiate between array spaces
            List<String> identifiers = new ArrayList<>(Arrays.asList(
                    connection.replace(",", "c").split("[0-9]")));
            identifiers.removeIf(""::equals);

            //"Triple" used to indicate a three node connection (1-2,3)
            if (id.size() == 3) {
                directionDecider(nodes, id, "triple");

                //"Regular" refers to all nodes except for the special case of producers
            } else if (identifiers.size() == 2) {
                directionDecider(nodes, id, "regular");
            } else {
                if (identifiers.get(0).equals("-")) {
                    if (nodes.get(Integer.parseInt(id.get(0))).getClass()
                            .equals(Producer.class)) {
                        directionDecider(nodes, id, "producer");
                    } else {
                        directionDecider(nodes, id, "regular");
                    }
                } else {
                    directionDecider(nodes, id, "producer");
                }
            }
        }
        //Checks the keys for each path match
        for (int i = 1; i < nodes.size(); i++) {
            if (nodes.get(i).getClass().equals(Producer.class)) {
                Receiver tail = (Receiver) nodes.get(i).getPath().tail().getNode();
                if (!nodes.get(i).getInventory().equals(tail.getKey())) {
                    throw new FileFormatException();
                }
            }
        }
    }

    /**
     * Draws 1-2 connections on the grid based on the current path information
     * @param nodes - map of ids to tiles in the grid
     * @param id - unique identifier of the node in the new connection
     * @param direction - decides the direction and quantity of connections
     * @throws FileFormatException if the keys in a given path do not match
     */
    private static void directionDecider(Map<Integer, Transport> nodes, List<String> id,
                                         String direction) throws FileFormatException {

        int input = 0;


        if (direction.equals("producer")) {
            //Flips around the input numbers for the reverse case of producer paths
            input++;

        }
        if (direction.equals("triple")) {
            //Triple paths get an extra connection
            nodes.get(Integer.parseInt(id.get(0)))
                    .setOutput(nodes.get(Integer.parseInt(id.get(2))).getPath());
            validPath(nodes.get(Integer.parseInt(id.get(0))),
                    nodes.get(Integer.parseInt(id.get(2))), "output");

            nodes.get(Integer.parseInt(id.get(2)))
                    .setInput(nodes.get(Integer.parseInt(id.get(0))).getPath());

            validPath(nodes.get(Integer.parseInt(id.get(0))),
                    nodes.get(Integer.parseInt(id.get(2))), "input");
        }
        nodes.get(Integer.parseInt(id.get((1 + input) % 2)))
                .setOutput(nodes.get(Integer.parseInt(id.get(input % 2))).getPath());

        validPath(nodes.get(Integer.parseInt(id.get((1 + input) % 2))),
                nodes.get(Integer.parseInt(id.get((input) % 2))), "output");

        nodes.get(Integer.parseInt(id.get(input % 2))).setInput(
                nodes.get(Integer.parseInt(id.get((1 + input) % 2))).getPath());

        validPath(nodes.get(Integer.parseInt(id.get((input) % 2))),
                nodes.get(Integer.parseInt(id.get((1 + input) % 2))), "input");

    }

    /**
     * Ensures the file is formatted correctly
     * @param reader the current reader that is reading through the file
     * @return true if file is invalid, false if not
     * @throws IOException  if there is an error reading from the reader
     */
    private static boolean fileChecker(BufferedReader reader)
            throws IOException {

        String line = reader.readLine();
        if (!line.matches("[0-9]+")) {
            return Boolean.TRUE;
        }
        int size = Integer.parseInt(line);

        if (!reader.readLine().startsWith("_____")) {
            return Boolean.TRUE;
        }
        int prodItems;
        int recItems;

        if (!(line = reader.readLine()).matches("[0-9]+")) {
            return Boolean.TRUE;
        } else {
            prodItems = Integer.parseInt(line);
        }
        if (!(line = reader.readLine()).matches("[0-9]+")) {
            return Boolean.TRUE;
        } else {
            recItems = Integer.parseInt(line);
        }
        if (!reader.readLine().startsWith("_____")) {
            return Boolean.TRUE;
        }
        //Producer and receiver items lists should have the same contents
        if (!new HashSet<>(List.of(recItems))
                .equals(new HashSet<>(List.of(prodItems)))) {
            return Boolean.TRUE;
        }
        for (int i = 0; i < prodItems; i++) {
            reader.readLine();
        }
        if (!reader.readLine().startsWith("_____")) {
            return Boolean.TRUE;
        }

        for (int i = 0; i < recItems; i++) {
            reader.readLine();
        }
        if (!reader.readLine().startsWith("_____")) {
            return Boolean.TRUE;
        }

        int gridline = 0;
        int linelength = size + 1;

        //Iterates through each line to ensure each has the appropriate size for a hexagon grid
        while (!(line = reader.readLine()).startsWith("_____")) {
            if (!line.matches("[ wopbr]+")) {
                return Boolean.TRUE;
            }
            if (!(line.replace(" ", "").length() == linelength)) {
                return Boolean.TRUE;
            }
            gridline++;
            linelength = gridline < size + 1 ? linelength + 1 : linelength - 1;

        }
        //No illegal characters in the path descriptions
        while (!((line = reader.readLine()) == null)) {
            if (!line.matches("[ 0-9,-]+")) {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }

    /**
     * The load method provides an access point to load and parse the grid map text file.
     * When reading the input grid:
     * p = insert a Producer at this position of the grid
     * b = insert a Belt node at this position of the grid
     * r = insert a Receiver at this position of the grid
     * o or w = insert a lambda into the grid that returns the appropriate character code
     * @param reader - the reader to read from
     * @return the game grid loaded from the reader file
     * @throws IOException  if there is an error reading from the reader
     * @throws FileFormatException if the file is not in the correct format
     * @throws NullPointerException if reader is null
     */
    public static GameGrid load(Reader reader)
            throws IOException, FileFormatException {

        if (reader == null) {
            throw new NullPointerException();
        }
        String line;
        BufferedReader save = new BufferedReader(reader);
        save.mark(10000);

        if (fileChecker(save)) {
            throw new FileFormatException();
        }
        save.reset();
        //System.out.print(save.readLine());

        int range = Integer.parseInt(save.readLine());

        GameGrid grid = new GameGrid(range);

        //Establish number of producers and receivers
        for (int i = 0; i < 4; i++) {
            save.readLine();
        }
        ArrayList<Item> prodItems = new ArrayList<>();

        //Second Section: producer items
        // If file is formatted incorrectly with throw FileFormatException
        while (!(line = save.readLine()).startsWith("_____")) {
            prodItems.add(new Item(line));
        }

        ArrayList<Item> recItems = new ArrayList<>();

        //Third Section: receiver items
        while (!(line = save.readLine()).startsWith("_____")) {
            recItems.add(new Item(line));
        }

        //Fourth Section: tile type
        ArrayList<String> letters = new ArrayList<>();
        while (!(line = save.readLine()).startsWith("_____")) {
            line = line.replaceAll(" ", "");
            letters.addAll(List.of(line.split("")));
        }

        Map<Integer, Transport> nodes = new HashMap<>();
        //Builds a map of unique nodes based off grid information
        int id = 1;
        for (String letter : letters) {
            switch (letter) {
                case "p" -> {
                    nodes.put(id, new Producer(id, prodItems.get(0)));
                    prodItems.remove(0);
                    id++;
                }
                case "b" -> {
                    nodes.put(id, new Belt(id));
                    id++;
                }
                case "r" -> {
                    nodes.put(id, new Receiver(id, recItems.get(0)));
                    recItems.remove(0);
                    id++;
                }
            }
        }
        ArrayList<String> connections = new ArrayList<>();
        while (!((line = save.readLine()) == null)) {
            connections.add(line);
        }
        pathHelper(nodes, connections);

        //Iterates through each spot on the grid, taking care to model the hexagonal shape
        //through the variables nextSize which describes the size of the next line and range
        // which relates to the total number of line through the formula lines = range * 2 + 1
        int nextSize = range + 1;
        int currentTileIndex = 0;
        int uniqueId = 1;
        for (int i = 0; i < range * 2 + 1; i++) {
            for (int ii = 0; ii < nextSize; ii++) {

                tileDecider(grid, letters.get(currentTileIndex), new Coordinate(
                        i < range + 1 ? ii - i : ii - range, i - range), uniqueId, nodes);

                if (!(letters.get(currentTileIndex).equals("o")
                        || letters.get(currentTileIndex).equals("w"))) {
                    uniqueId++;
                }
                currentTileIndex++;
            }
            if (i < range) {
                nextSize = nextSize + 1;
            } else {
                nextSize = nextSize - 1;
            }
        }
        return grid;
    }
}
