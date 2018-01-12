package com.example.dmitriysamoilov.randomchat.model;

/**
 * Created by dmitriysamoilov on 04.12.17.
 */

public class ChatRoom {
    private String roomName;
    private boolean roomStatus;

    public ChatRoom(String roomName,boolean roomStatus){
        this.roomName = roomName;
        this.roomStatus = roomStatus;
    }

    public ChatRoom() {
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public boolean isRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(boolean roomStatus) {
        this.roomStatus = roomStatus;
    }
}

