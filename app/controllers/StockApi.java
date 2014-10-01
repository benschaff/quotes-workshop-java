package controllers;

import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;

public class StockApi extends Controller {

    public static Result symbols(final String query) {
        return ok();
    }

    public static Result last30Days(final String symbol) {
        return ok();
    }

    public static Result javascriptRoutes() {
        return ok(Routes.javascriptRouter(
                "stockApiJavascriptRoutes",
                routes.javascript.StockApi.last30Days(),
                routes.javascript.StockApi.symbols()
        )).as("text/javascript");
    }

}
