module SSEProjekt {
    requires javafx.controls;
    requires javafx.media;
    requires java.rmi;

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