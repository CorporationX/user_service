Условия задачи
Реализовать возможность пользователю регистрироваться на события, созданные другим пользователем. Нужно проверить, что пользователь еще не зарегистрирован на это событие.

1. [x] В пакете dto создать класс UserDto, если его еще нет. Поля:

   + private long id;

   + private String username;

   + private String email;

2.[ ] В пакете controller.event
   + cоздайте класс EventParticipationController, если его нет. Он будет отвечать за обработку запросов пользователя и валидацию этих запросов. Сделайте так, чтобы он был Spring bean. Внедрите в него бин класса EventParticipationService - его создадим в следующих пунктах.

   + Создайте метод registerParticipant. Этот метод должен передавать id пользователя и эвента в метод registerParticipant класса EventPartcipationService.

4.[ ] В пакете service.event создайте класс EventParticipationService, если его еще нет. Он будет содержать бизнес-логику. Сделайте его Spring bean. Внедрите в него бин класса EventParticipationRepository. Этот класс уже предоставлен, его методы:

   + void register(long eventId, long userId);

   + void unregister(long eventId, long userId);

   + List<User> findAllParticipantsByEventId(long eventId);

   + int countParticipants(long eventId);

5.[ ] Создайте в классе EventParticipationService:
   + метод registerParticipant(long eventId, long userId) для регистрации пользователя. Нужно проверить, что данный пользователь еще не зарегистрирован на событие, и в этом случае зарегистрировать его. Иначе выбросить исключение. Подумайте, какие методы EventParticipationRepository нужно использовать для этих действий.

6.[ ] Покрыть весь написанный код unit-тестами.