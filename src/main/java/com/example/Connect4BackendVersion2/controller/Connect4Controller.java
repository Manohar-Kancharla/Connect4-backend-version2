package com.example.Connect4BackendVersion2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.example.Connect4BackendVersion2.room.Room;
import com.example.Connect4BackendVersion2.service.Connect4Service;

@CrossOrigin("*")
@RestController
public class Connect4Controller {
	@Autowired
	private Connect4Service connect4Service;
	
	@MessageMapping("/whoAmI")
	@SendToUser("/queue/whoAmI")
	public String whoAmI(SimpMessageHeaderAccessor messageHeaders)
	{
		return messageHeaders.getSessionId();
	}
	
	@MessageMapping("/connect4/start-new-game/sessionId/{sessionId}/type/{type}/roomCode/{roomCode}")
	@SendToUser("/queue/new-game")
	public Room startNewGame(@DestinationVariable String sessionId, @DestinationVariable String type, @DestinationVariable int roomCode)
	{
		return connect4Service.playNewGame(sessionId,type,roomCode);
	}
	
	@MessageMapping("/get/connect4/sessionId/{sessionId}")
	@SendToUser("/queue/game")
	public Room get(@DestinationVariable String sessionId)
	{
		return connect4Service.getGame(sessionId);
	}
	
	@MessageMapping("/end-current-game/sessionId/{sessionId}")
	public void endCurrentRoom(@DestinationVariable String sessionId)
	{
		connect4Service.endCurrentRoom(sessionId);
	}
	
	@MessageMapping("/run/sessionId/{sessionId}/col/{col}/roomCode/{roomCode}")
	public void run(@DestinationVariable String sessionId, @DestinationVariable int col, @DestinationVariable int roomCode)
	{
		connect4Service.run(sessionId,roomCode, col);
	}
	
	@MessageMapping("/chat/sessionId/{sessionId}/message/{message}/roomCode/{roomCode}")
	public void chat(@DestinationVariable String sessionId, @DestinationVariable String message, @DestinationVariable int roomCode)
	{
		connect4Service.chat(sessionId,roomCode, message);
	}
}