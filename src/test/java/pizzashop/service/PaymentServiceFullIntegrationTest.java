package pizzashop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import pizzashop.model.Payment;
import pizzashop.model.PaymentType;
import pizzashop.repository.PaymentRepository;
import pizzashop.repository.MenuRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class PaymentServiceFullIntegrationTest {

    @TempDir
    Path tempDir;

    private PaymentService service;
    private Path paymentsFile;

    @BeforeEach
    void setUp() throws Exception {
        // 1) Creăm fișierul temporar
        paymentsFile = tempDir.resolve("payments.txt");
        Files.createFile(paymentsFile);

        // 2) Suprascriem static filename în repo
        Field f = PaymentRepository.class.getDeclaredField("filename");
        f.setAccessible(true);
        f.set(null, paymentsFile.toString());

        // 3) Construim repo + service
        PaymentRepository payRepo = new PaymentRepository();
        MenuRepository menuRepo = mock(MenuRepository.class);
        service = new PaymentService(menuRepo, payRepo);
    }

    @Test
    void persistenceAcrossInstances() {
        // adaug o plată reală (E)
        service.addPayment(7, PaymentType.Card, 123.45);

        // reconstruiesc repo și service ca la o repornire
        PaymentRepository repo2 = new PaymentRepository();
        PaymentService service2 = new PaymentService(mock(MenuRepository.class), repo2);

        List<Payment> loaded = service2.getPayments();
        assertEquals(1, loaded.size());
        Payment p = loaded.get(0);
        assertAll("Verific proprietățile entității Payment",
                () -> assertEquals(7, p.getTableNumber()),
                () -> assertEquals(PaymentType.Card, p.getType()),
                () -> assertEquals(123.45, p.getAmount(), 1e-6)
        );
    }

    @Test
    void endToEndMultiplePaymentsScenario() {
        // adaug mai multe plăți de tipuri diferite
        service.addPayment(1, PaymentType.Cash, 10.0);
        service.addPayment(2, PaymentType.Card, 20.0);
        service.addPayment(1, PaymentType.Cash, 15.0);

        // citesc prin service
        List<Payment> all = service.getPayments();
        assertEquals(3, all.size(), "ar trebui 3 plăți în total");

        // calcul totaluri
        double cashTotal = service.getTotalAmount(PaymentType.Cash);
        double cardTotal = service.getTotalAmount(PaymentType.Card);
        assertEquals(25.0, cashTotal, 1e-6, "CASH: 10 + 15");
        assertEquals(20.0, cardTotal, 1e-6, "CARD: 20");

        // asigur că entitățile chiar există în lista returnată
        assertTrue(all.stream().anyMatch(p ->
                p.getTableNumber() == 2 &&
                        p.getType() == PaymentType.Card &&
                        p.getAmount() == 20.0
        ));
    }
}
