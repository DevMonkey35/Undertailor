package me.scarlet.undertailor.desktop;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import me.scarlet.undertailor.LaunchOptions;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.manager.EnvironmentManager.ViewportType;
import me.scarlet.undertailor.util.BorderedPane;
import me.scarlet.undertailor.util.JFXUtil;

import java.io.File;
import java.io.InputStream;
import java.util.Optional;

public class Launcher extends Scene {
    
    private LaunchOptions options;
    
    public Launcher(Stage stage, LaunchOptions options) {
        super(new AnchorPane());
        
        this.options = options;
        stage.setWidth(512);
        stage.setHeight(320);
        stage.setResizable(false);
        stage.setTitle("Undertailor Launcher");
        JFXUtil.loadIcon(stage, "defaultIcon.png");
        JFXUtil.loadIcon(stage, "defaultIcon_small.png");
        
        AnchorPane root = (AnchorPane) this.getRoot();
        root.setStyle("-fx-background-color: #F4F4F4");
        
        // -- left-side info pane
        // banner
        InputStream bannerImageStream = Undertailor.class.getResourceAsStream("/assets/banner.png");
        AnchorPane container = new AnchorPane();
        if(bannerImageStream != null) {
            ImageView banner = new ImageView(new Image(bannerImageStream));
            Tooltip.install(banner, new Tooltip(":P"));
            container.setStyle("-fx-border-color: #D4D4D4;"
                             + "-fx-border-width: 4px;");
            container.setMinSize(banner.getFitWidth() + 12, banner.getFitHeight() + 12);
            JFXUtil.setAnchorBounds(banner, 1D);
            container.getChildren().add(banner);
        } else {
            Label label = new Label("There was a pretty image here.\n"
                                  + "Java couldn't load it. :c");
            container.getChildren().add(label);
        }
        
        JFXUtil.setAnchorBounds(container, 12D, null, 12D, null);
        root.getChildren().add(container);
        
        // tabs
        
        TabPane tabs = new TabPane();
        tabs.setStyle("-fx-border-color: #D4D4D4;"
                    + "-fx-border-width: 0 0 0 1px;");
        tabs.setSide(Side.BOTTOM);
        
        tabs.getTabs().addAll(createOptionsTab(), createInfoTab());
        for(Tab tab : tabs.getTabs()) {
            tab.setClosable(false);
        }
        
        JFXUtil.setAnchorBounds(tabs, 0D, 0D, 194D, 0D);
        root.getChildren().add(tabs);
        
        // launch button
        Button launchButton = new Button("Launch Game");
        launchButton.setTooltip(new Tooltip("Launch Undertailor with the set options."));
        launchButton.setPrefSize(170D, 36D);
        
        launchButton.setOnMouseReleased(event -> {
            launchButton.setDisable(true);
            launchButton.setTooltip(new Tooltip("Wait for a little bit."));
            launchButton.setText("Launching...");
            
            this.options.save();
            if(options.useCustomDir && options.assetDir.list((file, name) -> {
                    return name.equals("main.lua");
                }).length == 0) {
                
                Alert alert = new Alert(AlertType.WARNING);
                alert.setHeaderText("This is gonna crash.");
                alert.setContentText("It looks like the selected folder doesn't have a main.lua file. The game'll crash from not finding it, but press OK if you're fine with that.");
                alert.getButtonTypes().add(ButtonType.CANCEL);
                
                Optional<ButtonType> response = alert.showAndWait();
                if(!response.isPresent() || response.get() == ButtonType.CANCEL) {
                    launchButton.setDisable(false);
                    launchButton.setTooltip(new Tooltip("Launch Undertailor with the set options."));
                    launchButton.setText("Launch Game");
                    return;
                }
            }
            
            DesktopLauncher.launchGame(options);
            stage.close();
        });
        
        JFXUtil.setAnchorBounds(launchButton, null, 12D, 12D, null);
        root.getChildren().add(launchButton);
    }
    
    Hyperlink generateHyperlink(String text, String url, String imagePath) {
        Hyperlink link = new Hyperlink(text);
        InputStream imageStream = Undertailor.class.getResourceAsStream("/assets/" + imagePath);
        if(imageStream != null) {
            ImageView img = new ImageView(new Image(imageStream));
            img.setFitHeight(24);
            img.setFitWidth(24);
            
            link.setGraphic(img);
            link.setGraphicTextGap(8D);
        }
        
        link.setOnMouseReleased(event -> {
            DesktopLauncher.instance.getHostServices().showDocument(url);
        });
        
        return link;
    }
    
