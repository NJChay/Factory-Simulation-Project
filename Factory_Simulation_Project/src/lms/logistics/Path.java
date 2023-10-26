package lms.logistics;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Consumer;

/**
 * Maintains a doubly linked list to maintain the links for each node.
 * Has previous and next item.
 * The path can't have an empty node, as it will throw an illegal
 * argument exception.
 * @version 1.0
 * @ass2
 */
public class Path {

    /**
     * The node corresponding to this Path
     */
    private Transport node;
    /**
     * The previous path (null if none)
     */
    private Path previous;
    /**
     * The next path (null if none)
     */
    private Path next;

    /**
     * Constructs a new Path object with the same Transport node,
     * previous Path, and next Path as the specified Path object.
     * @param path - the Path object to copy.
     * @throws IllegalArgumentException - if the path argument is null.
     */
    public Path(Path path) throws IllegalArgumentException {

        if (path.getNode() == null) {
            throw new IllegalArgumentException();
        }
        this.node = path.getNode();
        this.previous = path.getPrevious();
        this.next = path.getNext();
    }

    /**
     * Constructs a new Path object with the specified Transport node, and sets the previous
     * and next Path objects in the path to null. Throws an
     * IllegalArgumentException if the node argument is null.
     * @param node - the Transport node for this Path.
     * @throws IllegalArgumentException - if the node argument is null.
     */
    public Path(Transport node)
            throws IllegalArgumentException {

        if (node == null) {
            throw new IllegalArgumentException();
        } else {
            this.node = node;
            this.previous = null;
            this.next = null;
        }
    }

    /**
     * Constructs a new Path object with the specified Transport node, and the previous and next Path
     * objects in the path. Throws an IllegalArgumentException if the node argument is null.
     * @param node - the Transport node for this Path.
     * @param previous - the previous Path object in the path.
     * @param next - the next Path object in the path.
     * @throws IllegalArgumentException - if the node argument is null.
     */
    public Path(Transport node, Path previous, Path next)
            throws IllegalArgumentException {
        if (node == null) {
            throw new IllegalArgumentException();

        } else {
            this.node = node;
            this.previous = previous;
            this.next = next;
        }
    }

    /**
     * Returns the head of this Path, which is the first element in the path. If this Path is the first element, it is returned as is.
     * @return the head of this Path.
     */
    public Path head() {

        if (this.previous == null) {
            return this;
        } else {
            return this.getPrevious().head();
        }
    }

    /**
     * Accessor method for the transport node associated with this path.
     * @return the transport node associated with this path
     */

    public Transport getNode() {
        return this.node;
    }

    /**
     * Returns the tail of this Path, which is the last element in the path.
     * If this Path is the last element, it is returned as is.
     * @return the tail of this Path.
     */
    public Path tail() {
        if (this.next == null) {
            return this;
        } else {
            return this.getNext().tail();
        }
    }

    /**
     * Returns the previous Path object in the chain.
     * @return the previous Path object in the chain, or null if this is the first Path object
     */
    public Path getPrevious() {
        return this.previous;
    }

    /**
     * Sets the previous path for this path.
     * @param path - the previous path to be set for this path
     */
    public void setPrevious(Path path) {
        this.previous = path;
    }

    /**
     * Returns the next Path object in the chain.
     * @return the next Path object in the chain, or null if this is the last Path object
     */
    public Path getNext() {
        return this.next;
    }

    /**
     * Sets the next path for this path.
     * @param path - the next path to be set for this path
     */
    public void setNext(Path path) {
        this.next = path;
    }


    /**
     * This method takes a Transport Consumer,
     * using the Consumer&lt;T&gt; functional interface from java.util.
     * It finds the tail of the path and calls
     * Consumer&lt;T&gt;'s accept() method with the tail node as an argument.
     * Then it traverses the Path until the head is reached,
     * calling accept() on all nodes.
     *
     * This is how we call the tick method for all the different transport items.
     *
     * @param consumer Consumer&lt;Transport&gt;
     * @see java.util.function.Consumer
     * @provided
     */
    public void applyAll(Consumer<Transport> consumer) {
        Path path = tail(); // IMPORTANT: go backwards to aid tick
        do {
            consumer.accept(path.node);
            path = path.previous;
        } while (path != null);
    }

    /**
     * Helper method to recursively construct a string
     * representation of a Path based off connected nodes
     * @param path The Path to convert to string
     * @return String representation of the Path
     */

    private String constructPath(Path path) {
        //If the current path is the tail return its node as a string
        if (path.getNext() == null) {
            return  " " + path.getNode().toString() + " -> ";

            //Else return the current paths node and recall the function on the remaining path
        } else {
            return " " + path.getNode().toString()  + " ->" + constructPath(path.getNext());
        }
    }

    /**
     * toString that provides a list of Path nodes from a Producer, along the belt to a Receiver.
     * Hint: This is best done using recursive helper function/s.
     * @return String representing the entirety of the best path links in the format:
     */
    public String toString() {
        return "START ->" + constructPath(this.head()) + "END";
    }

    /**
     * Compares this Path object to the specified object for equality.
     * Returns true if and only if the specified object is also a Path
     * object and has the same Transport node as this Path object.
     * @param o - the object to compare this Path against
     * @return true if the specified object is equal to this Path object, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        //If the object is not a path it cannot be equal to one
        if (o.getClass() != this.getClass()) {
            return false;
        }
        return ((Path) o).getNode() == this.getNode();
    }

}
