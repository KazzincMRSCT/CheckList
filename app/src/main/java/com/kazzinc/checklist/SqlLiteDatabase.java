package com.kazzinc.checklist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.kazzinc.checklist.Model.Answer;
import com.kazzinc.checklist.Model.ChatModel;
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
import com.kazzinc.checklist.Model.UpdateAnswersModel;
import com.kazzinc.checklist.Model.User;
import com.kazzinc.checklist.Model.WorkPlace;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SqlLiteDatabase extends SQLiteOpenHelper {
    private static String DB_PATH;// = "/data/data/com.kazzinc.checklist/databases/";
    private static String DB_NAME = "Mobile.db";
    private static final int SCHEMA = 1;

    public SQLiteDatabase database;
    private Context myContext;

    private int qIndex = 0;

    public SqlLiteDatabase(Context context) {
        super(context, DB_NAME, null, SCHEMA);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //аутентификация
    public User auth(String login, String password) {
        String selectQuery = "SELECT  * FROM " + "User WHERE UserLogin == '" + login + "' AND UserPassword == '" + password + "'";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            int userId = Integer.parseInt(cursor.getString(0));
            String userName = cursor.getString(1);
            String userLogin = cursor.getString(2);
            String userPassword = cursor.getString(3);
            int userAreaId = cursor.getInt(4);
            String userRole = cursor.getString(5);

            User user = new User(userId, userName, userLogin, userPassword, userAreaId, userRole);
            return user;
        }

        selectQuery = "SELECT  * FROM " + "TaskEmployee WHERE TaskEmplLogin == '" + login + "' AND TaskEmplPassword == '" + password + "'";

        cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            int userId = Integer.parseInt(cursor.getString(0));
            String userName = cursor.getString(1);
            String userLogin = cursor.getString(2);
            String userPassword = cursor.getString(3);
            int userAreaId = cursor.getInt(4);
            String userRole = cursor.getString(6);

            User user = new User(userId, userName, userLogin, userPassword, userAreaId, userRole);
            return user;
        }

        selectQuery = "SELECT  * FROM " + "TaskUser WHERE TaskUserLogin == '" + login + "' AND TaskUserPassword == '" + password + "'";

        cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            int userId = Integer.parseInt(cursor.getString(0));
            String userName = cursor.getString(1);
            String userLogin = cursor.getString(2);
            String userPassword = cursor.getString(3);
            int userAreaId = cursor.getInt(4);
            String userRole = cursor.getString(6);

            User user = new User(userId, userName, userLogin, userPassword, userAreaId, userRole);
            return user;
        }

        return null;
    }

    //запросы к бд для ответов

    public ArrayList<Answer> getAnswers() {
        ArrayList<Answer> answers = new ArrayList<Answer>();
        String selectQuery = "SELECT  * FROM " + "Answer";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int ansId = Integer.parseInt(cursor.getString(0));
                int ansUserId = Integer.parseInt(cursor.getString(1));
                int quesId = Integer.parseInt(cursor.getString(2));
                String ansText = cursor.getString(3);
                String ansDate = cursor.getString(4);
                int ansShift = Integer.parseInt(cursor.getString(5));
                String ansComment = cursor.getString(6);
                String ansWorkPlaceName = cursor.getString(7);
                String ansDateTime = cursor.getString(8);
                String ansPhotos = cursor.getString(9);
                Answer answer = new Answer(ansId, ansUserId, quesId, ansText, ansDate, ansShift, ansComment, ansWorkPlaceName, ansDateTime, ansPhotos);
                answers.add(answer);
            } while (cursor.moveToNext());
        }
        return answers;
    }

    public void insertAnswer(Answer answer) throws Exception {
        ContentValues values = new ContentValues();
        values.put("AnswerUserId", answer.getAnswerUserId());
        values.put("AnswerQuesId", answer.getQuesId());
        values.put("AnswerText", answer.getAnswerText());
        values.put("AnswerDate", answer.getAnswerDate());
        values.put("AnswerShift", answer.getAnswerShift());
        values.put("AnswerComment", answer.getAnswerComment());
        values.put("AnswerWorkPlaceName", answer.getAnswerWorkPlaceName());
        values.put("AnswerDateTime", answer.getAnswerDateTime());
        values.put("AnswerPhotos", answer.getAnswerPhotos());

        try {
            database.insert("Answer", null, values);
            Log.d("Alexey", "Ответы правильно ");
        } catch (Exception e) {
            e.printStackTrace();
            String err = e.getMessage();
            Log.d("Alexey", "Ответы ошибка: " + e.getMessage());
        }


    }

    public void updateAnswer(Answer answer) throws Exception {
        ContentValues values = new ContentValues();
        values.put("AnswerUserId", answer.getAnswerUserId());
        values.put("AnswerQuesId", answer.getQuesId());
        values.put("AnswerText", answer.getAnswerText());
        values.put("AnswerDate", answer.getAnswerDate());
        values.put("AnswerShift", answer.getAnswerShift());
        values.put("AnswerComment", answer.getAnswerComment());
        values.put("AnswerWorkPlaceName", answer.getAnswerWorkPlaceName());
        values.put("AnswerDateTime", answer.getAnswerDateTime());
        values.put("AnswerPhotos", answer.getAnswerPhotos());

        database.update("Answer", values, "AnswerId=" + answer.getAnswerId(), null);
    }

    public Answer getAnswer(int questId, int userId, String date, int shift, String workPlaceName) {
        String selectQuery = "SELECT  * FROM " + "Answer WHERE AnswerQuesId = " + questId + " AND AnswerUserId = " + userId + " AND AnswerDate = '" + date + "' AND AnswerShift = " + shift + " AND AnswerWorkPlaceName = '" + workPlaceName + "'";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int ansId = Integer.parseInt(cursor.getString(0));
                int ansUserId = Integer.parseInt(cursor.getString(1));
                int quesId = Integer.parseInt(cursor.getString(2));
                String ansText = cursor.getString(3);
                String ansDate = cursor.getString(4);
                int ansShift = Integer.parseInt(cursor.getString(5));
                String ansComment = cursor.getString(6);
                String ansWorkPlaceName = cursor.getString(7);
                String ansDateTime = cursor.getString(8);
                String ansPhotos = cursor.getString(9);
                Answer answer = new Answer(ansId, ansUserId, quesId, ansText, ansDate, ansShift, ansComment, ansWorkPlaceName, ansDateTime, ansPhotos);

                return answer;
            } while (cursor.moveToNext());
        }
        return null;
    }

    public void updateAnswers(UpdateAnswersModel model) {

        for (int i = 0; i < model.getAnswers().size(); ++i) {
            Answer answer = model.getAnswers().get(i);

            try {
                int t = answer.getQuesId();

                Question ques = getQuestion(answer.getQuesId());

                if (ques != null) {
                    Answer answ = getAnswer(answer.getQuesId(), answer.getAnswerUserId(), answer.getAnswerDate(), answer.getAnswerShift(), answer.getAnswerWorkPlaceName());
                    if (answ != null) {
                        answ.setAnsText(answer.getAnswerText());
                        answ.setAnswerDate(answer.getAnswerDate());
                        answ.setAnswerShift(answer.getAnswerShift());
                        answ.setAnswerComment(answer.getAnswerComment());
                        answ.setAnswerWorkPlaceName(answer.getAnswerWorkPlaceName());
                        answ.setAnswerDateTime(answer.getAnswerDateTime());
                        answ.setAnswerPhotos(answer.getAnswerPhotos());
                        try {
                            updateAnswer(answ);
                        } catch (Exception e) {
                            e.printStackTrace();
                            String err = e.getMessage();
                            Log.d("Alexey", "Ответы ошибка: " + e.getMessage());
                        }
                    } else {
                        try {
                            insertAnswer(answer);
                        } catch (Exception e) {
                            e.printStackTrace();
                            String err = e.getMessage();
                            Log.d("Alexey", "Ответы ошибка: " + e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                Log.d("Alexey", e.getMessage());
            }
        }
    }

    //запросы к бд для вопросов

    public ArrayList<Question> getQuestions() {

        ArrayList<Question> quests = new ArrayList<Question>();
        try {
            String selectQuery = "SELECT  * FROM Question";

            Cursor cursor = database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    int quesId = Integer.parseInt(cursor.getString(0));
                    String questext = cursor.getString(1);
                    int quesType = Integer.parseInt(cursor.getString(2));
                    int quesIsCritical = Integer.parseInt(cursor.getString(3));
                    String quesEquipType = cursor.getString(4);

                    Question quest = new Question(quesId, questext, quesType, quesIsCritical, quesEquipType);
                    quests.add(quest);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("Alexey", e.getMessage());
        }

        return quests;
    }

    public ArrayList<Question> getQuestions(String quesType, String quesEquipType) {
        ArrayList<Question> quests = new ArrayList<Question>();
        String selectQuery = "";
        if (quesType.contains("1"))
            selectQuery = "SELECT  * FROM Question WHERE QuesType = " + quesType;
        else
            selectQuery = "SELECT  * FROM Question WHERE QuesType = " + quesType + " AND QuesEquipGroup like '%/" + quesEquipType + "/%'";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int quesId = Integer.parseInt(cursor.getString(0));
                String questext = cursor.getString(1);
                int quesIsCritical = Integer.parseInt(cursor.getString(3));

                Question quest = new Question(quesId, questext, Integer.parseInt(quesType), quesIsCritical, quesEquipType);
                quests.add(quest);
            } while (cursor.moveToNext());
        }
        return quests;
    }

    public void insertQuestion(Question question) throws Exception {
        ContentValues values = new ContentValues();
        values.put("QuesId", question.getQuesId());
        values.put("Questext", question.getQuestext());
        values.put("QuesType", question.getQuesType());
        values.put("QuesIsCritical", question.getQuesIsCritical());
        values.put("QuesEquipGroup", question.getQuesEquipGroup());

        database.insert("Question", null, values);
    }

    public void updateQuestion(Question question) throws Exception {
        ContentValues values = new ContentValues();
        values.put("Questext", question.getQuestext());
        values.put("QuesType", question.getQuesType());
        values.put("QuesIsCritical", question.getQuesIsCritical());
        values.put("QuesEquipGroup", question.getQuesEquipGroup());

        database.update("Question", values, "QuesId=" + question.getQuesId(), null);
    }

    public Question getQuestion(int questId) {
        String selectQuery = "SELECT  * FROM " + "Question WHERE QuesId = " + questId;

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int quesId = Integer.parseInt(cursor.getString(0));
                String questext = cursor.getString(1);
                int quesType = Integer.parseInt(cursor.getString(2));
                int quesIsCritical = Integer.parseInt(cursor.getString(3));
                String quesEquipType = cursor.getString(4);
                Question quest = new Question(quesId, questext, quesType, quesIsCritical, quesEquipType);

                return quest;
            } while (cursor.moveToNext());
        }
        return null;
    }

    public void deleteQuestion(int questId) {
        database.delete("Question", "QuesId=" + questId, null);
    }

    public void updateQuestions(List<Question> questions) {

        List<Question> existedQuestions = getQuestions();

        for (int i = 0; i < existedQuestions.size(); ++i) {
            boolean removing = true;
            for (int j = 0; j < questions.size(); ++j) {
                if (existedQuestions.get(i).getQuesId() == questions.get(j).getQuesId()) {
                    removing = false;
                    break;
                }
            }

            if (removing) {
                deleteQuestion(existedQuestions.get(i).getQuesId());
            }
        }

        for (int i = 0; i < questions.size(); ++i) {
            boolean inserting = true;
            for (int j = 0; j < existedQuestions.size(); ++j) {
                if (existedQuestions.get(j).getQuesId() == questions.get(i).getQuesId()) {
                    inserting = false;
                    break;
                }
            }

            if (inserting) {
                try {
                    insertQuestion(questions.get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    updateQuestion(questions.get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ArrayList<WorkPlace> getWorkPlaceList() {
        ArrayList<WorkPlace> workPlaces = new ArrayList<>();
        String selectQuery = "SELECT  * FROM WorkPlace WHERE WorkPlaceIsActive=1";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String workPlaceCode = cursor.getString(0);
                String workPlaceName = cursor.getString(1);
                String workPlaceGroupName = cursor.getString(2);
                String workPlaceGroupCode = cursor.getString(3);
                int workPlaceIsActive = cursor.getInt(4);

                WorkPlace workPlaceItem = new WorkPlace(workPlaceCode, workPlaceName, workPlaceGroupName, workPlaceGroupCode, workPlaceIsActive);
                workPlaces.add(workPlaceItem);
            } while (cursor.moveToNext());
        }
        return workPlaces;
    }

    public ArrayList<TaskUser> getUserList() {
        ArrayList<TaskUser> userList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM TaskUser";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int userId = cursor.getInt(0);
                String userName = cursor.getString(1);
                String userLogin = cursor.getString(2);
                String userPassword = cursor.getString(3);
                int userAreaId = cursor.getInt(4);
                String userAreaName = cursor.getString(5);
                String userRole = cursor.getString(6);
                String userEmail = cursor.getString(7);

                TaskUser userItem = new TaskUser(userId, userName, userLogin, userPassword, userAreaId, userAreaName, userRole, userEmail);
                userList.add(userItem);
            } while (cursor.moveToNext());
        }
        return userList;
    }

    public ArrayList<GSM> getGSMList() {
        ArrayList<GSM> gsmList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM GSM";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String DateEvent = cursor.getString(0);
                String Date = cursor.getString(1);
                String Shift = cursor.getString(2);
                String EquipOut = cursor.getString(3);
                String EquipIn = cursor.getString(4);
                String EmplOut = cursor.getString(5);
                String Reason = cursor.getString(6);
                float DT = cursor.getFloat(7);
                float SAE15W40 = cursor.getFloat(8);
                float SAE50 = cursor.getFloat(9);
                float SAE10W40 = cursor.getFloat(10);
                float T46 = cursor.getFloat(11);
                int Deleted = cursor.getInt(12);
                int SendToServer = cursor.getInt(13);
                int Confirmed = cursor.getInt(14);

                GSM gsmItem = new GSM(DateEvent, Date, Shift, EquipOut, EquipIn, EmplOut, Reason, DT, SAE15W40, SAE50, SAE10W40, T46, Deleted, SendToServer, Confirmed);
                gsmList.add(gsmItem);
            } while (cursor.moveToNext());
        }
        return gsmList;
    }

    public ArrayList<TaskEmployee> getEmployeeList() {
        ArrayList<TaskEmployee> employees = new ArrayList<>();
        String selectQuery = "SELECT  * FROM TaskEmployee";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int taskEmplId = cursor.getInt(0);
                String taskEmplName = cursor.getString(1);
                String taskEmplLogin = cursor.getString(2);
                String taskEmplPassword = cursor.getString(3);
                int taskEmplAreaId = cursor.getInt(4);
                String taskEmplAreaName = cursor.getString(5);
                String taskEmplProffesion = cursor.getString(6);
                String taskEmplRole = cursor.getString(7);

                TaskEmployee employeeItem = new TaskEmployee(taskEmplId, taskEmplName, taskEmplLogin, taskEmplPassword, taskEmplAreaId, taskEmplAreaName, taskEmplProffesion, taskEmplRole);
                employees.add(employeeItem);
            } while (cursor.moveToNext());
        }
        return employees;
    }

    //запросы к бд для пользователей
    public ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();
        String selectQuery = "SELECT  * FROM User";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int userId = Integer.parseInt(cursor.getString(0));
                String userName = cursor.getString(1);
                String userLogin = cursor.getString(2);
                String userPassword = cursor.getString(3);
                int userAreaId = cursor.getInt(4);
                String userRole = cursor.getString(5);
                User user = new User(userId, userName, userLogin, userPassword, userAreaId, userRole);
                users.add(user);
            } while (cursor.moveToNext());
        }
        return users;
    }

    public ArrayList<Equipment> getEquipment() {
        ArrayList<Equipment> equipments = new ArrayList<>();
        String selectQuery = "SELECT  * FROM Equipment";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int eqId = Integer.parseInt(cursor.getString(0));
                String eqName = cursor.getString(1);
                String eqArea = cursor.getString(2);
                Equipment eq = new Equipment(eqId, eqName, eqArea);
                equipments.add(eq);
            } while (cursor.moveToNext());
        }
        return equipments;
    }

    public void insertUser(User user) throws Exception {
        ContentValues values = new ContentValues();
        values.put("UserId", user.getUserId());
        values.put("UserName", user.getUserName());
        values.put("UserLogin", user.getUserLogin());
        values.put("UserPassword", user.getUserPassword());
        values.put("UserAreaId", user.getUserAreaId());
        values.put("UserRole", user.getUserRole());

        database.insert("User", null, values);
    }

    public void insertEquipment(Equipment eq) throws Exception {
        ContentValues values = new ContentValues();
        values.put("EquipmentId", eq.getEquipmentId());
        values.put("EquipmentName", eq.getEquipmentName());
        values.put("EquipmentArea", eq.getEquipmentArea());

        database.insert("Equipment", null, values);
    }

    public void insertRiskSafety(RiskSafety rs) throws Exception {
        ContentValues values = new ContentValues();
        values.put("Risk", rs.getRisk());
        values.put("Safety", rs.getSafety());
        values.put("Instruction", rs.getInstruction());
        database.insert("RiskSafety", null, values);
    }

    public void insertReason(Reason rs) throws Exception {
        ContentValues values = new ContentValues();
        values.put("Description", rs.getDescription());
        values.put("ReasonType", rs.getReasonType());
        database.insert("GSMReason", null, values);
    }

    public void updateUser(User user) throws Exception {
        ContentValues values = new ContentValues();
        values.put("UserName", user.getUserName());
        values.put("UserLogin", user.getUserLogin());
        values.put("UserPassword", user.getUserPassword());
        values.put("UserAreaId", user.getUserAreaId());
        values.put("UserRole", user.getUserRole());

        database.update("User", values, "UserId=" + user.getUserId(), null);
    }

    public void updateEquipmentData(Equipment eq) throws Exception {
        ContentValues values = new ContentValues();
        values.put("EquipmentName", eq.getEquipmentName());
        values.put("EquipmentArea", eq.getEquipmentArea());

        database.update("Equipment", values, "EquipmentId=" + eq.getEquipmentId(), null);
    }

    public User getUser(int userId) {
        String selectQuery = "SELECT  * FROM " + "User WHERE UserId = " + userId;

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String userName = cursor.getString(1);
                String userLogin = cursor.getString(2);
                String userPassword = cursor.getString(3);
                int userAreaId = cursor.getInt(4);
                String userRole = cursor.getString(5);

                User user = new User(userId, userName, userLogin, userPassword, userAreaId, userRole);

                return user;
            } while (cursor.moveToNext());
        }
        return null;
    }

    public void deleteUser(int userId) {
        database.delete("User", "UserId=" + userId, null);
    }

    public void deleteEquipment(int eqId) {
        database.delete("Equipment", "EquipmentId=" + eqId, null);
    }

    public void updateUsers(List<User> users) {
        List<User> existedUsers = getUsers();

        for (int i = 0; i < existedUsers.size(); ++i) {
            boolean removing = true;
            for (int j = 0; j < users.size(); ++j) {
                if (existedUsers.get(i).getUserId() == users.get(j).getUserId()) {
                    removing = false;
                    break;
                }
            }

            if (removing) {
                deleteUser(existedUsers.get(i).getUserId());
            }
        }

        for (int i = 0; i < users.size(); ++i) {
            boolean inserting = true;
            for (int j = 0; j < existedUsers.size(); ++j) {
                if (existedUsers.get(j).getUserId() == users.get(i).getUserId()) {
                    inserting = false;
                    break;
                }
            }

            if (inserting) {
                try {
                    insertUser(users.get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    updateUser(users.get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateEquipment(List<Equipment> equipment) {

        Log.d("Alexey", "List<Equipment> : " + equipment);
        List<Equipment> existedEquipment = getEquipment();

        for (int i = 0; i < existedEquipment.size(); ++i) {
            boolean removing = true;
            for (int j = 0; j < equipment.size(); ++j) {
                if (existedEquipment.get(i).getEquipmentId() == equipment.get(j).getEquipmentId()) {
                    removing = false;
                    break;
                }
            }

            if (removing) {
                deleteEquipment(existedEquipment.get(i).getEquipmentId());
            }
        }

        for (int i = 0; i < equipment.size(); ++i) {
            boolean inserting = true;
            for (int j = 0; j < existedEquipment.size(); ++j) {
                if (existedEquipment.get(j).getEquipmentId() == equipment.get(i).getEquipmentId()) {
                    inserting = false;
                    break;
                }
            }

            if (inserting) {
                try {
                    insertEquipment(equipment.get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Log.d("Alexey", "updateEquipmentData : " + equipment.get(i).getEquipmentId() + ", " + equipment.get(i).getEquipmentArea());
                    updateEquipmentData(equipment.get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateReason(List<Reason> rs) {
        deleteReason();
        for (int i = 0; i < rs.size(); ++i) {
            try {
                insertReason(rs.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateRiskSafety(List<RiskSafety> rs) {

        for (int i = 0; i < rs.size(); ++i) {
            try {
                insertRiskSafety(rs.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void create_db(Context context) throws IOException {
        InputStream myInput = null;
        OutputStream myOutput = null;

        DB_PATH = context.getDatabasePath(DB_NAME).getPath();

        try {
            //File file = new File(DB_PATH + DB_NAME);
            File file = new File(DB_PATH);
            if (!file.exists()) {

                this.getReadableDatabase();
                //получаем локальную бд как поток
                myInput = myContext.getAssets().open(DB_NAME);
                // Путь к новой бд
                //String outFileName = DB_PATH + DB_NAME;
                String outFileName = DB_PATH;

                // Открываем пустую бд
                myOutput = new FileOutputStream(outFileName);

                // побайтово копируем данные
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                myOutput.flush();
                myOutput.close();
                myInput.close();
            }
        } catch (IOException ex) {
            Toast.makeText(myContext.getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            throw new IOException();
        }
    }

    //открытие БД
    public void open(Context context) throws SQLException {

        DB_PATH = context.getDatabasePath(DB_NAME).getPath();
        //String path = DB_PATH + DB_NAME;
        String path = DB_PATH;
        database = SQLiteDatabase.openDatabase(path, null,
                SQLiteDatabase.OPEN_READWRITE);
    }

    //закрытие БД
    @Override
    public synchronized void close() {
        if (database != null) {
            database.close();
        }
        super.close();
    }

    public void deleteWorkPlace(String workPlace) {
        database.delete("WorkPlace", "WorkPlaceCode='" + workPlace + "'", null);
    }

    public void deleteTask() {
        database.delete("Task", null, null);
    }

    public void deleteTaskDetail() {
        database.delete("TaskDetail", null, null);
    }

    public void deleteTaskModify() {
        Log.d("Alexey", "ECP: deleteTaskModify 1 ");
        database.delete("TaskDetailModify", null, null);
        Log.d("Alexey", "ECP: deleteTaskModify 2 ");
    }

    public void deleteReason() {
        database.delete("GSMReason", null, null);
    }

    public void deleteTaskUser(int userId) {
        database.delete("TaskUser", "TaskUserId='" + userId + "'", null);
    }

    public void deleteEmployee(int employeeid) {
        database.delete("TaskEmployee", "TaskEmplId='" + employeeid + "'", null);
    }

    public void insertWorkPlace(WorkPlace workPlace) throws Exception {
        ContentValues values = new ContentValues();
        values.put("WorkPlaceCode", workPlace.getWorkPlaceCode());
        values.put("WorkPlaceName", workPlace.getWorkPlaceName());
        values.put("WorkPlaceGroupName", workPlace.getWorkPlaceGroupName());
        values.put("WorkPlaceGroupCode", workPlace.getWorkPlaceGroupCode());
        values.put("WorkPlaceIsActive", workPlace.getWorkPlaceIsActive());

        database.insert("WorkPlace", null, values);
    }

    public void insertTask(Task task) throws Exception {
        ContentValues values = new ContentValues();
        values.put("TaskId", task.getTaskId());
        values.put("TaskDate", task.getTaskDate());
        values.put("TaskShift", task.getTaskShift());
        values.put("TaskWorkPlaceCode", task.getTaskWorkPlaceCode());
        values.put("TaskEquipId", task.getTaskEquipId());
        values.put("TaskEmplId", task.getTaskEmplId());
        values.put("TaskWorkPlaceName", task.getTaskWorkPlaceName());
        values.put("TaskEquipName", task.getTaskEquipName());
        values.put("TaskEmplName", task.getTaskEmplName());
        values.put("TaskUserId", task.getTaskUserId());
        values.put("TaskUserName", task.getTaskUserName());
        values.put("TaskStateId", task.getTaskStateId());
        values.put("TaskStateName", task.getTaskStateName());
        values.put("TaskSignId", task.getTaskSignId());
        values.put("TaskSignName", task.getTaskSignName());
        values.put("HaveCkWP", task.getHaveCkWP());
        values.put("HaveCkEq", task.getHaveCkEq());
        values.put("TaskEquipTypeId", task.getTaskEquipTypeId());

        database.insert("Task", null, values);
    }

    public void insertTaskDetail(TaskDetail taskDetail) throws Exception {
        ContentValues values = new ContentValues();
        values.put("TDId", taskDetail.getTDId());
        values.put("TDTaskId", taskDetail.getTDTaskId());
        values.put("TDWorkType", taskDetail.getTDWorkType());
        values.put("TDUnit", taskDetail.getTDUnit());
        values.put("TDWP1", taskDetail.getTDWP1());
        values.put("TDWP2", taskDetail.getTDWP2());
        values.put("TDEquipment", taskDetail.getTDEquipment());
        values.put("TDValue", taskDetail.getTDValue());
        values.put("TDFact", taskDetail.getTDFact());
        values.put("TDModifySignedUser", taskDetail.getTDModifySignedUser());
        values.put("TDModifySignedEmpl", taskDetail.getTDModifySignedEmpl());

        database.insert("TaskDetail", null, values);
    }

    public void insertTaskModify(TaskDetailModify taskDetailModify) throws Exception {
        ContentValues values = new ContentValues();
        values.put("Id", taskDetailModify.getId());
        values.put("TaskId", taskDetailModify.getTaskId());
        values.put("WorkType", taskDetailModify.getWorkType());
        values.put("Unit", taskDetailModify.getUnit());
        values.put("WP1", taskDetailModify.getWP1());
        values.put("WP2", taskDetailModify.getWP2());
        values.put("Equipment", taskDetailModify.getEquipment());
        values.put("Value", taskDetailModify.getValue());
        values.put("Fact", taskDetailModify.getFact());
        values.put("SingedUser", taskDetailModify.getSingedUser());
        values.put("SingedEmpl", taskDetailModify.getSingedEmpl());

        database.insert("TaskDetailModify", null, values);
    }

    public void insertTaskUser(TaskUser taskUser) throws Exception {
        ContentValues values = new ContentValues();
        values.put("TaskUserId", taskUser.getUserId());
        values.put("TaskUserName", taskUser.getUserName());
        values.put("TaskUserLogin", taskUser.getUserLogin());
        values.put("TaskUserPassword", taskUser.getUserPassword());
        values.put("TaskUserAreaId", taskUser.getUserAreaId());
        values.put("TaskUserAreaName", taskUser.getUserAreaName());
        values.put("TaskUserRole", taskUser.getUserRole());
        values.put("TaskUserEmail", taskUser.getUserEmail());

        database.insert("TaskUser", null, values);
    }

    public void insertGSM(GSM gsm) throws Exception {
        ContentValues values = new ContentValues();
        values.put("DateEvent", gsm.getDateEvent().replace("T", " "));
        values.put("Date", gsm.getDate().replace("T", " "));
        values.put("Shift", gsm.getShift());
        values.put("EquipOut", gsm.getEquipOut());
        values.put("EquipIn", gsm.getEquipIn());
        values.put("EmplOut", gsm.getEmplOut());
        values.put("Reason", gsm.getReason());
        values.put("DT", gsm.getDT());
        values.put("SAE15W40", gsm.getSAE15W40());
        values.put("SAE50", gsm.getSAE50());
        values.put("SAE10W40", gsm.getSAE10W40());
        values.put("T46", gsm.getT46());
        values.put("Deleted", gsm.getDeleted());
        values.put("SendToServer", gsm.getSendToServer());
        values.put("Confirmed", gsm.getConfirmed());

        database.insert("GSM", null, values);
    }

    public void updateGSM(GSM gsm) throws Exception {
        ContentValues values = new ContentValues();
        values.put("DateEvent", gsm.getDateEvent().replace("T", " "));
        values.put("Date", gsm.getDate().replace("T", " "));
        values.put("Shift", gsm.getShift());
        values.put("EquipOut", gsm.getEquipOut());
        values.put("EquipIn", gsm.getEquipIn());
        values.put("EmplOut", gsm.getEmplOut());
        values.put("Reason", gsm.getReason());
        values.put("DT", gsm.getDT());
        values.put("SAE15W40", gsm.getSAE15W40());
        values.put("SAE50", gsm.getSAE50());
        values.put("SAE10W40", gsm.getSAE10W40());
        values.put("T46", gsm.getT46());
        values.put("Deleted", gsm.getDeleted());
        values.put("SendToServer", gsm.getSendToServer());
        values.put("Confirmed", gsm.getConfirmed());

        Log.d("Alexey", "GSM Заправщик update Confirmed= " + gsm.getConfirmed() + " DateEvent= " + gsm.getDateEvent().replace("T", " "));

        database.update("GSM", values, "DateEvent='" + gsm.getDateEvent().replace("T", " ") + "'", null);
    }

    public void insertEmployee(TaskEmployee taskEmployee) throws Exception {
        ContentValues values = new ContentValues();
        values.put("TaskEmplId", taskEmployee.getEmployeeId());
        values.put("TaskEmplName", taskEmployee.getEmployeeName());
        values.put("TaskEmplLogin", taskEmployee.getEmployeeLogin());
        values.put("TaskEmplPassword", taskEmployee.getEmployeePassword());
        values.put("TaskEmplAreaId", taskEmployee.getEmployeeAreaId());
        values.put("TaskEmplAreaName", taskEmployee.getEmployeeName());
        values.put("TaskEmplProffesion", taskEmployee.getEmployeeProffesion());
        values.put("TaskEmplRole", taskEmployee.getEmployeeRole());

        database.insert("TaskEmployee", null, values);
    }

    public void updateWorkPlace(WorkPlace workPlace) throws Exception {
        ContentValues values = new ContentValues();
        values.put("WorkPlaceName", workPlace.getWorkPlaceName());
        values.put("WorkPlaceGroupName", workPlace.getWorkPlaceGroupName());
        values.put("WorkPlaceGroupCode", workPlace.getWorkPlaceGroupCode());
        values.put("WorkPlaceIsActive", workPlace.getWorkPlaceIsActive());

        database.update("WorkPlace", values, "WorkPlaceCode='" + workPlace.getWorkPlaceCode() + "'", null);
    }

    public void updateTaskUser(TaskUser taskUser) throws Exception {
        ContentValues values = new ContentValues();

        values.put("TaskUserName", taskUser.getUserName());
        values.put("TaskUserLogin", taskUser.getUserLogin());
        values.put("TaskUserPassword", taskUser.getUserPassword());
        values.put("TaskUserAreaId", taskUser.getUserAreaId());
        values.put("TaskUserAreaName", taskUser.getUserAreaName());
        values.put("TaskUserRole", taskUser.getUserRole());
        values.put("TaskUserEmail", taskUser.getUserEmail());

        database.update("TaskUser", values, "TaskUserId='" + taskUser.getUserId() + "'", null);
    }

    public void updateEmployee(TaskEmployee taskEmployee) throws Exception {
        ContentValues values = new ContentValues();

        values.put("TaskEmplName", taskEmployee.getEmployeeName());
        values.put("TaskEmplLogin", taskEmployee.getEmployeeLogin());
        values.put("TaskEmplPassword", taskEmployee.getEmployeePassword());
        values.put("TaskEmplAreaId", taskEmployee.getEmployeeAreaId());
        values.put("TaskEmplAreaName", taskEmployee.getEmployeeName());
        values.put("TaskEmplProffesion", taskEmployee.getEmployeeProffesion());
        values.put("TaskEmplRole", taskEmployee.getEmployeeRole());

        database.update("TaskEmployee", values, "TaskEmplId='" + taskEmployee.getEmployeeId() + "'", null);
    }

    public void updateChatMsg(ChatModel chatModel) throws Exception {
        ContentValues values = new ContentValues();

        values.put("IsSendToServer", 1);

        Log.d("Alexey", "Chat123 424: " + chatModel.getDateTime());

        database.update("Chat", values, "DateTime='" + chatModel.getDateTime().replace("T"," ") + "'", null);
    }


    public void updateWorkPlaceList(List<WorkPlace> workPlaceList) {
        List<WorkPlace> existedWorkPlaces = getWorkPlaceList();

        for (int i = 0; i < existedWorkPlaces.size(); ++i) {
            boolean removing = true;
            for (int j = 0; j < workPlaceList.size(); ++j) {
                if (existedWorkPlaces.get(i).getWorkPlaceCode() == workPlaceList.get(j).getWorkPlaceCode()) {
                    removing = false;
                    break;
                }
            }

            if (removing) {
                deleteWorkPlace(existedWorkPlaces.get(i).getWorkPlaceCode());
            }
        }

        for (int i = 0; i < workPlaceList.size(); ++i) {
            boolean inserting = true;
            for (int j = 0; j < existedWorkPlaces.size(); ++j) {
                if (existedWorkPlaces.get(j).getWorkPlaceCode() == workPlaceList.get(i).getWorkPlaceCode()) {
                    inserting = false;
                    break;
                }
            }

            if (inserting) {
                try {
                    insertWorkPlace(workPlaceList.get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    updateWorkPlace(workPlaceList.get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateGSM(List<GSM> gsmList, String eq) {
        List<GSM> existedGSM = getGSMList();
        Log.d("Alexey", "GSMвбазу: кол-во " + gsmList.size());
        for (int i = 0; i < gsmList.size(); ++i) {
            boolean inserting = true;
            for (int j = 0; j < existedGSM.size(); ++j) {
                Log.d("Alexey", "GSMвбазу: время старое " + existedGSM.get(j).getDateEvent() + " / время новое " + gsmList.get(i).getDateEvent().replace("T", " "));
                if (existedGSM.get(j).getDateEvent().equals(gsmList.get(i).getDateEvent().replace("T", " "))) {
                    inserting = false;
                    break;
                }
            }

            Log.d("Alexey", "GSMвбазу: inserting " + inserting);
            if (inserting) {
                try {
                    insertGSM(gsmList.get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (eq.equals("Заправщик")) {
                    try {
                        updateGSM(gsmList.get(i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public ArrayList<HelpInUseApps> getHelpInUseApps() {
        ArrayList<HelpInUseApps> helpInUseAppsArray = new ArrayList<>();
        String selectQuery = "SELECT  * FROM HelpInUseApps";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String NameButton = cursor.getString(1);
                String LinkVideo = cursor.getString(2);
                int IsDelete = cursor.getInt(3);
                int OrderButton = cursor.getInt(4);

                HelpInUseApps helpInUseAppsItem = new HelpInUseApps(NameButton, LinkVideo, IsDelete, OrderButton);
                helpInUseAppsArray.add(helpInUseAppsItem);
            } while (cursor.moveToNext());
        }
        return helpInUseAppsArray;
    }


    public void insertHelpInUseApps(HelpInUseApps helpInUseApps) {
        ContentValues values = new ContentValues();
        values.put("NameButton", helpInUseApps.getNameButton());
        values.put("LinkVideo", helpInUseApps.getLinkVideo());
        values.put("IsDelete", helpInUseApps.getIsDelete());
        values.put("OrderButton", helpInUseApps.getOrderButton());

        database.insert("HelpInUseApps", null, values);
    }

    public void updateHelpInUseApps(List<HelpInUseApps> helpInUseApps) {
        List<HelpInUseApps> helpInUseAppsArray = getHelpInUseApps();
        Log.d("Alexey", "lesson кол-во " + helpInUseApps.size());

        for (int i = 0; i < helpInUseApps.size(); ++i) {
            boolean inserting = true;
            for (int j = 0; j < helpInUseAppsArray.size(); ++j) {
                Log.d("Alexey", "lesson test " + helpInUseAppsArray.get(j).getNameButton() + "  " + helpInUseApps.get(i).getNameButton());
                if (helpInUseAppsArray.get(j).getNameButton().equals(helpInUseApps.get(i).getNameButton())) {
                    inserting = false;
                    break;
                }
            }

            Log.d("Alexey", "lesson inserting " + inserting);
            if (inserting) {
                try {
                    insertHelpInUseApps(helpInUseApps.get(i));
                    Log.d("Alexey", "lesson values " + helpInUseApps.get(i));
                } catch (Exception e) {
                    Log.d("Alexey", "lesson values " + e);
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateemplEcp(List<EmlpECPKey> emplEcp) {

        for (int i = 0; i < emplEcp.size(); ++i) {
            try {
                Log.d("Alexey", "testess " + emplEcp.get(i));
                insertemplEcp(emplEcp.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    private void insertemplEcp(EmlpECPKey emlpECPKey) {
        ContentValues values = new ContentValues();

        values.put("id", emlpECPKey.getEMPL_ID());
        values.put("KeyDateEcpired", emlpECPKey.getEMPL_KEY_EXPIRED_DATE());
        values.put("KeyDescription", emlpECPKey.getEMPL_KEY_DESCRIPTION());

        database.insert("EmlpECPKey", null, values);
    }


    public void updateTask(List<Task> task) {
        try {
            deleteTask();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < task.size(); ++i) {
            try {
                Log.d("Alexey", "task.get(i) " + task.get(i));
                insertTask(task.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateTaskDetail(List<TaskDetail> taskDetail) {
        try {
            deleteTaskDetail();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < taskDetail.size(); ++i) {
            try {
                insertTaskDetail(taskDetail.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateTaskModify(List<TaskDetailModify> taskDetailModify) {
        try {
            deleteTaskModify();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < taskDetailModify.size(); ++i) {
            try {
                insertTaskModify(taskDetailModify.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateUserList(List<TaskUser> taskUserList) {
        List<TaskUser> existedTaskUser = getUserList();

        for (int i = 0; i < existedTaskUser.size(); ++i) {
            boolean removing = true;
            for (int j = 0; j < taskUserList.size(); ++j) {
                if (existedTaskUser.get(i).getUserId() == taskUserList.get(j).getUserId()) {
                    removing = false;
                    break;
                }
            }

            if (removing) {
                deleteTaskUser(existedTaskUser.get(i).getUserId());
            }
        }

        for (int i = 0; i < taskUserList.size(); ++i) {
            boolean inserting = true;
            for (int j = 0; j < existedTaskUser.size(); ++j) {
                if (existedTaskUser.get(j).getUserId() == taskUserList.get(i).getUserId()) {
                    inserting = false;
                    break;
                }
            }

            if (inserting) {
                try {
                    insertTaskUser(taskUserList.get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    updateTaskUser(taskUserList.get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateEmployeeList(List<TaskEmployee> employeeList) {
        List<TaskEmployee> existedTaskEmployee = getEmployeeList();

        for (int i = 0; i < existedTaskEmployee.size(); ++i) {
            boolean removing = true;
            for (int j = 0; j < employeeList.size(); ++j) {
                if (existedTaskEmployee.get(i).getEmployeeId() == employeeList.get(j).getEmployeeId()) {
                    removing = false;
                    break;
                }
            }

            if (removing) {
                deleteEmployee(existedTaskEmployee.get(i).getEmployeeId());
            }
        }

        for (int i = 0; i < employeeList.size(); ++i) {
            boolean inserting = true;
            for (int j = 0; j < existedTaskEmployee.size(); ++j) {
                if (existedTaskEmployee.get(j).getEmployeeId() == employeeList.get(i).getEmployeeId()) {
                    inserting = false;
                    break;
                }
            }

            if (inserting) {
                try {
                    insertEmployee(employeeList.get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    updateEmployee(employeeList.get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    ///////////////////// Уведомления о аварии

    public ArrayList<Notification> getNotification() {
        ArrayList<Notification> NotificationArray = new ArrayList<>();
        String selectQuery = "SELECT * FROM Notification";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int NotifyId = cursor.getInt(0);
                String NotifyText = cursor.getString(1);
                String NotifyDate = cursor.getString(2);
                String NotifyType = cursor.getString(4);

                Notification notification = new Notification(NotifyId, NotifyText, NotifyDate, NotifyType);

                NotificationArray.add(notification);
            } while (cursor.moveToNext());
        }
        return NotificationArray;
    }


    public void insertNotification(Notification notification) {
        ContentValues values = new ContentValues();
        values.put("NotifyId", notification.getNotifyId());
        values.put("NotifyText", notification.getNotifyText());
        values.put("NotifyDate", notification.getNotifyDate().replace("T", " "));
        values.put("NotifyType", notification.getNotifyType());

        database.insert("Notification", null, values);
    }

    public void updateNotification(List<Notification> notificationList) {
        try {

            List<Notification> notificationArray = getNotification();
            for (int i = 0; i < notificationList.size(); ++i) {
                boolean inserting = true;
                for (int j = 0; j < notificationArray.size(); ++j) {

                    if (notificationArray.get(j).getNotifyId() == notificationList.get(i).getNotifyId()) {
                        inserting = false;
                        break;
                    }
                }

                if (inserting) {
                    try {
                        insertNotification(notificationList.get(i));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//////////////////////////////////////

    public void insertChatMsg(ChatModel chatModel) {
        ContentValues values = new ContentValues();
        values.put("UserTNFrom", chatModel.getUserTNFrom());
        values.put("UserNameFrom", chatModel.getUserNameFrom());
        values.put("UserTNTo", chatModel.getUserTNTo());
        values.put("UserNameTo", chatModel.getUserNameTo());

        values.put("DateTime", chatModel.getDateTime().replace("T"," "));
        values.put("Message", chatModel.getMessage());
        values.put("Status", chatModel.getStatus());
        values.put("Deleted", chatModel.getDeleted());
        values.put("IsSendToServer", 1);

        database.insert("Chat", null, values);
    }

    public ArrayList<ChatModel> getChatModel() {
        ArrayList<ChatModel> ChatMsgList = new ArrayList<>();
        String selectQuery = "SELECT * FROM Chat";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int UserTNFrom = cursor.getInt(1);
                String UserNameFrom = cursor.getString(2);
                int UserTNTo = cursor.getInt(3);
                String UserNameTo = cursor.getString(4);

                String DateTime = cursor.getString(5);
                String Message = cursor.getString(6);
                int Status = cursor.getInt(7);
                int Deleted = cursor.getInt(8);

                //Notification notification = new Notification(NotifyId, NotifyText, NotifyDate, NotifyType);

                ChatModel chatModel = new ChatModel(UserTNFrom, UserNameFrom,UserTNTo,UserNameTo, DateTime, Message, Status, Deleted);

                ChatMsgList.add(chatModel);
            } while (cursor.moveToNext());
        }
        return ChatMsgList;
    }

    public void updateChatMsg(List<ChatModel> chatMsgList) {
        try {
            Log.d("Alexey", "Chat123 1");
            List<ChatModel> chatMsgArray = getChatModel();
            Log.d("Alexey", "Chat123 2");
            for (int i = 0; i < chatMsgList.size(); ++i) {
                boolean inserting = true;
                for (int j = 0; j < chatMsgArray.size(); ++j) {
                    Log.d("Alexey", "Chat123 3: " + chatMsgArray.get(j).getDateTime().replace("T"," ") + " | " + chatMsgList.get(i).getDateTime().replace("T"," "));
                    if (chatMsgArray.get(j).getDateTime().replace("T"," ").equals(chatMsgList.get(i).getDateTime().replace("T"," "))) {

                        inserting = false;

                        Log.d("Alexey", "Chat123 4: " + inserting);
                        break;
                    }
                }

                if (inserting) {
                    try {
                        insertChatMsg(chatMsgList.get(i));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Log.d("Alexey", "Chat123 423: " + inserting);

                        updateChatMsg(chatMsgList.get(i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /////////////////


//
//    //////////////// БД ВПН
//    public ArrayList<VpnConnection> getVpnConnection() {
//        ArrayList<VpnConnection> VpnConnectionArray = new ArrayList<>();
//        String selectQuery = "SELECT * FROM VpnConnection";
//
//        Cursor cursor = database.rawQuery(selectQuery, null);
//
//        if (cursor.moveToFirst()) {
//            do {
//                String TimeWorkPhone = cursor.getString(0);
//
//                VpnConnection vpnConnection = new VpnConnection(TimeWorkPhone);
//
//                VpnConnectionArray.add(vpnConnection);
//
//            } while (cursor.moveToNext());
//        }
//        return VpnConnectionArray;
//    }
//
//    public void insertVpnConnection(VpnConnection vpnConnection) {
//        ContentValues values = new ContentValues();
//        values.put("TimeWorkPhone", vpnConnection.getTimeWorkPhone());
//
//        database.insert("VpnConnection", null, values);
//    }
//
//
//    public void updateVpnConnection(List<VpnConnection> vpnConnectionList) {
//
//        try {
//            for (int i = 0; i < vpnConnectionList.size(); ++i) {
//                boolean inserting = true;
//
//                if (inserting) {
//                    try {
//                        insertVpnConnection(vpnConnectionList.get(i));
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    ///////////////

}
