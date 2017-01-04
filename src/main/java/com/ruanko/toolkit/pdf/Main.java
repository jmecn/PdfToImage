package com.ruanko.toolkit.pdf;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
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

	private ExecutorService executor = null;

	private Stage primaryStage;
	private FileChooser chooser;

	private PdfList pdfList;
	private TableView<PdfFile> tableView;

	// 状态栏
	private Label status;
	private ProgressBar progressBar;

	@Override
	public void init() throws Exception {
		executor = Executors.newSingleThreadExecutor(new DaemonThreadFactory());

	}

	@Override
	public void stop() throws Exception {
		executor.shutdown();
		executor = null;
	}

	@Override
	public void start(Stage stage) throws Exception {

		this.primaryStage = stage;

		pdfList = new PdfList();

		// 工具栏
		ToolBar toolBar = getToolBar();

		// 文件选择器
		chooser = getFileChooser();

		// 文件列表
		tableView = getTableView();

		// 状态栏
		HBox box = getStatusBar();

		// 表格菜单
		getContextMenu();

		BorderPane root = new BorderPane();
		root.setTop(toolBar);
		root.setCenter(tableView);
		root.setBottom(box);

		Scene scene = new Scene(root, 1024, 768);
		stage.setScene(scene);

		stage.setTitle(resource.getString("frame.title"));

		stage.show();
	}

	/**
	 * 工具栏
	 * 
	 * @return
	 */
	private ToolBar getToolBar() {
		Button selectAll = new Button("全选");
		Button open = new Button("添加PDF文件");
		Button export = new Button("转换");
		selectAll.setOnAction(e -> {
			if (pdfList.allSelected()) {
				pdfList.selectedAll(false);
			} else {
				pdfList.selectedAll(true);
			}
		});
		open.setOnAction(e -> addPdfFile());

		ComboBox<String> resolution = new ComboBox<String>();
		resolution.getItems().addAll("ldpi 120", "mdpi 160", "hdpi 240", "xhdpi 320", "xxhdpi 480");
		switch (Settings.get().getResolution()) {
		case ldpi:
			resolution.getSelectionModel().select(0);
			break;
		case mdpi:
			resolution.getSelectionModel().select(1);
			break;
		case hdpi:
			resolution.getSelectionModel().select(2);
			break;
		case xhdpi:
			resolution.getSelectionModel().select(3);
			break;
		case xxhdpi:
			resolution.getSelectionModel().select(4);
			break;
		}
		resolution.setOnAction(e -> {
			int index = resolution.getSelectionModel().getSelectedIndex();
			Settings.get().setResolution(DPI.values()[index]);
		});

		ComboBox<String> output = new ComboBox<String>();
		output.getItems().addAll("本地文件夹", "PDF文件所在位置");
		switch (Settings.get().getOutputModel()) {
		case Local:
			output.getSelectionModel().select(0);
			break;
		case Relative:
			output.getSelectionModel().select(1);
			break;
		}
		output.setOnAction(e -> {
			int index = output.getSelectionModel().getSelectedIndex();
			Settings.get().setOutputModel(OutputModel.values()[index]);
		});

		export.setOnAction(e -> exportAll());

		ToolBar toolBar = new ToolBar();
		toolBar.setOrientation(Orientation.HORIZONTAL);
		toolBar.getItems().addAll(selectAll, new Separator(), open, new Separator(), new Label("分辨率:"),
				resolution, new Separator(), new Label("输出到:"), output, new Separator(), export);
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
		box.setStyle("-fx-padding: 2px;" + "-fx-font: 10pt \"sans-serif\";");
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
		String lastPath = Settings.get().getLastPath();
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
		Button btn = new Button("添加文件");
		btn.setOnAction(e -> addPdfFile());

		TableView<PdfFile> tableView = new TableView<PdfFile>();
		tableView.setEditable(true);
		tableView.setItems(pdfList.getData());
		tableView.setPlaceholder(btn);
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TableColumn<PdfFile, Boolean> selectColumn = new TableColumn<>("选择");
		selectColumn.setCellValueFactory(new PropertyValueFactory<>("select"));
		selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
		selectColumn.setStyle("-fx-alignment:CENTER");
		selectColumn.setEditable(true);
		selectColumn.setSortable(false);
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
		fileSizeColumn.setSortable(false);
		fileSizeColumn.setPrefWidth(80);
		fileSizeColumn.setMinWidth(80);
		fileSizeColumn.setMaxWidth(80);

		TableColumn<PdfFile, Integer> pageCountColumn = new TableColumn<>("总页数");
		pageCountColumn.setCellValueFactory(new PropertyValueFactory<>("pageCount"));
		pageCountColumn.setStyle("-fx-alignment:CENTER_RIGHT");
		pageCountColumn.setMinWidth(60);
		pageCountColumn.setMaxWidth(60);

		TableColumn<PdfFile, String> stateColumn = new TableColumn<>("状态");
		stateColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
		stateColumn.setStyle("-fx-alignment:CENTER");
		stateColumn.setPrefWidth(80);
		stateColumn.setMinWidth(80);
		stateColumn.setMaxWidth(80);

		TableColumn<PdfFile, Double> openColumn = new TableColumn<>("进度");
		openColumn.setCellValueFactory(new PropertyValueFactory<>("progress"));
		openColumn.setCellFactory(ProgressBarTableCell.forTableColumn());
		openColumn.setStyle("-fx-alignment:CENTER");
		openColumn.setPrefWidth(80);
		openColumn.setMinWidth(80);
		openColumn.setMaxWidth(80);

		tableView.getColumns().add(selectColumn);
		tableView.getColumns().add(nameColumn);
		tableView.getColumns().add(fileSizeColumn);
		tableView.getColumns().add(pageCountColumn);
		tableView.getColumns().add(stateColumn);
		tableView.getColumns().add(openColumn);

		/**
		 * 支持快捷键
		 */
		tableView.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				PdfFile pdfFile = tableView.getSelectionModel().getSelectedItem();
				switch (event.getCode()) {
				case SPACE: {
					if (pdfFile != null)
						pdfFile.setSelect(!pdfFile.getSelect());
					break;
				}
				case DELETE: {
					pdfList.delete(pdfFile);
					break;
				}
				default:
					// do nothing
					return;
				}
			}
		});

		/**
		 * 支持拖拽
		 */
		// 拖入文件
		tableView.setOnDragOver(new EventHandler<DragEvent>() { // node添加拖入文件事件
			public void handle(DragEvent event) {
				Dragboard dragboard = event.getDragboard();
				if (dragboard.hasFiles()) {
					event.acceptTransferModes(TransferMode.COPY_OR_MOVE);// 接受拖入文件
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

	/**
	 * 打开文件选择框，添加向列表中添加pdf文件。
	 */
	private void addPdfFile() {
		// Show a file open dialog to select multiple files
		List<File> files = chooser.showOpenMultipleDialog(primaryStage);
		if (files != null) {
			// 记忆用户最后选择的文件路径。
			Settings.get().setLastPath(files.get(0).getParent());

			startLoadingTask(files);

		} else {
			logger.info("No files were selected.");
		}

	}

	/**
	 * 启动一个装载文件任务
	 */
	private void startLoadingTask(List<File> files) {
		LoadingTask loading = new LoadingTask(files);
		status.textProperty().bind(loading.messageProperty());
		progressBar.progressProperty().bind(loading.progressProperty());
		executor.submit(loading);
	}

	/**
	 * 导出全部被选中的文件为png
	 */
	public void exportAll() {
		int row = pdfList.size();
		for (int i = 0; i < row; i++) {
			PdfFile pdfFile = pdfList.get(i);
			if (pdfFile.getSelect()) {
				process(pdfFile);
			}
		}
	}

	/**
	 * 导出Pdf文件
	 * 
	 * @param pdfFile
	 */
	public void process(PdfFile pdfFile) {
		if (pdfFile.getState() != State.Ready) {
			return;
		}

		ProcessTask task = new ProcessTask(pdfFile);

		pdfFile.setState(State.Waiting);
		pdfFile.progressProperty().unbind();
		pdfFile.progressProperty().bind(task.progressProperty());
		pdfFile.statusProperty().unbind();
		pdfFile.statusProperty().bind(task.messageProperty());

		executor.submit(task);
	}

	/**
	 * 加载PDF文件
	 * 
	 * @author yanmaoyuan
	 * @param <V>
	 */
	public class LoadingTask extends Task<Void> {

		List<File> files;

		public LoadingTask(List<File> files) {
			this.files = files;
		}

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
			updateProgress(0, 100);
			updateMessage("加载中..");

			// 过滤pdf文件
			List<File> pdfs = pdfList.filter(files);

			int len = pdfs.size();
			for (int i = 0; i < len; i++) {

				if (isCancelled()) {
					updateMessage("Cancelled");
					break;
				}

				File file = pdfs.get(i);
				pdfList.add(file);
				updateProgress(i + 1, len);
				updateMessage("加载中:" + (i + 1) + "/" + len);
			}
			updateMessage("就绪");
			return null;
		}

	}
}
