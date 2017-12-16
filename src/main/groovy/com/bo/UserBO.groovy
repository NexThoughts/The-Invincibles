package com.bo

import io.vertx.core.json.JsonArray

/**
 * Created by karan on 16/12/17.
 */
class UserBO implements Serializable {

    String username
    String password
    String designation
    Integer id
    boolean isActive
    boolean canAssign
    String role

    UserBO(String username, String password, String designation, Integer id, boolean isActive, boolean canAssign, String role) {
        this.username = username
        this.password = password
        this.designation = designation
        this.id = id
        this.isActive = isActive
        this.canAssign = canAssign
        this.role = role
    }

    UserBO(JsonArray jsonArray) {

        this.username = jsonArray[0]
        this.password = jsonArray[1]
        this.designation = jsonArray[2]
        this.id = jsonArray.getInteger(3)
        this.isActive = jsonArray[4]
        this.canAssign = jsonArray[5]
        this.role = jsonArray[6]
    }

    UserBO() {
    }
}
