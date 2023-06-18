package com.example.onlineauction.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.util.ResourceBundle;

import com.example.onlineauction.DatabaseConnector;
import com.example.onlineauction.constants.Role;
import com.example.onlineauction.controller.buyer.ProductsBuyerController;
import com.example.onlineauction.controller.seller.ProductsSellerController;
import com.example.onlineauction.dao.BidDAO;
import com.example.onlineauction.dao.CategoryDAO;
import com.example.onlineauction.model.Lot;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class DetailProductsController implements Initializable {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Pane PaneBettingBuyerVisible;

    @FXML
    private Button backDetailLots;

    @FXML
    private Button buyerBettingButton;

    @FXML
    private TextField buyerBettingField;

    @FXML
    private Label categoryLotsLabel;

    @FXML
    private Label conditionLotsLabel;

    @FXML
    private Label dateLotsLabel;

    @FXML
    private Label descriptionLotsLabel;

    @FXML
    private Label finishLotsLabel;

    @FXML
    private Label nameLotsLabel;

    @FXML
    private Label sellerLotsLabel;

    @FXML
    private Label startPriceLotsLabel;

    @FXML
    private Label statusLotsLabel;

    @FXML
    private Label stepPriceLotsLabel;
    public static Lot lot = ProductsSellerController.lot;
    private ProductsBuyerController productsBuyerController;
    Connection connection = DatabaseConnector.ConnectDb();
    BidDAO bidDAO = new BidDAO(connection);

    public DetailProductsController() throws Exception {
    }

    public void setProductsBuyerController(ProductsBuyerController controller) {
        productsBuyerController = controller;
    }

    public void setRole(Role role) {
        boolean isBuyer = role == Role.BUYER;
        PaneBettingBuyerVisible.setVisible(isBuyer);
    }

    @FXML
    void BackDetail(ActionEvent event) {
        Stage stageClose = (Stage) backDetailLots.getScene().getWindow();
        stageClose.close();
    }

    @FXML
    void BuyerBet(ActionEvent event) throws Exception{
        double bet = Double.parseDouble(buyerBettingField.getText());
        bidDAO.setBidAmountByIdLot(lot.getId(), bet);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            nameLotsLabel.setText(lot.getName());
            descriptionLotsLabel.setText(lot.getDescription());
            sellerLotsLabel.setText(String.valueOf(lot.getSellerId()));
            categoryLotsLabel.setText(CategoryDAO.getCategoryById(lot.getCategoryId()));
            startPriceLotsLabel.setText(String.valueOf(lot.getStartPrice()));
            stepPriceLotsLabel.setText(String.valueOf(lot.getStepPrice()));
            dateLotsLabel.setText(lot.getDatepublicationDate());
            finishLotsLabel.setText(lot.getDatelosingDate());
            conditionLotsLabel.setText(lot.getCondition());
            statusLotsLabel.setText(lot.getStatusString());
        } catch (Exception exception){
            exception.printStackTrace();
        }
    }
}