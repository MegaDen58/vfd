package com.example.onlineauction.dao;

import com.example.onlineauction.DatabaseConnector;
import com.example.onlineauction.model.Bid;
import com.example.onlineauction.model.Lot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BidDAO {
    private Connection connection;

    public BidDAO(Connection connection) {
        this.connection = connection;
    }

    // Метод для добавления ставки в таблицу bids
    public void addBid(Bid bid) throws SQLException {
        String query = "INSERT INTO bids (lot_id, buyer_id, bid_amount) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, bid.getLotId());
            statement.setInt(2, bid.getBuyerId());
            statement.setDouble(3, bid.getBidAmount());
            statement.executeUpdate();
        }
    }
    public boolean existBidByIdLot(int idLot, int idUser) throws Exception{
        boolean result;
        int id = 0;

        String query = "select idbids from bids where lot_id=? and buyer_id=?";
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, idLot);
            statement.setInt(2, idUser);
            try (ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next()){
                    id = resultSet.getInt("idbids");
                }
            }
        }
        if(id == 0){
            result = false;
        }
        else{
            result = true;
        }
        return result;
    }

    // Метод для получения всех ставок для определенного лота
    public List<Bid> getBidsByLotId(int lotId) throws SQLException {
        List<Bid> bids = new ArrayList<>();
        String query = "SELECT * FROM bids WHERE lot_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, lotId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Bid bid = new Bid(
                            resultSet.getInt("idbids"),
                            resultSet.getInt("lot_id"),
                            resultSet.getInt("byuer_id"),
                            resultSet.getDouble("bid_amount")
                    );
                    bids.add(bid);
                }
            }
        }
        return bids;
    }

    // Метод для обновления ставки в таблице bids
    public void updateBid(Bid bid) throws SQLException {
        String query = "UPDATE bids SET lot_id = ?, buyer_id = ?, bid_amount = ? WHERE idbids = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, bid.getLotId());
            statement.setInt(2, bid.getBuyerId());
            statement.setDouble(3, bid.getBidAmount());
            statement.setInt(4, bid.getId());
            statement.executeUpdate();
        }
    }

    // Дополнительные методы, если необходимо

    public void setBidAmountByIdLot(int id, double amount, int idUser) throws Exception{
        String query = "UPDATE bids set bid_amount = ? where lot_id = ? and buyer_id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setDouble(1, amount);
            statement.setInt(2, id);
            statement.setInt(3, idUser);
            statement.executeUpdate();
        }
    }

    public double getBetByLotId(int lotId, int userId) throws Exception{
        String query = "select bid_amount from bids where lot_id=? and buyer_id=?";
        double result = 0;
        try (PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, lotId);
            statement.setInt(2, userId);
            try(ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next()){
                    result = resultSet.getDouble("bid_amount");
                }
            }
        }
        return result;
    }
    public List<Lot> getLotsByBuyerid(int id) throws Exception{
        LotDAO lotDAO = new LotDAO(DatabaseConnector.ConnectDb());
        List<Lot> lots = new ArrayList<>();
        String query = "select lot_id from bids where buyer_id=?";
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, id);
            try(ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next()){
                    lots.add(lotDAO.getLotById(resultSet.getInt("lot_id")));
                }
            }
        }
        return lots;
    }
}
