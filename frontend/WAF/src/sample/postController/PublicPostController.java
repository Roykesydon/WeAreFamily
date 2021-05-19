package sample.postController;

import com.google.gson.Gson;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import sample.RequestController;
import sample.ToastCaller;
import sample.global.GlobalVariable;
import sample.response.SetIdentityCodeResponse;

import java.io.IOException;
import java.util.Arrays;

public class PublicPostController {
    public Label ownerIDLabel;
    public Label categoryLabel;
    public Label priceLabel;
    public Label joinPeopleLabel;
    public Button joinBtn;

    public void setData(String ownerID,String category,String price,String joinPeople,String postID){
        ownerIDLabel.setText(ownerID);
        categoryLabel.setText(category);
        priceLabel.setText("NT$ "+price);
        joinPeopleLabel.setText(joinPeople+"/10");
        makeButton("加入",postID);
    }


    public void makeButton(String name, String postID)  {
//        Button button = new Button(name);
//        this.joinBtn .setId(postID);
        this.joinBtn .addEventHandler(MouseEvent.MOUSE_CLICKED,
                e -> buttonFunction(postID));
    }
    public void buttonFunction(String postID)
    {
        try {
            System.out.println("joinBtnclick");
            HttpResponse response= RequestController.post("http://localhost:13261/posts/joinPost",
                    new String[]{"accessKey", GlobalVariable.accessKey},
                    new String[]{"postID",postID}
            );
            String responseString= EntityUtils.toString(response.getEntity());
            if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                Gson gson =new Gson();
                SetIdentityCodeResponse gsonResponse = gson.fromJson(responseString, SetIdentityCodeResponse.class);
                if(Arrays.toString(gsonResponse.errors)=="[]"){
//                    joinStatusLabel.setText("成功!");
                    ToastCaller toast = new ToastCaller("加入成功",GlobalVariable.mainStage,ToastCaller.SUCCESS);
                }
                else{
//                    joinStatusLabel.setText("失敗!");
                    ToastCaller toast;
                    for(String error: gsonResponse.errors){
                        if(error.equals("already join post"))
                            toast = new ToastCaller("已加入貼文",GlobalVariable.mainStage,ToastCaller.ERROR);
                        else if(error.equals("can't join more Post"))
                            toast = new ToastCaller("不能加入更多貼文",GlobalVariable.mainStage,ToastCaller.ERROR);
                        else if(error.equals("can't join user's own post"))
                            toast = new ToastCaller("不能加入自己的貼文",GlobalVariable.mainStage,ToastCaller.ERROR);
                        else if(error.equals("post already full"))
                            toast = new ToastCaller("貼文人數已滿",GlobalVariable.mainStage,ToastCaller.ERROR);
                        else if(error.equals("joinPost fail"))
                            toast = new ToastCaller("伺服器錯誤",GlobalVariable.mainStage,ToastCaller.ERROR);
                    }
                }
            }
            else{
                System.out.println(response.getStatusLine().getStatusCode());
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        //System.out.println("You Clicked " + tmp + " from PublicPage");
    }
}
