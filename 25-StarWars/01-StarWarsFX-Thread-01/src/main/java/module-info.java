module dev.joseluisgs.starwarsfx {
    // Librerías que vamos a usar
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires io.github.microutils.kotlinlogging;
    requires org.slf4j;
    requires kotlin.result.jvm;
    requires java.desktop;
    requires open;
    requires koin.logger.slf4j;
    requires koin.core.jvm;


    // Abrimos y exponemos lo que va a usar desde clases con FXML
    opens dev.joseluisgs.starwarsfx to javafx.fxml;
    exports dev.joseluisgs.starwarsfx;

    // Controladores
    opens dev.joseluisgs.starwarsfx.controllers to javafx.fxml;
    exports dev.joseluisgs.starwarsfx.controllers;

    // Rutas
    opens dev.joseluisgs.starwarsfx.routes to javafx.fxml;
    exports dev.joseluisgs.starwarsfx.routes;

    // modelos
    opens dev.joseluisgs.starwarsfx.models to javafx.fxml;
    exports dev.joseluisgs.starwarsfx.models;


}