package com.kazzinc.checklist.Model;

public class HelpInUseApps {

    private String NameButton;
    private String LinkVideo;
    private int IsDelete;
    private int OrderButton;

    public HelpInUseApps(String nameButton, String linkVideo, int isDelete, int orderButton) {
        NameButton = nameButton;
        LinkVideo = linkVideo;
        IsDelete = isDelete;
        OrderButton = orderButton;
    }

    public String getNameButton() {
        return NameButton;
    }

    public void setNameButton(String nameButton) {
        NameButton = nameButton;
    }

    public String getLinkVideo() {
        return LinkVideo;
    }

    public void setLinkVideo(String linkVideo) {
        LinkVideo = linkVideo;
    }

    public int getIsDelete() {
        return IsDelete;
    }

    public void setIsDelete(int isDelete) {
        IsDelete = isDelete;
    }

    public int getOrderButton() {
        return OrderButton;
    }

    public void setOrderButton(int orderButton) {
        OrderButton = orderButton;
    }
}
