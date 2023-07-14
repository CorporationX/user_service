”слови€ задачи
–еализовать возможность пользователю снимать себ€ с регистрации на событи€, созданные другим пользователем. Ќужно проверить, что пользователь был зарегистрирован на это событие.

1. ¬ пакете controller.event cоздайте класс EventParticipationController, если его нет. ќн будет отвечать за обработку запросов пользовател€ и валидацию этих запросов. —делайте так, чтобы он был Spring bean. ¬недрите в него бин класса EventParticipationService - его создадим в следующих пунктах.

   * —оздайте метод unregisterParticipant. Ётот метод должен передавать айди пользовател€ и эвента в метод unregisterParticipant класса EventPartcipationService.

2. ¬ пакете service.event создайте класс EventParticipationService, если его еще нет. ќн будет содержать бизнес-логику. —делайте его Spring bean. ¬недрите в него бин класса EventParticipationRepository. Ётот класс уже предоставлен, его методы:

   * void register(long eventId, long userId);

   * void unregister(long eventId, long userId);

   * List<User> findAllParticipantsByEventId(long eventId);

   * int countParticipants(long eventId);

3. —оздайте в классе EventParticipationService метод unregisterParticipant(long eventId, long userId) дл€ отмены регистрации пользовател€. Ќужно проверить, что данный пользователь был зарегистрирован на событие, и в этом случае отменить регистрацию. »наче выбросить исключение. ѕодумайте, какие методы EventParticipationRepository нужно использовать дл€ этих действий.

4. ѕокрыть весь написанный код unit-тестами.