module edu.istu.achipiga.electroniccheckoutregister {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires static lombok;

    opens edu.istu.achipiga to javafx.fxml;
    exports edu.istu.achipiga;
    exports edu.istu.achipiga.controllers;
    opens edu.istu.achipiga.controllers to javafx.fxml;
}