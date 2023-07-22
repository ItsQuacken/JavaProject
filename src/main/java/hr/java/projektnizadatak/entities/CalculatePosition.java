package hr.java.projektnizadatak.entities;

import hr.java.projektnizadatak.presentation.views.ProfitCalculatorView;

public class CalculatePosition<T, U> {
    public double calculate(T selectedCrypto, double amount, ProfitCalculatorView<T, U> parentView) {
        double price = parentView.getPriceFromDatabase(selectedCrypto);
        return price * amount;
    }
}
