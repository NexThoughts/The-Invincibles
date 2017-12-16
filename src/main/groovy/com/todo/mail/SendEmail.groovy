package com.todo.mail

import io.vertx.core.Vertx
import io.vertx.ext.mail.MailClient

class SendEmail {


    public static triggerNow(String toMail,String subject,String content,Vertx vertx) {

//        def vertx = Vertx.vertx()

        println "---sendEmail--1--------"

        def config = [:]
        config.hostname = "smtp.gmail.com"
        config.port = 587
        config.starttls = "REQUIRED"
        config.username = "anubhavgoyalmca@gmail.com"
        config.password = "anubhavnextdefault"

        println "---sendEmail--2--------" + config

        def mailClient = MailClient.createNonShared(vertx, config)

        println "-----mailClient-------" + mailClient

        def message = [:]
        message.from = "anubhavgoyalmca@gmail.com"
        message.to = "${toMail}"
//        message.cc = "Another User <another@example.net>"
        message.text = content?:"Welcome to Vertx Demo"
        message.subject = subject?:"Test"
//        message.html = "Hi, this is html text <a href=\"http://vertx.io\">vertx.io</a>"

        println "-----message-------" + message

        mailClient.sendMail(message, { result ->
            println "--------- Sending Email Message ---------"
            if (result.succeeded()) {
                println(result.result())
                println "--------- Sending Email Message Succeeded---------"
            } else {
                println "--------- Sending Email Message FAILED---------"+result.cause().printStackTrace()
                result.cause().printStackTrace()
            }
        })

        println "------5--------"
    }
}


