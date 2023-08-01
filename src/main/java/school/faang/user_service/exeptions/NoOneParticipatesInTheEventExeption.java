package school.faang.user_service.exeptions;

public class NoOneParticipatesInTheEventExeption extends RuntimeException {
    public NoOneParticipatesInTheEventExeption(long eventId){
        super(String.format("No one participates in the event with %d id.",eventId));
    }
}
