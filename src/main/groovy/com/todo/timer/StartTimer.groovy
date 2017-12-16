package com.todo.timer

import com.diabolicallabs.vertx.cron.CronObservable
import com.todo.mail.SendEmail
import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.rx.java.RxHelper
import rx.Scheduler

class StartTimer extends AbstractVerticle {
    public void start() {
        Vertx vertx = Vertx.vertx()
        Router router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        executeTimer(vertx)
//        timeExperssion(vertx)
        vertx.createHttpServer().requestHandler(router.&accept).listen(8085)
    }

    void executeTimer(Vertx vertx) {
        println("-----Corn Job Mail")
        Scheduler scheduler = RxHelper.scheduler(vertx);
        CronObservable.cronspec(scheduler, "0 0/1 * * * ?", "IST")
                .subscribe({
            println("@@@@@@@@@@@@@Triger@@@@@@@@@@@@@")
            SendEmail.triggerNow("anubhav@fintechlabs.in", "iiiiiiii", "TESTING HELLO", vertx)
        });
    }

    void timeExperssion(Vertx vertx) {
        vertx.setTimer(1000, { id ->
//            SendEmail.triggerNow("anubhav@fintechlabs.in", "iiiiiiii", "TESTING HELLO", vertx)
        })
        vertx.setPeriodic(1000 * 60 * 60 * 24, { id ->
//            SendEmail.triggerNow("anubhav@fintechlabs.in", "iiiiiiii", "TESTING HELLO", vertx)
        })
    }
}
