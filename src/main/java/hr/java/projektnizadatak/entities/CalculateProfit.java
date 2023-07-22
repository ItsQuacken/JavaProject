package hr.java.projektnizadatak.entities;

import hr.java.projektnizadatak.presentation.views.ProfitCalculatorView;

public class CalculateProfit<T, U> {
    public double calculate(T selectedCrypto, double amount, String entryPrice, ProfitCalculatorView<T, U> parentView) {
        double price = parentView.getPriceFromDatabase(selectedCrypto);
        double entryPriceValue = Double.parseDouble(entryPrice);
        return (price - entryPriceValue) * amount;
    }
}