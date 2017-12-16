package com.util

/**
 * Created by karan on 16/12/17.
 */
class SqlUtil {

    // mapped with UserBO
    static String queryUserListWithRole() {
        "select username, password, designation, u.id as id, isActive, canAssign, r.name as role from USER u inner join USER_ROLE ur on ur.user_id = u.id inner join ROLE r on r.id = ur.role_id "
    }

}
