package org.example.boardback.repository.payment;

import org.example.boardback.entity.payment.Payment;
import org.example.boardback.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentKey(String paymentKey);
    List<Payment> findByUserOrderByCreatedAtDesc(User user);
}
