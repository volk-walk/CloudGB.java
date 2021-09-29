import com.geekbrains.Command;
import com.geekbrains.FileMessage;
import com.geekbrains.List_Request;
import com.geekbrains.List_Response;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Slf4j
public class Controller implements Initializable {


    private static String Root = "client/root";
    private static byte [] buffer= new byte[1024];
    public TextField input;
    public ListView <String> listView;
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;

    public void send(ActionEvent actionEvent) throws Exception {


        String fileName = input.getText();
        input.clear();
//        sendFile(fileName);
        os.writeObject(new List_Request());


    }

    private void sendFile(String fileName) throws IOException {
        Path file = Paths.get(Root, fileName);
        os.writeObject(new FileMessage(file));
        os.flush();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            fileView();
            Socket socket = new Socket("localhost",8189);
            os=new ObjectEncoderOutputStream(socket.getOutputStream());
            is=new ObjectDecoderInputStream(socket.getInputStream());
            Thread deamon = new Thread(()->{
                try {
                    while (true) {
                        Command cmd = (Command) is.readObject();
                       switch (cmd.getType()){
                           case LIST_RESPONSE:{
                               listFile(cmd);
                           }
                       }
                    }
                }catch (Exception e){
                    log.error("exception while read from input stream");
                }

            });
            deamon.setDaemon(true);
            deamon.start();
        } catch (IOException ioException) {
            log.error("e= ",ioException);
        }
//        Пытался сделать спомощью этих методов(как было в прошлых уроках сетевой чат), но наткнулся на ошибку которую так и не понял как устранить
//        File show = new File(Root);
//        for (File f: show.listFiles()){
//            listView.getItems().add(f.toString());
//        }

    }

//    public void click(MouseEvent mouseEvent) {
//        String clicker = listView.getSelectionModel().getSelectedItem();
//        input.setText(clicker);
//    }

    private void fileView () throws IOException {

        listView.getItems().clear();
        listView.getItems().addAll(
                Files.list(Paths.get(Root)).map(p->p.getFileName().toString()).collect(Collectors.toList())
        );
        listView.setOnMouseClicked(e->{
            String click = listView.getSelectionModel().getSelectedItem();
            input.setText(click);
        });
    }
    private void listFile (Command cmd) throws IOException {
            List_Response listResponse = (List_Response) cmd;
            List<String> listFile = listResponse.getBuf();
            Platform.runLater(() -> {
                listView.getItems().clear();
                for (int i = 0; i < listFile.size(); i++) {
                    listView.getItems().add(listFile.get(i));
                }
            });


        }
    }


