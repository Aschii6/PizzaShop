package pizzashop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import pizzashop.model.PaymentType;
import pizzashop.repository.PaymentRepository;
import pizzashop.repository.MenuRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

class PaymentServiceIntegrationRTest {

    @TempDir
    Path tempDir;

    private PaymentService service;

    @BeforeEach
    void setUp() throws Exception {
        // 1) Creăm un fișier gol în tempDir/payments.txt
        Path paymentsFile = tempDir.resolve("payments.txt");
        Files.createFile(paymentsFile);

        // 2) Override static filename din PaymentRepository
        Field f = PaymentRepository.class.getDeclaredField("filename");
        f.setAccessible(true);
        f.set(null, paymentsFile.toString());

        // 3) Instanțiem repo real + mock MenuRepository
        PaymentRepository payRepo = new PaymentRepository();
        MenuRepository    menuRepo = mock(MenuRepository.class);

        // 4) Construim service-ul cu dependențele
        service = new PaymentService(menuRepo, payRepo);
    }

    @Test
    void addPayment_ShouldPersistOnePayment() {
        service.addPayment(1, PaymentType.Cash, 15.0);
        assertEquals(1, service.getPayments().size(),
                "după addPayment, lista din repo ar trebui să conţină exact 1 plată");
    }

    @Test
    void getTotalAmount_ShouldSumOnlyRequestedType() {
        // adăugăm plăţi de două tipuri
        service.addPayment(2, PaymentType.Card, 20.0);
        service.addPayment(3, PaymentType.Card, 30.0);
        service.addPayment(4, PaymentType.Cash, 10.0);

        double totalCard = service.getTotalAmount(PaymentType.Card);
        double totalCash = service.getTotalAmount(PaymentType.Cash);

        assertEquals(50.0, totalCard, 1e-6,
                "totalul pentru CARD trebuie să fie 20 + 30");
        assertEquals(10.0, totalCash, 1e-6,
                "totalul pentru CASH trebuie să fie 10");
    }
}
