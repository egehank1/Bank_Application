    import bank.*;
import bank.exceptions.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class MainController {
    @FXML ListView<String> AccountListe;
    private PrivateBank fxmlBank;

    // Damit der benutzer etwas sehen kann muss die liste "nicht-leer"
    @FXML public void initialize() throws AccountAlreadyExistsException, IOException, TransactionAlreadyExistException, AccountDoesNotExistException {

        fxmlBank = new PrivateBank("Commerzbank AG","Banken",0.2,0.1);
        AccountListe.getItems().addAll(fxmlBank.getAllAccounts());
    }

    private void refresh(){
        AccountListe.getItems().clear();
        AccountListe.getItems().addAll(fxmlBank.getAllAccounts());
    }

    /**
     * Account-Auswahl Methode um weitere Details anzuzeigen.
     *
     * @param event
     * @throws IOException
     */
    @FXML public void auswaehlen(ActionEvent event) throws IOException {
        // Erste error behandlung: Wenn kein Konto ausgewaehlt wird.
        if(AccountListe.getSelectionModel().getSelectedItem() == null){
            return;
        }

        System.out.println( AccountListe.getSelectionModel().getSelectedItem());

        FXMLLoader fxmlLoader = new FXMLLoader(P5.class.getResource("account-view.fxml"));
        Stage stage = (Stage) AccountListe.getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);

        // Alle Transaktionen anzeigen.
        AccountController ac = fxmlLoader.getController();
        ac.setInformation(AccountListe.getSelectionModel().getSelectedItem(), fxmlBank);
    }

    /**
     * method to delete Account in GUI
     *
     * @param event
     * @throws AccountDoesNotExistException
     * @throws IOException
     */

    @FXML public void loeschen(ActionEvent event) throws AccountDoesNotExistException, IOException {
        String account = AccountListe.getSelectionModel().getSelectedItem();


        try{
            //meldung
            Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);

            dialog.setTitle("Delete account");
            dialog.setHeaderText("Do you really wanna delete the Account: " + account +" ?");

            ButtonType yesButton = new ButtonType("yes");
            ButtonType noButton = new ButtonType("no", ButtonBar.ButtonData.CANCEL_CLOSE);

            dialog.getButtonTypes().setAll(yesButton,noButton);

            var answer = dialog.showAndWait();

            if(answer.get()== yesButton){
                System.out.println("Lösche Account " + account );
                fxmlBank.deleteAccount(account);
                refresh();
            }
        }catch(AccountDoesNotExistException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Account Does not Exits");
            Optional<ButtonType> result = alert.showAndWait();
            System.out.println(e.getMessage());
        }
    }

    /**
     * method to create a new Account in GUI
     *
     * @param actionEvent
     * @throws AccountAlreadyExistsException
     * @throws IOException
     */
    public void newAccount(ActionEvent actionEvent) throws AccountAlreadyExistsException, IOException {
        // single-line text, textinputdialog ist anpassend.
        TextInputDialog dialog = new TextInputDialog("Account Name");
        dialog.setTitle("Create a new Account");
        dialog.setHeaderText("choose a name for the account: ");
        // var = undefined variabel, in unserem fall, wegen showandwait() <String>
        var input = dialog.showAndWait();
        try {
            if (input.isPresent()) {
                System.out.println("Chosen Account name: " + input.get());
                fxmlBank.createAccount(input.get());
                refresh();
            }
        }catch (AccountAlreadyExistsException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Account  already  Exits");
            Optional<ButtonType> result = alert.showAndWait();
            System.out.println(e.getMessage());
        }
    }
}