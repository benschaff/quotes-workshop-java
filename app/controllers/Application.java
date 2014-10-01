package controllers;

import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

    public static Result index() {
        return ok(views.html.index.render());
    }

    public static Result javascriptRoutes() {
        return ok(Routes.javascriptRouter(
                "javascriptRoutes"
        )).as("text/javascript");
    }

}
