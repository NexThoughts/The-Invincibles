package com.todo

import com.bo.ProjectBO
import com.bo.TaskBO
import com.bo.UserBO
import com.bo.UserProjectBO
import com.todo.mail.SendEmail
import com.util.AppUtil
import com.util.SqlUtil
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
    SQLConnection conn
    FreeMarkerTemplateEngine engine = null

    public void start() {
        JsonObject config = new JsonObject()
                .put("url", "jdbc:mysql://localhost:3306/rock_hackathon?autoreconnect=true")
                .put("user", "root")
                .put("password", "nextdefault")
                .put("driver_class", "com.mysql.jdbc.Driver")
                .put("max_pool_size", 30)
        client = JDBCClient.createShared(vertx, config)
        if (!conn) {
            println("creating conn")
            createConn()
        }
        engine = FreeMarkerTemplateEngine.create()
        Router router = Router.router(vertx)
        router.route().handler(BodyHandler.create())

        router.route("/*").handler(this.&showFirst)
        router.get("/").handler(this.&login)
        router.get("/login").handler(this.&login)
        router.get("/signup").handler(this.&signup)
        router.post("/loginAuth").handler(this.&loginAuth)
        router.post("/signup").handler(this.&createUser)
        router.post("/forgetPassword").handler(this.&forgetPassword)
        router.get("/logout").handler(this.&logOut)
        router.get("/users").handler(this.&showUsers)
//        router.get("/mailTrigeer").handler(this.&trigerNowMail)
        router.post("/saveUser").handler(this.&saveUserMeth)
        router.post("/loginAuth").handler(this.&loginAuth)
//        router.get("/test").handler(this.&test)
        router.get("/userslist").handler(this.&fetchUserListWithRole)
        router.get("/projects").handler(this.&fetchProjectList)
//        router.get("/projects/:projectID").handler(this.&fetchProjectList)
//        router.get("/projects/create").handler(this.&)
        vertx.createHttpServer().requestHandler(router.&accept).listen(8085)
    }

    def createConn() {
        client.getConnection({ res ->
            if (res.failed()) {
                println "conn fail"
//                ctx.fail(res.cause())
            } else {
                println "conn success"
                conn = res.result()
                bootStrap()
            }
        })
    }

    void showForm(RoutingContext ctx) {
//        SendEmail.triggerNow("anubhav@fintechlabs.in", "Test First", "Hello welcome to using vertx", vertx)
    }

    void login(RoutingContext ctx) {
        bootStrapUser(ctx)
        engine.render(ctx, "templates/user/login.ftl", { res ->
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
        conn.updateWithParams("INSERT INTO user (name, username, address) VALUES (?, ?, ?)",
                new JsonArray()
                        .add(ctx.request().getFormAttribute("name"))
                        .add(ctx.request().getFormAttribute("username"))
                        .add(ctx.request().getFormAttribute("address")),
                { query ->
                    if (query.failed()) {
                        sendError(500, response)
                    } else {
                        ctx.response().end("Record Inserted")
                    }
                })
    }

    void showUsers(RoutingContext ctx) {

    }

    void loginAuth(RoutingContext ctx) {
        SQLConnection connection = ctx.get("conn")
        String userName = ctx.request().getFormAttribute("username")
        JsonObject authInfo = new JsonObject().put("username", "${userName}").put("password", "${ctx.request().getFormAttribute("password")}");
        JDBCAuth authProvider = JDBCAuth.create(vertx, client);
        authProvider.authenticate(authInfo, { res ->
            if (res.succeeded()) {
                Router router = Router.router(vertx)
                println(res.result());
//                router.route().handler(CookieHandler.create());
                router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));

                engine.render(ctx, "templates/dashProfile.ftl", { res1 ->
                    ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end(res1.result())
                })
            } else {
                println("error----${res}-----------")
                ctx.put("errorMessage", "${res.cause()?.toString()}")
                ctx.response().putHeader("location", "/").setStatusCode(302).end(res.cause()?.toString());
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
                            sendError(500, ctx.response())
                        } else {
                            if (query.result().getNumRows() == 0) {
                                println("=============== ${query.result()}")
                                auth.setNonces(new JsonArray().add("random_hash_1").add("random_hash_1"));
                                String salt = auth.generateSalt();
                                String hash = auth.computeHash("123456", salt);
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
        context.response().putHeader("location", "/").setStatusCode(302).end();
    }

    void signup(RoutingContext context) {
        engine.render(context, "templates/user/signup.ftl", { res ->
            context.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end(res.result())
        })
    }

    void forgetPassword(RoutingContext context) {
        engine.render(context, "templates/user/forgetPassword.ftl", { res ->
            context.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end(res.result())
        })
    }

    void createUser(RoutingContext context) {
        SQLConnection conn = context.get("conn")
        HttpServerResponse response = context.response()
        String confirmPassword = context.request().getFormAttribute("confirmPassword")
        UserBO userBO = new UserBO()
        userBO.username = context.request().getFormAttribute("username")
        userBO.password = context.request().getFormAttribute("password")
        userBO.name = context.request().getFormAttribute("name")
        println("========= IN ACTION")
        if (!(userBO.password)) {
            println("========= IN PASSWORD")
            engine.render(context, "templates/user/signup.ftl", { res ->
                response.putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end("Please Enter Password")
            })
        } else if (!(userBO.username)) {
            println("========= IN USERNAME")
            engine.render(context, "templates/user/signup.ftl", { res ->
                response.putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end("Please Enter UserName")
            })
        } else if (!(userBO.name)) {
            println("========= IN NAME")
            engine.render(context, "templates/user/signup.ftl", { res ->
                response.putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end("Please Enter Name")
            })
        } else if (confirmPassword) {
            engine.render(context, "templates/user/signup.ftl", { res ->
                response.putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end("Please Enter Confirm Password")
            })
        } else if (userBO.password && confirmPassword && !(userBO.password?.equals(confirmPassword))) {
            engine.render(context, "templates/user/signup.ftl", { res ->
                response.putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end("Password and confirm Password should be match")
            })
        } else {
            println("========= IN USERNAME VALIDATION")
            String queryStr = "SELECT * FROM USER where username = ?"
            conn.queryWithParams(queryStr, new JsonArray().add(userBO.username), { query ->
                if (query.failed()) {
                    println query.cause()
                    engine.render(context, "templates/user/signup.ftl", { res ->
                        response.putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end("Something went wrong")
                    })
                } else {
                    if (query.result().getNumRows() > 0) {
                        engine.render(context, "templates/user/signup.ftl", { res ->
                            response.putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end("USER already Exists")
                        })
                    } else {
                        userBO.role = "admin"
                        if (createNewUser(userBO)) {
                            println("======= USER CREATED =======")
                            SendEmail.triggerNow(userBO.username, "Welcome Mail", "Hi ${userBO.name ?: ''},Thanks For Signup with us. ", vertx)
                            engine.render(context, "templates/dashProfile.ftl", { res ->
                                response.putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end(res.result())
                            })
                        } else {
                            println("======= HAPPENDED WRONG =======")
                            engine.render(context, "templates/user/signup.ftl", { res ->
                                response.putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end("Something went wrong")
                            })
                        }
                    }
                }
            })
        }
    }

    void trigerNowMail() {
    }


    void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end()
    }

    void fetchUserListWithRole(RoutingContext ctx) {

        println("---------- Query called---------")
//        SQLConnection conn = ctx.get("conn")
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
                    ctx.put('users', generateUsersTable(userList))
                    engine.render(ctx, "templates/user/list.ftl", { res ->
                        ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end(res.result())
                    })
                } else println 'no records found'
            }
        })
    }

    void fetchProjectList(RoutingContext ctx) {

        println("---------- Query called---------")
        String queryy = "select p.id, name, dateCreated, createdBy from PROJECT p "
        conn.queryWithParams(queryy, new JsonArray(), { query ->
            if (query.failed()) {
                println query.cause()
                sendError(500, response)
            } else {
                if (query.result().getNumRows() > 0) {
                    String json = query.result().results.toString()
                    println json
                    JsonArray array = new JsonArray()
                    ArrayList<ProjectBO> projectList = []
                    query.result().results.each {
                        ProjectBO bo = new ProjectBO(it)
                        projectList.add(bo)
                    }
                    println array
                    projectList.each {
                        println it.name
                    }

//                    ctx.put('userId',)
                    ctx.put('projects', generateProjectsTable(projectList))
                    engine.render(ctx, "templates/projects.ftl", { res ->
                        ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end(res.result())
                    })

                } else println 'no records found'
            }
        })
    }

    void fetchUserListForProject(RoutingContext ctx, Integer projectId) {

        println("---------- Query called---------")
        SQLConnection conn = ctx.get("conn")
        String queryy = "select u.id, u.username, u.name, p.name, p.id from USER u " +
                "inner join USER_PROJECT up on u.id = up.user_id inner join PROJECT p " +
                "on p.id = up.project_id where p.id = ?"
        conn.queryWithParams(queryy, new JsonArray().add(projectId), { query ->
            if (query.failed()) {
                println query.cause()
                sendError(500, response)
            } else {
                if (query.result().getNumRows() > 0) {
                    String json = query.result().results.toString()
                    println json
                    JsonArray array = new JsonArray()
                    ArrayList<UserProjectBO> userList = []
                    query.result().results.each {
                        UserProjectBO bo = new UserProjectBO(it)
                        userList.add(bo)
                    }
                    println array
                    userList.each {
                        println it.userName
                    }
                } else println 'no records found'
            }
        })
    }

    void fetchTaskListForProject(RoutingContext ctx, Integer projectId) {

        println("---------- Query called---------")
        SQLConnection conn = ctx.get("conn")
        String queryy = "select t.id, t.name, t.description, t.status, isActive, dueDate, p.id from TASK t " +
                " inner join PROJECT p on p.id = t.project_id where p.id = ?"
        conn.queryWithParams(queryy, new JsonArray().add(projectId), { query ->
            if (query.failed()) {
                println query.cause()
                sendError(500, response)
            } else {
                if (query.result().getNumRows() > 0) {
                    String json = query.result().results.toString()
                    println json
                    JsonArray array = new JsonArray()
                    ArrayList<TaskBO> taskList = []
                    query.result().results.each {
                        TaskBO bo = new TaskBO(it)
                        taskList.add(bo)
                    }
                    println array
                    taskList.each {
                        println it.name
                    }
                } else println 'no records found'
            }
        })
    }

    void getProjects(RoutingContext ctx) {

    }

    String generateProjectsTable(List<ProjectBO> projects) {
        println "projects - " + projects*.name
        if (!projects) {
            return ''
        }
        StringJoiner joiner = new StringJoiner('<li>', '<h2>Select Project</h2><ul><li>', '</ul>')
//        joiner.add('<h2>Select Project</h2>')


        projects*.name.each { name ->
            joiner.add("<a href='' class='btn btn-primary'>${name}</a>")
        }
        println(joiner.toString())
        return joiner.toString()
    }

    String generateUsersTable(List<UserBO> users) {
        if (!users) {
            return ''
        }
        String tableHeader = """<table><thead><tr><th>Name</th><th>Email</th></tr></thead>"""
        StringBuilder sb = new StringBuilder(tableHeader)
        users.each { user ->
            sb.append("<tr><td>${user.name}</td><td>${user.username}</td>")
        }
        sb.append('</table>')
        return sb.toString()
    }


    void fetchLabelListForTask(RoutingContext ctx, Integer taskId) {

        println("---------- Query called---------")
        SQLConnection conn = ctx.get("conn")
        String queryy = "select labelName, task_id, dateCreated, user_id, u.name, u.username, t.name from TASK_LABEL tl inner join TASK t " +
                "on t.id = tl.task_id inner join USER u on u.id = tl.user_id where t.id = ?"
        conn.queryWithParams(queryy, new JsonArray().add(taskId), { query ->
            if (query.failed()) {
                println query.cause()
                sendError(500, response)
            } else {
                if (query.result().getNumRows() > 0) {
                    String json = query.result().results.toString()
                    println json
                    JsonArray array = new JsonArray()
                    ArrayList<TaskBO> taskList = []
                    query.result().results.each {
                        TaskBO bo = new TaskBO(it)
                        taskList.add(bo)
                    }
                    println array
                    taskList.each {
                        println it.name
                    }
                } else println 'no records found'
            }
        })
    }

    // pass -> name, username, password, designation, isActive, canAssign, role
    boolean createNewUser(UserBO userBO) {
        String queryy = "select * from USER where username = '${userBO.username}'"
        JDBCAuth auth = JDBCAuth.create(vertx, client);
        conn.queryWithParams(queryy, new JsonArray(), { query ->
            if (query.failed()) {
                println query.cause()
                sendError(500, response)
            } else {
                if (query.result().getNumRows() > 0) {
                    println("======= USER FOUND ======")
                    'USER already exists'
                } else {
                    println("======= GOING TO SAVE USER ======")
                    auth.setNonces(new JsonArray().add("random_hash_1").add("random_hash_1"));
                    String salt = auth.generateSalt();
                    String hash = auth.computeHash(userBO.password, salt);
                    conn.updateWithParams("INSERT INTO USER (name, username, password, designation, isActive, canAssign, password_salt) VALUES (?, ?, ?, ?, ?, ?, ?)",
                            new JsonArray()
                                    .add(userBO.name)
                                    .add(userBO.username)
                                    .add(hash)
                                    .add(userBO.designation ?: '')
                                    .add(userBO.isActive ?: true)
                                    .add(userBO.canAssign ?: true)
                                    .add(salt),
                            { query2 ->
                                if (query2.failed()) {
                                    println("======= QUERY FAILED ---- ${query2.cause()} ======")
                                    return false
                                } else {
                                    println("=======  SAVED USER ======")
                                    conn.updateWithParams("INSERT INTO USER_ROLE (role_id) VALUES (?)",
                                            new JsonArray()
                                                    .add(AppUtil.getRoleId(userBO.role)),
                                            { query3 ->
                                                if (query3.failed()) {
                                                    println("======= QUERY FAILED ---- ${query3.cause()} ======")
                                                    return false
                                                } else {
                                                    println("======= QUERY SUCCESS ---- ${query3.cause()} ======")
                                                    return true
                                                }
                                            })
                                }
                            })
                }
            }
        })
    }

    void createProject(ProjectBO projectBO) {

//        SQLConnection conn = context.get("conn")
        conn.updateWithParams("INSERT INTO PROJECT (name, createdBy, dateCreated) VALUES (?, ?, ?)",
                new JsonArray()
                        .add(projectBO.name)
                        .add(projectBO.createdBy)
                        .add(projectBO.dateCreated),
                { query2 ->
                    if (query2.failed()) {
                        println query2.cause()
                        return false
                    } else {
                        println 'project created successfully'
                        return true
                    }
                })
    }

    void createTask(TaskBO taskBO) {
        SQLConnection conn = context.get("conn")
        String query = "INSERT INTO PROJECT (name, description, status, dueDate, isActive, projectId) VALUES (?, ?, ?, ?, ?, ?)"
        conn.updateWithParams(query,
                new JsonArray()
                        .add(taskBO.name)
                        .add(taskBO.description)
                        .add(taskBO.status)
                        .add(taskBO.dueDate)
                        .add(taskBO.isActive)
                        .add(taskBO.projectId),
                { query2 ->
                    if (query2.failed()) {
                        return false
                    } else {
                        return true
                    }
                })
    }

    void createLabel(String labelName) {
        SQLConnection conn = context.get("conn")
        String query = "INSERT INTO LABEL (name) VALUES (?)"
        conn.updateWithParams(query,
                new JsonArray()
                        .add(labelName),
                { query2 ->
                    if (query2.failed()) {
                        return false
                    } else {
                        return true
                    }
                })
    }

    void bootStrap() {
        createRoles()
//        createUsers()
//        createProjects()
    }

    void createProjects() {

        String queryy = "select * from PROJECT"
        conn.queryWithParams(queryy, new JsonArray(), { query ->
            if (query.failed()) {
                println query.cause()
                sendError(500, response)
            } else {
                if (query.result().getNumRows() == 0) {
                    3.times {
                        ProjectBO projectBO = new ProjectBO()
                        projectBO.name = "Project${it}"
                        projectBO.createdBy = "${it + 1}"
                        projectBO.dateCreated = AppUtil.formattedDate(new Date(), AppUtil.mySqlDateFormat)
                        createProject(projectBO)
                    }
                }
            }
        })
    }

    void createRoles() {
        List<String> roles = ['admin', 'user']
        String queryStr = "SELECT * FROM ROLE where name = ?"
//        JDBCAuth auth = JDBCAuth.create(vertx, client);
        roles.each { role ->
            conn.queryWithParams(queryStr, new JsonArray().add(role), { query ->
                if (query.failed()) {
                    println query.cause()
//                    sendError(500, ctx.response())
                } else {
                    if (query.result().getNumRows() == 0) {
                        println("=============== ${query.result()}")
                        conn.update("INSERT INTO ROLE (name) values('${role}')", { res1 ->
                            if (res1.succeeded()) {
                                createUsers()
                                // success!
                                println("------ created role ===== ${role}")
                            } else {
//                                println("----- error --- ${res.cause()}")
                                println("----- error ---")
                            }
                        });

                    }
                }
            })
        }
    }

    void createUsers() {
        JDBCAuth auth = JDBCAuth.create(vertx, client);
        List<String> usernames = ["anubhav@email.com", "akash@email.com", 'tarun@gmail.com', 'karan@gmail.com']
        List<String> admins = ['admin@invincible']
        String queryStr = "SELECT * FROM USER where username = ?"

        conn.queryWithParams('select id from ROLE where name = ?', new JsonArray().add('user'), { query0 ->
            if (query0.failed()) {
                println query0.cause()
            } else {
                Integer role_id = query0.result().results[0].getAt(0) as Integer
                usernames.eachWithIndex { username, index ->
                    conn.queryWithParams(queryStr, new JsonArray().add(username), { query ->
                        if (query.failed()) {
                            println query.cause()
                        } else {
                            if (query.result().getNumRows() == 0) {
                                println("=============== ${query.result()}")
                                auth.setNonces(new JsonArray().add("random_hash_1").add("random_hash_1"));
                                String salt = auth.generateSalt();
                                String hash = auth.computeHash("123456", salt);
// save to the database
                                conn.updateWithParams("INSERT INTO USER (username,password,password_salt,designation,name,isActive) VALUES (?, ?, ?,?,?,?)",
                                        new JsonArray().add(username).add(hash).add(salt).add('Software Developer').add(username.split('@')[0]).add(true),
                                        { res1 ->
                                            if (res1.succeeded()) {
                                                // success!
                                                createProjects()
                                                println("------ created user ===== ${username}")
                                            } else {
                                                println("----- error --- " + res1.cause())
                                            }
                                        });

                                conn.updateWithParams('insert into USER_ROLE values(?,?)',
                                        new JsonArray().add(index).add(role_id), { res1 ->
                                    if (res1.succeeded()) {
                                        // success!
                                        println("------ created user ===== ${username}")
                                    } else {
                                        println("----- error --- " + res1.cause())
                                    }
                                })
                            }
                        }
                    })
                }
            }
        })

        conn.queryWithParams('select id from ROLE where name = ?', new JsonArray().add('admin'), { query0 ->
            if (query0.failed()) {
                println query0.cause()
            } else {
                Integer role_id = query0.result().results[0].getAt(0) as Integer
                admins.eachWithIndex { username, index ->
                    conn.queryWithParams(queryStr, new JsonArray().add(username), { query ->
                        if (query.failed()) {
                            println query.cause()
                        } else {
                            if (query.result().getNumRows() == 0) {
                                println("=============== ${query.result()}")
                                auth.setNonces(new JsonArray().add("random_hash_1").add("random_hash_1"));
                                String salt = auth.generateSalt();
                                String hash = auth.computeHash("123456", salt);
// save to the database
                                conn.updateWithParams("INSERT INTO USER (username,password,password_salt,designation,isActive) VALUES (?, ?, ?,?,?)",
                                        new JsonArray().add(username).add(hash).add(salt).add('Software Developer').add(true),
                                        { res1 ->
                                            if (res1.succeeded()) {
                                                // success!
                                                println("------ created user ===== ${username}")
                                            } else {
                                                println("----- error --- " + res1.cause())
                                            }
                                        });

                                conn.updateWithParams('insert into USER_ROLE values(?,?)',
                                        new JsonArray().add(index).add(role_id), { res1 ->
                                    if (res1.succeeded()) {
                                        // success!
                                        println("------ created user ===== ${username}")
                                    } else {
                                        println("----- error --- " + res1.cause())
                                    }
                                })
                            }
                        }
                    })
                }
            }
        })


    }

    static String generateTable(JsonArray array) {
        String data = getTableHeader()
        array.each { JsonObject object ->
            println(object)
            data += """<tr>
        <td>${object.getInteger("id")}</td>
        <td>${object.getString("firstName")}</td>
        <td>${object.getString("lastName")}</td>
        <td>${object.getString("email")}</td>
        <td>${object.getInteger("age")}</td>
    </tr>"""
        }

        data += getTableFooter()
        return data
    }

    static String getTableHeader() {
        return """<table border=1>
    <thead>
    <tr>
        <td>ID</td>
        <td>First Name</td>
        <td>Last Name</td>
        <td>Email</td>
        <td>Age</td>
    </tr>
    </thead>
    <tbody>"""
    }

    static String getTableFooter() {
        """ </tbody>
</table>"""
    }

}
