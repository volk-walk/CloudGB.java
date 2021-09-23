import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Slf4j
public class Controller implements Initializable {


    private static String Root = "client/root";
    private static byte [] buffer= new byte[1024];
    public TextField input;
    public ListView <String> listView;
    private DataInputStream is;
    private DataOutputStream os;

    public void send(ActionEvent actionEvent) throws Exception {


        String fileName = input.getText();
        input.clear();
        sendFile(fileName);



    }

    private void sendFile(String fileName) throws IOException {
        Path file = Paths.get(Root, fileName);
        long size = Files.size(file);
        os.writeUTF(fileName);
        os.writeLong(size);
        InputStream fileStream = Files.newInputStream(file);
        int read;
        while ((read=fileStream.read(buffer))!=-1){
            os.write(buffer,0,read);
        }
        os.flush();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            fileView();
            Socket socket = new Socket("localhost",8189);
            is=new DataInputStream(socket.getInputStream());
            os=new DataOutputStream(socket.getOutputStream());
            Thread deamon = new Thread(()->{
                try {
                    while (true) {
                        String msg = is.readUTF();
                        log.debug("received: {}", msg);
                        Platform.runLater(()->listView.getItems().add(msg));
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



}
