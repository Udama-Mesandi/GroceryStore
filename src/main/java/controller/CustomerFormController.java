package controller;

import db.DBConnection;
import dto.Customer;
import dto.tm.CustomerTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;



import java.io.IOException;
import java.sql.*;

public class CustomerFormController {

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtSalary;

    @FXML
    private Button saveBtn;

    @FXML
    private Button updateBtn;

    @FXML
    private TableView<CustomerTm> tbl;

    @FXML
    private TableColumn colId;

    @FXML
    private TableColumn colName;

    @FXML
    private TableColumn colAddress;

    @FXML
    private TableColumn colSalary;

    @FXML
    private TableColumn colOption;

    @FXML
    private Button reloadBtn;


    public void initialize(){
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));
        loadCustomerTable();

        tbl.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            setData(newValue);
        });
    }

    private void setData(CustomerTm newValue) {
        if (newValue != null) {
            txtId.setEditable(false);
            txtId.setText(newValue.getId());
            txtName.setText(newValue.getName());
            txtAddress.setText(newValue.getAddress());
            txtSalary.setText(String.valueOf(newValue.getSalary()));
        }
    }


    private void loadCustomerTable() {
        ObservableList<CustomerTm> tmList = FXCollections.observableArrayList();
        String sql="SELECT * FROM customer";
        try {


            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            ResultSet i = stm.executeQuery(sql);
            while(i.next()){
                Button btn = new Button("Delete");
                CustomerTm c= new CustomerTm(i.getString(1),
                                            i.getString(2),
                                            i.getString(3),
                                            i.getDouble(4),
                                            btn
                );
                btn.setOnAction(actionEvent -> {
                    deleteCustomer(c.getId());
                });
                tmList.add(c);
            }



            tbl.setItems(tmList);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteCustomer(String id) {

        String sql="DELETE from customer WHERE id='"+id+"'";
        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            int i = stm.executeUpdate(sql);
            if(i>0){
                new Alert(Alert.AlertType.INFORMATION,"Customer Deleted !").show();
                loadCustomerTable();
            }else{
                new Alert(Alert.AlertType.INFORMATION,"Something Went Wrong").show();
            }


        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void reloadBtnOnAction(ActionEvent event) {
            loadCustomerTable();
            tbl.refresh();
            clearFields();
    }

    private void clearFields() {
        tbl.refresh();
        txtSalary.clear();
        txtAddress.clear();
        txtName.clear();
        txtId.clear();
        txtId.setEditable(true);
    }

    @FXML
    void saveBtnOnAction(ActionEvent event) {
        Customer c = new Customer(txtId.getText(),
                txtName.getText(),
                txtAddress.getText(),
                Double.parseDouble(txtSalary.getText())
        );
        String sql = "INSERT INTO customer VALUES('" + c.getId() + "','" + c.getName() + "','" + c.getAddress() + "'," + c.getSalary() + ")";
        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            int i = stm.executeUpdate(sql);
            if (i > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Customer Saved !").show();
                loadCustomerTable();
                clearFields();
            }


        } catch (SQLIntegrityConstraintViolationException ex){
            new Alert(Alert.AlertType.ERROR,"Duplicate Entry").show();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }


    }

    @FXML
    void updateBtnOnAction(ActionEvent event) {


        Customer c = new Customer(txtId.getText(),
                txtName.getText(),
                txtAddress.getText(),
                Double.parseDouble(txtSalary.getText())
        );
        String sql = "UPDATE customer SET name='"+c.getName()+"',address='"+c.getAddress()+"',salary="+c.getSalary()+" WHERE id='"+c.getId()+"'";
        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            int i = stm.executeUpdate(sql);
            if (i > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Customer"+c.getId()+" Updated !").show();
                loadCustomerTable();
                clearFields();
            }



        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }


    }


    public void backBtnOnAction(ActionEvent actionEvent) {

        Stage stage = (Stage) tbl.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/DashboardForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
