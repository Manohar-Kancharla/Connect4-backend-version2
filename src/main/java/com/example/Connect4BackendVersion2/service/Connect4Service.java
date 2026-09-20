package com.example.Connect4BackendVersion2.service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.Connect4BackendVersion2.enums.State;
import com.example.Connect4BackendVersion2.enums.Type;
import com.example.Connect4BackendVersion2.exceptions.MyException;
import com.example.Connect4BackendVersion2.room.Room;

@Service
public class Connect4Service {
	@Autowired
	SimpMessagingTemplate simpMessagingTemplate;
	ConcurrentHashMap<String,Integer> activeSessions;
	ConcurrentHashMap<Integer,Room> allRooms;
	Set<Integer> completed,normalWaiting,privateWaiting,active;
	int gameId;
	
	public Connect4Service()
	{
		activeSessions=new ConcurrentHashMap<>();
		allRooms=new ConcurrentHashMap<>();
		completed=ConcurrentHashMap.newKeySet();
		normalWaiting=ConcurrentHashMap.newKeySet();
		privateWaiting=ConcurrentHashMap.newKeySet();
		active=ConcurrentHashMap.newKeySet();
		gameId=1;
	}
	
	public synchronized int getGameId()
	{
		if(gameId==100000)
		{
			gameId=1;
		}
		while(allRooms.containsKey(gameId))
		{
			gameId=gameId+1;
			if(gameId==100000)
			{
				gameId=1;
			}
		}
		
		return gameId;
	}
	
	public synchronized void endCurrentRoom(String sessionId)
	{
		if(sessionId==null || sessionId.trim().length()==0)
		{
			throw new MyException("invalid session");
		}
		int roomId=0;
		Room room=null;
		if(activeSessions.containsKey(sessionId))
		{
			roomId=activeSessions.get(sessionId);
			room=allRooms.get(roomId);
			if(room!=null)
			{
				room.endOldRoom(sessionId);
				if(room.isSuccessfulOperation())
				{
					if(room.isCanBeDeleted())
					{
						completed.remove(roomId);
						normalWaiting.remove(roomId);
						privateWaiting.remove(roomId);
						active.remove(roomId);
						allRooms.remove(roomId);
					}
					else if(room.getState()==State.FINISHED)
					{
						normalWaiting.remove(roomId);
						privateWaiting.remove(roomId);
						active.remove(roomId);
						completed.add(roomId);
					}
					simpMessagingTemplate.convertAndSend("/topic/get-game/"+roomId,room);
				}
			}
			activeSessions.remove(sessionId);
		}
	}
	
	public synchronized Room getGame(String sessionId)
	{
		if(sessionId==null || sessionId.trim().length()==0 || !activeSessions.containsKey(sessionId))
		{
			throw new MyException("invalid session");
		}
		int roomId=activeSessions.get(sessionId);
		Room room=allRooms.get(roomId);
		if(room==null)
		{
			completed.remove(roomId);
			normalWaiting.remove(roomId);
			privateWaiting.remove(roomId);
			active.remove(roomId);
			allRooms.remove(roomId);
			activeSessions.remove(sessionId);
			throw new MyException("room not found");
		}
		return room;
	}
	
