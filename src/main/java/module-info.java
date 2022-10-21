module SSEProjekt {
    requires javafx.controls;
    requires javafx.media;
    requires javafx.swing;
    requires java.rmi;

    opens App;
    opens Dialogs;
    opens DataAndMethods;

    exports RMIInterfaces;
    exports Server;
    exports DataAndMethods;
    exports Dialogs;
}