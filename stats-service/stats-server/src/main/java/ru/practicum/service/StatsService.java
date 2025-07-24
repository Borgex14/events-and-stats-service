package ru.practicum.service;

import ru.EndpointHitDtoRequest;
import ru.StatDtoResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    /**
     * Получение статистики по посещениям
     *
     * @param start Дата и время начала диапазона за который нужно выгрузить статистику
     * @param end Дата и время конца диапазона за который нужно выгрузить статистику
     * @param uris Список URI для которых нужно выгрузить статистику
     * @param unique Нужно ли учитывать только уникальные посещения
     * @return Список статистики
     */
    List<StatDtoResponse> getStats(LocalDateTime start,
                                   LocalDateTime end,
                                   List<String> uris,
                                   Boolean unique);

    /**
     * Сохранение информации о том, что к эндпоинту был запрос
     *
     * @param dto Данные о запросе
     */
    void create(EndpointHitDtoRequest dto);
}