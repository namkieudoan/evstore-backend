package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "orders")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fullname", nullable = false, length = 100)
    private String fullName;

    @Column(name = "email", length = 100)
    private Float email;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "address", nullable = false, length = 200)
    private String address;

    @Column(name = "note", length = 200)
    private String note;

    @Column(name = "total_money", nullable = false)
    private Float totalMoney;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "shipping_method")
    private String shippingMethod;

    @Column(name = "shipping_address",length = 200)
    private String shippingAddress;

    @Column(name = "shipping_date")
    private Date shippingDate;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "payment_method")
    private String paymentMethod;

    private boolean active;

    @Column(name = "status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