    Tab createInfoTab() {
        Tab tab = new Tab("Info");
        
        VBox infoRoot = new VBox();
        infoRoot.setPadding(new Insets(10));
        
        tab.setContent(infoRoot);
        tab.setTooltip(new Tooltip("Information about the engine."));
        
        Label engineExp = new Label("Undertailor is a Lua-based engine made in Java, wrapped around the libGDX project. The aim of the project is to create an engine that specifically targets the creation of Undertale fangames.");
        Label disclaimerExp = new Label("UNDERTALE, the game of which this engine is based on, is the creation of Toby \"Radiation\" Fox. All assets used for interfaces of this program belong to their respective owners (logos and sprites).");
        
        engineExp.setWrapText(true);
        disclaimerExp.setWrapText(true);
        
        Hyperlink underLink = generateHyperlink("Check out Undertale!", "http://store.steampowered.com/app/391540/", "defaultIcon.png");
        Hyperlink docsLink = generateHyperlink("Learn how to use the engine!", "http://xemiru.github.io/Undertailor/luadocs/", "lua.png");
        Hyperlink githubLink = generateHyperlink("Help out on GitHub!", "http://github.com/Xemiru/Undertailor", "github.png");
        
        underLink.setTooltip(new Tooltip("Toby Fox is a cool dood."));
        docsLink.setTooltip(new Tooltip("Fear the wrath of Lua."));
        githubLink.setTooltip(new Tooltip("I swear to god if you spam my issue tracker."));
        
        VBox.setVgrow(engineExp, Priority.ALWAYS);
        VBox.setVgrow(disclaimerExp, Priority.ALWAYS);
        
        VBox.setVgrow(underLink, Priority.NEVER);
        VBox.setVgrow(docsLink, Priority.NEVER);
        VBox.setVgrow(githubLink, Priority.NEVER);
        
        VBox.setMargin(disclaimerExp, new Insets(8, 0, 0, 0));
        VBox.setMargin(underLink, new Insets(16, 0, 0, 0));
        
        infoRoot.setAlignment(Pos.TOP_LEFT);
        infoRoot.getChildren().addAll(engineExp, disclaimerExp, underLink, docsLink, githubLink);
        
        return tab;
    }
    
