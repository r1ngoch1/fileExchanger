package ru.royal.fileExchanger.entities;

/**
 * перечисление статусов отчета
 */
public enum ReportStatus {
    /**
     * отчет создается
     */
    GENERATED,
    /**
     * отчет создался успешно
     */
    COMPLETED,
    /**
     * при создании отчета произошла ошибка
     */
    ERROR
}
