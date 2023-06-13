module dev.joseluisgs.expedientesacademicos {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires open;

    // Logger
    requires io.github.microutils.kotlinlogging;
    requires koin.logger.slf4j;
    requires org.slf4j;

    // Gson
    requires com.google.gson;

    // Result
    requires kotlin.result.jvm;

    // SqlDeLight
    requires runtime.jvm;
    requires sqlite.driver;
    // Como no pongas esto te vas a volver loco con los errores
    requires java.sql;

    // Koin
    requires koin.core.jvm;


    // Abrimos y exponemos el paquete a JavaFX
    opens dev.joseluisgs.expedientesacademicos to javafx.fxml;
    exports dev.joseluisgs.expedientesacademicos;

    // Controladores
    opens dev.joseluisgs.expedientesacademicos.controllers to javafx.fxml;
    exports dev.joseluisgs.expedientesacademicos.controllers;

    // Rutas
    opens dev.joseluisgs.expedientesacademicos.routes to javafx.fxml;
    exports dev.joseluisgs.expedientesacademicos.routes;

    // dtos, abrimos a Gson
    opens dev.joseluisgs.expedientesacademicos.dto.json to com.google.gson;

    // Modelos a javafx para poder usarlos en las vistas
    opens dev.joseluisgs.expedientesacademicos.models to javafx.fxml;
    exports dev.joseluisgs.expedientesacademicos.models;

}