package bg.sofia.uni.fmi.mjt.escaperoom;

import bg.sofia.uni.fmi.mjt.escaperoom.exception.PlatformCapacityExceededException;
import bg.sofia.uni.fmi.mjt.escaperoom.exception.RoomAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.escaperoom.exception.RoomNotFoundException;
import bg.sofia.uni.fmi.mjt.escaperoom.exception.TeamNotFoundException;
import bg.sofia.uni.fmi.mjt.escaperoom.room.EscapeRoom;
import bg.sofia.uni.fmi.mjt.escaperoom.room.Review;
import bg.sofia.uni.fmi.mjt.escaperoom.team.Team;

import java.util.Arrays;

public class EscapeRoomPlatform implements EscapeRoomAdminAPI, EscapeRoomPortalAPI{
    private final Team[] teams;
    private final int maxCapacity;
    EscapeRoom[] escapeRooms;
    private int escapeRoomsSize;


    public EscapeRoomPlatform(Team[] teams, int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.teams = new Team[teams.length];

        for(int i = 0; i < teams.length; i++) {
            this.teams[i] = Team.of(teams[i].getName(), teams[i].getTeamMembers());
        }

        escapeRooms = new EscapeRoom[4];
        escapeRoomsSize = 0;
    }


    @Override
    public void addEscapeRoom(EscapeRoom room) throws RoomAlreadyExistsException {
        if(room == null) {
            throw new IllegalArgumentException();
        }

        if(escapeRoomsSize == maxCapacity) {
            throw new PlatformCapacityExceededException();
        }

        if(escapeRooms != null) {
            for(int i = 0; i < escapeRoomsSize; i++) {
                if(escapeRooms[i].equals(room)) {
                    throw new RoomAlreadyExistsException();
                }
            }
        }

        if(escapeRoomsSize == escapeRooms.length) {
            resizeEscapeRooms();
        }

        rearrangeEscapeRooms();

        escapeRooms[0] = room;
        escapeRoomsSize++;
    }

    @Override
    public void removeEscapeRoom(String roomName) throws RoomNotFoundException {
        if(roomName == null || roomName.isBlank() || roomName.isEmpty()) {
            throw new IllegalArgumentException();
        }

        int index = 0;
        for(; index < escapeRoomsSize; index++) {
            if(escapeRooms[index].getName().equals(roomName)) {
                break;
            }
        }

        if(index == escapeRoomsSize) {
            throw new RoomNotFoundException();
        }

        for(; index + 1 < escapeRoomsSize; index++) {
            escapeRooms[index] = escapeRooms[index + 1];
        }

        escapeRoomsSize--;
    }

    @Override
    public EscapeRoom[] getAllEscapeRooms() {
        EscapeRoom[] cutEscapeRooms = Arrays.copyOf(escapeRooms, escapeRoomsSize);

        return cutEscapeRooms;
    }

    @Override
    public void registerAchievement(String roomName, String teamName, int escapeTime) throws RoomNotFoundException, TeamNotFoundException {
        if(roomName == null || roomName.isEmpty() || roomName.isBlank()) {
            throw new IllegalArgumentException();
        }

        if(teamName == null || teamName.isEmpty() || teamName.isBlank()) {
            throw new IllegalArgumentException();
        }

        if(escapeTime <= 0) {
            throw new IllegalArgumentException();
        }

        int roomIndex = 0;
        for(; roomIndex < escapeRoomsSize; roomIndex++) {
            if(escapeRooms[roomIndex].getName().equals(roomName)) {
                break;
            }
        }

        if(roomIndex == escapeRoomsSize) {
            throw new RoomNotFoundException();
        }

        if(escapeTime > escapeRooms[roomIndex].getMaxTimeToEscape()) {
            throw new IllegalArgumentException();
        }

        int teamIndex = 0;
        for(; teamIndex < teams.length; teamIndex++) {
            if(teams[teamIndex].getName().equals(teamName)) {
                break;
            }
        }

        if(teamIndex == teams.length) {
            throw new TeamNotFoundException();
        }


        int points = escapeRooms[roomIndex].getDifficulty().getRank();
        if(escapeTime * 2 <= escapeRooms[roomIndex].getMaxTimeToEscape()) {
            points += 2;
        }
        else if(escapeTime * 4 <= escapeRooms[roomIndex].getMaxTimeToEscape() * 3) {
            points += 1;
        }

        teams[teamIndex].updateRating(points);
    }

    @Override
    public EscapeRoom getEscapeRoomByName(String roomName) throws RoomNotFoundException {
        if(roomName == null || roomName.isEmpty() || roomName.isBlank()) {
            throw new IllegalArgumentException();
        }

        for(int i = 0; i < escapeRoomsSize; i++) {
            if(escapeRooms[i].getName().equals(roomName)) {
                return escapeRooms[i];
            }
        }

        throw new RoomNotFoundException();
    }

    @Override
    public void reviewEscapeRoom(String roomName, Review review) throws RoomNotFoundException {
        if(review == null) {
            throw new IllegalArgumentException();
        }

        if(roomName == null || roomName.isEmpty() || roomName.isBlank()) {
            throw new IllegalArgumentException();
        }

        int index;
        for(index = 0; index < escapeRoomsSize; index++) {
            if(escapeRooms[index].getName().equals(roomName)) {
                break;
            }
        }

        if(index == escapeRoomsSize) {
            throw new RoomNotFoundException();
        }

        escapeRooms[index].addReview(review);
    }

    @Override
    public Review[] getReviews(String roomName) throws RoomNotFoundException {
        if(roomName == null || roomName.isEmpty() || roomName.isBlank()) {
            throw new IllegalArgumentException();
        }

        int index;
        for(index = 0; index < escapeRoomsSize; index++) {
            if(escapeRooms[index].getName().equals(roomName)) {
                break;
            }
        }

        if(index == escapeRoomsSize) {
            throw new RoomNotFoundException();
        }

        if(escapeRooms[index].getReviews().length == 0) {
            return new Review[0];
        }

        return escapeRooms[index].getReviews();
    }

    @Override
    public Team getTopTeamByRating() {
        if(teams == null || teams.length == 0) {
            return null;
        }

        if(teams.length == 1) {
            return teams[0];
        }

        double maxPoints = -1;
        int maxIndex = 0;
        for(int i = 0; i < teams.length; i++) {
            if(teams[i].getRating() > maxPoints) {
                maxIndex = i;
                maxPoints = teams[i].getRating();
            }
        }

        return teams[maxIndex];
    }

    private void resizeEscapeRooms() {
        int newSize;
        if(escapeRooms.length * 2 > maxCapacity) {
            newSize = maxCapacity;
        }
        else {
            newSize = escapeRooms.length * 2;
        }

        EscapeRoom[] newEscapeRooms = new EscapeRoom[newSize];
        for(int i = 0; i < escapeRoomsSize; i++) {
            newEscapeRooms[i] = escapeRooms[i];
        }

        escapeRooms = Arrays.copyOf(newEscapeRooms, newEscapeRooms.length);
    }

    private void rearrangeEscapeRooms() {
        int endIndex = escapeRoomsSize;
        if(escapeRoomsSize == maxCapacity) {
            endIndex--;
        }

        for(; endIndex > 0; endIndex--) {
            escapeRooms[endIndex] = escapeRooms[endIndex - 1];
        }
    }
}
