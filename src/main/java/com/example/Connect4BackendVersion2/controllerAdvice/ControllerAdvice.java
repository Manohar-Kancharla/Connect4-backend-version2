package com.example.Connect4BackendVersion2.controllerAdvice;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.Connect4BackendVersion2.exceptions.MyException;

@RestControllerAdvice
public class ControllerAdvice {

	@MessageExceptionHandler(MyException.class)
	@SendToUser("/queue/invalid-message")
	public String handleUserNotFoundException(MyException e)
	{
		return e.getMessage();
	}
}
