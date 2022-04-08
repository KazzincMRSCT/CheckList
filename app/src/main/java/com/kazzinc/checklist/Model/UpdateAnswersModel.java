package com.kazzinc.checklist.Model;

import java.util.ArrayList;

public class UpdateAnswersModel {
    private ArrayList<Answer> answers;

    public UpdateAnswersModel(ArrayList<Answer> answers){
        this.answers = answers;
    }

    public ArrayList<Answer> getAnswers(){
        return answers;
    }
}