    Tab createOptionsTab() {
        Tab tab = new Tab("Options");
        
        VBox optionsRoot = new VBox(10);
        optionsRoot.setPadding(new Insets(10));
        
        tab.setContent(optionsRoot);
        tab.setTooltip(new Tooltip("Options as to how some parts of the engine will run."));
        
        // ###### Window options
        
        BorderedPane windowOptionsContainer = new BorderedPane("Graphics/Window", "Options in relation to how the game looks.");
        GridPane windowOptionsContent = new GridPane();
        
        Label sizeLabel = new Label("Window Size");
        sizeLabel.setTooltip(new Tooltip("The size of the window when it first launches. Does not affect fullscreen mode."));
        
        HBox sizeBoxes = new HBox(4);
        TextField widthBox = new TextField(options.windowWidth + "");
        widthBox.setTooltip(new Tooltip("The width of the window."));
        TextField heightBox = new TextField(options.windowHeight + "");
        heightBox.setTooltip(new Tooltip("The height of the window."));
        
        widthBox.textProperty().addListener((value, old, neww) -> {
            try {
                if(neww.isEmpty()) {
                    this.options.windowWidth = 640;
                } else {
                    this.options.windowWidth = Integer.parseInt(neww);
                }
                
                Platform.runLater(() -> {
                    widthBox.setStyle("");
                });
            } catch(NumberFormatException e) {
                Platform.runLater(() -> {
                    widthBox.setText(old);
                });
            }
        });
        
        heightBox.textProperty().addListener((value, old, neww) -> {
            try {
                if(neww.isEmpty()) {
                    this.options.windowHeight = 480;
                } else {
                    this.options.windowHeight = Integer.parseInt(neww);
                }
                
                Platform.runLater(() -> {
                    heightBox.setStyle("");
                });
            } catch(NumberFormatException e) {
                Platform.runLater(() -> {
                    heightBox.setText(old);
                });
            }
        });
        
        widthBox.setMaxWidth(50D);
        widthBox.setMinHeight(20D);
        heightBox.setMaxWidth(50D);
        heightBox.setMinHeight(20D);
        Label x = new Label("x");
        
        sizeBoxes.setAlignment(Pos.CENTER_RIGHT);
        sizeBoxes.getChildren().addAll(widthBox, x, heightBox);
        
        GridPane.setRowIndex(sizeLabel, 0);
        GridPane.setRowIndex(sizeBoxes, 0);
        GridPane.setColumnIndex(sizeLabel, 0);
        GridPane.setColumnIndex(sizeBoxes, 1);
        GridPane.setHalignment(sizeBoxes, HPos.RIGHT);
        GridPane.setHgrow(sizeLabel, Priority.ALWAYS);
        GridPane.setHgrow(sizeBoxes, Priority.NEVER);
        windowOptionsContent.getChildren().addAll(sizeLabel, sizeBoxes);
        
        Label scalingLabel = new Label("Scaling");
        scalingLabel.setTooltip(new Tooltip("How the game rendering reacts to differring aspect ratios."));
        HBox choicesBox = new HBox(4);
        ToggleGroup choices = new ToggleGroup();
        RadioButton scalingFit = new RadioButton("Fit");
        scalingFit.setTooltip(new Tooltip("The game will keep its aspect ratio and sit as large as it can in the window size."));
        RadioButton scalingStretch = new RadioButton("Stretch");
        scalingStretch.setTooltip(new Tooltip("The game will adapt to the window size and fill it completely."));
        choices.getToggles().addAll(scalingFit, scalingStretch);
        if(this.options.scaling == ViewportType.FIT) {
            choices.selectToggle(scalingFit);
        } else {
            choices.selectToggle(scalingStretch);
        }
        
        choices.selectedToggleProperty().addListener((value, old, neww) -> {
            if(old != neww && neww != null) {
                if(neww == scalingFit) {
                    this.options.scaling = ViewportType.FIT;
                } else {
                    this.options.scaling = ViewportType.STRETCH;
                }
            }
        });
        
        choicesBox.setAlignment(Pos.CENTER_RIGHT);
        choicesBox.setPrefHeight(20D);
        choicesBox.getChildren().addAll(scalingFit, scalingStretch);
        GridPane.setRowIndex(scalingLabel, 1);
        GridPane.setRowIndex(choicesBox, 1);
        GridPane.setColumnIndex(scalingLabel, 0);
        GridPane.setColumnIndex(choicesBox, 1);
        GridPane.setHalignment(choicesBox, HPos.RIGHT);
        GridPane.setHgrow(scalingLabel, Priority.ALWAYS);
        GridPane.setHgrow(choicesBox, Priority.NEVER);
        windowOptionsContent.getChildren().addAll(scalingLabel, choicesBox);
        
        Label fpsLabel = new Label("Framerate Cap");
        fpsLabel.setTooltip(new Tooltip("The max framerate that the game will achieve."));
        HBox slider = new HBox(4);
        slider.setAlignment(Pos.CENTER_RIGHT);
        Label valueLabel = new Label(options.frameCap + "");
        Slider fpsSlider = new Slider(30, 120, options.frameCap);
        fpsSlider.setShowTickMarks(true);
        fpsSlider.setMajorTickUnit(30);
        fpsSlider.setMinorTickCount(0);
        fpsSlider.setSnapToTicks(true);
        fpsSlider.setOrientation(Orientation.HORIZONTAL);
        fpsSlider.valueProperty().addListener((value, old, neww) -> {
            this.options.frameCap = neww.intValue();
            valueLabel.setText(neww.intValue() + "");
        });
        
        slider.getChildren().addAll(valueLabel, fpsSlider);
        
        GridPane.setRowIndex(fpsLabel, 2);
        GridPane.setRowIndex(slider, 2);
        GridPane.setColumnIndex(fpsLabel, 0);
        GridPane.setColumnIndex(slider, 1);
        GridPane.setHalignment(slider, HPos.RIGHT);
        GridPane.setHgrow(fpsLabel, Priority.ALWAYS);
        GridPane.setHgrow(slider, Priority.NEVER);
        windowOptionsContent.getChildren().addAll(fpsLabel, slider);
        
        windowOptionsContent.getRowConstraints().addAll(
                new RowConstraints(20D),
                new RowConstraints(20D),
                new RowConstraints(20D));
        windowOptionsContent.setVgap(6);
        windowOptionsContent.setPadding(new Insets(8));
        windowOptionsContainer.getContentProperty().set(windowOptionsContent);
        optionsRoot.getChildren().add(windowOptionsContainer);
        
        // ###### System options
        
        BorderedPane systemOptionsContainer = new BorderedPane("System/Misc", "Options for some internal things the system needs to consider.");
        GridPane systemOptionsContent = new GridPane();
        
        Label assetDirLabel = new Label("Asset Folder");
        ToggleGroup dirChoice = new ToggleGroup();
        HBox dirChoiceBox = new HBox(4);
        HBox dirSetBox = new HBox(4);
        
        dirChoiceBox.setAlignment(Pos.CENTER_RIGHT);
        dirSetBox.setAlignment(Pos.CENTER_RIGHT);
        
        RadioButton jarDirectory = new RadioButton("Jar Folder");
        RadioButton setDirectory = new RadioButton("Choose Folder");
        TextField directory = new TextField();
        Button setDir = new Button("...");
        
        assetDirLabel.setTooltip(new Tooltip("The folder where all the fangame's stuff is. (it has the main.lua)"));
        jarDirectory.setTooltip(new Tooltip("Use the folder the .jar file is in as the asset folder."));
        setDirectory.setTooltip(new Tooltip("Use the folder selected below."));
        directory.setTooltip(new Tooltip("The path to the folder to use as the asset folder, if we choose to use it."));
        directory.setEditable(false);
        
        HBox.setHgrow(directory, Priority.ALWAYS);
        dirSetBox.setMaxHeight(20);
        
        dirSetBox.getChildren().addAll(directory, setDir);
        dirChoice.getToggles().addAll(jarDirectory, setDirectory);
        dirChoiceBox.getChildren().addAll(jarDirectory, setDirectory);
        
        directory.setText(this.options.assetDir.getAbsolutePath());
        Platform.runLater(() -> directory.positionCaret(directory.getText().length()));
        directory.caretPositionProperty().addListener((value, old, neww) -> {
            if(!directory.isFocused()) {
                directory.positionCaret(directory.getText().length());
            }
        });
        
        if(this.options.useCustomDir) {
            dirChoice.selectToggle(setDirectory);
            setDir.setDisable(false);
        } else {
            dirChoice.selectToggle(jarDirectory);
            setDir.setDisable(true);
        }
        
        dirChoice.selectedToggleProperty().addListener((value, old, neww) -> {
            this.options.useCustomDir = neww == setDirectory;
            setDir.setDisable(!this.options.useCustomDir);
        });
        
        setDir.setOnMouseReleased(event -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Open");
            chooser.setInitialDirectory(this.options.assetDir);
            File selected = chooser.showDialog(this.getWindow());
            if(selected != null) {
                this.options.assetDir = selected;
                directory.setText(this.options.assetDir.getAbsolutePath());
                Platform.runLater(() -> directory.positionCaret(directory.getText().length()));
            }
        });
        
