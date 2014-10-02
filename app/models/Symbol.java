package models;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

public class Symbol {

    public final String name;

    public final String symbol;

    public Symbol(final JsonNode jsonNode) {
        this.name = jsonNode.get("Name").asText();
        this.symbol = jsonNode.get("Symbol").asText();
    }

    public boolean equals(final Object other) {
        if (this.getClass() != other.getClass()) return false;

        final Symbol otherSymbol = (Symbol) other;
        return Objects.equals(this.name, otherSymbol.name) && Objects.equals(this.symbol, otherSymbol.symbol);
    }

    public int hashCode() {
        return Objects.hash(this.name, this.symbol);
    }

}
