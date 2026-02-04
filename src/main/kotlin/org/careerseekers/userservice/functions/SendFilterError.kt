package org.careerseekers.userservice.functions

import jakarta.servlet.http.HttpServletResponse

fun sendError(response: HttpServletResponse, status: Int, message: String) {
    response.status = status
    response.contentType = "application/json; charset=UTF-8"
    response.characterEncoding = "UTF-8"
    response.writer.write("""{"status":$status,"message":"$message"}""")
}