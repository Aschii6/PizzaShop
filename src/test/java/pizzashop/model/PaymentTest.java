package pizzashop.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import pizzashop.model.Payment;
import pizzashop.model.PaymentType;

class PaymentTest {

    @Test
    void gettersAndSettersShouldWork() {
        Payment p = new Payment(3, PaymentType.Cash, 25.5);
        assertEquals(3, p.getTableNumber());
        assertEquals(PaymentType.Cash, p.getType());
        assertEquals(25.5, p.getAmount(), 0.0001);

        p.setTableNumber(5);
        p.setType(PaymentType.Card);
        p.setAmount(100.0);
        assertEquals(5, p.getTableNumber());
        assertEquals(PaymentType.Card, p.getType());
        assertEquals(100.0, p.getAmount(), 0.0001);
    }

    @Test
    void toStringReturnsCsvFormat() {
        Payment p = new Payment(2, PaymentType.Card, 45.0);
        String expected = "2,Card,45.0";
        assertEquals(expected, p.toString());
    }
}
