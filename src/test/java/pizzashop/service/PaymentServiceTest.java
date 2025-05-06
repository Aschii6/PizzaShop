package pizzashop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pizzashop.repository.PaymentRepository;
import pizzashop.repository.MenuRepository;
import pizzashop.model.Payment;
import pizzashop.model.PaymentType;

import java.util.Arrays;
import java.util.Collections;

class PaymentServiceTest {

    private PaymentRepository payRepo;
    private MenuRepository menuRepo;
    private PaymentService service;

    @BeforeEach
    void init() {
        payRepo = mock(PaymentRepository.class);
        menuRepo = mock(MenuRepository.class);
        service = new PaymentService(menuRepo, payRepo);
    }

    @Test
    void addPaymentValidShouldCallRepoAdd() {
        service.addPayment(4, PaymentType.Card, 50.0);
        verify(payRepo, times(1)).add(any(Payment.class));
    }

    @Test
    void addPaymentInvalidAmountShouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addPayment(2, PaymentType.Cash, -5.0));
    }

    @Test
    void getTotalAmountReturnsSumForType() {
        when(payRepo.getAll()).thenReturn(Arrays.asList(
                new Payment(1, PaymentType.Card, 10.0),
                new Payment(2, PaymentType.Card, 20.0),
                new Payment(3, PaymentType.Cash, 5.0)
        ));

        double total = service.getTotalAmount(PaymentType.Card);
        assertEquals(30.0, total, 0.0001);
    }

    @Test
    void getTotalAmountEmptyOrNullReturnsZero() {
        when(payRepo.getAll()).thenReturn(Collections.emptyList());
        assertEquals(0.0, service.getTotalAmount(PaymentType.Cash), 0.0001);

        when(payRepo.getAll()).thenReturn(null);
        assertEquals(0.0, service.getTotalAmount(PaymentType.Card), 0.0001);
    }
}
