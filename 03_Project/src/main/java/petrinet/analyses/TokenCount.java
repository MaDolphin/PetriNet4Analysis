package petrinet.analysis;

/**
 * A representation of the number of tokens in a place
 */
class TokenCount implements Comparable<TokenCount> {

    private int value;

    TokenCount(int value) {
        this.value = value;
    }

    /**
     * Increase the number of tokens, e.g. through an incoming transition
     * @param other the number of tokens added
     */
    void add(int other) {
        value += other;
    }

    /**
     * Decrease the number of tokens, e.g. through an outgoing transition
     * @param other the number of tokens removed
     */
    void subtract(int other) {
        if (other > value) {
            throw new UnsupportedOperationException("Not enough tokens");
        }
        value -= other;
    }

    /**
     * Creates a new {@link TokenCount} with the same value that can be modified independently
     * @return a copy of the current object
     */
    TokenCount copy() {
        return new TokenCount(value);
    }

    @Override
    public int compareTo(TokenCount other) {
        return -other.doCompareTo(this);
    }

    /**
     * Compare the value of two token counts. In order for subclasses to modify both left-hand and right-hand comparison,
     * both {@link Comparable#compareTo(Object)} and this method need to be overridden and return
     * the same value; subclasses should determine whether interaction with other subclasses is possible.
     * @param other Another {@link TokenCount} that should be compared to this object
     * @return A positive value if this object is larger than {@code other}; 0 if they are considered equal;
     * a negative value if this object is smaller than {@code other}.
     */
    int doCompareTo(TokenCount other) {
        return value - other.value;
    }

    /**
     * Compare the value of this object to an integer. This is for example needed to determine transition fireability
     * and safeness.
     * @param other A number of tokens
     * @return A positive value if more than {@code other} tokens are in this place; 0 if they are equal; a negative
     * value if there are less than {@code other} tokens.
     */
    final int compareTo(int other) {
        return compareTo(new TokenCount(other));
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof TokenCount) && ((TokenCount) other).value == value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
