package com.bo

import io.vertx.core.json.JsonArray

/**
 * Created by karan on 16/12/17.
 */
class UserProjectBO {

    String userId
    String userUserName
    String userName
    String projectName
    String projectId


    UserProjectBO() {

    }

    UserProjectBO(JsonArray jsonArray) {
        userId = jsonArray[0] as Integer
        userUserName = jsonArray[1] as Integer
        userName = jsonArray[2] as Integer
        projectName = jsonArray[3] as Integer
        projectId = jsonArray[4] as Integer
    }
}
