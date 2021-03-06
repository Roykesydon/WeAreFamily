package sample.view.detailCardController;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import sample.global.GlobalVariable;

public class NotificationController {
    public Label detailLabel;
    public Label titleLabel;
    public AnchorPane anchorPane;

    public void setData(String detail){
        String[] detailArr = detail.split("\n");
        int count =detailArr.length;

        if(detailArr[0].equals("Match successfully"))
            anchorPane.setStyle("-fx-background-color: #3c3f41;-fx-effect: dropshadow(gaussian, rgba(0,255,0,0.8), 10, 0, 0, 0);");
        else
            anchorPane.setStyle("-fx-background-color: #3c3f41;-fx-effect: dropshadow(gaussian, rgba(255,60,60,0.8), 10, 0, 0, 0);");


        titleLabel.setText(detailArr[0]);
        titleLabel.setStyle("-fx-text-fill: "+GlobalVariable.primaryColor+";-fx-font-size:38;");
        String tmp ="";
        for(int i=1;i<detailArr.length;i++)
        {
            if(i!=1)
                tmp+="\n";
            tmp+=detailArr[i];
        }
        detailLabel.setText(tmp);
        detailLabel.setPrefHeight((count-1)*38);

    }
}
