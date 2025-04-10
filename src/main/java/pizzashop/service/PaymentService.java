package pizzashop.service;

import pizzashop.model.MenuDataModel;
import pizzashop.model.Payment;
import pizzashop.model.PaymentType;
import pizzashop.repository.MenuRepository;
import pizzashop.repository.PaymentRepository;

import java.util.List;

public class PaymentService {

    private MenuRepository menuRepo;
    private PaymentRepository payRepo;

    public PaymentService(MenuRepository menuRepo, PaymentRepository payRepo){
        this.menuRepo=menuRepo;
        this.payRepo=payRepo;
    }

    public List<MenuDataModel> getMenuData(){return menuRepo.getMenu();}

    public List<Payment> getPayments(){return payRepo.getAll(); }

    public void addPayment(int table, PaymentType type, double amount) {
        if (amount <= 0 || amount > 10000.0) {
            throw new IllegalArgumentException("Invalid amount.");
        }
        if(table < 1 || table > 8){
            throw new IllegalArgumentException("Table number must be between 1 and 8");
        }
        Payment payment = new Payment(table, type, amount);
        payRepo.add(payment);
    }


    public double getTotalAmount(PaymentType type){
        double total=0.0f;
        List<Payment> l=getPayments();

        if (l==null) return total;

        if ((l.isEmpty())) return total;

        for (Payment p:l){
            if (p.getType().equals(type))
                total+=p.getAmount();
        }
        return total;
    }

    public double getTotalAmount(PaymentType type, List<Payment> l) {
        double total=0.0f;

        if (l ==null) return total;

        if ((l.isEmpty())) return total;

        for (Payment p: l){
            if (p.getType().equals(type))
                total+=p.getAmount();
        }
        return total;
    }
}