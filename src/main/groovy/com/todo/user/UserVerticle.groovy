package com.todo.user

import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.templ.FreeMarkerTemplateEngine

class UserVerticle extends AbstractVerticle {
    JDBCClient client = null
    FreeMarkerTemplateEngine engine = null

    public void start() {
        JsonObject config = new JsonObject()
                .put("url", "jdbc:mysql://localhost:3306/rock_hackathon?autoreconnect=true")
                .put("user", "root")
                .put("password", "nextdefault")
                .put("driver_class", "com.mysql.jdbc.Driver")
                .put("max_pool_size", 30)
        client = JDBCClient.createShared(vertx, config)
        engine = FreeMarkerTemplateEngine.create()
        Router router = Router.router(vertx)
        router.route().handler(BodyHandler.create())

        router.route("/*").handler(this.&signup)
        router.get("/user").handler(this.&signup)
        router.get("/signup").handler(this.&signup)
//        router.get("/logout").handler(this.&logOut)
//        router.get("/users").handler(this.&showUsers)
//        router.post("/saveUser").handler(this.&saveUserMeth)
//        router.post("/loginAuth").handler(this.&loginAuth)
//        vertx.createHttpServer().requestHandler(router.&accept).listen(8085)
    }

    void signup(RoutingContext ctx) {
        println("---------")
        engine.render(ctx, "templates/login.ftl", { res ->
            ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end(res.result())
        })
    }
}
