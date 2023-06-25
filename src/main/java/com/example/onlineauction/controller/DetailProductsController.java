package com.example.onlineauction.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.util.ResourceBundle;

import com.example.onlineauction.DatabaseConnector;
import com.example.onlineauction.WindowsManager;
import com.example.onlineauction.constants.Role;
import com.example.onlineauction.controller.authentication.AuthorizationController;
import com.example.onlineauction.controller.authentication.RegistrationController;
import com.example.onlineauction.controller.buyer.ProductsBuyerController;
import com.example.onlineauction.controller.seller.ProductsSellerController;
import com.example.onlineauction.dao.BidDAO;
import com.example.onlineauction.dao.CategoryDAO;
import com.example.onlineauction.dao.LotDAO;
import com.example.onlineauction.dao.UserDAO;
import com.example.onlineauction.model.Bid;
import com.example.onlineauction.model.Lot;
import com.example.onlineauction.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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
    public static Lot lot;
    int id = 0;
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
        LotDAO lotDAO = new LotDAO(DatabaseConnector.ConnectDb());
        double bet = Double.parseDouble(buyerBettingField.getText());
        id = 0;
        if(AuthorizationController.userId == 0){
            id = RegistrationController.registeredUserId;
        }
        else{
            id = AuthorizationController.userId;
        }

        if(bet >= lot.getCurrentPrice() + lot.getStepPrice()){
            lotDAO.updateCurrentPriceById(bet, lot.getId());
            lotDAO.updateBuyerIdByLotId(id, lot.getId());
            if(bidDAO.existBidByIdLot(lot.getId(), id)){
                bidDAO.setBidAmountByIdLot(lot.getId(), bet, id);
                Stage stageClose = (Stage) buyerBettingButton.getScene().getWindow();
                stageClose.close();

                WindowsManager.openWindow("buyer/products-buyer.fxml","Окно покупателя");
            }
            else{
                Bid bid = new Bid(lot.getId(), id, bet);
                bidDAO.addBid(bid);

                Stage stageClose = (Stage) buyerBettingButton.getScene().getWindow();
                stageClose.close();

                WindowsManager.openWindow("buyer/products-buyer.fxml","Окно покупателя");
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR, "Некорректная ставка");
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            UserDAO userDAO = new UserDAO(DatabaseConnector.ConnectDb());

            lot = ProductsSellerController.lot;
            nameLotsLabel.setText(lot.getName());
            descriptionLotsLabel.setText(lot.getDescription());
            sellerLotsLabel.setText(userDAO.getNameAndSurnameById(lot.getSellerId()));
            categoryLotsLabel.setText(CategoryDAO.getCategoryById(Integer.parseInt(lot.getCategory())));
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