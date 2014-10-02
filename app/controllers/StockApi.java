package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.ChartRequest;
import models.ChartRequestElement;
import models.Symbol;
import org.joda.time.DateTimeConstants;
import play.Logger;
import play.Play;
import play.Routes;
import play.cache.Cache;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.*;

public class StockApi extends Controller {

    private final static String SYMBOLS_API_URL = Play.application().configuration().getString("markitondemand-api.lookup");

    private final static String CHART_DATA_API_URL = Play.application().configuration().getString("markitondemand-api.chartData");

    private final static Logger.ALogger logger = Logger.of(StockApi.class);

    public static F.Promise<Result> symbols(final String query) {
        return WS.url(SYMBOLS_API_URL).setQueryParameter("input", query).get().map(
            new F.Function<WSResponse, Result>() {
                public Result apply(final WSResponse response) {
                    final Set<Symbol> symbols = new LinkedHashSet<>();

                    final Iterator<JsonNode> jsonNodeIterator = response.asJson().iterator();
                    while (jsonNodeIterator.hasNext()) {
                        symbols.add(new Symbol(jsonNodeIterator.next()));
                    }

                    return ok(Json.toJson(symbols));
                }
            }
        ).recover(
            new F.Function<Throwable, Result>() {
                public Result apply(final Throwable throwable) {
                    logger.error("An error occurred while calling {} with query {}.", SYMBOLS_API_URL, query, throwable);

                    return ok(Json.toJson(new ArrayList<>()));
                }
            }
        );
    }

    public static F.Promise<Result> last30Days(final String symbol) {
        final JsonNode data = (JsonNode) Cache.get(symbol + ".chartData.last30Days");
        if (data != null) {
            return F.Promise.<Result>pure(ok(data));
        } else {
            final String parameters = Json.stringify(Json.toJson(new ChartRequest(Arrays.asList(new ChartRequestElement(symbol)))));

            final WSRequestHolder request = WS.url(CHART_DATA_API_URL).setQueryParameter("parameters", parameters);

            return request.get().map(
                    new F.Function<WSResponse, Result>() {
                        public Result apply(final WSResponse response) {
                            final JsonNode jsonNode = response.asJson();

                            Cache.set(symbol + ".chartData.last30Days", jsonNode, DateTimeConstants.SECONDS_PER_DAY);

                            return ok(jsonNode);
                        }
                    }
            ).recover(
                    new F.Function<Throwable, Result>() {
                        public Result apply(final Throwable throwable) {
                            logger.error("An error occurred while calling {} with symbol {}.", CHART_DATA_API_URL, symbol, throwable);

                            return ok(Json.toJson(new ArrayList<>()));
                        }
                    }
            );
        }
    }

    public static Result javascriptRoutes() {
        return ok(Routes.javascriptRouter(
                "stockApiJavascriptRoutes",
                routes.javascript.StockApi.last30Days(),
                routes.javascript.StockApi.symbols()
        )).as("text/javascript");
    }

}
