package pro.arejim.tester.gui.controllers;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import pro.arejim.tester.gui.frames.Frame;
import pro.arejim.tester.utils.CurrentPane;
import pro.arejim.tester.utils.Utils;

import java.math.BigInteger;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

public class Controller implements Initializable {

    @FXML
    private Button hideBtn;
    @FXML
    private Button exitBtn;
    @FXML
    private VBox processingPane;
    @FXML
    private Label numberLbl;
    @FXML
    private Label lengthLbl2;
    @FXML
    private Label elapsedTimeLbl;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label statusLbl;
    @FXML
    private VBox firstMethodPane;
    @FXML
    private VBox secondMethodPane;
    @FXML
    private TextField pFld;
    @FXML
    private Label pLbl1;
    @FXML
    private HBox mPane;
    @FXML
    private Label pLbl2;
    @FXML
    private Label mLbl;
    @FXML
    private HBox lengthPane;
    @FXML
    private Label lengthLbl;
    @FXML
    private TextField nFld;
    @FXML
    private Label lengthLbl3;
    @FXML
    private FlowPane selectionPane;
    @FXML
    private Button firstMethodBtn;
    @FXML
    private Button secondMethodBtn;
    @FXML
    private BorderPane bottomPanel;
    @FXML
    private Button testBtn;
    @FXML
    private Button backBtn;

