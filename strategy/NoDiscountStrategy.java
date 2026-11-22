package strategy;

public class NoDiscountStrategy implements DiscountStrategy {

    @Override
    public double getDiscountRate(int jumlah) {
        return 0;
    }
}