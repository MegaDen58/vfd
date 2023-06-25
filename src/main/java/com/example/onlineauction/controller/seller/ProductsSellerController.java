package com.example.onlineauction.controller.seller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.example.onlineauction.DatabaseConnector;
import com.example.onlineauction.WindowsManager;
import com.example.onlineauction.constants.Role;
import com.example.onlineauction.constants.StatusLot;
import com.example.onlineauction.controller.DetailProductsController;
import com.example.onlineauction.controller.ManagementProductsController;
import com.example.onlineauction.controller.authentication.AuthorizationController;
import com.example.onlineauction.controller.authentication.RegistrationController;
import com.example.onlineauction.dao.LotDAO;
import com.example.onlineauction.model.Lot;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import static com.example.onlineauction.controller.ManagementProductsController.sellerId;

public class ProductsSellerController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane AnchorPaneLotsSeller;

    @FXML
    private Button addLotsSeller;

    @FXML
    private TableColumn<Lot, String> col_categoryLotsSeller;

    @FXML
    private TableColumn<Lot, Double> col_currentPriceLotsSeller;

    @FXML
    private TableColumn<Lot, String> col_nameLotsSeller;

    @FXML
    private TableColumn<Lot, Double> col_startPriceLotsSeller;

    @FXML
    private TableColumn<Lot, String> col_statusLotsSeller;

    @FXML
    private Button deleteLotsSeller;

    @FXML
    private Button editLotsSeller;

    @FXML
    private Button finishLotsSeller;

    @FXML
    private Button moreDetailLots;

    @FXML
    private TableView<Lot> tableViewLotsSeller;
    public static Lot lot;
    LotDAO lotDAO = new LotDAO(DatabaseConnector.ConnectDb());
    public static boolean booleanAdd = false;
    public static boolean booleanEdit = false;
    public static boolean isAdmin = false;
    public static List<Lot> closeLots;
    private int id;
    public static int userId;

    public ProductsSellerController() throws Exception {
    }

    @FXML
    void AddLotsSeller(ActionEvent event) throws Exception{
        Stage stageClose = (Stage) addLotsSeller.getScene().getWindow();
        stageClose.close();
        ProductsSellerController.lot = null;
        WindowsManager.openWindow("/com/example/onlineauction/AllUsers/add-edit-products.fxml","Добавление лота");
        booleanAdd = true;
    }

    @FXML
    void DeleteLotsSeller(ActionEvent event) throws Exception{
        int id = lot.getId();
        lotDAO.delete(id);

        update();
    }

    @FXML
    void EditLotsSeller(ActionEvent event) {
        if(ProductsSellerController.lot != null) {
            Stage stageClose = (Stage) editLotsSeller.getScene().getWindow();
            stageClose.close();

            WindowsManager.openWindow("AllUsers/add-edit-products.fxml","Редактирование лота");
            booleanEdit = true;
        }
        else{
           Alert alert = new Alert(Alert.AlertType.ERROR, "Элемент не выбран");
           alert.showAndWait();
        }
    }

    @FXML
    void FinishLotsSeller(ActionEvent event) throws Exception{
        lotDAO = new LotDAO(DatabaseConnector.ConnectDb());
        lotDAO.updateLotStatus(id, StatusLot.COMPLETED);
        update();
    }

    @FXML
    void MoreInfoDetailsLots(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/onlineauction/AllUsers/details-products.fxml"));
        Parent root = loader.load();

        DetailProductsController detailProductsController = loader.getController();
        detailProductsController.setRole(Role.SELLER);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Детали лота");
        stage.show();
    }

    @FXML
    void initialize() throws Exception {
        update();

//        // Загружаем лоты продавца и добавляем их в таблицу
//        try {
//            List<Lot> sellerLots = lotDAO.getLotsBySeller(ManagementProductsController.sellerId);
//            ObservableList<Lot> lotsObservableList = FXCollections.observableArrayList(sellerLots);
//            tableViewLotsSeller.setItems(lotsObservableList);
//            tableViewLotsSeller.refresh();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    public void getSelected() throws Exception {
        lot = tableViewLotsSeller.getSelectionModel().getSelectedItem();
        lot = lotDAO.getLotById(lot.getId());
        id = lot.getId();
        System.out.println(lot.Print());
    }

    public void update() throws Exception{
        Connection connection = DatabaseConnector.ConnectDb(); // Получаем соединение с базой данных
        LotDAO lotDAO = new LotDAO(connection);
        List<Lot> lotus = new ArrayList<>();
        closeLots = new ArrayList<>();
        if(AuthorizationController.userId != 0){
            List<Lot> temp = lotDAO.getLotsBySellerId(AuthorizationController.userId);
            for(Lot lot : temp){
                if(!lot.getStatusString().equals("Завершен")){
                    lotus.add(lot);
                }
                else{
                    closeLots.add(lot);
                }
            }
        }
        else if(RegistrationController.registeredUserId != 0){
            List<Lot> temp = lotDAO.getLotsBySellerId(RegistrationController.registeredUserId);
            for(Lot lot : temp){
                if(!lot.getStatusString().equals("Завершен")){
                    lotus.add(lot);
                }
                else{
                    closeLots.add(lot);
                }
            }
        }
        ObservableList<Lot> lots = FXCollections.observableArrayList(lotus);

        // Инициализируем столбцы таблицы
        col_categoryLotsSeller.setCellValueFactory(new PropertyValueFactory<>("category"));
        col_currentPriceLotsSeller.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
        col_nameLotsSeller.setCellValueFactory(new PropertyValueFactory<>("name"));
        col_startPriceLotsSeller.setCellValueFactory(new PropertyValueFactory<>("startPrice"));
        col_statusLotsSeller.setCellValueFactory(new PropertyValueFactory<>("statusString"));

        tableViewLotsSeller.setItems(lots);
        tableViewLotsSeller.refresh();
    }
}