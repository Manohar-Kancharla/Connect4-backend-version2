package com.example.Connect4BackendVersion2.room;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;

import com.example.Connect4BackendVersion2.enums.State;
import com.example.Connect4BackendVersion2.enums.Type;

public class Room {
	int roomId,m,n,count,required;
	Type type;
	State state;
	int[][] board,winningBlocks;
	int[] last;
	boolean haveWinningBlocks,isRed,canBeDeleted;
	String redSessionId,yellowSessionId;
	int whoWon;
	Deque<String> messages;
	boolean isSuccessfulOperation;
	
	public Room(int roomId, Type type, String sessionId)
	{
		this.roomId=roomId;
		this.type=type;
		state=State.WAITING;
		m=6;
		n=7;
		required=4;
		board=new int[m][n];
		last=new int[n];
		for(int i=0;i<n;i++)
		{
			last[i]=-1;
		}
		winningBlocks=new int[required][2];
		haveWinningBlocks=false;
		whoWon=-1;
		isRed=true;
		count=0;
		messages=new LinkedList<>();
		Random random=new Random();
		int randomNumber=random.nextInt(2)+1;
		if(randomNumber==1)
		{
			redSessionId=sessionId;
			yellowSessionId=null;
		}
		else
		{
			redSessionId=null;
			yellowSessionId=sessionId;
		}
		canBeDeleted=false;
		isSuccessfulOperation=true;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public int getM() {
		return m;
	}

	public void setM(int m) {
		this.m = m;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getRequired() {
		return required;
	}

	public void setRequired(int required) {
		this.required = required;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public int[][] getBoard() {
		return board;
	}

	public void setBoard(int[][] board) {
		this.board = board;
	}

	public int[][] getWinningBlocks() {
		return winningBlocks;
	}

	public void setWinningBlocks(int[][] winningBlocks) {
		this.winningBlocks = winningBlocks;
	}

	public int[] getLast() {
		return last;
	}

	public void setLast(int[] last) {
		this.last = last;
	}

	public boolean isHaveWinningBlocks() {
		return haveWinningBlocks;
	}

	public void setHaveWinningBlocks(boolean haveWinningBlocks) {
		this.haveWinningBlocks = haveWinningBlocks;
	}

	public boolean isRed() {
		return isRed;
	}

	public void setRed(boolean isRed) {
		this.isRed = isRed;
	}

	public boolean isCanBeDeleted() {
		return canBeDeleted;
	}

	public void setCanBeDeleted(boolean canBeDeleted) {
		this.canBeDeleted = canBeDeleted;
	}

	public String getRedSessionId() {
		return redSessionId;
	}

	public void setRedSessionId(String redSessionId) {
		this.redSessionId = redSessionId;
	}

	public String getYellowSessionId() {
		return yellowSessionId;
	}

	public void setYellowSessionId(String yellowSessionId) {
		this.yellowSessionId = yellowSessionId;
	}

	public int getWhoWon() {
		return whoWon;
	}

	public void setWhoWon(int whoWon) {
		this.whoWon = whoWon;
	}

	public Deque<String> getMessages() {
		return messages;
	}

	public void setMessages(Deque<String> messages) {
		this.messages = messages;
	}
	
	public boolean isSuccessfulOperation() {
		return isSuccessfulOperation;
	}

	public void setSuccessfulOperation(boolean isSuccessfulOperation) {
		this.isSuccessfulOperation = isSuccessfulOperation;
	}

	public void joinRoom(String sessionId)
	{
		isSuccessfulOperation=false;
		if(state!=State.WAITING)
		{
			return;
		}
		if((redSessionId==null && yellowSessionId==null) || (redSessionId!=null && yellowSessionId!=null))
		{
			return;
		}
		if(sessionId.equals(redSessionId) || sessionId.equals(yellowSessionId))
		{
			return;
		}
		if(redSessionId==null)
		{
			redSessionId=sessionId;
		}
		else
		{
			yellowSessionId=sessionId;
		}
		state=State.ACTIVE;
		isSuccessfulOperation=true;
	}
	
	public void endOldRoom(String sessionId)
	{
		isSuccessfulOperation=false;
		if(!(sessionId.equals(redSessionId) || sessionId.equals(yellowSessionId)))
		{
			return;
		}
		if(whoWon==-1)
		{
			if(sessionId.equals(redSessionId) && yellowSessionId!=null)
			{
				whoWon=2;
			}
			else if(sessionId.equals(yellowSessionId) && redSessionId!=null)
			{
				whoWon=1;
			}
			else
			{
				whoWon=0;
			}
			state=State.FINISHED;
		}
		if(sessionId.equals(redSessionId))
		{
			redSessionId=null;
		}
		else
		{
			yellowSessionId=null;
		}
		if(redSessionId==null && yellowSessionId==null)
		{
			canBeDeleted=true;
		}
		isSuccessfulOperation=true;
	}
	
	public void checkIsGameOver()
	{
		if(whoWon!=-1)
		{
			return;
		}
		if(count==m*n)
		{
			whoWon=0;
			state=State.FINISHED;
		}
	}
	
	public void checkIsWon(int row, int col)
	{
		int val=board[row][col];
        int z,r,c;
        int i=0;
        winningBlocks[i][0]=row;
        winningBlocks[i][1]=col;

        //horizontal
        i=1;
        z=1;
        r=row;
        c=col-1;
        while(c>=0 && board[r][c]==val && z<required)
        {
        	winningBlocks[i][0]=r;
            winningBlocks[i][1]=c;
            i++;
            c--;
            z++;
        }
        c=col+1;
        while(c<n && board[r][c]==val && z<required)
        {
            winningBlocks[i][0]=r;
            winningBlocks[i][1]=c;
            i++;
            c++;
            z++;
        }
        if(z>=required)
        {
            whoWon=val;
            haveWinningBlocks=true;
            state=State.FINISHED;
            return;
        }

        //vertical
        i=1;
        z=1;
        r=row-1;
        c=col;
        while(r>=0 && board[r][c]==val && z<required)
        {
            winningBlocks[i][0]=r;
            winningBlocks[i][1]=c;
            i++;
            r--;
            z++;
        }
        r=row+1;
        while(r<m && board[r][c]==val && z<required)
        {
            winningBlocks[i][0]=r;
            winningBlocks[i][1]=c;
            i++;
            r++;
            z++;
        }
        if(z>=required)
        {
            whoWon=val;
            haveWinningBlocks=true;
            state=State.FINISHED;
            return;
        }

        //top left to bottom right diagnoal
        i=1;
        z=1;
        r=row-1;
        c=col-1;
        while(r>=0 && c>=0 && board[r][c]==val && z<required)
        {
            winningBlocks[i][0]=r;
            winningBlocks[i][1]=c;
            i++;
            r--;
            c--;
            z++;
        }
        r=row+1;
        c=col+1;
        while(r<m && c<n && board[r][c]==val && z<required)
        {
            winningBlocks[i][0]=r;
            winningBlocks[i][1]=c;
            i++;
            r++;
            c++;
            z++;
        }
        if(z>=required)
        {
            whoWon=val;
            haveWinningBlocks=true;
            state=State.FINISHED;
            return;
        }

        //top right to bottom left diagnoal
        i=1;
        z=1;
        r=row-1;
        c=col+1;
        while(r>=0 && c<n && board[r][c]==val && z<required)
        {
            winningBlocks[i][0]=r;
            winningBlocks[i][1]=c;
            i++;
            r--;
            c++;
            z++;
        }
        r=row+1;
        c=col-1;
        while(r<m && c>=0 && board[r][c]==val && z<required)
        {
            winningBlocks[i][0]=r;
            winningBlocks[i][1]=c;
            i++;
            r++;
            c--;
            z++;
        }
        if(z>=required)
        {
            whoWon=val;
            haveWinningBlocks=true;
            state=State.FINISHED;
            return;
        }
	}
	
	public void run(String sessionId, int col)
	{
		isSuccessfulOperation=false;
		if(whoWon!=-1 || col<0 || col>=n || last[col]>=m-1 || state!=State.ACTIVE)
		{
			return;
		}
		if(redSessionId==null || yellowSessionId==null)
		{
			return;
		}
		if(!(sessionId.equals(redSessionId) || sessionId.equals(yellowSessionId)))
		{
			return;
		}
		int val=0;
		if(sessionId.equals(redSessionId))
		{
			if(isRed==false)
			{
				return;
			}
			val=1;
		}
		else
		{
			if(isRed==true)
			{
				return;
			}
			val=2;
		}
		last[col]++;
		board[last[col]][col]=val;
		isRed=!isRed;
		count=count+1;
		checkIsWon(last[col],col);
		checkIsGameOver();
		isSuccessfulOperation=true;
	}
	
	public void chat(String sessionId, String message)
	{
		isSuccessfulOperation=false;
		if(!(sessionId.equals(redSessionId) || sessionId.equals(yellowSessionId)))
		{
			return;
		}
		if(message==null || message.trim().length()==0)
		{
			return;
		}
		if(sessionId.equals(redSessionId))
		{
			message="Red :- "+message.trim();
		}
		else
		{
			message="Yellow :- "+message.trim();
		}
		if(messages.size()>=5)
		{
			messages.removeFirst();
		}
		messages.addLast(message);
		isSuccessfulOperation=true;
	}
}
