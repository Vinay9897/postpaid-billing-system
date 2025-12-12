package com.abc.postpaid.billing.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentRequest {
    private LocalDate paymentDate;
    private BigDecimal amount;
    private String paymentMethod;

    public PaymentRequest() {}

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
