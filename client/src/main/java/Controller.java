import com.geekbrains.*;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PseudoColumnUsage;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Slf4j
public class Controller implements Initializable {


    private static byte [] buffer= new byte[1024];
    public TextField clientPath;
    public TextField serverPath;
    public ListView <String> clientView;
    public ListView <String> serverView;
    public TextField loginField;
    public PasswordField passwordField;
    public HBox authPanel;
    public HBox cloudPanel;
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    private Path dir = Paths.get("client","root");



    private boolean authenticated;
    public void setAuthenticated(boolean authenticated){
        this.authenticated=authenticated;
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        cloudPanel.setVisible(authenticated);
        cloudPanel.setManaged(authenticated);
    }




    @Override
    public void initialize(URL location, ResourceBundle resources) {

        connect();
        setAuthenticated(false);
      navigation();

    }

    private void connect (){
        try {

            Socket socket = new Socket("localhost",8189);
            is=new ObjectDecoderInputStream(socket.getInputStream());
            os=new ObjectEncoderOutputStream(socket.getOutputStream());


            Thread daemon = new Thread(()->{
                try {
                    while (true) {
                        Command cmd = (Command) is.readObject();
                        log.debug("received: {}",cmd);
                        switch (cmd.getType()){
                            case LIST_RESPONSE:
                                List_Response response = (List_Response) cmd;
                                List<String> name = response.getBuf();
                                serverView_w(name);
                                break;
                            case PATH_RESPONSE:
                                PathResponse pathResponse = (PathResponse) cmd;
                                String path = pathResponse.getPath();
                                Platform.runLater(()->{
                                    serverPath.setText(path);

                                });
                                break;

                            case FILE_MESSAGE:
                                FileMessage msg = (FileMessage) cmd;
                                Files.write(dir.resolve(msg.getName()), msg.getBytes());
                                clientView_w();
                                break;

                            case AUTH_RESPONSE:
                                AuthResponse auth = (AuthResponse) cmd;
                                if(auth.getStatus()){
                                    setAuthenticated(auth.getStatus());
                                }break;
                        }
                    }
                }catch (Exception e){
                    log.error("exception while read from input stream");
                    e.printStackTrace();
                }

            });
            daemon.setDaemon(true);
            daemon.start();
        } catch (IOException ioException) {
            log.error("e= ",ioException);
        }
    }

    private void clientView_w () throws IOException {

        clientPath.setText(dir.toString());
        List<String> names = Files.list(dir).map(p->p.getFileName().toString())
                .collect(Collectors.toList());
        Platform.runLater(()->{
            clientView.getItems().clear();
            clientView.getItems().addAll(names);
        });
    }
    private void serverView_w (List<String> names) {

        Platform.runLater(()->{
            serverView.getItems().clear();
            serverView.getItems().addAll(names);
        });

        }

        private void navigation (){
        clientView.setOnMouseClicked(e->{
            if(e.getClickCount()==2){
                String name2 = clientView.getSelectionModel().getSelectedItem();
                Path newPath = dir.resolve(name2);
                if(Files.isDirectory(newPath)){
                    dir=newPath;
                    try {
                        clientView_w();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });

        serverView.setOnMouseClicked(e->{
            if(e.getClickCount()==2){
                String name2 = serverView.getSelectionModel().getSelectedItem();
                try {
                    os.writeObject(new PathInRequest(name2));
                    os.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        }
    public void clientUp(ActionEvent actionEvent) throws IOException {
        dir = dir.getParent();
        clientPath.setText(dir.toString());
        clientView_w();
    }

    public void serverUp(ActionEvent actionEvent) throws Exception {
        os.writeObject(new PathUpRequest());
        os.flush();
    }

    public void download(ActionEvent actionEvent) throws IOException {
        String dwld = serverView.getSelectionModel().getSelectedItem();

        os.writeObject(new FileRequest(dwld));
        os.flush();
    }

    public void upLoad(ActionEvent actionEvent) throws IOException {
        String upld = clientView.getSelectionModel().getSelectedItem();
        FileMessage msg = new FileMessage(dir.resolve(upld));

        os.writeObject(msg);
        os.flush();

    }

    public void tryToAuth(ActionEvent actionEvent) {
        String login = loginField.getText();
        String pass = passwordField.getText();
        try {
            os.writeObject(new AuthRequest(login,pass));
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
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


