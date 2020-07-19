package EpamFlightTest.TestCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TestApp {

	final By SOURCE_ELEMENT = By.xpath("//input[@name='or-src']");
	final By DESTINATION_ELEMENT = By.xpath("//input[@name='or-dest']");
	final By SOURCE_DROPDOWN_SELECT = By.xpath(
			"//input[@name='or-src']/parent::div/div/div/div[contains(@class,'pop-dest')]/div[@class='airport-city']");
	final By DESTINATION_DROPDOWN_SELECT = By.xpath(
			"//input[@name='or-dest']/parent::div/div/div/div[contains(@class,'pop-dest')]/div[@class='airport-city']");
	final By CLOSE_DATE_PICKER = By.xpath("//a[contains(text(),'Book Flight')]");
	final By SEARCH_BUTTON = By.xpath("//button/span[contains(text(),'Search Flight')]");
	final By NON_STOP_BUTTON = By.xpath("//div[contains(@class,'filter-items')]//button[contains(text(),'Non-stop')]");
	final By FLIGHT_LIST = By.xpath("//div[contains(@class,'trips-row ') and contains(@class,'d-flex')]");
	final By FLIGHT_NUMBERS_LIST = By.xpath(
			"//div[contains(@class,'trips-row ') and contains(@class,'d-flex')]//div[@class='row']//span[@class='flightNo']");
	final By FLIGHT_DURATION_LIST = By.xpath(
			"//div[contains(@class,'trips-row ') and contains(@class,'d-flex')]//div[@class='row']//div[contains(@class,'duration-Big')]");
	final By FLIGHT_PRICE_LIST = By.xpath(
			"//div[contains(@class,'trips-row ') and contains(@class,'d-flex')]//div[contains(@class,'price-details')]/label[1]//div[contains(@class,'price')]/span");
	final By FLIGHT_TIME_LIST = By
			.xpath("//div[contains(@class,'trips-row ') and contains(@class,'d-flex')]//div[@class='time']");
	private String source;
	private String destination;

	WebDriver driver = null;
	WebDriverWait wait;

	@Given("I am at flight homepage")

	public void goToFlightBookingSite() {
		System.setProperty("webdriver.chrome.driver", "./chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get("https://www.goindigo.in");
	}

	@When("^I enter source \"(.*)\"$")

	public void enterSource(String source) throws PendingException {
		this.source = source;
		if (driver.findElement(SOURCE_ELEMENT).getText() != null) {
			driver.findElement(SOURCE_ELEMENT).sendKeys(Keys.CONTROL, "a");
		}
		driver.findElement(SOURCE_ELEMENT).sendKeys(source);
		for (WebElement ele : driver.findElements(SOURCE_DROPDOWN_SELECT)) {
			if (ele.getText().contains(source)) {
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", ele);
			}
		}
	}

	@And("^I enter destination \"(.*)\"$")

	public void enterDestination(String destination) {
		this.destination = destination;
		driver.findElement(DESTINATION_ELEMENT).sendKeys(destination);
		for (WebElement ele : driver.findElements(DESTINATION_DROPDOWN_SELECT)) {
			if (ele.getText().contains(destination)) {
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", ele);
			}
		}
		driver.findElement(CLOSE_DATE_PICKER).click();

	}

	@When("I click on search button")

	public void clickSearch() {

		driver.findElement(SEARCH_BUTTON).click();
	}

	@Then("I will display the best flight")

	public void displayBestFlightDetails() {
		displayBestFlightNumber();
		driver.close();
		driver.quit();
	}

	public void displayBestFlightNumber() {
		List<String> flightNumbers = new ArrayList<String>();
		List<Integer> flightDuration = new ArrayList<Integer>();
		List<Integer> flightFares = new ArrayList<Integer>();
		List<Integer> flightTimes = new ArrayList<Integer>();
		List<Integer> indexes = new ArrayList<Integer>();
		wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.visibilityOfElementLocated(NON_STOP_BUTTON));
		driver.findElement(NON_STOP_BUTTON).click();
		if (driver.findElements(FLIGHT_LIST).size() > 0) {
			for (int i = 0; i < driver.findElements(FLIGHT_LIST).size(); i++) {
				flightNumbers.add(driver.findElements(FLIGHT_NUMBERS_LIST).get(i).getText());
				flightDuration.add(getTimeInMinutes(driver.findElements(FLIGHT_DURATION_LIST).get(i).getText()));
				flightFares.add(getFormattedPrice(driver.findElements(FLIGHT_PRICE_LIST).get(i).getText()));
				flightTimes.add(getFlightTimeFormatted(driver.findElements(FLIGHT_TIME_LIST).get(i).getText()));
			}

			System.out.println("flight details from " + source + " to " + destination + " : " + flightNumbers + " "
					+ flightDuration + " " + flightFares);

			int leastDuration = Collections.min(flightDuration);
			for (int i = 0; i < flightDuration.size(); i++) {
				if (flightDuration.get(i) == leastDuration) {
					indexes.add(i);
				}
			}
			if (indexes.size() > 1) {
				List<Integer> tempListFares = new ArrayList<Integer>();
				List<Integer> tempListTimes = new ArrayList<Integer>();
				for (int o = 0; o < indexes.size(); o++) {
					tempListFares.add(flightFares.get(indexes.get(o)));
					tempListTimes.add(flightTimes.get(indexes.get(o)));
				}
				for (int j = 0; j < indexes.size(); j++) {
					for (int k = j + 1; k < indexes.size(); k++) {
						int x = flightFares.get(indexes.get(j));
						int y = flightFares.get(indexes.get(k));
						if (x == y) {
							int flightTime = Collections.max(tempListTimes);
							System.out.println("fastest and cheapest filght from " + source + " to " + destination
									+ " : " + flightNumbers.get(flightTimes.indexOf(flightTime)));
						} else if (x != y) {
							int index = flightFares.indexOf(Collections.min(tempListFares));
							System.out.println("fastest and cheapest filght from " + source + " to " + destination
									+ " : " + flightNumbers.get(index));
						}

					}
				}
			} else {
				System.out.println("fastest and cheapest filght from " + source + " to " + destination + " : "
						+ flightNumbers.get(indexes.get(0)));
			}
		} else {
			System.out.println("No Direct flights available");
		}

	}

	public int getTimeInMinutes(String duration) {
		String output[] = duration.split(" ");
		int hours = Integer.parseInt(output[0].replace("h", ""));
		int minutes = Integer.parseInt(output[1].replace("m", ""));
		int totalTimeInMiutes = hours * 60 + minutes;

		return totalTimeInMiutes;
	}

	public int getFormattedPrice(String price) {
		int priceFormatted = Integer.parseInt(price.replaceAll("[^a-zA-Z0-9]", ""));

		return priceFormatted;
	}

	public int getFlightTimeFormatted(String time) {
		String times[] = time.split("-");
		String output[] = times[0].split(":");
		int finalValue = Integer.parseInt(output[0].replaceAll("[^a-zA-Z0-9]", ""));

		return finalValue;
	}
}