        GridPane.setRowIndex(assetDirLabel, 0);
        GridPane.setRowIndex(dirChoiceBox, 0);
        GridPane.setRowIndex(dirSetBox, 1);
        GridPane.setColumnIndex(assetDirLabel, 0);
        GridPane.setColumnIndex(dirChoiceBox, 1);
        GridPane.setColumnIndex(dirSetBox, 0);
        GridPane.setHgrow(assetDirLabel, Priority.ALWAYS);
        GridPane.setHalignment(dirChoiceBox, HPos.RIGHT);
        GridPane.setColumnSpan(dirSetBox, 2);
        GridPane.setHgrow(dirSetBox, Priority.ALWAYS);
        GridPane.setVgrow(dirSetBox, Priority.NEVER);
        systemOptionsContent.getChildren().addAll(assetDirLabel, dirChoiceBox, dirSetBox);
        
        Separator separator = new Separator(Orientation.HORIZONTAL);
        separator.setTooltip(new Tooltip("wink wonk"));
        
        GridPane.setRowIndex(separator, 2);
        GridPane.setColumnSpan(separator, GridPane.REMAINING);
        systemOptionsContent.getChildren().add(separator);
        
        CheckBox showDebug = new CheckBox("Show debug messages");
        CheckBox skipLauncher = new CheckBox("Skip launcher");
        
        showDebug.setTooltip(new Tooltip("Whether or not to show messages tagged [DEBUG] in the logging console."));
        skipLauncher.setTooltip(new Tooltip("Whether or not to skip the launcher when opening the game.\n"
                                          + "You can cancel this feature by pressing F12 in-game."));
        
        showDebug.setSelected(this.options.debug);
        showDebug.selectedProperty().addListener((value, old, neww) -> {
            this.options.debug = neww;
        });
        
        skipLauncher.setSelected(this.options.skipLauncher);
        skipLauncher.selectedProperty().addListener((value, old, neww) -> {
            this.options.skipLauncher = neww;
        });
        
        GridPane.setRowIndex(showDebug, 3);
        GridPane.setRowIndex(skipLauncher, 4);
        GridPane.setColumnSpan(showDebug, GridPane.REMAINING);
        GridPane.setColumnSpan(skipLauncher, GridPane.REMAINING);
        systemOptionsContent.getChildren().addAll(showDebug, skipLauncher);
        
        systemOptionsContent.getRowConstraints().addAll(
                new RowConstraints(20D),
                new RowConstraints(20D),
                new RowConstraints(20D),
                new RowConstraints(20D),
                new RowConstraints(20D));
        systemOptionsContent.setVgap(6);
        systemOptionsContent.setPadding(new Insets(8));
        systemOptionsContainer.getContentProperty().set(systemOptionsContent);
        optionsRoot.getChildren().add(systemOptionsContainer);
        
        return tab;
    }
}
