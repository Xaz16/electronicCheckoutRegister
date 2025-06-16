package edu.istu.achipiga.controllers;

import edu.istu.achipiga.Employee;
import edu.istu.achipiga.Organization;
import edu.istu.achipiga.dao.OrganizationDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class Info {
    @FXML
    Label nameLabel;
    @FXML Label innLabel;
    @FXML Label bossLabel;
    @FXML Label locationLabel;
    @FXML
    private ListView<Employee> employeeListView;

    private Organization organization;

    public void initialize() {
        organization = OrganizationDAO.getCurrentOrganization();
        System.out.println(organization);
        nameLabel.textProperty().bind(new SimpleStringProperty(organization.name));
        innLabel.textProperty().bind(new SimpleStringProperty(organization.inn));
        locationLabel.textProperty().bind(new SimpleStringProperty(organization.location.toString()));
        ObservableList<Employee> workers = FXCollections.observableArrayList();
        for(Employee worker: organization.workers) {
            workers.add(worker);
        }
        employeeListView.setItems(workers);
        bossLabel.textProperty().bind(new SimpleStringProperty(organization.boss.getName()));
    }
}