package ru.lanit.atschool.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.ru.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import ru.lanit.atschool.webdriver.WebDriverManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainPageSteps{
    WebDriver driver = WebDriverManager.getDriver();
    protected final Wait<WebDriver> wait = new WebDriverWait(WebDriverManager.getDriver(), 100, 1000);

    private boolean isElementPresent(String locator_string){
        try {
            WebDriverManager.getDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
            return WebDriverManager.getDriver().findElement(By.xpath(locator_string)).isDisplayed();
        } catch (NoSuchElementException e){
            return false;
        } finally {
            WebDriverManager.getDriver().manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
        }
    }

    @Пусть("открыт браузер и введен адрес \"(.*)\"$")
    public void открытБраузерИВведенАдрес(String url) {
        driver.get(url);
    }

    @Тогда("тест завершен")
    public void тестЗавершен() {
        driver.quit();
    }

    @И("переход на страницу Категории")
    public void переходНаСтраницуКатегории() throws InterruptedException {
        WebElement webElement = driver.findElement(By.linkText("Категории"));
        webElement.click();
        System.out.println("Нашли и клинкули ссылку 'Категории'");
        Thread.sleep(1000);
    }

    @И("переход на страницу Пользователи")
    public void переходНаСтраницуПользователи() throws InterruptedException{
        WebElement webElement = driver.findElement(By.linkText("Пользователи"));
        webElement.click();
        System.out.println("Нашли и клинкули ссылку 'Пользователи'");
        Thread.sleep(1000);
    }

    @И("поиск пользователя из предыстории")
    public void поискПользователяИзПредыстории() throws InterruptedException {
        WebElement webElement = driver.findElement(By.xpath("//div[@class='navbar-search dropdown']"));
        webElement.click();
        Thread.sleep(1000);
        webElement = driver.findElement(By.xpath("//*[@aria-controls='dropdown-menu dropdown-search-results']"));
        webElement.click();
        webElement.sendKeys("gromovalex");
        Thread.sleep(1000);
        webElement = driver.findElement(By.xpath("//*[@class='dropdown-search-user']"));
        webElement.click();
        Assert.assertTrue(driver.findElement(By.xpath("//abbr[@title='Присоединился 26 марта 2020 г., 11:35']")).isDisplayed());
        System.out.println("Нашли пользователя 'gromovalex'");
        Thread.sleep(1000);
    }

//    эту штуку с циклами по любому не разбить никак на отдельные
    @Дано("^проверка логинов и паролей пользователей$")
    public void логиныИПаролиПользователей(DataTable arg) throws InterruptedException, NullPointerException {
        List<Map<String, String>> table = arg.asMaps(String.class, String.class);
        for (int i=0; i<table.size(); i++) {
            WebElement webElement = driver.findElement(By.xpath("//button[@class='btn navbar-btn btn-default btn-sign-in']"));
            webElement.click();
            Thread.sleep(1000);
            String username = table.get(i).get("login");
            String password = table.get(i).get("password");
            WebElement webElementLogin = driver.findElement(By.xpath("//input[@id='id_username']"));
            webElementLogin.clear();
            webElementLogin.click();
            try {
                webElementLogin.sendKeys(username.toString());
                if (password.isEmpty()) {
                    webElementLogin.findElement(By.xpath("//button[@class='btn btn-primary btn-block' and @type='submit']"));
                    webElement.click();
                }
                WebElement webElementPassword = driver.findElement(By.xpath("//input[@id='id_password']"));
                webElementPassword.clear();
                webElementPassword.click();
                webElementPassword.sendKeys(password.toString());
                if (password.isEmpty()) {
                    webElementLogin.findElement(By.xpath("//button[@class='btn btn-primary btn-block' and @type='submit']"));
                    webElement.click();}
                Thread.sleep(100);
                webElementPassword.sendKeys(Keys.ENTER);
//                webElementLogin.findElement(By.xpath("//button[@class='btn btn-primary btn-block' and @type='submit']"));
//                webElement.click();
            } catch (NullPointerException npe) {}
            Thread.sleep(1000);

            if (isElementPresent("//div[@class='alerts-snackbar in']")|| isElementPresent("//button[@class='close' and @aria-label='Закрыть']")) {
                System.out.println("Неверные параметры. Авторизоваться нет возможности! Проверка пройдена!"); // там 2 вида вывода, или поля пустые или неверный логин пароль. причем это все выведено через 1 элемент, не разделить их
//                Assert.assertTrue(driver.findElement(By.xpath("//div[@class='alerts-snackbar in']")).isDisplayed());
                Thread.sleep(4000); // закрывает кнопку красная надпись, попасть по элементу не может если меньше сделать. так она сама пропадет
                webElementLogin = driver.findElement(By.xpath("//button[@class='close' and @aria-label='Закрыть']"));
                webElementLogin.click();
            } else {
                System.out.println("Пользователь "+username+" авторизован! Проверка пройдена!");
                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//img[@class='user-avatar' and @width='64']")));
                Assert.assertTrue(driver.findElement(By.xpath("//img[@class='user-avatar' and @width='64']")).isDisplayed());
                webElement = driver.findElement(By.xpath("//img[@class='user-avatar' and @width='64']"));
                webElement.click();
                Thread.sleep(100);
                webElement = driver.findElement(By.xpath("//button[@class='btn btn-default btn-block']"));
                try {
                    webElement.click();
                    Thread.sleep(1000);
                    driver.switchTo().alert().accept();
                } catch (org.openqa.selenium.UnhandledAlertException e) {
                }
            }
        }
    }
}
