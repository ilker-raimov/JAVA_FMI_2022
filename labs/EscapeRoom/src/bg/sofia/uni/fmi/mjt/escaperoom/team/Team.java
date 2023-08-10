package bg.sofia.uni.fmi.mjt.escaperoom.team;

import bg.sofia.uni.fmi.mjt.escaperoom.rating.Ratable;

public class Team implements Ratable {
    private final String name;
    private final TeamMember[] teamMembers;
    private int teamRating;


    public static Team of(String name, TeamMember[] teamMembers) {
        return new Team(name, teamMembers);
    }

    private Team(String name, TeamMember[] teamMembers) {
        this.name = name;
        this.teamMembers = new TeamMember[teamMembers.length];
        System.arraycopy(teamMembers, 0, this.teamMembers, 0, teamMembers.length);
        teamRating = 0;
    }

    public void updateRating(int points) {
        if(points < 0) {
            throw new IllegalArgumentException();
        }

        teamRating += points;
    }

    public String getName() {
        return name;
    }

    public TeamMember[] getTeamMembers() {
        return teamMembers;
    }

    @Override
    public double getRating() {
        return (double)teamRating;
    }
}
