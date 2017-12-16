package com.todo.timer

import com.diabolicallabs.vertx.cron.CronObservable
import com.todo.mail.SendEmail
import io.vertx.core.Vertx
import io.vertx.rx.java.RxHelper
import rx.Scheduler

class StartTimer {
    public static void main(String[] args) {
        new StartTimer().executeTimer()
        new StartTimer().timeExperssion()
    }

    void executeTimer() {
        Vertx vertx = Vertx.vertx()
        Scheduler scheduler = RxHelper.scheduler(vertx);
        CronObservable.cronspec(scheduler, "0 0/1 * * * ?", "IST")
                .subscribe({
            println("@@@@@@@@@@@@@Triger@@@@@@@@@@@@@")
            SendEmail.triggerNow("anubhav@fintechlabs.in", "iiiiiiii", "TESTING HELLO", vertx)
        });
    }

    void timeExperssion() {
        Vertx vertx = Vertx.vertx()
        vertx.setTimer(1000, { id ->
//            SendEmail.triggerNow("anubhav@fintechlabs.in", "iiiiiiii", "TESTING HELLO", vertx)
        })
        vertx.setPeriodic(1000 * 60 * 60 * 24, { id ->
//            SendEmail.triggerNow("anubhav@fintechlabs.in", "iiiiiiii", "TESTING HELLO", vertx)
        })
    }
}
