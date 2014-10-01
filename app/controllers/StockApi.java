package controllers;

import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;

public class StockApi extends Controller {

    public static Result javascriptRoutes() {
        return ok(Routes.javascriptRouter("stockApiJavascriptRoutes")).as("text/javascript");
    }

}
