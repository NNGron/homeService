package ir.entity.enums;

public enum OrderStatus {
    WAITING_FOR_EXPERT_PROPOSALS, //    منتظر پیشنهاد متخصص‌ها
    WAITING_FOR_EXPERT_SELECTION_BY_CUSTOMER, //    منتظر انتخاب متخصص توسط مشتری
    WAITING_FOR_EXPERT_ARRIVAL, //     منتظر رسیدن متخصص
    STARTED,
    COMPLETED,
    PAID
}