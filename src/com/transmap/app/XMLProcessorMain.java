package com.transmap.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.FileSystemLoopException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.FilenameUtils;

public final class XMLProcessorMain extends Application {

	private static final int width = 400;
	private static final int height = 400;

	private static SingleSelectionModel<Tab> selectionModel = null;
	
	private static final String fileSep = System.getProperty("file.separator");

	private static final String DATA_FILE = "raw.csv";

	// dialog
	private Stage dialogStage = null;
	private static final Text dialogMsg = new Text();

	// objects
	private static final TextField txtSaveDir = new TextField();
	private static final TextArea txtArea = new TextArea();
	private static final ProgressBar pbar = new ProgressBar();
	private static final ListView<String> listView = new ListView<String>();
	private static final DirectoryChooser dirChooser = new DirectoryChooser();
	private static final DirectoryChooser dirSave = new DirectoryChooser();

	// buttons
	private static final Button openMultipleButton = new Button(
			"Choose XML Files");
	private static final Button processBtn = ButtonBuilder.create()
			.text("Process Files").id("button1").build();
	private static final Button openSaveLocButton = new Button("Save Directory");
	private static final CheckBox iriMath = new CheckBox("Calculate IRI");
	private static final CheckBox rutInfo = new CheckBox("Include Rut Data");

	// pane
	private static final BorderPane borderPane = new BorderPane();
	private static final TabPane tabPane = new TabPane();
	private static final Tab tab1 = new Tab();
	private static final Tab tab2 = new Tab();
	private static final Tab tab3 = new Tab();
	private static final GridPane processPane = new GridPane();
	private static final GridPane filePane = new GridPane();
	private static final GridPane configPane = new GridPane();
	
	//label
    private static final Label configLabel = LabelBuilder.create().text("Config Options").id("configLabel").build();

	static {
		// progress bar
		pbar.setPrefHeight(30);
		pbar.setPrefWidth(width);
		pbar.setLayoutX(215);
		pbar.setProgress(0);

		// info about the processing
		txtArea.setPrefWidth(width);
		txtArea.setPrefHeight(height - 50);
		
		//save dir
		txtSaveDir.setPrefWidth(width-100);
		txtSaveDir.setText(System.getProperty("user.dir"));

		// the text field holding the file location
		listView.setPrefHeight(height - 75);
		listView.setPrefWidth(width);

		// the tabs
		tab1.setText("Select Files");
		tab1.setId("files");
		tab2.setText("Processing Files");
		tab2.setId("process");
		tab3.setText("Configuration");

		tabPane.setPrefSize(width, height);
		tabPane.setSide(Side.TOP);
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
		tabPane.getTabs().addAll(tab1, tab2, tab3);
		borderPane.setCenter(tabPane);

		selectionModel = tabPane.getSelectionModel();

		processPane.setPadding(new Insets(5));
		processPane.setHgap(2);
		processPane.setVgap(2);

		filePane.setPadding(new Insets(5));
		filePane.setHgap(2);
		filePane.setVgap(2);
		
		configPane.setPadding(new Insets(5));
		configPane.setHgap(2);
		configPane.setVgap(2);
		
		iriMath.setId("configMath");
		iriMath.setSelected(true);
		
		rutInfo.setId("rutData");
		rutInfo.setSelected(true);

	}

	private static void setupTabSelctionListener() {
		selectionModel.selectedItemProperty().addListener(
				new ChangeListener<Tab>() {
					@Override
					public void changed(ObservableValue<? extends Tab> ov,
							Tab t, Tab t1) {
						if ("files".equals(t.getId())) {

						}

					}
				});
	}
	
