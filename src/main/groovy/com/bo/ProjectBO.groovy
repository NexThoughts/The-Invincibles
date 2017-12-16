package com.bo

import io.vertx.core.json.JsonArray

/**
 * Created by karan on 16/12/17.
 */
class ProjectBO {

    String id
    String name
    String dateCreated
    String createdBy

    ProjectBO(JsonArray jsonArray) {
        id = jsonArray[0]
        name = jsonArray[1]
        dateCreated = jsonArray[2]
        createdBy = jsonArray[3]
    }

    ProjectBO() {

    }
}
