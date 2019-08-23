import PageFactory.AmazonMainPageFactory;
import PageFactory.AmazonSearchPageFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

class SearchResultObject {
    String ProductTitle;
    String ProductAuthor;
    String ProductRating;
    String ProductPrice;
    Boolean ProductIsBestSeller;

    SearchResultObject(String ProductTitle, String ProductAuthor,
                       String ProductRating, String ProductPrice, Boolean ProductIsBestSeller) {
        this.ProductTitle = ProductTitle;
        this.ProductAuthor = ProductAuthor;
        this.ProductRating = ProductRating;
        this.ProductPrice = ProductPrice;
        this.ProductIsBestSeller = ProductIsBestSeller;
    }
}

public class TestWebSite implements TestInterface {
    private WebDriver driver;
    private AmazonMainPageFactory pageMain;
    private AmazonSearchPageFactory pageSearch;

    @BeforeTest
    public void driverStart() {
        System.setProperty("webdriver.chrome.driver", "./libs/chromedriver_win32/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        driver.navigate().to("http://www.amazon.com");
    }

    @Test
    public void driverActions() {
        int QuerySize = 16;
        pageMain = new AmazonMainPageFactory(driver);
        pageSearch = new AmazonSearchPageFactory(driver);

        pageMain.fillSearchBar("Java");

        pageSearch.isPageLoaded();

        List<String> titles_str = new ArrayList<>();
        List<String> authors_str = new ArrayList<>();
        List<String> ratings_str = new ArrayList<>();
        List<String> prices_str = new ArrayList<>();
        List<Boolean> bestSellers_str = new ArrayList<>();
        List<String> isRating = new ArrayList<>();
        List<String> pricePairs = new ArrayList<>();
        List<SearchResultObject> search_results = new ArrayList<>();

        titles_str = pageSearch.GetTitles(titles_str);
        authors_str = pageSearch.GetAuthors(authors_str);
        isRating = pageSearch.CheckRatings(isRating);
        ratings_str = pageSearch.GetRatings(ratings_str, isRating);
        pricePairs = pageSearch.GetPricePairs(pricePairs);
        prices_str = pageSearch.GetPrices(prices_str, pricePairs);
        bestSellers_str = pageSearch.GetBestSellers(bestSellers_str);

        for (int i = 0; i < QuerySize; i++) {
            search_results.add(new SearchResultObject(titles_str.get(i),
                    authors_str.get(i), ratings_str.get(i), prices_str.get(i), bestSellers_str.get(i)));
        }

        for (SearchResultObject item : search_results) {
            System.out.println("\n" + item.ProductTitle);
            System.out.println(item.ProductAuthor);
            System.out.println(item.ProductRating);
            System.out.println("Pricing: \n" + item.ProductPrice);System.out.println("\nBest Seller: " +
                    item.ProductIsBestSeller.toString() + "\n\n");
        }

        System.out.println("Is there a: 'Head First Java, 2nd Edition'?");
        if (titles_str.contains("Head First Java, 2nd Edition")) {
            System.out.println("Yes");
        } else {
            System.out.println("Nope");
        }
    }

    @AfterTest
    public void driverStop() {
        driver.close();
    }
}
