module SSEProjekt {
    requires javafx.controls;
    requires javafx.media;
    requires java.rmi;

    opens app;
    opens dialogs;
    opens dataAndMethods;

    exports rmiInterfaces;
    exports server;
    exports dataAndMethods;
    exports dialogs;
}