package PageFactory;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class AmazonMainPageFactory {
    private WebDriver driver;

    @FindBy(xpath=".//input[@id='twotabsearchtextbox']")
    private WebElement searchBar;

    public AmazonMainPageFactory(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void fillSearchBar(String searchTerm) {
        searchBar.sendKeys(searchTerm);
        searchBar.sendKeys(Keys.ENTER);
    }
}