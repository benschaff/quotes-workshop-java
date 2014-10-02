package models;

import java.util.Arrays;
import java.util.List;

public class ChartRequestElement {

    public final String symbol;

    public final String value = "price";

    public final List<String> params = Arrays.asList("c");

    public ChartRequestElement(final String symbol) {
        this.symbol = symbol;
    }

}
