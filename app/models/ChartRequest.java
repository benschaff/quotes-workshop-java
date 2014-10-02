package models;

import java.util.List;

public class ChartRequest {

    public final boolean normalized = false;

    public final int numberOfDays = 30;

    public final String dataPeriod = "Day";

    public final List<ChartRequestElement> elements;

    public ChartRequest(final List<ChartRequestElement> elements) {
        this.elements = elements;
    }

}
