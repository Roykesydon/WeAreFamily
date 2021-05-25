package sample;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.global.GlobalVariable;
import sample.response.posts.MakeNewPostResponse;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MakeNewPostController implements Initializable {
    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    public TextField priceTextField;
    public ComboBox categoryComboBox;
    public Label makeNewPostResult;

    private static final String regexPrice = "^[0-9]+.?[0-9]+$";
    private static  final String regexIntPrice="[0-9]+";
    private Pattern pattern;
    private Matcher matcher;

    public void initialize(URL url, ResourceBundle rb) {
        try {
            VBox box = FXMLLoader.load(getClass().getResource("fxml/SidePanel.fxml"));
            if(GlobalVariable.isAdmin)
                box = FXMLLoader.load(getClass().getResource("fxml/AdminSidePanel.fxml"));
            drawer.setSidePane(box);


            drawer.open();


        }catch (IOException ex){
            Logger.getLogger(ManagePostController.class.getName()).log(Level.SEVERE,null,ex);
        }

        categoryComboBox.getItems().addAll("Spotify","NintendoSwitchOnline","YoutubePremium","Netflix","AppleMusic");
    }

    public void makeNewPostRequest(ActionEvent actionEvent) throws IOException
    {

        boolean success = true;
        //verify submit form
        //有選擇種類
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
                        ToastCaller toast = new ToastCaller("價格超出上限", GlobalVariable.mainStage, ToastCaller.ERROR);
                    } else if (priceInt == 0) {
                        success = false;
                        ToastCaller toast = new ToastCaller("價格不可為零", GlobalVariable.mainStage, ToastCaller.ERROR);
                    }
                }
                else{
                    success = false;
                    ToastCaller toast = new ToastCaller("輸入含有小數點",GlobalVariable.mainStage,ToastCaller.ERROR);
                }
            }
            else {
                success = false;
                ToastCaller toast = new ToastCaller("輸入項目函非法字元",GlobalVariable.mainStage,ToastCaller.ERROR);
            }
        }
        catch(NumberFormatException e){
            success = false;
            ToastCaller toast = new ToastCaller("價格超出上限", GlobalVariable.mainStage, ToastCaller.ERROR);
        }
        //輸入的是數字 而且是整數 而且數字屬於合理範圍(不會溢位)
        //
        if(categoryComboBox.getValue() == null || priceTextField.getText().equals("")){
            success = false;
            ToastCaller toast = new ToastCaller("輸入項目不可為空",GlobalVariable.mainStage,ToastCaller.ERROR);
        }

        //表單格式皆合法
        if(success){
            try {
                String category = categoryComboBox.getValue().toString();
                String price = priceTextField.getText();

                HttpResponse response = RequestController.post("http://127.0.0.1:13261/posts/createPost",
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
                            ToastCaller toast = new ToastCaller("已達到創建貼文上限",GlobalVariable.mainStage,ToastCaller.ERROR);
                        }
                        if(error.equals("createPost fail")){
                            ToastCaller toast = new ToastCaller("伺服器錯誤",GlobalVariable.mainStage,ToastCaller.ERROR);
                        }
                    }

                    System.out.println();

                    if(jsonResponse.errors.length==0){
                        ToastCaller toast = new ToastCaller("創建成功",GlobalVariable.mainStage,ToastCaller.SUCCESS);
                    }
                    else{
//                        makeNewPostResult.setText(errorsResult);
                    }
                } else {
                    System.out.println(response.getStatusLine());
//                    makeNewPostResult.setText(errorsResult);
                }
            }
            catch (IOException  e) {
                e.printStackTrace();
            }
        }
    }

}
