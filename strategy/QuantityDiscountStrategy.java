package strategy;

public class QuantityDiscountStrategy implements DiscountStrategy {

    @Override
    public double getDiscountRate(int jumlah) {
        if (jumlah >= 5) {
            return 0.10;
        }

        return 0;
    }
}