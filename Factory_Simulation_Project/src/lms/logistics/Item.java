package lms.logistics;

/**
 * Class to manage the name of an Item object. Provides implementations for equals, hashcode, and toString.
 */

public class Item {

    /**
     * Name of the item
     */
    private String name;

    /**
     * The constructor to instantiate an Item
     * @param name - name of the Item instance
     * @throws IllegalArgumentException
     * -  if the item name is null or empty string
     */

    public Item(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException();
        } else {
            this.name = name;
        }
    }

    /**
     * Default and expected implementation specific to the needs of the comparison requirements.
     * Indicates whether some other object is "equal to" this one
     * @param o - the object to compare for equality
     * @return true if the given object is equal to this object; false otherwise
     */

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        return this.hashCode() == o.hashCode();
    }

    /**
     * Hashcode implementation, where hashcode is calculated based on the item's name
     * @return A hashcode calculated based on the item's name.
     */

    public int hashCode() {
        int hash = 1;
        // A unique integer is created by assigning a word the
        // product of the ascii values making up the words characters
        for (int i = 0; i < this.name.length(); i++) {
            hash *= this.name.charAt(i);
        }
        return hash;
    }

    /**
     * A String representation of the Item.
     * @return the Item name.
     */

    public String toString() {
        return this.name;
    }

}
