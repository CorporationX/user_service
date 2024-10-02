package school.faang.user_service.service.filter;

import school.faang.user_service.entity.goal.GoalInvitation;



public class InviterNameFilter implements InvitationFilter {
    private String inviterNamePattern;

    public InviterNameFilter(String inviterNamePattern) {
        this.inviterNamePattern = inviterNamePattern;
    }

    @Override
    public boolean filter(GoalInvitation goalInvitation) {
        return inviterNamePattern == null || goalInvitation.getInviter().getUsername().contains(inviterNamePattern);
    }

}
