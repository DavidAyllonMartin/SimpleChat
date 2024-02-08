module org.ielena.simplechat {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.ielena.simplechat to javafx.fxml;
    exports org.ielena.simplechat;

    opens org.ielena.simplechat.controllers to javafx.fxml;
    exports org.ielena.simplechat.controllers;
}