package com.example.onlineauction.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.example.onlineauction.*;
import com.example.onlineauction.constants.Role;
import com.example.onlineauction.controller.authentication.AuthorizationController;
import com.example.onlineauction.controller.authentication.RegistrationController;
import com.example.onlineauction.controller.seller.ProductsSellerController;
import com.example.onlineauction.dao.CategoryDAO;
import com.example.onlineauction.dao.LotDAO;
import com.example.onlineauction.dao.UserDAO;
import com.example.onlineauction.model.Category;
import com.example.onlineauction.model.Lot;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import static com.example.onlineauction.util.AlertUtil.showAlert;

public class ManagementProductsController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backButtonManageLots;

    private static final Logger LOGGER = Logger.getLogger(ManagementProductsController.class.getName());

    @FXML
    private ComboBox<Category> categoryComboBox;

    @FXML
    private DatePicker dateFinish;

    @FXML
    private DatePicker datePublication;

    @FXML
    private TextArea descriptionLotsArea;

    @FXML
    private TextField nameLotsField;

    @FXML
    private Button saveButtonManageLots;

    @FXML
    private TextField startPriceField;

    @FXML
    private TextField stepPriceField;

    @FXML
    private TextField conditionField;
    @FXML
    private ImageView backButton;

    private CategoryDAO categoryDAO = new CategoryDAO();
    private UserDAO userDAO;
    private int userId;
    private LotDAO lotDAO;
    public static int sellerId;

    private ProductsSellerController productsSellerController;

    public void setProductsSellerController(ProductsSellerController controller) {
        this.productsSellerController = controller;
    }

    public Lot lot = ProductsSellerController.lot;

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public ManagementProductsController() throws SQLException {
    }

    @FXML
    void BackManageLots(ActionEvent event) {
        Stage currentStage = (Stage) backButtonManageLots.getScene().getWindow();
        currentStage.close();
    }
    UserDAO userDAO1 = new UserDAO(DriverManager.getConnection("jdbc:mysql://localhost:3306/auction?serverTimezone=Europe/Moscow", "root", "12345"));
    @FXML
    void SaveManageLots(ActionEvent event) throws Exception {
        if(ProductsSellerController.booleanAdd){
            String name = nameLotsField.getText();
            String description = descriptionLotsArea.getText();
            LocalDate publicationDate = datePublication.getValue();
            LocalDate finishDate = dateFinish.getValue();
            String startPriceText = startPriceField.getText();
            String stepPriceText = stepPriceField.getText();
            String condition = conditionField.getText();

            Category selectedCategory = categoryComboBox.getValue();

            if (name.isEmpty() || description.isEmpty() || publicationDate == null || finishDate == null ||
                    startPriceText.isEmpty() || stepPriceText.isEmpty() || condition.isEmpty() ||
                    selectedCategory == null) {
                showAlert(Alert.AlertType.ERROR, "Ошибка!", "Пожалуйста, заполните все поля!");
                return; // Прерываем выполнение метода, если найдены пустые поля
            }

            double startPrice;
            double stepPrice;

            try {
                startPrice = Double.parseDouble(startPriceText);
                stepPrice = Double.parseDouble(stepPriceText);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Ошибка!", "Пожалуйста, введите числа в поля цены и шага!");
                return; // Прерываем выполнение метода, если введены неверные числовые значения
            }

            int categoryId = selectedCategory.getId();

            int sellerId = 0;
            if (RegistrationController.isRegistred){
                sellerId = RegistrationController.registeredUserId;
            }
            else if(userDAO1.getUserRole(AuthorizationController.login, AuthorizationController.password) == Role.SELLER) {
                String login = AuthorizationController.login;
                sellerId = userDAO1.getIdByLogin(login);
            }

            int buyerId = 49;

            // Создание объекта лота
            Lot lotAdd = new Lot(name, description, startPrice, startPrice, stepPrice, publicationDate.toString(), finishDate.toString(), condition);
            lotAdd.setSellerId(sellerId);
            lotAdd.setCurrentBuyerId(buyerId); // Установка фиктивного покупателя
            lotAdd.setCategoryId(categoryId);

            // Сохраняем лот в базе данных
            try {
                lotDAO = new LotDAO(DatabaseConnector.ConnectDb());
                lotDAO.create(lotAdd);
                showAlert(Alert.AlertType.INFORMATION, "Успешно!", "Лот успешно добавлен!");
                Stage stageClose = (Stage) saveButtonManageLots.getScene().getWindow();
                stageClose.close();

                if(ProductsSellerController.isAdmin){
                    WindowsManager.openWindow("administrator/all-products-administrator.fxml", "Окно администратора");
                }
                else{
                    WindowsManager.openWindow("seller/products-seller.fxml", "Окно продавца");
                }
                ProductsSellerController.booleanAdd = false;
                ProductsSellerController.booleanEdit = false;
                ProductsSellerController.isAdmin = false;

            } catch (SQLException e) {
                e.printStackTrace();
                // Обработка ошибки сохранения лота
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else if(ProductsSellerController.booleanEdit){
            lotDAO = new LotDAO(DatabaseConnector.ConnectDb());
            categoryDAO = new CategoryDAO(DatabaseConnector.ConnectDb());
            Lot editLot = lotDAO.getLotById(ProductsSellerController.lot.getId());

            String name = nameLotsField.getText();
            String description = descriptionLotsArea.getText();
            LocalDate publicationDate = datePublication.getValue();
            LocalDate finishDate = dateFinish.getValue();
            String startPriceText = startPriceField.getText();
            String stepPriceText = stepPriceField.getText();
            String condition = conditionField.getText();

            Category selectedCategory = categoryComboBox.getValue();

            double startPrice = 0;
            double stepPrice = 0;

            try {
                startPrice = Double.parseDouble(startPriceText);
                stepPrice = Double.parseDouble(stepPriceText);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Ошибка!", "Пожалуйста, введите числа в поля цены и шага!");
                return; // Прерываем выполнение метода, если введены неверные числовые значения
            }

            int categoryId = selectedCategory.getId();

            editLot.setName(name);
            editLot.setDescription(description);
            editLot.setPublicationDate(publicationDate.toString());
            editLot.setClosingDate(finishDate.toString());
            editLot.setCondition(condition);
            editLot.setStartPrice(startPrice);
            editLot.setStepPrice(stepPrice);
            editLot.setCategoryId(categoryId);

            try{
                lotDAO.update(editLot);
                showAlert(Alert.AlertType.INFORMATION, "Успешно!", "Лот успешно обновлен!");
                Stage stageClose = (Stage) saveButtonManageLots.getScene().getWindow();
                stageClose.close();

                if(ProductsSellerController.isAdmin){
                    WindowsManager.openWindow("administrator/all-products-administrator.fxml", "Окно администратора");
                } else{
                    WindowsManager.openWindow("seller/products-seller.fxml","Окно продавца");
                }
                ProductsSellerController.booleanAdd = false;
                ProductsSellerController.booleanEdit = false;
            } catch (Exception exception){
                exception.printStackTrace();
            }
        }

    }

    @FXML
    void initialize() throws Exception {
        int index = 0;
        if(lot != null){
            lotDAO = new LotDAO(DatabaseConnector.ConnectDb());
            lot = lotDAO.getLotById(lot.getId());
            nameLotsField.setText(lot.getName());
            descriptionLotsArea.setText(lot.getDescription());
            startPriceField.setText(String.valueOf(lot.getStartPrice()));
            stepPriceField.setText(String.valueOf(lot.getStepPrice()));
            String publish = lot.getDatepublicationDate();
            String close = lot.getDatelosingDate();
            datePublication.setValue(LocalDate.of(Integer.parseInt(publish.split("-")[0]),
                    Integer.parseInt(publish.split("-")[1]),
                    Integer.parseInt(publish.split("-")[2])));
            dateFinish.setValue(LocalDate.of(Integer.parseInt(close.split("-")[0]),
                    Integer.parseInt(close.split("-")[1]),
                    Integer.parseInt(close.split("-")[2])));
            conditionField.setText(lot.getCondition());
        }
        //for(String text : categoryDAO.getAllStringCategories())
        if(lot != null){
            for(; index < categoryDAO.getAllStringCategories().size() - 1; index++){
                String text = categoryDAO.getAllCategoriesList().get(index).getName();
                String result = CategoryDAO.getCategoryById(Integer.parseInt(lot.getCategory()));
                if(text.equals(result)){
                    break;
                }
            }
            Category category = categoryDAO.getAllCategoriesList().get(index);
            categoryComboBox.setValue(category);
        }

        categoryComboBox.setItems(categoryDAO.getAllCategoriesObservable());

//        try {
//            Connection connection = DatabaseConnector.ConnectDb(); // Получаем подключение к базе данных
//            categoryDAO = new CategoryDAO(connection);
//            lotDAO = new LotDAO(connection);
//            // Создаем экземпляр UserDAO
//            userDAO = new UserDAO(connection);
//            // Заполняем ComboBox категориями из базы данных
//            ObservableList<Category> categories = FXCollections.observableArrayList(categoryDAO.getAllCategories());
//            categoryComboBox.setItems(categories);
//        } catch (Exception e) {
//            e.printStackTrace();
//            // Обработка ошибки подключения к базе данных
//        }
    }

    @FXML
    public void goBack() {
        Stage stageClose = (Stage) backButton.getScene().getWindow();
        stageClose.close();

        WindowsManager.openWindow("seller/products-seller.fxml","Окно продавца");
    }
}