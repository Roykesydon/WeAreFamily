package sample.controller.posts;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.Main;
import sample.global.GlobalVariable;
import sample.tool.response.detailCard.MakeNewPostResponse;
import sample.tool.RequestController;
import sample.tool.ToastCaller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MakeNewPostController implements Initializable {
    public Label primaryMakeLabel;
    public Label secondaryCate;
    public Label secondaryPrice;
    public JFXButton primarySubmitButton;
    public VBox box;
    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    public TextField priceTextField;
    public ComboBox categoryComboBox;
    public Label makeNewPostResult;

    public ProgressIndicator loading;

    private static final String regexPrice = "^[0-9]+.?[0-9]+$";
    private static  final String regexIntPrice="[0-9]+";
    private Pattern pattern;
    private Matcher matcher;

    public void initialize(URL url, ResourceBundle rb) {
        loading.setVisible(false);
        primaryMakeLabel.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-font-size:53;");
        secondaryPrice.setStyle("-fx-text-fill: "+GlobalVariable.secondaryColor+";-fx-font-size:31;");
        secondaryCate.setStyle("-fx-text-fill: "+GlobalVariable.secondaryColor+";-fx-font-size:31;");
        primarySubmitButton.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-border-color: "+GlobalVariable.primaryColor+";-fx-font-size:23;");
        try {
            box.getChildren().add(FXMLLoader.load(getClass().getResource("/sample/view/fxml/sidePanel/SidePanel.fxml")));
        }catch (IOException ex){
            Logger.getLogger(ManagePostController.class.getName()).log(Level.SEVERE,null,ex);
        }

        categoryComboBox.getItems().addAll("Spotify","NintendoSwitchOnline","YoutubePremium","Netflix","AppleMusic");
    }

    public void makeNewPostRequest(ActionEvent actionEvent) throws IOException
    {

        boolean success = true;
        //verify submit form
        //???????????????
        try
        {
            this.pattern = Pattern.compile(regexPrice);
            this.matcher = this.pattern.matcher(priceTextField.getText());
            if(this.matcher.matches()) {
                this.pattern = Pattern.compile(regexIntPrice);
                this.matcher = this.pattern.matcher(priceTextField.getText());
                if(this.matcher.matches()) {
                    int priceInt = Integer.parseInt(priceTextField.getText());
                    if (priceInt > 10000) {
                        success = false;
                        ToastCaller toast = new ToastCaller("??????????????????", GlobalVariable.mainStage, ToastCaller.ERROR);
                    } else if (priceInt == 0) {
                        success = false;
                        ToastCaller toast = new ToastCaller("??????????????????", GlobalVariable.mainStage, ToastCaller.ERROR);
                    }
                }
                else{
                    success = false;
                    ToastCaller toast = new ToastCaller("?????????????????????",GlobalVariable.mainStage,ToastCaller.ERROR);
                }
            }
            else {
                success = false;
                ToastCaller toast = new ToastCaller("???????????????????????????",GlobalVariable.mainStage,ToastCaller.ERROR);
            }
        }
        catch(NumberFormatException e){
            success = false;
            ToastCaller toast = new ToastCaller("??????????????????", GlobalVariable.mainStage, ToastCaller.ERROR);
        }
        //?????????????????? ??????????????? ??????????????????????????????(????????????)
        if(categoryComboBox.getValue() == null || priceTextField.getText().equals("")){
            success = false;
            ToastCaller toast = new ToastCaller("????????????????????????",GlobalVariable.mainStage,ToastCaller.ERROR);
        }

        //?????????????????????
        if(success){
            new Thread(new Runnable() {
                public void run() {
                    MakeNewPostController.this.loading.setVisible(true);
                    try {
                        String category = categoryComboBox.getValue().toString();
                        String price = priceTextField.getText();

                        HttpResponse response = RequestController.post(GlobalVariable.server+"posts/createPost",
                                new String[]{"accessKey", GlobalVariable.accessKey},
                                new String[]{"category", category},
                                new String[]{"price", price}
                        );
                        String responseString = EntityUtils.toString(response.getEntity());

                        String errorsResult = "";
                        int errorsResultCount = 0;

                        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                            Gson gson = new Gson();
                            MakeNewPostResponse jsonResponse = gson.fromJson(responseString, MakeNewPostResponse.class);

                            errorsResult = "";
                            errorsResultCount=0;
                            for(String error:jsonResponse.errors){
                                if(errorsResultCount != 0)
                                    errorsResult += " , ";

                                errorsResult += error;
                                errorsResultCount++;
                                System.out.print(',' + error);
                                if(error.equals("can't create more Post")){
                                    ToastCaller toast = new ToastCaller("???????????????????????????",GlobalVariable.mainStage,ToastCaller.ERROR);
                                }
                                if(error.equals("createPost fail")){
                                    ToastCaller toast = new ToastCaller("???????????????",GlobalVariable.mainStage,ToastCaller.ERROR);
                                }
                            }

                            System.out.println();

                            if(jsonResponse.errors.length==0){
                                ToastCaller toast = new ToastCaller("????????????",GlobalVariable.mainStage,ToastCaller.SUCCESS);
                            }
                        } else {
                            System.out.println(response.getStatusLine());
                        }
                    }
                    catch (IOException  e) {
                        e.printStackTrace();
                    }
                    MakeNewPostController.this.loading.setVisible(false);
                }
            }).start();
        }
    }

}
