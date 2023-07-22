package hr.java.projektnizadatak.presentation;

public enum ApplicationScreen {
	Login("Login", "views/login-view.fxml"),
	PriceData("Price Data", "views/price.fxml"),
	AdminPanel("Admin Panel","views/manage-users-view.fxml"),
	ProfitCalculator("Profit Calculator", "views/profit-calculator-view.fxml"),
	ChartScreen("Chart Screen", "views/chart-view.fxml");
	
	private final String title;
	private final String fxmlPath;
	
	ApplicationScreen(String title, String fxmlPath) {
		this.title = title;
		this.fxmlPath = fxmlPath;
	}

	public String getTitle() {
		return title;
	}

	public String getFxmlPath() {
		return fxmlPath;
	}
	
	@Override
	public String toString() {
		return title + "(" + fxmlPath + ")";
	}
}
