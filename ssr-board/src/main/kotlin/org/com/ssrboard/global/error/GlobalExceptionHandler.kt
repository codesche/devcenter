package org.com.ssrboard.global.error

import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.ConstraintViolationException
import org.com.ssrboard.global.web.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.ui.Model
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException::class)
    fun notFound(ex: EntityNotFoundException, resp: HttpServletResponse): String {
        resp.status = HttpStatus.NOT_FOUND.value()
        return "error/404"
    }

    @ResponseBody
    @ExceptionHandler(BusinessException::class)
    fun business(ex: BusinessException): ApiResponse<Nothing> =
        ApiResponse.fail(ex.message ?: "Business error")

    @ExceptionHandler(Exception::class)
    fun any(ex: Exception, resp: HttpServletResponse): String {
        resp.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        return "error/500"
    }

}