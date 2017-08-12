package com.ANC_TeamEagles.mypurse.pojo;

/**
 * Created by EmmanuelBaldwin on 8/6/2017.
 */

public class IncomeExpenditure {
    private String descriptionIcon;
    private String descriptionText;
    private String amountText;
    private String dateCaptured;

    public IncomeExpenditure(String descriptionText, String amountText, String date){
        this.descriptionText = descriptionText;
        this.amountText = amountText;
        this.dateCaptured = date;
    }

    public String getDescriptionIcon() {
        return String.valueOf(descriptionText.charAt(0));
    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public String getAmountText() {
        return amountText;
    }


    public String getDateCaptured() {
        return dateCaptured;
    }
}
