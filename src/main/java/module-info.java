module SSEProjekt {
    requires javafx.controls;
    requires javafx.media;
    requires javafx.swing;
    requires java.rmi;
    requires java.desktop;

    opens App;
    opens Dialogs;
    opens NoLongerInUse;
    opens ClientLogic;
    opens DataAndMethods;

    exports ClientLogic;
    exports RMIInterfaces;
    exports Server;
    exports DataAndMethods;
    exports Dialogs;
}