	public synchronized Room playNewGame(String sessionId, String type, int roomCode)
	{
		if(sessionId==null || sessionId.trim().length()==0 || type==null || !(type.toLowerCase().equals("public") || type.toLowerCase().equals("private")))
		{
			throw new MyException("invalid session (or) type");
		}
		int roomId=0;
		Room room=null;
		if(activeSessions.containsKey(sessionId))
		{
			endCurrentRoom(sessionId);
		}
		
		if(type.toLowerCase().equals("public"))
		{
			if(normalWaiting.size()==0)
			{
				roomId=getGameId();
				room=new Room(roomId,Type.PUBLIC,sessionId);
				if(room.isSuccessfulOperation()==false)
				{
					throw new MyException("operation failed");
				}
				normalWaiting.add(roomId);
				allRooms.put(roomId, room);
			}
			else
			{
				roomId=normalWaiting.iterator().next();
				room=allRooms.get(roomId);
				if(room==null)
				{
					normalWaiting.remove(roomId);
					allRooms.remove(roomId);
					throw new MyException("room not found");
				}
				room.joinRoom(sessionId);
				if(room.isSuccessfulOperation()==false)
				{
					throw new MyException("operation failed");
				}
				normalWaiting.remove(roomId);
				active.add(roomId);
			}
		}
		else
		{
			if(roomCode!=0)
			{
				if(!privateWaiting.contains(roomCode))
				{
					throw new MyException("invalid code");
				}
				roomId=roomCode;
				room=allRooms.get(roomId);
				if(room==null)
				{
					privateWaiting.remove(roomId);
					allRooms.remove(roomId);
					throw new MyException("room not found");
				}
				room.joinRoom(sessionId);
				if(room.isSuccessfulOperation()==false)
				{
					throw new MyException("operation failed");
				}
				privateWaiting.remove(roomId);
				active.add(roomId);
			}
			else
			{
				roomId=getGameId();
				room=new Room(roomId,Type.PRIVATE,sessionId);
				if(room.isSuccessfulOperation()==false)
				{
					throw new MyException("operation failed");
				}
				privateWaiting.add(roomId);
				allRooms.put(roomId, room);
			}
		}
		simpMessagingTemplate.convertAndSend("/topic/get-game/"+roomId,room);
		activeSessions.put(sessionId, roomId);
		return room;
	}
	
	public synchronized void disconnect(String sessionId)
	{
		if(sessionId==null || sessionId.trim().length()==0 || !activeSessions.containsKey(sessionId))
		{
			throw new MyException("invalid session");
		}
		int roomId=activeSessions.get(sessionId);
		Room room=allRooms.get(roomId);
		if(room==null)
		{
			activeSessions.remove(sessionId);
			allRooms.remove(roomId);
			throw new MyException("room not found");
		}
		room.endOldRoom(sessionId);
//		if(room.isSuccessfulOperation()==false)
//		{
//			return;
//		}
		if(room.isCanBeDeleted())
		{
			completed.remove(roomId);
			normalWaiting.remove(roomId);
			privateWaiting.remove(roomId);
			active.remove(roomId);
			allRooms.remove(roomId);
		}
		else if(room.getState()==State.FINISHED)
		{
			normalWaiting.remove(roomId);
			privateWaiting.remove(roomId);
			active.remove(roomId);
			completed.add(roomId);
		}
		activeSessions.remove(sessionId);
		simpMessagingTemplate.convertAndSend("/topic/get-game/"+roomId,room);
	}
	
	public synchronized void run(String sessionId, int roomCode, int col)
	{
		if(sessionId==null || sessionId.trim().length()==0 || !activeSessions.containsKey(sessionId))
		{
			throw new MyException("invalid session");
		}
		int roomId=activeSessions.get(sessionId);
		if(roomId!=roomCode)
		{
			throw new MyException("invalid room code");
		}
		Room room=allRooms.get(roomId);
		if(room==null)
		{
			activeSessions.remove(sessionId);
			active.remove(roomId);
			allRooms.remove(roomId);
			throw new MyException("room not found");
		}
		room.run(sessionId, col);
		if(room.isSuccessfulOperation()==false)
		{
			throw new MyException("operation failed");
		}
		if(room.getState()==State.FINISHED)
		{
			active.remove(roomId);
			completed.add(roomId);
		}
		simpMessagingTemplate.convertAndSend("/topic/get-game/"+roomId,room);
	}
	
	public synchronized void chat(String sessionId, int roomCode, String message)
	{
		if(sessionId==null || sessionId.trim().length()==0 || message==null || message.trim().length()==0 || !activeSessions.containsKey(sessionId))
		{
			throw new MyException("invalid session");
		}
		int roomId=activeSessions.get(sessionId);
		if(roomId!=roomCode)
		{
			throw new MyException("invalid room code");
		}
		Room room=allRooms.get(roomId);
		if(room==null)
		{
			completed.remove(roomId);
			normalWaiting.remove(roomId);
			privateWaiting.remove(roomId);
			active.remove(roomId);
			allRooms.remove(roomId);
			activeSessions.remove(sessionId);
			throw new MyException("room not found");
		}
		room.chat(sessionId, message);
		if(room.isSuccessfulOperation()==false)
		{
			throw new MyException("operation failed");
		}
		simpMessagingTemplate.convertAndSend("/topic/get-game/"+roomId,room);
	}
}
