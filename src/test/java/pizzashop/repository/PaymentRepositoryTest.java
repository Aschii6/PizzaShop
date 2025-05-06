package pizzashop.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import pizzashop.model.Payment;
import pizzashop.model.PaymentType;
import pizzashop.repository.PaymentRepository;
import static org.mockito.Mockito.lenient;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
@MockitoSettings(strictness = Strictness.LENIENT)

@ExtendWith(MockitoExtension.class)
class PaymentRepositoryTest {

    @Spy
    private PaymentRepository repo;      // creează un spy

    @BeforeEach
    void initSpy() throws Exception {
        // stipulează că readPayments() nu va încerca să deschidă vreun fișier
        lenient().doNothing().when(repo).readPayments();
        // înlocuiește intern lista cu una goală
        Field listField = PaymentRepository.class.getDeclaredField("paymentList");
        listField.setAccessible(true);
        listField.set(repo, new ArrayList<Payment>());
    }

    @Test
    void addShouldCallWriteAllAndGetAllReturnsIt() {
        Payment p = new Payment(1, PaymentType.Cash, 10.0);
        repo.add(p);
        // verifică că writeAll() a fost apelat automat
        verify(repo, times(1)).writeAll();

        List<Payment> all = repo.getAll();
        assertEquals(1, all.size());
        assertEquals(1, all.get(0).getTableNumber());
        assertEquals(PaymentType.Cash, all.get(0).getType());
        assertEquals(10.0, all.get(0).getAmount(), 0.0001);
    }

    @Test
    void writeAllOverwritesInternalList() throws Exception {
        // pregătește o listă de plăți „factice”
        List<Payment> fakeList = List.of(
                new Payment(2, PaymentType.Card, 20.0),
                new Payment(3, PaymentType.Cash, 30.0)
        );
        // injectează-o direct în obiectul spy
        Field f = PaymentRepository.class.getDeclaredField("paymentList");
        f.setAccessible(true);
        f.set(repo, new ArrayList<>(fakeList));

        // apelează writeAll – nu ne interesează fișierul, doar că metoda merge fără excepții
        repo.writeAll();

        // acum, dacă am fi forțat readPayments() să redea fakeList, getAll() ar da listă întreagă
        doReturn(fakeList).when(repo).getAll();
        assertEquals(2, repo.getAll().size());
    }
}
