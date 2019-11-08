package petrinet.analyses;

/**
 * A value representing infinitely many (or arbitrarily many) tokens in a single place.
 */
public class InfiniteTokenCount extends TokenCount {

    /**
     * Constructs a new {@link TokenCount} designating infinitely many tokens
     */
    InfiniteTokenCount() {
        super(0);
    }

    @Override
    void add(int other) { }

    @Override
    void subtract(int other) { }

    @Override
    InfiniteTokenCount copy() {
        return new InfiniteTokenCount();
    }

    @Override
    public int compareTo(TokenCount other) {
        return equals(other) ? 0 : 1;
    }

    @Override
    protected int doCompareTo(TokenCount other) {
        return equals(other) ? 0 : 1;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof InfiniteTokenCount;
    }

    @Override
    public String toString() {
        return "ω";
    }
}
