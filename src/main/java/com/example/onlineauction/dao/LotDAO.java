package com.example.onlineauction.dao;

import com.example.onlineauction.controller.authentication.AuthorizationController;
import com.example.onlineauction.controller.authentication.RegistrationController;
import com.example.onlineauction.model.Lot;
import com.example.onlineauction.constants.StatusLot;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class LotDAO {
    private Connection connection;

    public LotDAO(Connection connection){
        this.connection = connection;
    }
    public LotDAO(){

    }
    public void create(Lot lot) throws SQLException {

        String query = "INSERT INTO lots (name_lots, description_lots, start_price, current_price, step_price, " +
                "publication_date, closing_date, condition_lots, status_lots, category_id, seller_id, current_buyer_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, lot.getName());
            statement.setString(2, lot.getDescription());
            statement.setDouble(3, lot.getStartPrice());
            statement.setDouble(4, lot.getCurrentPrice());
            statement.setDouble(5, lot.getStepPrice());
            statement.setString(6, lot.getPublicationDate());
            statement.setString(7, lot.getClosingDate());
            statement.setString(8, lot.getCondition());
            statement.setString(9, lot.getStatusLot().getStatus());
            statement.setInt(10, lot.getCategoryIdd());
            statement.setInt(11, lot.getSellerId());
            statement.setInt(12, lot.getCurrentBuyerId());

            statement.executeUpdate();
        }
    }

    public void update(Lot lot) throws SQLException {
        String query = "UPDATE lots SET name_lots = ?, description_lots = ?, start_price = ?, current_price = ?, " +
                "step_price = ?, publication_date = ?, closing_date = ?, condition_lots = ?, status_lots = ?, " +
                "category_id = ?, seller_id = ?, current_buyer_id = ? WHERE idlots = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, lot.getName());
            statement.setString(2, lot.getDescription());
            statement.setDouble(3, lot.getStartPrice());
            statement.setDouble(4, lot.getCurrentPrice());
            statement.setDouble(5, lot.getStepPrice());
            statement.setString(6, lot.getDatepublicationDate());
            statement.setString(7, lot.getDatelosingDate());
            statement.setString(8, lot.getCondition());
            statement.setString(9, lot.getStatusString());
            statement.setInt(10, lot.getCategoryId());
            statement.setInt(11, lot.getSellerId());
            statement.setInt(12, lot.getCurrentBuyerId());
            statement.setInt(13, lot.getId());

            statement.executeUpdate();
        }
    }

    public void delete(int lotId) throws SQLException {
        String query = "DELETE FROM lots WHERE idlots = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, lotId);
            statement.executeUpdate();
        }
    }


    public Lot getById(int lotId) throws SQLException {
        String query = "SELECT * FROM lots WHERE idlots = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, lotId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Lot lot = new Lot();
                    lot.setId(resultSet.getInt("idlots"));
                    lot.setName(resultSet.getString("name_lots"));
                    lot.setDescription(resultSet.getString("description_lots"));
                    lot.setStartPrice(resultSet.getDouble("start_price"));
                    lot.setCurrentPrice(resultSet.getDouble("current_price"));
                    lot.setStepPrice(resultSet.getDouble("step_price"));
                    lot.setPublicationDate(resultSet.getString("publication_date"));
                    lot.setClosingDate(resultSet.getString("closing_date"));
                    lot.setCondition(resultSet.getString("condition_lots"));
                    lot.setStatusLot(StatusLot.valueOf(resultSet.getString("status_lots")));
                    lot.setCategoryId(resultSet.getInt("category_id"));
                    lot.setSellerId(resultSet.getInt("seller_id"));
                    lot.setCurrentBuyerId(resultSet.getInt("current_buyer_id"));

                    return lot;
                }
            }
        }
        return null;
    }

    public List<Lot> getAllLots() throws SQLException {
        List<Lot> lots = new ArrayList<>();
        String query = "SELECT * FROM lots";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Lot lot = new Lot();
                lot.setId(resultSet.getInt("idlots"));
                lot.setName(resultSet.getString("name_lots"));
                lot.setDescription(resultSet.getString("description_lots"));
                lot.setStartPrice(resultSet.getDouble("start_price"));
                lot.setCurrentPrice(resultSet.getDouble("current_price"));
                lot.setStepPrice(resultSet.getDouble("step_price"));
                lot.setPublicationDate(resultSet.getString("publication_date"));
                lot.setClosingDate(resultSet.getString("closing_date"));
                lot.setCondition(resultSet.getString("condition_lots"));
                lot.setStatusString(resultSet.getString("status_lots"));
                lot.setCategoryId(resultSet.getInt("category_id"));
                lot.setSellerId(resultSet.getInt("seller_id"));
                lot.setCurrentBuyerId(resultSet.getInt("current_buyer_id"));
                lot.setCategory(CategoryDAO.getCategoryById(lot.getCategoryId()));

                lots.add(lot);
            }
        }

        return lots;
    }

    public List<Lot> getLotsByCategory(int categoryId) throws SQLException {
        List<Lot> lots = new ArrayList<>();
        String query = "SELECT * FROM lots WHERE category_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, categoryId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Lot lot = new Lot();
                    lot.setId(resultSet.getInt("idlots"));
                    lot.setName(resultSet.getString("name_lots"));
                    lot.setDescription(resultSet.getString("description_lots"));
                    lot.setStartPrice(resultSet.getDouble("start_price"));
                    lot.setCurrentPrice(resultSet.getDouble("current_price"));
                    lot.setStepPrice(resultSet.getDouble("step_price"));
                    lot.setPublicationDate(resultSet.getString("publication_date"));
                    lot.setClosingDate(resultSet.getString("closing_date"));
                    lot.setCondition(resultSet.getString("condition_lots"));
                    String status = resultSet.getString("status_lots");
                    if(status.equals("Ожидает подтверждения")){
                        lot.setStatusLot(StatusLot.AWAITING_CONFIRMATION);
                    }
                    else if(status.equals("Завершен")){
                        lot.setStatusLot(StatusLot.COMPLETED);
                    }
                    else if(status.equals("Активный")){
                        lot.setStatusLot(StatusLot.ACTIVE);
                    }

                    //lot.setStatusLot(StatusLot.valueOf(resultSet.getString("status_lots")));
                    lot.setCategoryId(resultSet.getInt("category_id"));
                    lot.setSellerId(resultSet.getInt("seller_id"));
                    lot.setCurrentBuyerId(resultSet.getInt("current_buyer_id"));

                    lots.add(lot);
                }
            }
        }

        return lots;
    }


    public List<Lot> getActiveLots() throws Exception {
        List<Lot> lots = new ArrayList<>();
        BidDAO bidDAO = new BidDAO(connection);
        String query = "SELECT * FROM lots WHERE status_lots = 'Активный'";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Lot lot = new Lot();
                lot.setId(resultSet.getInt("idlots"));
                lot.setName(resultSet.getString("name_lots"));
                lot.setDescription(resultSet.getString("description_lots"));
                lot.setStartPrice(resultSet.getDouble("start_price"));
                lot.setCurrentPrice(resultSet.getDouble("current_price"));
                lot.setStepPrice(resultSet.getDouble("step_price"));
                lot.setPublicationDate(resultSet.getString("publication_date"));
                lot.setClosingDate(resultSet.getString("closing_date"));
                lot.setCondition(resultSet.getString("condition_lots"));
                lot.setStatusString(resultSet.getString("status_lots"));
                lot.setCategoryId(resultSet.getInt("category_id"));
                lot.setSellerId(resultSet.getInt("seller_id"));
                lot.setCurrentBuyerId(resultSet.getInt("current_buyer_id"));
                lot.setCategory(CategoryDAO.getCategoryById(lot.getCategoryId()));

                if(bidDAO.getBetByLotId(lot.getId(), AuthorizationController.userId) != 0 && AuthorizationController.userId != 0){
                    lot.setMyBet(bidDAO.getBetByLotId(lot.getId(), AuthorizationController.userId));
                } else if (bidDAO.getBetByLotId(lot.getId(), RegistrationController.registeredUserId) != 0 && RegistrationController.registeredUserId != 0) {
                    lot.setMyBet(bidDAO.getBetByLotId(lot.getId(), RegistrationController.registeredUserId));
                } else{
                    lot.setMyBet(0);
                }
                lots.add(lot);
            }
        }

        return lots;
    }

    public List<Lot> getInactiveLots() throws Exception {
        List<Lot> lots = new ArrayList<>();
        BidDAO bidDAO = new BidDAO(connection);
        String query = "SELECT * FROM lots WHERE status_lots = 'Завершен'";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Lot lot = new Lot();
                lot.setId(resultSet.getInt("idlots"));
                lot.setName(resultSet.getString("name_lots"));
                lot.setDescription(resultSet.getString("description_lots"));
                lot.setStartPrice(resultSet.getDouble("start_price"));
                lot.setCurrentPrice(resultSet.getDouble("current_price"));
                lot.setStepPrice(resultSet.getDouble("step_price"));
                lot.setPublicationDate(resultSet.getString("publication_date"));
                lot.setClosingDate(resultSet.getString("closing_date"));
                lot.setCondition(resultSet.getString("condition_lots"));
                lot.setStatusString(resultSet.getString("status_lots"));
                lot.setCategoryId(resultSet.getInt("category_id"));
                lot.setSellerId(resultSet.getInt("seller_id"));
                lot.setCurrentBuyerId(resultSet.getInt("current_buyer_id"));
                lot.setCategory(CategoryDAO.getCategoryById(lot.getCategoryId()));


                if(bidDAO.getBetByLotId(lot.getId(), AuthorizationController.userId) != 0 && AuthorizationController.userId != 0){
                    lot.setMyBet(bidDAO.getBetByLotId(lot.getId(), AuthorizationController.userId));
                } else if (bidDAO.getBetByLotId(lot.getId(), RegistrationController.registeredUserId) != 0 && RegistrationController.registeredUserId != 0) {
                    lot.setMyBet(bidDAO.getBetByLotId(lot.getId(), RegistrationController.registeredUserId));
                } else{
                    lot.setMyBet(0);
                }
                lots.add(lot);
            }
        }

        return lots;
    }

    public List<Lot> getLotsBySeller(int sellerId) throws SQLException {
        List<Lot> lots = new ArrayList<>();
        String query = "SELECT * FROM lots WHERE seller_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, sellerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Lot lot = new Lot();
                    lot.setId(resultSet.getInt("idlots"));
                    lot.setName(resultSet.getString("name_lots"));
                    lot.setDescription(resultSet.getString("description_lots"));
                    lot.setStartPrice(resultSet.getDouble("start_price"));
                    lot.setCurrentPrice(resultSet.getDouble("current_price"));
                    lot.setStepPrice(resultSet.getDouble("step_price"));
                    lot.setPublicationDate(resultSet.getString("publication_date"));
                    lot.setClosingDate(resultSet.getString("closing_date"));
                    lot.setCondition(resultSet.getString("condition_lots"));
                    String status = resultSet.getString("status_lots");
                    if(status.equals("Ожидает подтверждения")){
                        lot.setStatusLot(StatusLot.AWAITING_CONFIRMATION);
                    }
                    else if(status.equals("Завершен")){
                        lot.setStatusLot(StatusLot.COMPLETED);
                    }
                    else if(status.equals("Активный")){
                        lot.setStatusLot(StatusLot.ACTIVE);
                    }
                    lot.setCategoryId(resultSet.getInt("category_id"));
                    lot.setSellerId(resultSet.getInt("seller_id"));
                    lot.setCurrentBuyerId(resultSet.getInt("current_buyer_id"));

                    lots.add(lot);
                }
            }
        }

        return lots;
    }

    public List<Lot> getLotsBySellerId(int sellerId) throws SQLException{
        List<Lot> lots = new ArrayList<>();
        String query = "SELECT * FROM lots WHERE seller_id = ?";

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, sellerId);

            try(ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next()){
                    String name = resultSet.getString("name_lots");
                    int id = resultSet.getInt("idlots");
                    int category = resultSet.getInt("category_id");
                    double price = resultSet.getDouble("start_price");
                    double currentPrice = resultSet.getDouble("current_price");
                    String status = resultSet.getString("status_lots");
                    Lot lot = new Lot(id ,name, CategoryDAO.getCategoryById(category), price, currentPrice, status);
                    lots.add(lot);
                }
            }
        }
        return lots;
    }

    public Lot getLotById(int id) throws Exception{
        String query = "select * from lots where idlots=" + id;
        Lot lot = new Lot();
        try(PreparedStatement statement = connection.prepareStatement(query)){
            try (ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next()){
                     lot = new Lot(resultSet.getInt("idlots"),
                            resultSet.getString("name_lots"),
                            resultSet.getString("description_lots"),
                            resultSet.getDouble("start_price"),
                            resultSet.getDouble("current_price"),
                            resultSet.getDouble("step_price"),
                            resultSet.getDate("publication_date"),
                            resultSet.getDate("closing_date"),
                            resultSet.getString("condition_lots"),
                            resultSet.getString("status_lots"),
                            resultSet.getString("category_id"), // Изменить на преобразованый в текст
                            resultSet.getInt("seller_id"),
                            resultSet.getInt("current_buyer_id")
                    );
                }
            }
        }
        return lot;
    }


    public void updateLotStatus(int lotId, StatusLot statusLot) throws SQLException {
        String query = "UPDATE lots SET status_lots = ? WHERE idlots = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, statusLot.getStatus());
            statement.setInt(2, lotId);
            statement.executeUpdate();
        }
    }
    public void updateCurrentPriceById(double price, int idLot) throws Exception{
        String query = "UPDATE lots set current_price= ? WHERE idlots = ?";

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setDouble(1, price);
            statement.setInt(2, idLot);
            statement.executeUpdate();
        }
    }

    public void updateBuyerIdByLotId(int buyerId, int lotId) throws Exception{
        String query = "UPDATE lots set current_buyer_id = ? where idlots = ?";

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, buyerId);
            statement.setInt(2, lotId);
            statement.executeUpdate();
        }
    }
    public List<Lot> getInactiveLotsBySellerId(int sellerId) throws Exception {
        List<Lot> lots = new ArrayList<>();
        BidDAO bidDAO = new BidDAO(connection);
        String query = "SELECT * FROM lots WHERE status_lots = 'Завершен' AND seller_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, sellerId);
            try(ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Lot lot = new Lot();
                    lot.setId(resultSet.getInt("idlots"));
                    lot.setName(resultSet.getString("name_lots"));
                    lot.setDescription(resultSet.getString("description_lots"));
                    lot.setStartPrice(resultSet.getDouble("start_price"));
                    lot.setCurrentPrice(resultSet.getDouble("current_price"));
                    lot.setStepPrice(resultSet.getDouble("step_price"));
                    lot.setPublicationDate(resultSet.getString("publication_date"));
                    lot.setClosingDate(resultSet.getString("closing_date"));
                    lot.setCondition(resultSet.getString("condition_lots"));
                    lot.setStatusString(resultSet.getString("status_lots"));
                    lot.setCategoryId(resultSet.getInt("category_id"));
                    lot.setSellerId(resultSet.getInt("seller_id"));
                    lot.setCurrentBuyerId(resultSet.getInt("current_buyer_id"));
                    lot.setCategory(CategoryDAO.getCategoryById(lot.getCategoryId()));


                    if (bidDAO.getBetByLotId(lot.getId(), AuthorizationController.userId) != 0 && AuthorizationController.userId != 0) {
                        lot.setMyBet(bidDAO.getBetByLotId(lot.getId(), AuthorizationController.userId));
                    } else if (bidDAO.getBetByLotId(lot.getId(), RegistrationController.registeredUserId) != 0 && RegistrationController.registeredUserId != 0) {
                        lot.setMyBet(bidDAO.getBetByLotId(lot.getId(), RegistrationController.registeredUserId));
                    } else {
                        lot.setMyBet(0);
                    }
                    lots.add(lot);
                }
            }
        } catch (Exception exception){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Завершенные лоты отсутствуют");
            alert.showAndWait();
        }

        return lots;
    }
}