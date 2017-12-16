package com.todo

import com.bo.UserBO
import com.util.SqlUtil
import com.todo.mail.SendEmail
import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.jdbc.JDBCAuth
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.sql.SQLConnection
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.Session
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.SessionHandler
import io.vertx.ext.web.sstore.LocalSessionStore
import io.vertx.ext.web.templ.FreeMarkerTemplateEngine

class BasicCrud extends AbstractVerticle {
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

        router.route("/*").handler(this.&showFirst)
        router.get("/").handler(this.&showForm)
        router.get("/logout").handler(this.&logOut)
        router.get("/users").handler(this.&showUsers)
        router.get("/mailTrigeer").handler(this.&trigerNowMail)
        router.post("/saveUser").handler(this.&saveUserMeth)
        router.post("/loginAuth").handler(this.&loginAuth)
        router.get("/test").handler(this.&test)
        vertx.createHttpServer().requestHandler(router.&accept).listen(8085)
    }


    void showForm(RoutingContext ctx) {
        SendEmail.triggerNow("anubhav@fintechlabs.in", "Test First", "Hello welcome to using vertx",vertx)
        bootStrapUser(ctx)
        engine.render(ctx, "templates/loginPage.ftl", { res ->
            ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end(res.result())
        })
    }

    void showFirst(RoutingContext ctx) {
        client.getConnection({ res ->
            if (res.failed()) {
                ctx.fail(res.cause())
            } else {
                SQLConnection conn = res.result()

                ctx.put("conn", conn)
                ctx.addHeadersEndHandler({ done ->
                    conn.close({ v ->

                    })
                })
                ctx.next()
            }
        })
        /* engine.render(ctx, "templates/createUser.ftl", { res ->
             ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end(res.result())
         })*/
    }

    void saveUserMeth(RoutingContext ctx) {
        println("---------- Saving Record ---------")
        SQLConnection conn = ctx.get("conn")
        conn.updateWithParams("INSERT INTO USER (name, username) VALUES (?, ?)",
                new JsonArray()
                        .add(ctx.request().getFormAttribute("name"))
                        .add(ctx.request().getFormAttribute("username")),
//                        .add(ctx.request().getFormAttribute("address")),
                { query ->
                    if (query.failed()) {
                        sendError(500, response)
                    } else {
                        ctx.response().end("Record Inserted")
                    }
                })
    }

    void showUsers() {

    }

    void loginAuth(RoutingContext ctx) {
        SQLConnection connection = ctx.get("conn")
        JsonObject authInfo = new JsonObject().put("username", "${ctx.request().getFormAttribute("emailId")}").put("password", "${ctx.request().getFormAttribute("password")}");
        JDBCAuth authProvider = JDBCAuth.create(vertx, client);
        authProvider.authenticate(authInfo, { res ->
            if (res.succeeded()) {
                Router router = Router.router(vertx)
                println(res.result());
//                router.route().handler(CookieHandler.create());
                router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));

                engine.render(ctx, "templates/successLogin.ftl", { res1 ->
                    ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end(res1.result())
                })
            } else {
                println("error----${res}-----------")
                ctx.put("errorMessage", "${res.cause()?.toString()}")
                ctx.response().putHeader("location", "/").setStatusCode(302).end();
            }
        });
    }

    void bootStrapUser(RoutingContext ctx) {
        SQLConnection conn
        client.getConnection({ res ->
            if (res.failed()) {
                println("000000000000000000000000000000")
                ctx.fail(res.cause())
            } else {
                println("666666666666666666666666666666666")
                conn = res.result()
                List<String> usernames = ["anubhav@email.com", "akash@email.com"]
                String queryStr = "SELECT * FROM USER where username = ?"

                JDBCAuth auth = JDBCAuth.create(vertx, client);
                usernames.each {
                    conn.queryWithParams(queryStr, new JsonArray().add(it), { query ->
                        if (query.failed()) {
                            println query.cause()
                            sendError(500, response)
                        } else {
                            if (query.result().getNumRows() == 0) {
                                println("=============== ${query.result()}")
                                auth.setNonces(new JsonArray().add("random_hash_1").add("random_hash_1"));
                                String salt = auth.generateSalt();
                                String hash = auth.computeHash("123456", salt);
// save to the database
                                conn.updateWithParams("INSERT INTO USER VALUES (?, ?, ?)", new JsonArray().add(it).add(hash).add(salt), { res1 ->
                                    if (res1.succeeded()) {
                                        // success!
                                        println("------ created user ===== ${it}")
                                    } else {
                                        println("----- error --- ${res.cause()}")
                                    }
                                });

                            }
                        }
                    })
                }

            }
        })
    }

    void logOut(RoutingContext context) {
        final Session session = context.session()
        /*
        session.remove("login");
        session.remove("userId");
        String accessToken=session.get("accessToken");
        if (accessToken != null) {
            context.vertx().sharedData().getLocalMap("access_tokens").remove(accessToken);
        }*/
        context.response().putHeader("location", "/").setStatusCode(302).end();
    }

    void trigerNowMail() {
    }


    void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end()
    }

    void test(RoutingContext ctx) {

        println("---------- Testing called---------")
        SQLConnection conn = ctx.get("conn")
        conn.queryWithParams(SqlUtil.queryUserListWithRole(), new JsonArray(), { query ->
            if (query.failed()) {
                println query.cause()
                sendError(500, response)
            } else {
                if (query.result().getNumRows() > 0) {
                    String json = query.result().results.toString()
                    println json
                    JsonArray array = new JsonArray()
                    ArrayList<UserBO> userList = []
                    query.result().results.each {
                        UserBO bo = new UserBO(it)
                        userList.add(bo)
                    }
                    println array
                    userList.each {
                        println it.username
                    }
                } else println 'no records found'
            }
        })
    }
}
