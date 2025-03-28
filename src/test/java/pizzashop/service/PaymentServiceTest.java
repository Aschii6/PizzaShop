package pizzashop.service;
import org.junit.jupiter.api.*;
import pizzashop.model.PaymentType;
import pizzashop.repository.MenuRepository;
import pizzashop.repository.PaymentRepository;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {
    private PaymentService service;
    private PaymentRepository repository;
    @BeforeEach
    void setUp() {
        MenuRepository repoMenu = new MenuRepository();
        this.repository = new PaymentRepository();
        this.service = new PaymentService(repoMenu,repository);

        repository.getAll().clear();
        repository.writeAll();
    }

    @AfterEach
    void tearDown() {
        repository.getAll().clear();
        repository.writeAll();
    }

    @Test
    @Order(1)
    @DisplayName("TC1_ECP")
    @Tag("ECP")
    @Timeout(1)
    void addPaymentValidECP() {
        assertDoesNotThrow(() -> service.addPayment(3, PaymentType.Cash, 40));
        assertEquals(1, service.getPayments().size());
    }

    @Test
    @Order(2)
    @DisplayName("TC2_ECP")
    @Tag("ECP")
    @Timeout(1)
    void addPaymentNonValidECP() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> { service.addPayment(2, PaymentType.Card, -10.0);
                },"Invalid amount.");
        assertTrue(e.getMessage().contains("Invalid amount."),"Exception message does not match expected text.");
    }


    @Test
    @Order(3)
    @DisplayName("TC3_BVA")
    @Tag("BVA")
    @Timeout(1)
    void addPaymentValidBVA() {
        assertDoesNotThrow(() -> service.addPayment(7, PaymentType.Card, 9999.99));
        assertEquals(1, service.getPayments().size());
    }

    @Test
    @Order(4)
    @DisplayName("TC8_BVA")
    @Tag("BVA")
    @Timeout(1)
    void addPaymentNonValidBVA() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> { service.addPayment(9, PaymentType.Card, 50);
        },"Table number must be between 1 and 8");
        assertTrue(e.getMessage().contains("Table number must be between 1 and 8"),"Exception message does not match expected text.");
    }
}