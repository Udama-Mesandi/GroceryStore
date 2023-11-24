package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import db.DBConnection;
import dto.Customer;
import dto.ItemDto;
import dto.tm.CustomerTm;
import dto.tm.ItemTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


import java.io.IOException;
import java.sql.*;

public class ItemFormController {

    @FXML
    private BorderPane pane;

    @FXML
    private JFXTextField txtCode;

    @FXML
    private JFXTextField txtDescription;

    @FXML
    private JFXTextField txtUnitPrice;

    @FXML
    private JFXTextField txtQty;

    @FXML
    private JFXTextField txtSearch;

    @FXML
    private JFXTreeTableView<ItemTm> tblItem;

    @FXML
    private TreeTableColumn colCode;

    @FXML
    private TreeTableColumn colDesc;

    @FXML
    private TreeTableColumn colUnitPrice;

    @FXML
    private TreeTableColumn colQty;

    @FXML
    private TreeTableColumn colOption;


    public void initialize(){

        colCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("code"));
        colDesc.setCellValueFactory(new TreeItemPropertyValueFactory<>("desc"));
        colUnitPrice.setCellValueFactory(new TreeItemPropertyValueFactory<>("unitPrice"));
        colQty.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));

        loadItemTable();

        tblItem.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            setData(newValue.getValue());
        });
    }

    private void setData(ItemTm newValue) {
        if (newValue != null) {
            txtCode.setEditable(false);
            txtCode.setText(newValue.getCode());
            txtDescription.setText(newValue.getDesc());
            txtUnitPrice.setText(String.valueOf(newValue.getUnitPrice()));
            txtQty.setText(String.valueOf(newValue.getQty()));
        }
    }

    private void loadItemTable() {
        ObservableList<ItemTm> tmList = FXCollections.observableArrayList();
        String sql="SELECT * FROM item";
        try {


            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            ResultSet i = stm.executeQuery(sql);
            while(i.next()){
                JFXButton btn = new JFXButton("Delete");
                ItemTm c= new ItemTm(i.getString(1),
                        i.getString(2),
                        i.getDouble(3),
                        i.getInt(4),
                        btn
                );
                btn.setOnAction(actionEvent -> {
                    deleteItem(c.getCode());
                });
                tmList.add(c);
            }



            RecursiveTreeItem<ItemTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
            tblItem.setRoot(treeItem);
            tblItem.setShowRoot(false);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteItem(String code) {
        String sql="DELETE from item WHERE code=?";
        try {
            PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
            pstm.setString(1,code);
            int result=pstm.executeUpdate(sql);
            if(result>0){
                new Alert(Alert.AlertType.INFORMATION,"Customer Deleted !").show();
                loadItemTable();
            }else{
                new Alert(Alert.AlertType.INFORMATION,"Something Went Wrong").show();
            }


        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void backButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) pane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void saveButtonOnAction(ActionEvent event) {
        ItemDto dto = new ItemDto(txtCode.getText(),
                txtDescription.getText(),
                Double.parseDouble(txtUnitPrice.getText()),
                Integer.parseInt(txtQty.getText())
        );
        String sql = "INSERT INTO item VALUES('" + dto.getCode() + "','" + dto.getDesc() + "'," + dto.getUnitPrice() + "," + dto.getQty() + ")";
        try {
            PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
            pstm.setString(1,dto.getCode());
            pstm.setString(2, dto.getDesc());
            pstm.setDouble(3,dto.getUnitPrice());
            pstm.setInt(4,dto.getQty());
            int result=pstm.executeUpdate();
            if (result> 0) {
                new Alert(Alert.AlertType.INFORMATION, "Item Saved !").show();
                    loadItemTable();
//                clearFields();
            }


        } catch (SQLIntegrityConstraintViolationException ex){
            new Alert(Alert.AlertType.ERROR,"Duplicate Entry").show();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void updateButtonOnAction(ActionEvent event) {
        ItemDto c = new ItemDto(txtCode.getText(),
                txtDescription.getText(),
                Double.parseDouble(txtUnitPrice.getText()),
                Integer.parseInt(txtQty.getText())

        );


        String sql = "UPDATE item SET desc='"+c.getDesc()+"',unitPrice="+c.getUnitPrice()+",qty="+c.getQty()+" WHERE code='"+c.getCode()+"'";
        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            int i = stm.executeUpdate(sql);
            if (i > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Item"+c.getCode()+" Updated !").show();
                loadItemTable();
//                clearFields();
            }



        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

}
