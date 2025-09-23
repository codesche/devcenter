package org.com.ssrboard.web.controller

import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class ErrorPageController : ErrorController {
    @RequestMapping("/error")
    fun error(): String = "error/500"
}