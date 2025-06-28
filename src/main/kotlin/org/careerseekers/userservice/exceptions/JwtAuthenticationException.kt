package org.careerseekers.userservice.exceptions

import javax.naming.AuthenticationException

class JwtAuthenticationException(message: String) : AuthenticationException(message)