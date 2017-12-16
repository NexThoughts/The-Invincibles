package com.bo

import io.vertx.core.json.JsonArray
/**
 * Created by karan on 16/12/17.
 */
class TaskBO {

    String id
    String name
    String description
    String status
    boolean isActive
    String dueDate
    String projectId

    TaskBO() {

    }

    TaskBO(JsonArray jsonArray) {
        id = jsonArray[0]
        name = jsonArray[1]
        description = jsonArray[2]
        status = jsonArray[3]
        isActive = jsonArray.getBoolean(4)
        dueDate = jsonArray[5]
        projectId = jsonArray[6]
    }
}
