package school.faang.user_service.exeptions;

public class NoOneParticipatesInTheEvent extends RuntimeException {
    public NoOneParticipatesInTheEvent(long eventId){
        super(String.format("No one participates in the event with %d id.",eventId));
    }
}
