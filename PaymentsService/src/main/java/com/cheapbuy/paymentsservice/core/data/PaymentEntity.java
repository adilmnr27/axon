package com.cheapbuy.paymentsservice.core.data;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table(name = "payments")
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEntity {

    @Id
    private String paymentId;

    @Column
    public String orderId;


}