	private static void setupLocationButton(final Stage stage){
		openSaveLocButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				File temp = dirSave.showDialog(stage);
				if (temp == null)
					return;
				
				txtSaveDir.setText(temp.toString());
				
			}
		});
	}

	private static void setupFileButton(final Stage stage) {
		openMultipleButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				File dir = dirChooser.showDialog(stage);
				if (dir == null)
					return;

				List<File> list = null;

				try {
					list = Utils.scanDir2(dir);
				} catch (FileSystemLoopException ex) {
					String msg = "Directory depth too deep.  Max 4 directories subdirectories.";
					listView.setItems(FXCollections
							.observableArrayList(new String[] { msg }));
					return;
				}

				List<String> files = new ArrayList<String>();
				if (list != null) {
					for (File file : list)
						files.add(file.toString());
				}
				listView.setItems(FXCollections.observableArrayList(files));
			}
		});
	}

	private void setupProcessButton() {
		processBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent evt) {
				txtArea.setText("Processing...");
				pbar.setProgress(0);
				tab2.setDisable(false);
				selectionModel.select(tab2);

				// anonymouss thread to run this in
				(new Thread() {
					public void run() {
						ObservableList<String> ol = listView.getItems();

						Map<String, String> errors = new HashMap<String, String>();
						int fileCount = 0;
						double count = 1;
						double size = ol.size();
						
						//iterate over directories
						for (String dname : ol) {
							File dir = new File(dname);

							if (!dir.exists()) {
								errors.put(dname, "Directory does not exist");
								continue;
							}

							List<File> fileList = null;
							try {
								fileList = Utils.scanDir(dir);
							} catch (FileSystemLoopException e2) {
								// TODO Auto-generated catch block
								msgBox("ABORTED!\nERROR: \n" + e2.getMessage());
								txtArea.setText("ABORTED!\nERROR:\n" + e2.getMessage());
								return;
							}
								
							fileCount+=fileList.size();
							
							int lastSepIndex = FilenameUtils.indexOfLastSeparator(dname);
							String base = dname.substring(lastSepIndex+1);
							String saveLocation = txtSaveDir.getText();
							String saveFile = (saveLocation.isEmpty()?"":saveLocation+fileSep);
							
							//verify we can save to this location
							File test = new File(saveFile);
							if(!test.isDirectory() || !test.canWrite()){
								msgBox("ABORTED!\nCannot write to location: "+saveFile);
								return;
							}	
							
							saveFile = saveFile+ base+"-"+((int)count)+"-"+DATA_FILE;
							saveFile = saveFile.replace(" ", "_");
							
							//delete the file if it exists
							File temp = new File(saveFile);
							if (temp.exists()){
								if(!temp.canWrite()){
									msgBox("ABORTED!\nCannot write to file: "+temp.toString());
									return;
								}
								temp.delete();
							}
							
							PrintWriter writer = null;
							try {										
								writer = new PrintWriter(new BufferedWriter(
										new FileWriter(saveFile, true)));
								
								writer.print(Utils.HEADER);
								
								if(iriMath.isSelected())
									writer.print(Utils.HEADER_EXT);
								
								if(rutInfo.isSelected())
									writer.print(Utils.HEADER_EXT2);
								
								writer.print("\n");
								
							} catch (IOException e1) {
								msgBox("ABORTED!\nERROR: \n" + e1.getMessage());
								txtArea.setText("ABORTED!\nERROR:\n" + e1.getMessage());
								
								if(writer != null) writer.close();
								
								return;
							}
							
							//process the files
							for(File f : fileList){
								try{
									process(f, writer);
								} catch (Exception e) {
									errors.put(f.toString(), e.getMessage());
								} 
							}
							
							writer.close();
							
							pbar.setProgress(count / size);
							count++;
							
						}

						// print errors
						if (errors.size() == 0) {
							txtArea.setText("Complete with no Errors.  Processed "
									+ fileCount
									+ " files.\nSee "
									+ DATA_FILE
									+ " for results");
						} else {
							StringBuilder msg = new StringBuilder();
							msg.append("Complete with errors\nError Count: "
									+ errors.size() + "\n");
							//msg.append("See " + DATA_FILE + " for results\n");
							for (Entry<String, String> info : errors.entrySet())
								msg.append(info.getKey() + " - "
										+ info.getValue() + "\n");
							txtArea.setText(msg.toString());
						}
					}
				}).start();
			}
		});
	}

	@Override
	public void start(final Stage stage) {
		stage.setTitle("TransMap.com XML Parser");

		// add the tab listener
		setupTabSelctionListener();

		// setup buttons
		setupProcessButton();
		setupFileButton(stage);
		setupLocationButton(stage);

		GridPane.setConstraints(openSaveLocButton, 0,0);
		GridPane.setConstraints(txtSaveDir,1,0);
		GridPane.setConstraints(configLabel,0,3);
		GridPane.setConstraints(iriMath,0,4);
		GridPane.setConstraints(rutInfo,0,6);
		
		GridPane.setColumnSpan(configLabel,2);
		GridPane.setColumnSpan(iriMath,2);
		GridPane.setColumnSpan(rutInfo,2);
		
		GridPane.setConstraints(pbar, 0, 0);
		GridPane.setConstraints(txtArea, 0, 1);
		
		GridPane.setConstraints(openMultipleButton, 0, 0);
		GridPane.setConstraints(processBtn, 1, 0);
		GridPane.setConstraints(listView, 0, 1);
		GridPane.setColumnSpan(listView, 3);

		tab1.setContent(filePane);
		tab2.setContent(processPane);
		tab3.setContent(configPane);

		filePane.getChildren().add(openMultipleButton);
		filePane.getChildren().add(listView);
		filePane.getChildren().add(processBtn);
		
		configPane.getChildren().add(openSaveLocButton);
		configPane.getChildren().add(txtSaveDir);
		configPane.getChildren().add(iriMath);
		configPane.getChildren().add(rutInfo);
		configPane.getChildren().add(configLabel);

		processPane.getChildren().add(pbar);
		processPane.getChildren().add(txtArea);

		final Pane rootGroup = new VBox(12);
		rootGroup.getChildren().addAll(tabPane);
		rootGroup.setPadding(new Insets(12, 12, 12, 12));

		Scene scene = new Scene(rootGroup);
		scene.getStylesheets().add("style.css");

		stage.setScene(scene);
		stage.show();

		// dialog
		dialogStage = new Stage();
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.setScene(new Scene(VBoxBuilder.create().children(dialogMsg)
				.alignment(Pos.CENTER).padding(new Insets(25)).build()));

		// things don't work quite right with java 1.8
		if (System.getProperty("java.version").startsWith("1.8")) {
			msgBox("Warning.  Java 1.8 has known bugs.");
		}
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	private void msgBox(String msg) {
		dialogMsg.setText(msg);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				dialogStage.show();
			}
		});

	}

	private void process(File file, PrintWriter writer) throws IOException,
			XMLStreamException {
		String fileName = file.getName();
		String surveyPath = "?";
		Map<String, String> info = Collections.emptyMap();
		Map<String, String> pinfo = Collections.emptyMap();
		List<Map<String, String>> gps = new ArrayList<Map<String, String>>();
		List<RoughnessBean> roughness = null;
		RutBean[] rutData = null;
		boolean iriMathVal = iriMath.isSelected();
		boolean rutDataVal = rutInfo.isSelected();

		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		// Setup a new eventReader
		InputStream in = new FileInputStream(file);
		XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

		// read forward till we hit the "RoadSectionInfo" element
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			if (event.isStartElement()) {
				if (Utils.isEqual(Survey.PROCESSINFO, event)){
					pinfo = Survey.processProcessingInfo(eventReader);
				} else if (Utils.isEqual(Survey.ROADSECINFO, event)) {
					info = Survey.processRoadSectionInfo(eventReader);
				} else if (Utils.isEqual(Survey.GPSCOORD, event)) {
					gps.add(Survey.processGPSCoordinate(eventReader));
				} else if (Utils.isEqual(Survey.SURVEYPATH, event)) {
					surveyPath = eventReader.getElementText();
				} else if (iriMathVal && Utils.isEqual(Survey.ROUGHNESSINFO, event)){
					roughness = Survey.processRoughnessInfo(eventReader);
				} else if(rutDataVal && Utils.isEqual(Survey.RUTINFO, event)){
					rutData = Survey.processRutMeasurement(eventReader);
				}
			}
		}

		/*
		if (gps.size() == 0)
			throw new IOException("No Longitude or Latitude");
		*/

		Map<String, String> temp = null;
		if(gps != null && gps.size() > 0){
			temp = gps.get(0);
			if (!temp.containsKey(Survey.LON) || !temp.containsKey(Survey.LAT))
				throw new IOException("No Longitude or Latitude");
		}

		/*
		if(iriMathVal && roughness == null)
			throw new IOException("No Roughness found");
		
		if(iriMathVal && roughness.size() != 2)
			throw new IOException("Expected Roughness count of 2, but had "+roughness.size());
		
		if(rutData == null)
			throw new IOException("Expected Rut data, but none found");
		*/
		
		// save the data
		Utils.saveData(writer, iriMathVal, rutDataVal, fileName, surveyPath, info, pinfo, temp, roughness, rutData);
	}

}