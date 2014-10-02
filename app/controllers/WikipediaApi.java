package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Symbol;
import org.joda.time.DateTimeConstants;
import play.Logger;
import play.Play;
import play.Routes;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.Iterator;

public class WikipediaApi extends Controller {

    private final static Logger.ALogger logger = Logger.of(WikipediaApi.class);

    private static final String WIKIPEDIA_API_PAGE_SEARCH = Play.application().configuration().getString("wikipedia-api.lookup");

    private static final String WIKIPEDIA_API_PAGE_GET = Play.application().configuration().getString("wikipedia-api.page");

    public static F.Promise<Result> vcard() {
        final Symbol symbol = new Symbol(request().body().asJson());

        return WS.url(WIKIPEDIA_API_PAGE_SEARCH).setQueryParameter("titles", toWikipediaApiLookupQueryPath(symbol)).get().flatMap(
                new F.Function<WSResponse, F.Promise<Result>>() {
                    @Override
                    public F.Promise<Result> apply(final WSResponse response) {
                        final JsonNode jsonNode = response.asJson();
                        if (!jsonNode.has("query")) {
                            return F.Promise.<Result>pure(ok());
                        }

                        final JsonNode queryNode = jsonNode.get("query");
                        if (!queryNode.has("pages")) {
                            return F.Promise.<Result>pure(ok());
                        }

                        final JsonNode pagesNode = queryNode.get("pages");

                        final Iterator<JsonNode> pages = pagesNode.iterator();
                        if (pages.hasNext()) {
                            final int page = pages.next().get("pageid").asInt();

                            return WS.url(WIKIPEDIA_API_PAGE_GET).setQueryParameter("pageids", String.valueOf(page)).get().map(
                                    new F.Function<WSResponse, Result>() {
                                        @Override
                                        public Result apply(final WSResponse response) {
                                            final JsonNode jsonNode = response.asJson();
                                            if (!jsonNode.has("query")) {
                                                return ok();
                                            }

                                            final JsonNode queryNode = jsonNode.get("query");
                                            if (!queryNode.has("pages")) {
                                                return ok();
                                            }

                                            final JsonNode pagesNode = queryNode.get("pages");
                                            if (pagesNode.has(String.valueOf(page))) {
                                                return ok(WS.url(pagesNode.get(String.valueOf(page)).get("fullurl").asText()).get().get(DateTimeConstants.MILLIS_PER_MINUTE).getBodyAsStream());
                                            }

                                            return ok();
                                        }
                                    }
                            );
                        }

                        return F.Promise.<Result>pure(ok(response.asJson()));
                    }
                }
        ).recover(
                new F.Function<Throwable, Result>() {
                    public Result apply(final Throwable throwable) {
                        logger.error("An error occurred while calling {} with query {}.", WIKIPEDIA_API_PAGE_SEARCH, symbol, throwable);

                        return ok(Json.toJson(new ArrayList<>()));
                    }
                }
        );
    }

    private static String toWikipediaApiLookupQueryPath(final Symbol symbol) {
        return symbol.symbol + "|" + symbol.name + "|" + symbol.name.split(" ")[0];
    }

    public static Result javascriptRoutes() {
        return ok(Routes.javascriptRouter(
                "wikipediaApiJavascriptRoutes",
                routes.javascript.WikipediaApi.vcard()
        )).as("text/javascript");
    }

}