    // Регулярное выражение для проверки (Только числа)
    private Pattern pattern = Pattern.compile("^[0-9]*$");
    // Поток для фоновых процессов
    private Thread thread;
    // Текущее окно (для возвращения в нужный метод после тестирования и правильной работы кнопок "Назад" и "Тестировать")
    private CurrentPane currentPane = CurrentPane.SELECTION_PANE;
    // Большое число П (для первого метода)
    private volatile BigInteger p;
    // Таймер для подсчёта времени в миллисекундах
    private Timer timer;
    private volatile DoubleProperty percents = new SimpleDoubleProperty(0);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Слушатель на изменение текста в текстовом поле P (Первый медод)
        pFld.textProperty().addListener((observable, oldValue, newValue) -> {
            // Отключение кнопки "Тестировать"
            testBtn.setDisable(true);
            // Останавка фонового потока
            if (thread != null && thread.isAlive()) {
                thread.interrupt();
                thread.stop();
            }
            // Проверка текстового поля с помощью регулярного выражения (только числа)
            // также проверка на длинну вводимой строки (до 10 символов из-за особенности метода, но больше и не нужно)
            // также вводимая строка не должна быть нулём или набором нулей
            if (pattern.matcher(newValue).matches() && newValue.length() <= 10 && !newValue.equals("0")) {
                if (newValue.equals("")) {          // Если строка пуста
                    pLbl1.setText("P");             // Отобразить сепень "Р" в формуле вместо числа
                    pLbl2.setText("p");             // Отобразить в индексе числа Мерсенна "р" вместо числа
                    mPane.setVisible(false);        // Спрятать число Мерсенна
                    lengthPane.setVisible(false);   // Спрятать длинну числа
                    mLbl.setText("");               // Обнулить строку с числом Мерсенна
                    lengthLbl.setText("");          // Обнулить строку с днинной числа
                } else {
                    pLbl1.setText(newValue);             // Отобразить текущую сепень в формуле вместо "Р"
                    pLbl2.setText(newValue);             // Отобразить в индексе числа Мерсенна текущее число вместо "р"
                    p = new BigInteger(newValue);        // Инициализация числа P
                    if (Utils.isPrime(p)) {              // Проверка вводимого числа Р на простоту (Особенности метода)
                        mPane.setVisible(true);          // Отобразить число Мерсенна
                        lengthPane.setVisible(true);     // Отобразить длинну числа Мерсенна
                        mLbl.setText("Обсчет...");
                        lengthLbl.setText("Обсчет...");
                        // Создание фонового потока для вычисления числа Мерсенна
                        thread = new Thread(() -> {
                            // Вычисление числа Мерсенна и превращение его в строку
                            String tmp = String.valueOf(Utils.calcM(p));
                            // если поток не прерван
                            if (!thread.isInterrupted())
                                Platform.runLater(() -> {   // Особенности GUI JavaFX
                                    // Отобразить число Мерсенна в предназначеной для этого строке
                                    mLbl.setText(tmp);
                                    // Отобразить длинну числа предварительно превратив длинну в строку
                                    lengthLbl.setText(String.valueOf(tmp.length()));
                                    // Включение кнопки "Тестировать"
                                    testBtn.setDisable(false);
                                });
                        });
                        // Запустить поток
                        thread.start();
                    } else {    // Если вводимое число Р составное
                        mPane.setVisible(false);        // Скрыть число Мерсенна
                        lengthPane.setVisible(false);   // Скрыть длинну числа
                        mLbl.setText("");               // Обнучить строку числа Мерсенна
                        lengthLbl.setText("");          // Обнулить строку длинны числа
                    }
                }
            } else {    // Если вводимый текст не проходит проверку возвращать предыдущее значение
                pFld.setText(oldValue);
            }
        });
        // Слушатель на изменение текста в текстовом поле (Второй медод)
        nFld.textProperty().addListener((observable, oldValue, newValue) -> {
            // Убирает пробелы, если в текстовом поле они есть
            if (newValue.contains(" ")) nFld.setText(newValue.replace(" ", ""));
            // Если текстовое поле пустое - отключение кнопки "Тестировать"
            if (newValue.equals("")) testBtn.setDisable(true);
                // Иначе - включение кнопки "Тестировать"
            else testBtn.setDisable(false);
            // Проверка текстового поля с помощью регулярного выражения (только числа)
            // также вводимая строка не должна быть нулём или набором нулей
            if (pattern.matcher(newValue).matches() && !newValue.equals("0")) {
                // Отображение длинны вводимого числа
                lengthLbl3.setText(String.valueOf(newValue.length()));
            } else {    // Если вводимый текст не проходит проверку возвращать предыдущее значение
                nFld.setText(oldValue);
            }
        });
        // Действие по клику на "Крестик"
        exitBtn.setOnAction(e -> {
            Platform.exit();           // Закрыть GUI
            System.exit(0);     // Закрыть приложение
        });
        // Действие по нажатию на кнопку "Свернуть"
        hideBtn.setOnAction(e -> Frame.stage.setIconified(true));
        // Действие по нажатию на кнопку "Тест Люка-Лемера"
        firstMethodBtn.setOnAction(e -> {
            selectionPane.setVisible(false);    // Спрятать окно выбора метода тестирования
            bottomPanel.setVisible(true);       // Отобразить панель с кнопками
            firstMethodPane.setVisible(true);   // Отобразить окно метода Люка-Лемера (Первый метод)
            currentPane = CurrentPane.FIRST_METHOD;     // Текущее окно = первый метод
        });
        // Действие по нажатию на кнопку "Тест Аткина-Морейна"
        secondMethodBtn.setOnAction(e -> {
            selectionPane.setVisible(false);    // Спрятать окно выбора метода тестирования
            bottomPanel.setVisible(true);       // Отобразить панель с кнопками
            secondMethodPane.setVisible(true);  // Отобразить окно метода Аткина-Морейна (Второй метод)
            currentPane = CurrentPane.SECOND_METHOD;    // Текущее окно = второй метод
        });
        // Слушатель на отображение окна первого метода
        firstMethodPane.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) pFld.requestFocus();  // Поместить курсор в текстовое поле
        });
        // Слушатель на отображение окна второго метода
        secondMethodPane.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) nFld.requestFocus();  // Поместить курсор в текстовое поле
        });
        // Действие по нажатию на кнопку тестировать
        testBtn.setOnAction(e -> {
            if (currentPane == CurrentPane.FIRST_METHOD) {      // Если текущее окно == первый метод
                firstMethodPane.setVisible(false);      // Скрытие окна первого метода
                testBtn.setVisible(false);              // Скрытие кнопки "Тестировать"
                numberLbl.setText(mLbl.getText());      // Отображение тестируемого числа (*перенос из предыдушего окна)
                lengthLbl2.setText(lengthLbl.getText());// Отображение длинны тестируемого числа (*)
                pFld.setText("");                       // Обнуление строки вводимого числа Р в предыдущем окне
                mPane.setVisible(false);                // Скрытие числа Мерсенна в предыдущем окне
                lengthPane.setVisible(false);           // Скрытие длинны числа в предыдущем окне
                processingPane.setVisible(true);        // Отображение окна результатов
                elapsedTimeLbl.setText("0");            // Обнулить значение строки затраченого времени
                statusLbl.setText("");                  // Скрыть строку статуса
                backBtn.setText("Прервать");            // Заменить текст на кнопке "Назад" на "Прервать"
                progressIndicator.setVisible(true);     // Отображение индикатора загрузки
                // Создание фонового потока для тестирования числа на простоту
                progressIndicator.setProgress(0);
                progressIndicator.progressProperty().bind(percents);
                thread = new Thread(() -> {
                    startTimer();                       // Запуск таймера
                    if (Utils.testM(p, new BigInteger(numberLbl.getText()), percents))  // Проверка числа на простоту
                        Platform.runLater(() -> statusLbl.setText("Число простое"));
                    else Platform.runLater(() -> statusLbl.setText("Число составное"));
                    Platform.runLater(() -> {           // Особенности JavaFX
                        progressIndicator.setVisible(false);    // Скрытие индикатора загрузки
                        backBtn.setText("Назад");       // Заменить текст на кнопке "Прервать" на "Назад"
                    });
                });
                // Запуск фонового потока
                thread.start();
                // Текущее окно = "результат первого метода"
                currentPane = CurrentPane.RESULT_OF_FIRST;
            } else if (currentPane == CurrentPane.SECOND_METHOD) {      // Если текущее окно == второй метод
                secondMethodPane.setVisible(false);     // Скрытие окна второго метода
                testBtn.setDisable(true);               // Отключение кнопки "Тестировать"
                testBtn.setVisible(false);              // Скрытие кнопки "Тестировать"
                numberLbl.setText(nFld.getText());      // Отображение тестируемого числа (*перенос из предыдушего окна)
                lengthLbl2.setText(lengthLbl3.getText());// Отображение длинны тестируемого числа (*)
                nFld.setText("");                       // Обнуление строки вводимого числа в предыдущем окне
                processingPane.setVisible(true);        // Отображение окна результатов
                elapsedTimeLbl.setText("0");            // Обнулить значение строки затраченого времени
                statusLbl.setText("");                  // Скрыть строку статуса
                backBtn.setText("Прервать");            // Заменить текст на кнопке "Назад" на "Прервать"
                progressIndicator.setVisible(true);     // Отображение индикатора загрузки
                // Создание фонового потока для тестирования числа на простоту
                thread = new Thread(() -> {
                    startTimer();                       // Запуск таймера
                    if (Utils.testA(new BigInteger(numberLbl.getText()))) {     // Проверка числа на простоту
                        Platform.runLater(() -> statusLbl.setText("Число простое"));
                    } else {
                        Platform.runLater(() -> statusLbl.setText("Число составное"));
                    }
                    Platform.runLater(() -> {
                        progressIndicator.setVisible(false);    // Скрытие индикатора загрузки
                        backBtn.setText("Назад");       // Заменить текст на кнопке "Прервать" на "Назад"
                    });
                });
                // Запуск фонового потока
                thread.start();
                // Текущее окно = "результат второго метода"
                currentPane = CurrentPane.RESULT_OF_SECOND;
            }
        });
        // Действие по нажатию на кнопку назад
        backBtn.setOnAction(e -> {
            if (currentPane == CurrentPane.FIRST_METHOD) {              // Если текущее окно == первый метод
                selectionPane.setVisible(true);             // Отображение окна выбора
                bottomPanel.setVisible(false);              // Скрытие панели кнопок
                firstMethodPane.setVisible(false);          // Скрытие окна первого метода
                currentPane = CurrentPane.SELECTION_PANE;   // Текущее окно = окно выбора метода
                pFld.setText("");                           // Обнуление вводимого числа Р в окне первго метода
                mPane.setVisible(false);                    // Скрытие числа Мерсенна в окне первого метода
                lengthPane.setVisible(false);               // Скрытие длинны числа в окне первого метода
            } else if (currentPane == CurrentPane.SECOND_METHOD) {      // Если текущее окно == второй метод
                selectionPane.setVisible(true);             // Отображение окна выбора
                bottomPanel.setVisible(false);              // Скрытие панели кнопок
                secondMethodPane.setVisible(false);         // Скрытие окна второго метода
                currentPane = CurrentPane.SELECTION_PANE;   // Текущее окно = окно выбора метода
                nFld.setText("");                           // Обнуление вводимого числа в окне первого метода
            } else if (currentPane == CurrentPane.RESULT_OF_FIRST) {    // Если текущее окно == результат первого метода
                backFromResult(firstMethodPane, pFld, String.valueOf(p), CurrentPane.FIRST_METHOD);
            } else if (currentPane == CurrentPane.RESULT_OF_SECOND) {   // Если текущее окно == результат второго метода
                backFromResult(secondMethodPane, nFld, numberLbl.getText(), CurrentPane.SECOND_METHOD);
            }
        });
    }

    // Возвращение с окна результатов
    private void backFromResult(Pane pane, TextField t, String value, CurrentPane name) {
        if (thread.isAlive()) {             // Если поток запущен
            thread.interrupt();                     // Прервать поток
            thread.stop();                          // Остановить поток
            statusLbl.setText("Прервано.");         // Установить статус "Прервано."
            progressIndicator.setVisible(false);    // Скрыть индикатор загрузки
            backBtn.setText("Назад");               // Заменить текст на кнопке "Прервать" на "Назад"
        } else {                            // Если поток не запущен или остановлен
            t.setText(value);                       // Возвращение предыдущего вводимого числа
            processingPane.setVisible(false);       // Скрытие окна тестирования
            pane.setVisible(true);                  // Отображение окна @pane@ метода
            testBtn.setVisible(true);               // Отобращение кнопки "Тестировать"
            currentPane = name;                     // Текущее окно = окно @name@ метода
        }
    }

    // Запуск таймера
    private void startTimer() {
        timer = new Timer("Timer");              // Создание нового таймера
        long time = System.currentTimeMillis();        // Запоминание текущего времени
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long elapsedTime = System.currentTimeMillis() - time;   // Подсчёт прошедшего времени
                if (thread.isAlive()) {                 // Если поток запущен
                    Platform.runLater(() -> elapsedTimeLbl.setText(elapsedTime + " мс."));  // Обновление строки
                } else {
                    Platform.runLater(() -> {
                        progressIndicator.progressProperty().unbind();
                        progressIndicator.progressProperty().setValue(-1);
                    });
                    timer.cancel();                     // остановка таймера
                }
            }
        }, 0, 1);       // Выполнять спустя 0 секунд каждую 1 миллисекунду
    }
}
