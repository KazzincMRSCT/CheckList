package com.kazzinc.checklist;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kazzinc.checklist.Model.EmlpECPKey;
import com.kazzinc.checklist.Model.Equipment;
import com.kazzinc.checklist.Model.GSM;
import com.kazzinc.checklist.Model.HelpInUseApps;
import com.kazzinc.checklist.Model.Notification;
import com.kazzinc.checklist.Model.Question;
import com.kazzinc.checklist.Model.Reason;
import com.kazzinc.checklist.Model.RiskSafety;
import com.kazzinc.checklist.Model.Task;
import com.kazzinc.checklist.Model.TaskDetail;
import com.kazzinc.checklist.Model.TaskDetailModify;
import com.kazzinc.checklist.Model.TaskEmployee;
import com.kazzinc.checklist.Model.TaskUser;
import com.kazzinc.checklist.Model.User;
import com.kazzinc.checklist.Model.WorkPlace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MsSqlDatabase {

    private String serverURL = "http://192.168.164.5:816/Service.asmx/";
    private HttpRequestUtility httpRequestUtility;

    public MsSqlDatabase() {
        httpRequestUtility = new HttpRequestUtility();
    }
    SharedPreferences sPref;
    //проверка соединения
    public boolean checkConnection() {
        Gson gson = new Gson();
        String methodURL = "CheckConnection";
        try {
            String resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", null);
            Boolean result = gson.fromJson(resultJson, new TypeToken<Boolean>() {
            }.getType());
            return result;
        } catch (Exception e) {
            return false;
        }
    }

    //аутентификация
    public User auth(String login, String password) {
        Gson gson = new Gson();
        String methodURL = "Auth";
        try {
            ContentValues params = new ContentValues();
            params.put("login", login);
            params.put("password", password);

            String resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", params);
            User user = gson.fromJson(resultJson, new TypeToken<User>() {
            }.getType());
            return user;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //обновить ответы
    public void updateAnswer(int AnswerUserId, int AnswerQuesId, String AnswerText, String AnswerDate, int AnswerShift, String AnswerComment, String AnswerWorkPlaceName, String AnswerDateTime, String AnswerPhotos) {
        Gson gson = new Gson();
        String methodURL = "UpdateAnswer";
        String resultJson = "";
        try {
            ContentValues params = new ContentValues();
            params.put("AnswerUserId", AnswerUserId);
            params.put("AnswerQuesId", AnswerQuesId);
            params.put("AnswerText", AnswerText);
            params.put("AnswerDate", AnswerDate);
            params.put("AnswerShift", AnswerShift);
            params.put("AnswerComment", AnswerComment);
            params.put("AnswerWorkPlaceName", AnswerWorkPlaceName);
            params.put("AnswerDateTime", AnswerDateTime);
            params.put("AnswerPhotos", AnswerPhotos);
            params.put("AnswerReview", "");

             resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", params);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("Alexey", "ECP: updateAnswer результат выполнения процедуры на сервере - " + resultJson);
    }

    //обновить предложения по улучшению
    public void updateImprovement(int Id, int EmplId, String EmplName, String EmplArea, String EmplProff, String DateTime, String Title, String Offer, String Result, String Photos, int IsDeleted) {
        Gson gson = new Gson();
        Log.d("Alexey", "SyncService1 (timer SendToServer step sever 1)");
        String methodURL = "UpdateImprovement";
        String resultJson = "";
        try {
            ContentValues params = new ContentValues();
            params.put("Id", Id);
            params.put("EmplId", EmplId);
            params.put("EmplName", EmplName);
            params.put("EmplArea", EmplArea);
            params.put("EmplProff", EmplProff);
            params.put("DateTime", DateTime);
            params.put("Title", Title);
            params.put("Offer", Offer);
            params.put("Result", Result);
            params.put("Photos", Photos);
            params.put("IsDeleted", IsDeleted);

            Log.d("Alexey", "SyncService1 (timer SendToServer step sever 2) " + serverURL + methodURL + " " + params);

            resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", params);


        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("Alexey", "updateImprovement результат выполнения процедуры на сервере - " + resultJson);
    }

    public void ImprovementDocs(int ImprId, String FileName) {
        Gson gson = new Gson();
        Log.d("Alexey", "SyncService1 (timer SendToServer step sever Photo 1)");
        String methodURL = "ImprovementDocs";
        String resultJson = "";
        try {
            ContentValues params = new ContentValues();
            params.put("ImprId", ImprId);
            params.put("FileName", FileName);

            Log.d("Alexey", "SyncService1 (timer SendToServer step sever Photo 2) " + serverURL + methodURL + " " + params);

            resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", params);


        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("Alexey", "SyncService1 (timer SendToServer step sever Photo result) - " + resultJson);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean updateTask(int TASK_ID, int TASK_SIGN_ID, int TASK_EMPL_ID, int SIGN_TYPE, String FILE_NAME) throws IOException {
        Gson gson = new Gson();
        String methodURL = "TaskSign";
        boolean result=false;

        String resultJson="";
        try {

            //Читаем пароль от сертификата ЭЦП
            File file = new File("/storage/emulated/0/Key", "data.cfg");
            StringBuilder pswd = new StringBuilder();
            String line;

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                while ((line = br.readLine()) != null) {
                    pswd.append(line);
                }
                br.close();
            } catch (IOException e) {
                //You'll need to add proper error handling here
            }
            //

            Log.d("Alexey", "P12: list " +  pswd.toString());
            Log.d("Alexey", "P12: list " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + FILE_NAME);

            ContentValues params = new ContentValues();
            params.put("TASK_ID", TASK_ID);
            params.put("TASK_SIGN_ID", TASK_SIGN_ID);
            params.put("TASK_EMPL_ID", TASK_EMPL_ID);

            //params.put("P12",  Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("/data/data/com.kazzinc.checklist/databases/RSA256_28e140879252a59cd2e2e9676ff9ced7e4fe5aaa.p12"))));
            params.put("P12",  Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("/storage/emulated/0/Key/" + FILE_NAME))));
            params.put("SIGN_TYPE", SIGN_TYPE);
            params.put("PSWD", pswd.toString());


            resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", params);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Alexey", "ECP: ошибка выполнения процедуры на сервере- " + e.getMessage());
        }

        if (resultJson.replace('"', '1').equals("1true1"))
            result=true;

        Log.d("Alexey", "ECP: результат выполнения процедуры на сервере resultJson - " + resultJson);
        Log.d("Alexey", "ECP: результат выполнения процедуры на сервере - " + result);

        writeLog("updateTask"+ "\t" + "TASK_ID="+TASK_ID+";TASK_SIGN_ID="+TASK_SIGN_ID+";TASK_EMPL_ID="+TASK_EMPL_ID+";SIGN_TYPE="+SIGN_TYPE+";FILE_NAME="+FILE_NAME + "\t" + "Результат: " + result);

        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String keyInfo(String key, String PSWD) throws IOException {
        Gson gson = new Gson();
        String methodURL = "CheckKey";

        String resultJson="";
        try {

            ContentValues params = new ContentValues();
            params.put("P12",  key);
            params.put("PSWD", PSWD);

            resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", params);

        } catch (Exception e) {
            //e.printStackTrace();
            Log.d("Alexey", "ECP key: ошибка выполнения процедуры на сервере- " + e.getMessage());
        }

        Log.d("Alexey", "ECP key: результат выполнения процедуры на сервере resultJson - " + resultJson);

        return resultJson;
    }

    public void writeLog(String txt) throws IOException {
        Log.d("Alexey", "LOG: " + txt);

        String path = "/storage/emulated/0/Key/Checklist.log";
        String pathDir = "/storage/emulated/0/Key/";

        File dir = new File(pathDir);
        if (!dir.exists()) {
            dir.mkdir();
        }

        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter writer = new FileWriter(path,true);

        Date c = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(c);
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        txt = df.format(c) + "\t" + txt;

        writer.write(txt+"\n");

        writer.close();
    }

    public boolean updateTaskNoKey(int TASK_ID, int TASK_SIGN_ID) throws IOException {
        Gson gson = new Gson();
        String methodURL = "UpdateTask";
        boolean result=false;
        String resultJson="";

        try {
            ContentValues params = new ContentValues();
            params.put("TASK_ID", TASK_ID);
            params.put("TASK_SIGN_ID", TASK_SIGN_ID);

            resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", params);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (resultJson.replace('"', '1').equals("1true1"))
            result=true;

        writeLog("updateTask"+ "\t" + "TASK_ID:"+TASK_ID+";TASK_SIGN_ID="+TASK_SIGN_ID+ "\t" + "Результат: " + result);

        return result;
    }

    public void updateMoto(String MotoEquipName, String MotoDate, int MotoShift, float MotoDVS, float MotoCompress, float MotoPerfor, float MotoMaslo, float MotoLeft, float MotoRight) {
        Gson gson = new Gson();
        String methodURL = "UpdateMoto";
        try {

            ContentValues params = new ContentValues();
            params.put("MotoEquipName", MotoEquipName);
            params.put("MotoDate", MotoDate);
            params.put("MotoShift", MotoShift);
            params.put("MotoDVS", MotoDVS);
            params.put("MotoCompress", MotoCompress);
            params.put("MotoPerfor", MotoPerfor);
            params.put("MotoMaslo", MotoMaslo);
            params.put("MotoLeft", MotoLeft);
            params.put("MotoRight", MotoRight);

            String resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", params);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean UpdateGSM(String DateEvent, String Date, Integer Shift, String EquipOut, String EquipIn, String EmplOut, String Reason, double DT, double SAE15W40, double SAE50, double SAE10W40, double T46, int Deleted, String ReasonOil, int Confirmed, double T86) {
        Gson gson = new Gson();
        String resultJson ="";
        boolean result=false;

        String methodURL = "UpdateGSMConfirmT86";

        try {

            ContentValues params = new ContentValues();
            params.put("DateEvent", DateEvent);
            params.put("Date", Date);
            params.put("Shift", Shift);
            params.put("EquipOut", EquipOut);
            params.put("EquipIn", EquipIn);
            params.put("EmplOut", EmplOut);
            params.put("Reason", Reason);
            params.put("DT", DT);
            params.put("SAE15W40", SAE15W40);
            params.put("SAE50", SAE50);
            params.put("SAE10W40", SAE10W40);
            params.put("T46", T46);
            params.put("T86", T86);
            params.put("Deleted", Deleted);
            params.put("ReasonOil", ReasonOil);
            params.put("Confirmed", Confirmed);

            Log.d("Alexey","GSM methodURL " + methodURL + " до "+ Date + " после " + Date.toString());

            Log.d("Alexey","GSM Update resultJson " + serverURL + params);

            resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", params);

            Log.d("Alexey","GSM Update resultJson params: " + params + " / Confrirmed " +  Confirmed);

            Log.d("Alexey","GSM Update resultJson " + serverURL + methodURL);

            if (resultJson.replace('"', '1').equals("1true1"))
                result=true;

            Log.d("Alexey","GSM resultJson " + resultJson);

        } catch (Exception e) {
            Log.d("Alexey", "GSM Err1: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    public boolean UpdateRVD(String DateEvent, String Date, int Shift, String Equipment, int OldNumber, int NewNumber, int MotoHours, int SpecialHours, String Place, String Reason, int Deleted) {
        Gson gson = new Gson();
        String resultJson ="";
        boolean result=false;

        String methodURL = "UpdateRVD";

        try {

            ContentValues params = new ContentValues();
            params.put("DateEvent", DateEvent);
            params.put("Date", Date);
            params.put("Shift", Shift);
            params.put("Equipment", Equipment);
            params.put("OldNumber", OldNumber);
            params.put("NewNumber", NewNumber);
            params.put("MotoHours", MotoHours);
            params.put("SpecialHours", SpecialHours);
            params.put("Place", Place);
            params.put("Reason", Reason);
            params.put("Deleted", Deleted);

            resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", params);

            if (resultJson.replace('"', '1').equals("1true1"))
                result=true;
        } catch (Exception e) {
            Log.d("Alexey", "RVD Err1: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    public String GetGSM(String Date, int Shift, String Equipment) {
        Gson gson = new Gson();
        String methodURL = "GetGSM";
        String resultJson = "";
        try {
            ContentValues params = new ContentValues();
            params.put("Date", Date);
            params.put("Shift", Shift);
            params.put("Equipment", Equipment);
            /*params.put("Date", "02.19.2021");
            params.put("Shift", "2");
            params.put("Equipment", "AD30 №52");*/
            resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", params);

            Log.d("Alexey", "GetGSM : " + Date + " " + Shift + " " + Equipment);
            Log.d("Alexey", "GetGSM resultJson: " + resultJson);



        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Alexey", "GetGSM err: " + e.getMessage());
        }

        return resultJson;
    }

    public void updateCheckListByEmpl(int UserId, String Date, int Shift, String Object, String UserDateTime, String Danger, String ControlTools, String Improvement, String UserIsSafety) {
        Gson gson = new Gson();
        String methodURL = "UpdateCheckListByEmpl?UserId=" + UserId + "&Date=" + Date + "&Shift=" + Shift + "&Object=" + Object + "&UserDateTime=" + UserDateTime + "&Danger=" + Danger + "&ControlTools=" + ControlTools + "&Improvement=" + Improvement + "&UserIsSafety=" + UserIsSafety;

        Log.d("Alexey","updateCheckListByEmpl " + methodURL);

        try {

            ContentValues params = new ContentValues();
            params.put("UserId", UserId);
            params.put("Date", Date);
            params.put("Shift", Shift);
            params.put("Object", Object);
            params.put("UserDateTime", UserDateTime);
            params.put("Danger", Danger);
            params.put("ControlTools", ControlTools);
            params.put("Improvement", Improvement);
            params.put("UserIsSafety", UserIsSafety);

            Log.d("Alexey","updateCheckListByEmpl параметры " + methodURL);

            String resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", null);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Alexey","updateCheckListByEmpl ошибка " + e.getMessage());
        }

    }

    public void updatePNVR (String User, String Date, int Shift, String CheckedPersonal, String Task, String Workplace, String Responsible, String Team, String PodrjadOrg, String RiskTool, String Dangers, String Instruction, String Сonformity, String Assessment, String ShortReview, String ClassBehavior, String BehaviorReview, String PSO, String ActionReview, String Event) {
        try {
            Log.d("Alexey","PNVR Assessment " + Assessment);

            String methodURL = "UpdatePNVR1?User=" + User + "&Date=" + Date + "&Shift=" + Shift + "&CheckedPersonal=" + CheckedPersonal + "&Task=" + Task + "&Workplace=" + Workplace + "&Responsible=" + Responsible + "&Team=" + Team + "&PodrjadOrg=" + PodrjadOrg;
            String resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", null);

            methodURL = "UpdatePNVR2?User=" + User + "&Date=" + Date + "&Shift=" + Shift + "&RiskTool=" + RiskTool + "&Dangers=" + Dangers + "&Instruction=" + Instruction;
            resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", null);

            methodURL = "UpdatePNVR3?User=" + User + "&Date=" + Date + "&Shift=" + Shift + "&Сonformity=" + Сonformity + "&Assessment=" + Assessment + "&ShortReview=" + ShortReview + "&ClassBehavior=" + ClassBehavior;
            resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", null);

            methodURL = "UpdatePNVR4?User=" + User + "&Date=" + Date + "&Shift=" + Shift + "&BehaviorReview=" + BehaviorReview + "&PSO=" + PSO + "&ActionReview=" + ActionReview + "&Event=" + Event;
            resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", null);

        } catch (Exception e) {
            Log.d("Alexey","PNVR exeption " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updatePNVRAll (String User, String Date, int Shift, String CheckedPersonal, String Task, String Workplace, String Responsible, String Team, String PodrjadOrg, String RiskTool, String Dangers, String Instruction, String Сonformity, String Assessment, String ShortReview, String ClassBehavior, String BehaviorReview, String PSO, String ActionReview, String Event) {
        Gson gson = new Gson();
        String methodURL = "UpdatePNVR?User=" + User + "&Date=" + Date + "&Shift=" + Shift + "&CheckedPersonal=" + CheckedPersonal + "&Task=" + Task + "&Workplace=" + Workplace + "&Responsible=" + Responsible + "&Team=" + Team + "&PodrjadOrg=" + PodrjadOrg + "&RiskTool=" + RiskTool + "&Dangers=" + Dangers + "&Instruction=" + Instruction + "&Сonformity=" + Сonformity + "&Assessment=" + Assessment + "&ShortReview=" + ShortReview + "&ClassBehavior=" + ClassBehavior + "&BehaviorReview=" + BehaviorReview + "&PSO=" + PSO + "&ActionReview=" + ActionReview + "&Event=" + Event;
        try {
            String resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", null);
        } catch (Exception e) {
            Log.d("Alexey","PNVR exeption " + e.getMessage());
            Log.d("Alexey","PNVR url " + serverURL + methodURL);
            e.printStackTrace();
        }
    }

    public void updatePNVWithParams (String User, String Date, int Shift, String CheckedPersonal, String Task, String Workplace, String Responsible, String Team, String PodrjadOrg, String RiskTool, String Dangers, String Instruction, String Сonformity, String Assessment, String ShortReview, String ClassBehavior, String BehaviorReview, String PSO, String ActionReview, String Event) {
        Gson gson = new Gson();
        String methodURL = "UpdatePNVR";
        try {

            ContentValues params = new ContentValues();
            params.put("User", User);
            params.put("Date", Date);
            params.put("Shift", Shift);
            params.put("CheckedPersonal", CheckedPersonal);
            params.put("Task", Task);
            params.put("Workplace", Workplace);
            params.put("Responsible", Responsible);
            params.put("Team", Team);
            params.put("PodrjadOrg", PodrjadOrg);
            params.put("RiskTool", RiskTool);
            params.put("Dangers", Dangers);
            params.put("Instruction", Instruction);
            params.put("Сonformity", Сonformity);
            params.put("Assessment", Assessment);
            params.put("ShortReview", ShortReview);
            params.put("ClassBehavior", ClassBehavior);
            params.put("BehaviorReview", BehaviorReview);
            params.put("PSO", PSO);
            params.put("ActionReview", ActionReview);
            params.put("Event", Event);

            String resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", params);

        } catch (Exception e) {
            Log.d("Alexey","PNVR exeption " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateVersion(String user, String mac, String version, String date, String wifi, String phone) {
        Log.d("Alexey","WIFI1_");
        Gson gson = new Gson();
        String methodURL = "UpdateVersionNew";
        try {
            ContentValues params = new ContentValues();
            Log.d("Alexey", user + "user " + mac + " mac");
            params.put("User", user);
            params.put("MacAdress", mac);
            params.put("Version", version);
            params.put("Date", date);
            params.put("Wifi", wifi);
            params.put("Phone", phone);

            Log.d("Alexey","WIFI1 " + params);

            String resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", params);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //получить список рабочих мест
    public ArrayList<WorkPlace> getWorkPlaceList() {
        Gson gson = new Gson();
        String methodURL = "GetWorkPlaceList";
        try {
            String resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", null);
            Log.d("Alexey", "GetWorkPlaceList");
            ArrayList<WorkPlace> workPlacesList = gson.fromJson(resultJson, new TypeToken<List<WorkPlace>>() {}.getType());
            return workPlacesList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //получить данные по ГСМ
    public ArrayList<GSM> getGSM(String equipmentId) {
        Gson gson = new Gson();
        String methodURL = "GetGSMForMobile_2?Equipment=" + equipmentId;
        String resultJson = "";
        try {
            resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", null);
            Log.d("Alexey", "GSMвбазу: получение: " + resultJson);
            ArrayList<GSM> gsm = gson.fromJson(resultJson, new TypeToken<List<GSM>>() {}.getType());
            return gsm;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Alexey", "GSMвбазу: result GSM ошибка " + resultJson);
        }
        return null;
    }

    //Получить данные о видео обучении
    public ArrayList<HelpInUseApps> GetHelpInUseApps() {
        Gson gson = new Gson();
        String methodURL = "GetHelpInUseApps";
        String resultJson = "";
        try {
            resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", null);
            ArrayList<HelpInUseApps> helpInUseApps = gson.fromJson(resultJson, new TypeToken<List<HelpInUseApps>>() {}.getType());

            return helpInUseApps;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //получить список задач СУЭНЗ
    public ArrayList<Task> getTaskList(int userId, int userType, String date) {
        Gson gson = new Gson();
        String methodURL = "GetTask?UserId=" + userId + "&UserType=" + userType + "&Date=" + date;
        Log.d("Alexey", "GetTask? 1" + methodURL);
        String resultJson = "";
        try {
            resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", null);
            //Log.d("Alexey", "JSON result getTaskList " + resultJson);
            ArrayList<Task> task = gson.fromJson(resultJson, new TypeToken<List<Task>>() {}.getType());
            Log.d("Alexey", "JSON result1 " + task);
            return task;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Alexey", "JSON result getTaskList ошибка " + resultJson);
        }
        return null;
    }

    //Получить данные о ключе ЭЦП
    public List<EmlpECPKey> GetECPEmplId(String id) {
        Gson gson = new Gson();
        String methodURL = "GetECPEmplId?id="+id;
        String resultJson = "";
        try {
            resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", null);
            List<EmlpECPKey> ecpEmplIds = gson.fromJson(resultJson, new TypeToken<List<EmlpECPKey>>() {}.getType());
            return ecpEmplIds;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //получить список работ СУЭНЗ
    public ArrayList<TaskDetail> getTaskDetailList(int userId, int userType, String date) {
        Gson gson = new Gson();
        String methodURL = "GetTaskDetailSign?UserId=" + userId + "&UserType=" + userType + "&Date=" + date;
        Log.d("Alexey", "GetTaskDetailSign " + "GetTaskDetailSign?UserId=" + userId + "&UserType=" + userType + "&Date=" + date);
        try {
            String resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", null);
            Log.d("Alexey", "JSON result getTaskDetailList " + resultJson);
            ArrayList<TaskDetail> taskDetails = gson.fromJson(resultJson, new TypeToken<List<TaskDetail>>() {}.getType());
            return taskDetails;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Alexey", "JSON result getTaskDetailList ошибка");
        }
        return null;
    }

    //получить изменение наряда СУЭНЗ
    public ArrayList<TaskDetailModify> getTaskDetailModify(int userId, String date) {
        Gson gson = new Gson();
        String methodURL = "GetTaskModifySign?UserId=" + userId + "&Date=" + date;
        try {
            String resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", null);
            ArrayList<TaskDetailModify> taskDetailslModify = gson.fromJson(resultJson, new TypeToken<List<TaskDetailModify>>() {}.getType());
            return taskDetailslModify;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //получить список рабочих из СУЭНЗ
    public ArrayList<TaskEmployee> getTaskEmployeeList() {
        Gson gson = new Gson();
        String methodURL = "GeEmployeeList";
        try {
            String resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", null);
            ArrayList<TaskEmployee> employeeList = gson.fromJson(resultJson, new TypeToken<List<TaskEmployee>>() {}.getType());
            return employeeList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //получить список мастеров и начальников участков
    public ArrayList<TaskUser> getTaskUserList() {
        Gson gson = new Gson();
        String methodURL = "GetTaskUsers";
        try {
            String resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", null);
            ArrayList<TaskUser> userList = gson.fromJson(resultJson, new TypeToken<List<TaskUser>>() {}.getType());
            return userList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //получить всех пользователей
    public ArrayList<User> getUsers() {
        Gson gson = new Gson();
        String methodURL = "GetUsers";
        try {
            String resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", null);
            ArrayList<User> users = gson.fromJson(resultJson, new TypeToken<List<User>>() {}.getType());
            return users;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //получить список оборудования из СУЭНЗ
    public ArrayList<Equipment> getEquipment() {
        Gson gson = new Gson();
        String methodURL = "GetEquipment";
        try {
            String resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", null);
            Log.d("Alexey", "GetEquipment " + resultJson);
            ArrayList<Equipment> equipment = gson.fromJson(resultJson, new TypeToken<List<Equipment>>() {}.getType());
            return equipment;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //получить список оборудования из СУЭНЗ
    public ArrayList<Reason> getReason() {
        Gson gson = new Gson();
        String methodURL = "GetGSMReason";
        try {
            String resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", null);
            Log.d("Alexey", "GetGSMReason " + resultJson);
            ArrayList<Reason> equipment = gson.fromJson(resultJson, new TypeToken<List<Reason>>() {}.getType());
            return equipment;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<RiskSafety> getRiskSafety(int EmplId) {
        Gson gson = new Gson();
        String methodURL = "GetRiskSafetyInstruction";
        try {

            Log.d("Alexey", "getRiskSafety EmplId: " + EmplId);

            ContentValues params = new ContentValues();
            params.put("EmplId", EmplId);

            Log.d("Alexey", "getRiskSafety method: " + serverURL + methodURL);
            String resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", params);
            ArrayList<RiskSafety> rs = gson.fromJson(resultJson, new TypeToken<List<RiskSafety>>() {}.getType());
            Log.d("Alexey", "getRiskSafety resultJson: " + resultJson);
            return rs;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    //получить все вопросы
    public ArrayList<Question> getQuestions() {
        Gson gson = new Gson();
        String methodURL = "GetQuestionsList";
        try {
            String resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", null);
            ArrayList<Question> questions = gson.fromJson(resultJson, new TypeToken<List<Question>>() {}.getType());
            return questions;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //получить всех пользователей
    public ArrayList<Notification> GetNotification() {
        Gson gson = new Gson();
        String methodURL = "GetNotification";
        try {
            String resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", null);
            ArrayList<Notification> NotificationArray = gson.fromJson(resultJson, new TypeToken<List<Notification>>() {}.getType());
            Log.d("Alexey", "getNitification resultJson: " + resultJson);
            return NotificationArray;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Alexey", "getNitification error " + e.getMessage());
        }
        return null;
    }


    //////////////////////////////
    public void InsertCheckApp(String EmplName, int tubNum, int AreaId, String VersionApp) {
        Gson gson = new Gson();
        String resultJson ="";

        String methodURL = "CheckApp";

        try {
            ContentValues params = new ContentValues();
            params.put("EmplName", EmplName);
            params.put("tubNum", tubNum);
            params.put("AreaId", AreaId);
            params.put("VersionApp", VersionApp);

            resultJson = httpRequestUtility.RequestToServer(serverURL + methodURL, "GET", params);


        } catch (Exception e) {
            Log.d("Alexey", "InsertCheckApp1 " + e.getMessage());
            e.printStackTrace();
        }
        Log.d("Alexey", "InsertCheckApp1 " + resultJson);
    }

    /////////////////////////////

}

