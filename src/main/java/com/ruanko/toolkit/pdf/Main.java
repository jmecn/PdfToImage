package com.ruanko.toolkit.pdf;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruanko.toolkit.pdf.table.PdfFile;
import com.ruanko.toolkit.pdf.table.PdfList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	private static Logger logger = LoggerFactory.getLogger(Main.class);

	// Resource bundle for i18n.
	private static ResourceBundle resource = ResourceBundle.getBundle("com.ruanko.toolkit.pdf.UI");

	private final static String REGISTRY_KEY = "/com/ruanko/toolkit/pdf";

	private final static int WIDTH = 800;
	private final static int HEIGHT = 600;

	private Stage primaryStage;

	private FileChooser chooser;
	private TableView<PdfFile> tableView;
	private PdfList pdfList;
	private final ObservableList<PdfFile> data = FXCollections.observableArrayList();

	private TextArea console;
	
	private Label status;
	private ProgressBar progressBar;

	@Override
	public void start(Stage stage) throws Exception {

		this.primaryStage = stage;

		pdfList = new PdfList();

		// 主菜单
		Menu fileMenu = getFileMenu();
		Menu optionsMenu = getOptionsMenu();
		Menu helpMenu = getHelpMenu();

		MenuBar menuBar = new MenuBar();
		menuBar.setUseSystemMenuBar(true);
		menuBar.setStyle("-fx-font-size: 12;");
		menuBar.getMenus().addAll(fileMenu, optionsMenu, helpMenu);

		// 工具栏
		ToolBar toolBar = getToolBar();

		// 文件选择器
		chooser = getFileChooser();

		// 文件列表
		tableView = getTableView();

		// 控制台
		console = getTextArea();

		// 状态栏
		HBox box = getStatusBar();
		
		// 表格菜单
		getContextMenu();

		BorderPane root = new BorderPane();
		BorderPane center = new BorderPane();
		root.setTop(menuBar);
		root.setCenter(center);
		root.setBottom(box);
		center.setTop(toolBar);
		center.setCenter(tableView);
		center.setBottom(console);

		Scene scene = new Scene(root, WIDTH, HEIGHT);
		stage.setScene(scene);

		stage.setTitle(resource.getString("frame.title"));

		stage.show();
	}

	/**
	 * 文件菜单
	 * 
	 * @return
	 */
	private Menu getFileMenu() {
		Menu fileMenu = new Menu("文件");
		// fileMenu.setGraphic(new ImageView(new Image("image.jpg")));

		MenuItem openItem = new MenuItem("打开");
		MenuItem pngItem = new MenuItem("PNG");
		MenuItem htmItem = new MenuItem("HTML");
		MenuItem exitItem = new MenuItem("退出");
		Menu saveAsSubMenu = new Menu("生成为");
		saveAsSubMenu.getItems().addAll(pngItem, htmItem);
		fileMenu.getItems().addAll(openItem, saveAsSubMenu, new SeparatorMenuItem(), exitItem);

		openItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
		exitItem.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.ALT_DOWN));

		openItem.setOnAction(e -> addPdfFile());
		exitItem.setOnAction(e -> Platform.exit());

		return fileMenu;
	}

	/**
	 * 选项菜单
	 * 
	 * @return
	 */
	private Menu getOptionsMenu() {
		Menu optionsMenu = new Menu("选项");

		// 输出文件夹选项
		Menu outputMenu = new Menu("输出到");
		RadioMenuItem localItem = new RadioMenuItem("本地文件夹");
		RadioMenuItem relativeItem = new RadioMenuItem("文件所在目录");
		RadioMenuItem absoluteItem = new RadioMenuItem("自定义目录");
		localItem.setSelected(true);
		
		ToggleGroup outputGroup = new ToggleGroup();
		outputGroup.getToggles().addAll(localItem, relativeItem, absoluteItem);
		
		outputMenu.getItems().addAll(localItem, relativeItem, absoluteItem);

		// 分辨率选项
		Menu resolutionMenu = new Menu("分辨率");
		RadioMenuItem ldpiItem = new RadioMenuItem("ldpi 120");
		RadioMenuItem mdpiItem = new RadioMenuItem("mdpi 160");
		RadioMenuItem hdpiItem = new RadioMenuItem("hdpi 240");
		RadioMenuItem xhdpiItem = new RadioMenuItem("xhdpi 320");
		RadioMenuItem xxhdpiItem = new RadioMenuItem("xxhdpi 480");
		RadioMenuItem customItem = new RadioMenuItem("自定义分辨率");
		mdpiItem.setSelected(true);

		ToggleGroup dpiGroup = new ToggleGroup();
		dpiGroup.getToggles().addAll(ldpiItem, mdpiItem, hdpiItem, xhdpiItem, xxhdpiItem, customItem);

		resolutionMenu.getItems().addAll(ldpiItem, mdpiItem, hdpiItem, xhdpiItem, xxhdpiItem, customItem);

		optionsMenu.getItems().addAll(outputMenu, resolutionMenu);

		return optionsMenu;
	}

	/**
	 * 帮助菜单
	 * 
	 * @return
	 */
	private Menu getHelpMenu() {
		Menu helpMenu = new Menu("帮助");

		return helpMenu;
	}

	/**
	 * 工具栏
	 * 
	 * @return
	 */
	private ToolBar getToolBar() {
		Button selectAll = new Button("全选");
		Button open = new Button("添加PDF文件");
		Button export = new Button("转换PNG");
		Button exitBtn = new Button("Exit");
		selectAll.setOnAction(e -> {
			if (pdfList.allSelected()) {
				pdfList.selectedAll(false);
			} else {
				pdfList.selectedAll(true);
			}
		});
		open.setOnAction(e -> addPdfFile());
		export.setOnAction(e -> {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					pdfList.exportAll();
				}
			});
		});
		exitBtn.setOnAction(e -> Platform.exit());

		ToolBar toolBar = new ToolBar();
		toolBar.setOrientation(Orientation.HORIZONTAL);
		toolBar.getItems().addAll(selectAll, open, export, new Separator(), exitBtn);
		return toolBar;
	}

	/**
	 * 状态栏
	 */
	private HBox getStatusBar() {
		// 状态栏
		status = new Label("就绪");
		progressBar = new ProgressBar(0);
		progressBar.setVisible(false);
		Hyperlink hyperlink = new Hyperlink("http://www.ruanko.com");
		hyperlink.setOnAction(e -> {  
            try {
				Desktop.getDesktop().browse(new URI("http://www.ruanko.com"));
			} catch (Exception ex) {
				logger.error("Error when open http://www.ruanko.com : {}", ex.getMessage(), ex);
			}
        }); 
		
		
		HBox box = new HBox();
		box.setAlignment(Pos.CENTER_RIGHT);
		box.setStyle(
				"-fx-padding: 2px;"+
				"-fx-font: 10pt \"sans-serif\";");
		box.getChildren().add(progressBar);
		box.getChildren().add(new Separator(Orientation.VERTICAL));
		box.getChildren().add(status);
		box.getChildren().add(new Separator(Orientation.VERTICAL));
		box.getChildren().add(hyperlink);
		
		return box;
	}
	/**
	 * 表格的上下文菜单
	 * 
	 * @return
	 */
	private ContextMenu getContextMenu() {
		MenuItem rectItem = new MenuItem("Rectangle");
		MenuItem circleItem = new MenuItem("Circle");
		MenuItem ellipseItem = new MenuItem("Ellipse");

		ContextMenu ctxMenu = new ContextMenu(rectItem, circleItem, ellipseItem);

		return ctxMenu;
	}

	/**
	 * 文件选择器
	 * 
	 * @return
	 */
	private FileChooser getFileChooser() {
		FileChooser chooser = new FileChooser();

		chooser.setTitle("Add PDF file");

		// 初始化文件目录
		Preferences pref = Preferences.userRoot().node(REGISTRY_KEY);
		String lastPath = pref.get("lastPath", ".");
		File folder = new File(lastPath);
		if (folder.exists() && folder.isDirectory()) {
			chooser.setInitialDirectory(folder);
		} else {
			chooser.setInitialDirectory(new File("."));
		}

		// 后缀名过滤
		chooser.getExtensionFilters().add(new ExtensionFilter("PDF Files", "*.pdf"));

		return chooser;
	}

	/**
	 * 表格视图
	 * 
	 * @return
	 */
	private TableView<PdfFile> getTableView() {
		Button btn = new Button("Add pdf files");
		btn.setOnAction(e -> addPdfFile());
		
		TableView<PdfFile> tableView = new TableView<PdfFile>();
		tableView.setItems(data);
		tableView.setPlaceholder(btn);
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		TableColumn<PdfFile, CheckBox> selectColumn = new TableColumn<>("选择");
		selectColumn.setCellValueFactory(new PropertyValueFactory<>("select"));
		selectColumn.setStyle("-fx-alignment:CENTER");
		selectColumn.setPrefWidth(32);
		selectColumn.setMinWidth(32);
		selectColumn.setMaxWidth(32);
		
		TableColumn<PdfFile, String> nameColumn = new TableColumn<>("文件名");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameColumn.setPrefWidth(200);
		nameColumn.setMinWidth(200);
		
		TableColumn<PdfFile, String> fileSizeColumn = new TableColumn<>("文件大小");
		fileSizeColumn.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
		fileSizeColumn.setStyle("-fx-alignment:CENTER_RIGHT");
		fileSizeColumn.setPrefWidth(80);
		fileSizeColumn.setMinWidth(80);
		fileSizeColumn.setMaxWidth(80);
		
		TableColumn<PdfFile, Integer> pageCountColumn = new TableColumn<>("总页数");
		pageCountColumn.setCellValueFactory(new PropertyValueFactory<>("pageCount"));
		pageCountColumn.setStyle("-fx-alignment:CENTER_RIGHT");
		pageCountColumn.setMinWidth(60);
		pageCountColumn.setMaxWidth(60);
		
		TableColumn<PdfFile, String> stateColumn = new TableColumn<>("状态");
		stateColumn.setCellValueFactory(new PropertyValueFactory<>("busy"));
		stateColumn.setStyle("-fx-alignment:CENTER");
		stateColumn.setPrefWidth(80);
		stateColumn.setMinWidth(80);
		stateColumn.setMaxWidth(80);
		
		TableColumn<PdfFile, Button> openColumn = new TableColumn<>("打开");
		openColumn.setCellValueFactory(new PropertyValueFactory<>("open"));
		openColumn.setStyle("-fx-alignment:CENTER");
		openColumn.setPrefWidth(80);
		openColumn.setMinWidth(80);
		openColumn.setMaxWidth(80);
		
		TableColumn<PdfFile, Button> exportColumn = new TableColumn<>("转换png");
		exportColumn.setCellValueFactory(new PropertyValueFactory<>("export"));
		exportColumn.setStyle("-fx-alignment:CENTER");
		exportColumn.setPrefWidth(80);
		exportColumn.setMinWidth(80);
		exportColumn.setMaxWidth(80);
		
		tableView.getColumns().add(selectColumn);
		tableView.getColumns().add(nameColumn);
		tableView.getColumns().add(fileSizeColumn);
		tableView.getColumns().add(pageCountColumn);
		tableView.getColumns().add(stateColumn);
		tableView.getColumns().add(openColumn);
		tableView.getColumns().add(exportColumn);
		
		// 支持拖拽
		// 拖入文件
		tableView.setOnDragOver(new EventHandler<DragEvent>() { //node添加拖入文件事件
			public void handle(DragEvent event) {
				Dragboard dragboard = event.getDragboard(); 
				if (dragboard.hasFiles()) {
					event.acceptTransferModes(TransferMode.COPY_OR_MOVE);//接受拖入文件
//					File file = dragboard.getFiles().get(0);
//					if (file.getAbsolutePath().endsWith(".pdf")) { //用来过滤拖入类型
//					}
//					System.out.println("drag over:" + file);
				}

			}
		});
		// 释放文件
		tableView.setOnDragDropped(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				Dragboard dragboard = event.getDragboard();
				
				if (dragboard.hasFiles()) {
					List<File> files = dragboard.getFiles();
					
					startLoadingTask(files);
				}
			}

		});
		
		return tableView;
	}

	private TextArea getTextArea() {
		// 用自己的重载的OutputStream创建一个PrintStream
		PrintStream printStream = new PrintStream(new MyOutputStream());
		// 指定标准输出到自己创建的PrintStream
		System.setOut(printStream);
		System.setErr(printStream);

		TextArea console = new TextArea();
		console.setWrapText(true);
		console.setEditable(false);
		console.setStyle("-fx-font-size:14");

		return console;
	}

	/**
	 * 将数据输出到一个TextArea中。
	 * 
	 * @author yanmaoyuan
	 *
	 */
	public class MyOutputStream extends OutputStream {
		public void write(int arg0) throws IOException {
			// 写入指定的字节，忽略
		}

		public void write(byte data[]) throws IOException {
			// 追加一行字符串
			console.appendText(new String(data));
		}

		public void write(byte data[], int off, int len) throws IOException {
			// 追加一行字符串中指定的部分，这个最重要
			console.appendText(new String(data, off, len));
		}
	}

	/**
	 * 打开文件选择框，添加向列表中添加pdf文件。
	 */
	private void addPdfFile() {
		// Show a file open dialog to select multiple files
		List<File> files = chooser.showOpenMultipleDialog(primaryStage);
		if (files != null) {
			// 记忆用户最后选择的文件路径。
			Preferences pref = Preferences.userRoot().node(REGISTRY_KEY);
			String lastPath = files.get(0).getParent();
			pref.put("lastPath", lastPath);

			startLoadingTask(files);
			
		} else {
			logger.info("No files were selected.");
		}

	}
	
	/**
	 * 启动一个装载文件任务
	 */
	private void startLoadingTask(List<File> files) {
		
		Task<Void> task = new Task<Void> () {
			
			@Override
			protected void running() {
				super.running();
				progressBar.setVisible(true);
			}

			@Override
			protected void succeeded() {
				super.succeeded();
				progressBar.setVisible(false);
				progressBar.progressProperty().unbind();
			}

			@Override
			protected Void call() throws Exception {
				updateMessage("加载中..");
				
				List<File> pdfs = pdfList.filter(files);
				
				int len = pdfs.size();
				for(int i=0; i<len; i++) {
					File file = pdfs.get(i);
					pdfList.add(file);
					data.add(pdfList.get(file));
					updateProgress(i+1, len);
					updateMessage("加载中:" + (i+1) + "/" + len);
				}
				updateMessage("就绪");
				return null;
			}
		};
		
		status.textProperty().bind(task.messageProperty());
		
		progressBar.progressProperty().bind(task.progressProperty());
		
		new Thread(task).start();
	}
}
