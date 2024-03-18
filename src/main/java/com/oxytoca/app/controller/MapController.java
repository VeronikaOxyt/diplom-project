package com.oxytoca.app.controller;

import javax.script.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MapController {
    @GetMapping("/location")
    public String activityLocation() throws ScriptException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        String locationAct = "Рязань, Геккон";
        String pathToScript = "src/main/resources/static/js/script-map.js";

        engine.put("locationAct", locationAct);
        engine.eval("load('" + pathToScript + "');");
        return "map";
    }
}
