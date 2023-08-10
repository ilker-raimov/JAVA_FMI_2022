package bg.sofia.uni.fmi.mjt.escaperoom;

import bg.sofia.uni.fmi.mjt.escaperoom.exception.PlatformCapacityExceededException;
import bg.sofia.uni.fmi.mjt.escaperoom.exception.RoomNotFoundException;
import bg.sofia.uni.fmi.mjt.escaperoom.exception.RoomAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.escaperoom.exception.TeamNotFoundException;
import bg.sofia.uni.fmi.mjt.escaperoom.room.EscapeRoom;

public interface EscapeRoomAdminAPI {
    void addEscapeRoom(EscapeRoom room) throws RoomAlreadyExistsException;

    void removeEscapeRoom(String roomName) throws RoomNotFoundException;

    EscapeRoom[] getAllEscapeRooms();

    void registerAchievement(String roomName, String teamName, int escapeTime)
            throws RoomNotFoundException, TeamNotFoundException;

}