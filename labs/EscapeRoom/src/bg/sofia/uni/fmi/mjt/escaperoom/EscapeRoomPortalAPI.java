package bg.sofia.uni.fmi.mjt.escaperoom;

import bg.sofia.uni.fmi.mjt.escaperoom.exception.RoomNotFoundException;
import bg.sofia.uni.fmi.mjt.escaperoom.room.EscapeRoom;
import bg.sofia.uni.fmi.mjt.escaperoom.room.Review;
import bg.sofia.uni.fmi.mjt.escaperoom.team.Team;

public interface EscapeRoomPortalAPI {

    EscapeRoom getEscapeRoomByName(String roomName) throws RoomNotFoundException;

    void reviewEscapeRoom(String roomName, Review review) throws RoomNotFoundException;

    Review[] getReviews(String roomName) throws RoomNotFoundException;

    Team getTopTeamByRating();

}
