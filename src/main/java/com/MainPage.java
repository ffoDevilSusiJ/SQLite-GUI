package com;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class MainPage {
    static Scene scene = App.getScene();
    static Stage stage = App.getStage();
    static String currentTable = null;

    // show start tip label
    public static void showStartTipLabel() {
        VBox gridContainer = (VBox) scene.lookup("#gridContainer");
        gridContainer.setAlignment(Pos.CENTER);
        Label label = new Label("To get started, open or create an SQLite database using (file -> New/Open)");
        label.getStyleClass().add("start_label");
        gridContainer.getChildren().add(label);
    }

    // hide start tip label
    public static void hideStartTipLabel() {
        VBox gridContainer = (VBox) scene.lookup("#gridContainer");
        gridContainer.getChildren().clear();
        gridContainer.setAlignment(Pos.TOP_LEFT);
    }

    // File Chooser
    static File file;
    static File file_temp;
    static String filePath;
    static Path fileCopy = null;
    public static void FilePicker(FXMLLoader fxmlLoader) {

        MenuItem openMenuItem = (MenuItem) fxmlLoader.getNamespace().get("file_open");
        openMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(final ActionEvent e) {

                if (file_temp == null) {
                    openProcess();
                } else {
                    Alert alert = new Alert(AlertType.CONFIRMATION, "Do you want to save the changes?",
                            ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                    alert.setTitle("Save before opening");
                    alert.showAndWait();
                    if (alert.getResult() == ButtonType.YES) {
                        saveProcess();
                        openProcess();
                    } else if (alert.getResult() == ButtonType.NO) {
                        openProcess();
                    }
                }
            }
        });
    }

    public static void openProcess() {
        final FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("DataBase Files (*.db)",
                "*.db");
        fileChooser.getExtensionFilters().add(extFilter);

        file = fileChooser.showOpenDialog(stage);
        filePath = file.toPath().toString();
        
        try {
            if (DataBaseHandler.dbConnection != null) {
                DataBaseHandler.dbConnection.close();
            }
            Files.deleteIfExists(Paths.get("C:\\Windows\\TEMP\\" + file.getName() + "_temp.db"));
            file_temp = new File("C:\\Windows\\TEMP\\" + file.getName() + "_temp.db");
            fileCopy = Files.copy(file.toPath(), file_temp.toPath(),
                    (CopyOption) StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | SQLException e1) {
            e1.printStackTrace();
        }

        if (file != null) {
            try {
                hideStartTipLabel();
                DataBaseHandler.openSQLFile(fileCopy.toString().replace("\\", "//"));

                Cleaner.newTableList();
                showTables();
            } catch (SQLException | ClassNotFoundException ex2) {
                ex2.printStackTrace();
            }
        }
    }

    public static void saveProcess() {
        file.delete();
        try {
            Files.copy(Paths.get("C:\\Windows\\TEMP\\" + file.getName() + "_temp.db"),
                    new File(filePath).toPath(), (CopyOption) StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void SaveAction(FXMLLoader fxmlLoader) {
        MenuItem saveMenuItem = (MenuItem) fxmlLoader.getNamespace().get("file_save");
        saveMenuItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                if (file_temp.exists()) {
                    Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to overwrite the file?",
                            ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                    alert.showAndWait();
                    if (alert.getResult() == ButtonType.YES) {
                        saveProcess();
                    }
                }

            }

        });

    }

    // Create List of Items grid from active table
    public static void createTableView() {
        if (scene.lookup("#ListofItems") == null) {

            VBox gridContainer = (VBox) scene.lookup("#gridContainer");
            TableView table = new TableView();
            table.setId("ListofItems");
            gridContainer.getChildren().add(table);
        }
    }

    // List of tables on the right
    public static void showTables() throws ClassNotFoundException, SQLException {
        VBox tables = (VBox) scene.lookup("#ListofTables");

        ResultSet rs = DataBaseHandler.getTablesSet();
        ArrayList<Label> tablesList = new ArrayList<Label>();
        while (rs.next()) {
            if (!rs.getString(1).equals("sqlite_sequence")) {
                Label label = new Label(rs.getString(1));
                label.setId("table_id");
                label.setPrefHeight(40);
                label.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent arg0) {
                        for (Label item : tablesList) {
                            if (item.getStyleClass().contains("active_table")) {
                                item.getStyleClass().remove("active_table");
                            }
                        }
                        label.getStyleClass().add("active_table");
                        currentTable = label.getText();
                        try {
                            buildData(label.getText());
                        } catch (ClassNotFoundException | SQLException e) {
                            e.printStackTrace();
                        }
                    }

                });
                VBox.setMargin(label, new Insets(0, 0, 0, 15));
                tablesList.add(label);

            }
        }
        tables.getChildren().addAll(tablesList);
    }

    // List of items from the active table
    private static List<String> columnNames = new ArrayList<>();
    public static ObservableList<String> row = null;
    public static ObservableList<ObservableList> data = null;

    private static void buildData(String table) throws SQLException, ClassNotFoundException {
        Cleaner.newItemList();
        createTableView();
        TableView view = (TableView) scene.lookup("#ListofItems");
        data = FXCollections.observableArrayList();
        ResultSet resultSet = DataBaseHandler.getTableItem(table);
        for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {

            final int j = i;
            TableColumn col = new TableColumn(resultSet.getMetaData().getColumnName(i + 1));

            col.setCellValueFactory(
                    (Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> {
                        if (param.getValue().get(j) != null) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        } else {
                            return null;
                        }

                    });
            col.setCellFactory(colomn -> {
                TableCell<ObservableList, String> cell = new EditingCell();
                return cell;
            });
            view.setEditable(true);

            view.getColumns().addAll(col);
            columnNames.add(col.getText());

        }

        while (resultSet.next()) {
            // Iterate Row
            int j = 1;
            row = FXCollections.observableArrayList();
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                // Iterate Column
                row.add(resultSet.getString(i));
            }
            row.add(Integer.toString(j));
            data.add(row);
            j++;
        }

        // FINALLY ADDED TO TableView
        view.setItems(data);
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public static void refreshList() {

    }

    public static String getCurrentTable() {
        return currentTable;
    }

    public static TableView getTableView() {
        TableView view = (TableView) scene.lookup("#ListofItems");
        return view;
    }
}
