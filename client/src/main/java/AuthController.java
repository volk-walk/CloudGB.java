import com.geekbrains.*;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import netty.NettyClient;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.ResourceBundle;

@Slf4j
public class AuthController implements Initializable {
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    public TextField loginField;
    public PasswordField passwordField;
    private NettyClient nc;

    public void initialize(URL location, ResourceBundle resources) {

        connect();
    }

    private void connect(){
        nc = NettyClient.getInstance(cmd -> {
            if (cmd.getType() == CommandType.AUTH_RESPONSE){
                chatWindow();
            }

        });
    }
    private void chatWindow() throws IOException {
        Platform.runLater(()-> {
            Parent chat_window_parent = null;
            try {
                chat_window_parent = FXMLLoader.load(getClass().getResource("chat.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage window = (Stage) loginField.getScene().getWindow();
            window.setScene(new Scene(chat_window_parent));
            window.setResizable(true);
        });
    }
    public void tryToAuth(ActionEvent actionEvent) {
        String login = loginField.getText();
        String pass = passwordField.getText();
        nc.sendCommand(new AuthRequest(login,pass));
    }
    public void tryToReg(ActionEvent actionEvent) {
        String login = loginField.getText();
        String pass = passwordField.getText();
        try {
            os.writeObject(new RegRequest(login,pass));
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
