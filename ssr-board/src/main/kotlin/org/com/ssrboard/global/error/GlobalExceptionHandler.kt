package org.com.ssrboard.global.error

import jakarta.validation.ConstraintViolationException
import org.com.ssrboard.global.web.ApiResponse
import org.springframework.ui.Model
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFound(ex: EntityNotFoundException, model: Model): String {
        model.addAttribute("message", ex.message)
        return "error/404"
    }

    @ResponseBody
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessApi(ex: BusinessException): ApiResponse<Nothing>
            = ApiResponse.fail(ex.message ?: "Business error")

    @ResponseBody
    @ExceptionHandler(
        MethodArgumentNotValidException::class,
        BindException::class,
        ConstraintViolationException::class,
    )
    fun handleValidation(): ApiResponse<Nothing> = ApiResponse.fail("Validation failed")

    @ExceptionHandler(Exception::class)
    fun handleOther(ex: Exception, model: Model): String {
        model.addAttribute("message", ex.message ?: "Unexpected error")
        return "error/500"
    }

}