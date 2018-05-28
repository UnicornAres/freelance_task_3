package pro.arejim.tester.gui.frames;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

import static javafx.scene.paint.Color.TRANSPARENT;

public class Frame extends Application {

    public static Stage stage;          // Обьект окна
    private double xOffset = 50;        // Координаты окна по у
    private double yOffset = 50;        // Координаты окна по х

    public static void start(String[] args) {
        launch(args);
    }   // Просто запуск GUI

    @Override
    public void start(Stage stage) {
        Frame.stage = stage;            // Запоминание обьекта окна, для обращения извне
        Scene scene;                    // Создание сцены
        try {
            // Местонахождение шаблона окна fxml
            URL location = ClassLoader.getSystemResource("pro/arejim/tester/assets/templates/tester.fxml");
            // Загрузка сцены из шаблона и его инициализация методом initialize из класса Controller
            scene = new Scene(FXMLLoader.load(location), TRANSPARENT);
        } catch (IOException e) {   // При ошибке загрузки окна
            System.err.println("Can`t load frame.");
            // Создание окна ошибки при загрузке файла fxml
            Alert alert = new Alert(Alert.AlertType.ERROR);
            // Событие при закрытии окна ошибки
            alert.setOnCloseRequest(event -> System.exit(0));
            // Текст заглавления окна
            alert.setHeaderText("GUI starting error!");
            // Текст ошибки
            alert.setContentText(e.getMessage());
            // Загрузка сцены из окна ошибки
            scene = alert.getDialogPane().getScene();
        }
        // Действие при клике по сцене
        scene.setOnMousePressed(e -> {
            xOffset = stage.getX() - e.getScreenX();    // Запоминание положения окна по Х
            yOffset = stage.getY() - e.getScreenY();    // Запоминание положения окна по У
        });
        // Действие при передвижении мыши с зажатой ПКМ
        scene.setOnMouseDragged((MouseEvent e) -> {
            stage.setX(e.getScreenX() + xOffset);       // Перемещение окна по Х
            stage.setY(e.getScreenY() + yOffset);       // Перемещение она по У
        });

        stage.initStyle(StageStyle.TRANSPARENT);        // Скрытие стандартной рамки окна
        // Устанавливает иконку приложения
        Image ico = new Image("pro/arejim/tester/assets/images/favicon.png");
        stage.getIcons().add(ico);

        stage.setResizable(false);                      // Отключение "растяжимости" окна
        stage.setScene(scene);                          // Присвоение сцены окну
        stage.show();                                   // Отображение окна
    }
}