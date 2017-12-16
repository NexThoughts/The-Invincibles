package com.todo.timer

import com.todo.mail.SendEmail
import io.vertx.core.Vertx

class StartTimer {
    public static void main(String[] args) {
        new StartTimer().executeTimer()
    }

    void executeTimer() {
        Vertx vertx = Vertx.vertx()
        vertx.setTimer(1000, { id ->
//            SendEmail.triggerNow("anubhav@fintechlabs.in", "iiiiiiii", "TESTING HELLO", vertx)
        })
        vertx.setPeriodic(1000 * 60 * 60 * 24, { id ->
//            SendEmail.triggerNow("anubhav@fintechlabs.in", "iiiiiiii", "TESTING HELLO", vertx)
        })
    }
}
