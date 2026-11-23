package strategy;

public class DiscountStrategyFactory {

    public static DiscountStrategy createStrategy(int jumlah) {

        if (jumlah >= 5) {
            return new QuantityDiscountStrategy();
        } else {
            return new NoDiscountStrategy();
        }
    }
}