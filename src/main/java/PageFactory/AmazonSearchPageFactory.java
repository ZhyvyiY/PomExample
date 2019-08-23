package PageFactory;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AmazonSearchPageFactory {
    private WebDriver driver;

    @FindAll(@FindBy(xpath="//span[@class='a-size-medium a-color-base a-text-normal']"))
    private List<WebElement> titles_web;

    @FindAll(@FindBy(xpath="//a[@class='a-size-base a-link-normal'] | //span[@class='a-size-base']"))
    private List<WebElement> authors_web;

    @FindAll(@FindBy(xpath="//div[@class='a-row a-size-small']//span[@class='a-icon-alt']"))
    private List<WebElement> ratings_web;

    @FindAll(@FindBy(xpath="//div[@class='a-section a-spacing-none a-spacing-top-small']//div[@class='a-row']" +
            "//a[@class='a-size-base a-link-normal s-no-hover a-text-normal']//span[@class='a-price']" +
            " | //div[@class='a-section a-spacing-none a-spacing-top-mini']//div[@class='a-row a-" +
            "spacing-mini']//a[@class='a-size-base a-link-normal s-no-hover a-text-normal']//span[" +
            "@class='a-price']"))
    private List<WebElement> prices_web;

    @FindAll(@FindBy(xpath="//div[@class='a-section a-spacing-micro s-min-height-small']"))
    private List<WebElement> bestSellers_web;

    @FindAll(@FindBy(xpath="//a[@class='a-size-base a-link-normal a-text-bold']"))
    private List<WebElement> price_labels;

    @FindAll(@FindBy(xpath="//div[@class='a-section a-spacing-none a-spacing-top-small']/parent::*"))
    private List<WebElement> price_parents;

    @FindAll(@FindBy(xpath="//h2[@class='a-size-mini a-spacing-none a-color-base s-line-clamp-2']/parent::*/parent::*"))
    private List<WebElement> rating_parents;

    public AmazonSearchPageFactory(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public List<String> GetTitles(List<String> titles_str) {
        for (WebElement title : titles_web) {
            titles_str.add(title.getText());
        }
        return titles_str;
    }

    public List<String> GetAuthors(List<String> authors_str) {
        StringBuilder author = new StringBuilder();
        boolean again = false;
        for (WebElement elem : authors_web) {
            String tempStr = elem.getText();

            if (!again && !tempStr.contains("by")) {
                authors_str.add(tempStr);
                author = new StringBuilder();
                again = false;
            }
            if (!tempStr.equals("by") && !tempStr.matches("^[0-9]+$") && !tempStr.contains("by")) {
                author.append(" ").append(tempStr);
            } else if (again) {
                authors_str.add(author.toString());
                author = new StringBuilder();
                again = false;
            } else {
                if (tempStr.equals("by")) {
                    author.append(tempStr);
                    again = true;
                }
            }
        }
        return authors_str;
    }

    public List<String> CheckRatings(List<String> isRating) {
        for (WebElement parent : rating_parents) {
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.MILLISECONDS);
            List<WebElement> children = parent.findElements(By.xpath(".//*"));
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

            int numOfChildren = children.size();

            if (numOfChildren > 15) {
                isRating.add("yes");
            } else {
                isRating.add("no");
            }
        }
        return isRating;
    }

    public List<String> GetRatings(List<String> ratings_str, List<String> isRating) {
        int ratingCounter = 0;
        for (WebElement rating : ratings_web) {
            if (isRating.get(ratingCounter).equals("yes")) {
                ratings_str.add(rating.getAttribute("innerHTML"));
                ratingCounter++;
            } else {
                ratings_str.add("No Ratings");
                ratingCounter++;
            }
        }
        return ratings_str;
    }

    public List<String> GetPricePairs(List<String> pricePairs) {
        int pairCounter = 0;
        for (WebElement parent : price_parents) {
            String childContents = getTextNode(parent);
            if (childContents.contains("Paperback")) {
                pairCounter++;
            }
            if (childContents.contains("Kindle")) {
                pairCounter++;
            }
            if (childContents.contains("Prime Video")) {
                pairCounter++;
            }

            if (pairCounter == 2) {
                pricePairs.add("pair");
                pairCounter = 0;
            } else {
                pricePairs.add("single");
                pairCounter = 0;
            }
        }
        return pricePairs;
    }

    public List<String> GetPrices(List<String> prices_str, List<String> pricePairs) {
        StringBuilder price = new StringBuilder();
        int labelCounter = 0;
        for (int i = 0; i < pricePairs.size(); i++) {
            for (int j = 0; j < prices_web.size(); j++) {
                String tempStr = prices_web.get(j).getText();

                if (pricePairs.get(i).equals("pair")) {
                    tempStr = tempStr.replace("\n", ",");
                    if (price_labels.get(j).getText().equals("Paperback")) {
                        price.append("\nPaperback ").append(tempStr);
                        labelCounter++;
                    } else if (price_labels.get(j).getText().equals("Kindle")) {
                        if (tempStr.equals("$0,00")) {
                            price.append("\n").append(tempStr).append(" kindleunlimited");
                            labelCounter++;
                        } else {
                            price.append("\nKindle ").append(tempStr);
                            labelCounter++;
                        }
                    }
                    if (labelCounter == 2) {
                        prices_str.add(price.toString());
                        price = new StringBuilder();
                        labelCounter = 0;
                        i++;
                    }
                } else {
                    tempStr = tempStr.replace("\n", ",");
                    if (price_labels.get(j).getText().equals("Paperback")) {
                        price.append("\nPaperBack ").append(tempStr);
                        prices_str.add(price.toString());
                        labelCounter = 0;
                        price = new StringBuilder();
                        i++;
                    } else if (price_labels.get(j).getText().equals("Kindle")) {
                        if (tempStr.equals("$0,00")) {
                            price.append("\n").append(tempStr).append(" kindleunlimited");
                            prices_str.add(price.toString());
                            labelCounter = 0;
                            price = new StringBuilder();
                            i++;
                        } else {
                            price.append("\nKindle ").append(tempStr);
                            prices_str.add(price.toString());
                            labelCounter = 0;
                            price = new StringBuilder();
                            i++;
                        }
                    } else if (price_labels.get(j).getText().equals("Prime Video")) {
                        price.append("\nPrime Video ").append(tempStr);
                        prices_str.add(price.toString());
                        labelCounter = 0;
                        price = new StringBuilder();
                        i++;
                    }
                }
            }
        }
        return prices_str;
    }

    public List<Boolean> GetBestSellers(List<Boolean> bestSellers_str) {
        for (WebElement bestSeller : bestSellers_web) {
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.MILLISECONDS);
            List<WebElement> children = bestSeller.findElements(By.xpath(".//*"));
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

            int numOfChildren = children.size();

            if (numOfChildren > 0) {
                bestSellers_str.add(true);
            } else {
                bestSellers_str.add(false);
            }
        }
        return bestSellers_str;
    }

    public void isPageLoaded() {
        WebElement logo = new WebDriverWait(driver, 5)
                .until(ExpectedConditions.elementToBeClickable(By.xpath(
                        ".//span[@class='nav-sprite nav-logo-base']")));

        if (logo.isDisplayed()) {
            System.out.println("Page Status: LOADED");
        } else {
            System.out.println("Page Status: EMPTY");
        }
    }

    private static String getTextNode(WebElement e) {
        String text = e.getText().trim();
        List<WebElement> children = e.findElements(By.xpath("./*"));
        for (WebElement child : children) {
            text = text.replaceFirst(child.getText(), "").trim();
        }
        return text;
    }
}