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

import com.dbase.DataBaseHandler;
import com.dialogs.AddFieldDialog;
import com.dialogs.AddItemDialog;
import com.dialogs.AddTableDialog;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class MainPage {
    static Scene scene = App.getScene();
    static Stage stage = App.getStage();
    static String currentTable = null;
    TableView tableView = null;

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

    // show start tip label
    static EventHandler<MouseEvent> addTableEvent;

    public static void showNoTablesTip() {
        if (getTableNames().size() < 1) {
            VBox gridContainer = (VBox) scene.lookup("#gridContainer");
            gridContainer.addEventFilter(MouseEvent.MOUSE_CLICKED, addTableEvent = new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent arg0) {
                    try {
                        new AddTableDialog().display();
                        showTables();
                        hideNoTablesTip();
                    } catch (ClassNotFoundException | SQLException | IOException e) {
                        e.printStackTrace();
                    }
                }

            });
            gridContainer.setAlignment(Pos.CENTER);
            Label label = new Label("To add a table, use (Table-> Create Table) or click here");
            label.getStyleClass().add("start_label");
            gridContainer.getChildren().add(label);
        }
    }

    // hide start tip label
    public static void hideNoTablesTip() {
        if (getTableNames().size() > 0) {
            VBox gridContainer = (VBox) scene.lookup("#gridContainer");
            gridContainer.removeEventFilter(MouseEvent.MOUSE_CLICKED, addTableEvent);
            gridContainer.getChildren().clear();
            gridContainer.setAlignment(Pos.TOP_LEFT);
        }
    }

    public static void MenuActions(FXMLLoader fxmlLoader) {
        SaveAction(fxmlLoader);
        FilePicker(fxmlLoader);
        FileAdder(fxmlLoader);
        ItemAdder(fxmlLoader);
        ItemRemover(fxmlLoader);
        TableRemover(fxmlLoader);
        TableAdder(fxmlLoader);
        FieldAdder(fxmlLoader);
        FieldRemover(fxmlLoader);
    }

    // Add Table
    private static void TableAdder(FXMLLoader fxmlLoader) {
        MenuItem addTableMenu = (MenuItem) fxmlLoader.getNamespace().get("table_add");
        addTableMenu.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(final ActionEvent e) {
                AddTableDialog table_Dialog = new AddTableDialog();
                try {
                    table_Dialog.display();
                    showTables();
                    hideNoTablesTip();
                } catch (ClassNotFoundException | SQLException | IOException e1) {
                    e1.printStackTrace();
                }

            }
        });
    }

    // Remove Table
    private static void TableRemover(FXMLLoader fxmlLoader) {
        MenuItem removeTableMenu = (MenuItem) fxmlLoader.getNamespace().get("table_remove");
        removeTableMenu.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(final ActionEvent e) {
                try {
                    DataBaseHandler.removeTable(getCurrentTable());
                    showTables();
                    showNoTablesTip();
                } catch (ClassNotFoundException | SQLException e1) {
                    e1.printStackTrace();
                }

            }
        });
    }

    // Add Field to Table
    private static void FieldAdder(FXMLLoader fxmlLoader) {

        MenuItem addFieldMenu = (MenuItem) fxmlLoader.getNamespace().get("table_addColumn");
        addFieldMenu.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(final ActionEvent e) {
                try {
                    new AddFieldDialog().display();
                    MainPage.buildData(MainPage.getCurrentTable());
                } catch (ClassNotFoundException | SQLException | IOException e1) {
                    e1.printStackTrace();
                }

            }
        });
    }

    // Remove Field from Table
    private static void FieldRemover(FXMLLoader fxmlLoader) {

        MenuItem removeFieldMenu = (MenuItem) fxmlLoader.getNamespace().get("table_removeColumn");
        removeFieldMenu.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(final ActionEvent e) {
                try {
                    DataBaseHandler.removeField(getCurrentTable(),
                            getColumnNames().get(getTableView().getFocusModel().getFocusedCell().getColumn()));
                    MainPage.buildData(MainPage.getCurrentTable());
                } catch (ClassNotFoundException | SQLException e1) {
                    e1.printStackTrace();
                }

            }
        });
    }

    // Add Item
    private static void ItemAdder(FXMLLoader fxmlLoader) {

        MenuItem addItemMenu = (MenuItem) fxmlLoader.getNamespace().get("item_add");
        addItemMenu.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(final ActionEvent e) {
                AddItemDialog item_Dialog = new AddItemDialog();
                try {
                    // DataBaseHandler.formField(getCurrentTable());
                    item_Dialog.display();
                    MainPage.buildData(MainPage.getCurrentTable());
                } catch (ClassNotFoundException | SQLException | IOException e1) {
                    e1.printStackTrace();
                }

            }
        });
    }

    // Remove Item
    private static void ItemRemover(FXMLLoader fxmlLoader) {
        MenuItem removeItemMenu = (MenuItem) fxmlLoader.getNamespace().get("item_remove");
        removeItemMenu.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(final ActionEvent e) {
                try {

                    ResultSet primaryKeys = DataBaseHandler.getPrimaryKeys(getCurrentTable());
                    String prim = primaryKeys.getString("COLUMN_NAME");

                    primaryKeys.getStatement().getConnection().close();
                    DataBaseHandler.removeItem(getCurrentTable(), prim,
                            data.get(getTableView().getFocusModel().getFocusedCell().getRow())
                                    .get(columnNames.indexOf(prim)));
                    updateAfterRemove(getTableView().getFocusModel().getFocusedCell().getRow());
                    buildData(getCurrentTable());
                } catch (ClassNotFoundException | SQLException e1) {
                    e1.printStackTrace();
                }

            }
        });
    }

    private static void updateAfterRemove(int i) throws SQLException, ClassNotFoundException {
        ResultSet primaryKeys = DataBaseHandler.getPrimaryKeys(getCurrentTable());
        String prim = primaryKeys.getString("COLUMN_NAME");
        int column = columnNames.indexOf(primaryKeys.getString("COLUMN_NAME"));
        primaryKeys.getStatement().getConnection().close();
        for (; i < data.size(); i++) {
            DataBaseHandler.updateValue(getCurrentTable(), prim, (i + 1),
                    String.valueOf((Integer.parseInt(MainPage.data.get(i).get(column)) - 1)));
        }
    }

    // File Chooser
    static File file;
    static File file_temp;
    static String filePath;
    static Path fileCopy = null;

    private static void FilePicker(FXMLLoader fxmlLoader) {

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

    // Add File
    private static void FileAdder(FXMLLoader fxmlLoader) {

        MenuItem addFileItem = (MenuItem) fxmlLoader.getNamespace().get("file_add");
        addFileItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(final ActionEvent e) {
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("DataBase Files (*.db)",
                        "*.db");
                fileChooser.getExtensionFilters().add(extFilter);

                fileChooser.setTitle("New SQLite file");
                file = fileChooser.showSaveDialog(stage);
                if (file != null) {

                    try {
                        file.createNewFile();
                        System.out.println();
                        Files.deleteIfExists(Paths.get("C:\\Windows\\TEMP\\" + file.getName() + "_temp.db"));
                        file_temp = new File("C:\\Windows\\TEMP\\" + file.getName() + "_temp.db");
                        fileCopy = Files.copy(file.toPath(), file_temp.toPath(),
                                (CopyOption) StandardCopyOption.REPLACE_EXISTING);

                        hideStartTipLabel();
                        DataBaseHandler.openSQLFile(fileCopy.toString().replace("\\", "//"));

                        Cleaner.newTableList();
                        showTables();
                        showNoTablesTip();
                    } catch (IOException | ClassNotFoundException | SQLException e1) {
                        e1.printStackTrace();
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

        if (file != null) {
            try {
                Files.deleteIfExists(Paths.get("C:\\Windows\\TEMP\\" + file.getName() + "_temp.db"));
                file_temp = new File("C:\\Windows\\TEMP\\" + file.getName() + "_temp.db");
                fileCopy = Files.copy(file.toPath(), file_temp.toPath(),
                        (CopyOption) StandardCopyOption.REPLACE_EXISTING);

                hideStartTipLabel();
                DataBaseHandler.openSQLFile(fileCopy.toString().replace("\\", "//"));

                Cleaner.newTableList();
                showTables();
                showNoTablesTip();

            } catch (SQLException | ClassNotFoundException | IOException ex2) {
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

    private static void SaveAction(FXMLLoader fxmlLoader) {
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
    static ArrayList<String> tableNames = new ArrayList<>();

    public static void showTables() throws ClassNotFoundException, SQLException {
        tableNames.clear();
        VBox tables = (VBox) scene.lookup("#ListofTables");
        Cleaner.newTableList();
        ResultSet rs = DataBaseHandler.getTablesSet();
        ArrayList<Label> tablesList = new ArrayList<Label>();
        while (rs.next()) {
            if (!rs.getString(1).equals("sqlite_sequence")) {
                tableNames.add(rs.getString(1));
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
        rs.getStatement().getConnection().close();
        rs.getStatement().close();
    }

    // List of items from the active table
    private static ArrayList<String> columnNames = new ArrayList<>();
    public static ObservableList<String> row = null;
    public static ObservableList<ObservableList<String>> data = null;

    @SuppressWarnings("unchecked")
    public static void buildData(String table) throws SQLException, ClassNotFoundException {
        columnNames.clear();
        Cleaner.newItemList();
        createTableView();
        ResultSet primaryKeys = DataBaseHandler.getPrimaryKeys(getCurrentTable());
        TableView view = (TableView) scene.lookup("#ListofItems");
        data = FXCollections.observableArrayList();
        ResultSet resultSet = DataBaseHandler.getTableItem(table);
        for (int i = 0; i < resultSet.getMetaData().getColumnCount() + 1; i++) {
            if (i < resultSet.getMetaData().getColumnCount()) {
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
                String primaryColumn;
                try {
                    primaryColumn = primaryKeys.getString("COLUMN_NAME");
                } catch (SQLException e) {
                    primaryColumn = "none";
                }

                if (!primaryColumn.equals(col.getText())) {

                    col.setCellFactory(colomn -> {
                        TableCell<ObservableList<Object>, String> cell = new EditingCell();
                        return cell;
                    });
                } else {
                    col.setCellFactory(colomn -> {
                        TableCell<ObservableList<Object>, String> cell = new PrimaryKeyCell();

                        return cell;
                    });
                }

                view.getColumns().addAll(col);
                columnNames.add(col.getText());
            } else {
                TableColumn col = new TableColumn("");
                final int j = i;
                col.setCellValueFactory(
                        (Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> {
                            if (param.getValue().get(j) != null) {
                                return new SimpleStringProperty("");
                            } else {
                                return null;
                            }

                        });
                col.setCellFactory(colomn -> {
                    TableCell<ObservableList<Object>, String> cell = new AddColumnCell();
                    return cell;
                });
                view.getColumns().addAll(col);
            }
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
        view.setEditable(true);
        resultSet.getStatement().getConnection().close();
        resultSet.getStatement().close();
        primaryKeys.getStatement().getConnection().close();
        primaryKeys.getStatement().close();

    }

    public static List<String> getColumnNames() {
        return columnNames;
    }

    public static void refreshList() {

    }

    public static String getCurrentTable() {
        return currentTable;
    }

    public static ArrayList<String> getTableNames() {
        return tableNames;
    }

    public static TableView getTableView() {
        TableView view = (TableView) scene.lookup("#ListofItems");
        return view;
    }
}
