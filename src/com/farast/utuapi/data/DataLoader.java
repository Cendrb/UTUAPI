package com.farast.utuapi.data;

import com.farast.utuapi.util.*;
import com.farast.utuapi.util.functional_interfaces.Action;
import com.farast.utuapi.util.operations.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.util.*;

/**
 * Created by cendr_000 on 25.07.2016.
 */
public class DataLoader {

    private String baseUrl;

    private boolean dataLoaded;

    private Sclass lastSclass = null;

    private OperationManager operationListeners = new OperationManager();

    private Predata predata;
    private CreateUpdate editor;

    private List<AdditionalInfo> additionalInfosList;
    private List<Event> eventsList;
    private List<Exam> examsList;
    private List<Task> tasksList;
    private List<Article> articlesList;
    private List<Timetable> timetablesList;
    private List<Lesson> lessonsList;

    private User currentUser = null;

    public DataLoader(String url) {
        baseUrl = url;
        additionalInfosList = new ArrayList<>();
        eventsList = new ArrayList<>();
        examsList = new ArrayList<>();
        tasksList = new ArrayList<>();
        articlesList = new ArrayList<>();
        timetablesList = new ArrayList<>();
        lessonsList = new ArrayList<>();
        predata = new Predata();
        editor = new CreateUpdate();

        // automatically save cookies
        CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
    }

    public void loadPredata() throws IOException, SAXException, NumberFormatException {
        predata.load();
    }

    public boolean login(String email, String password) throws IOException {
        try {
            operationListeners.startOperation(new LoggingInOperation());
            URL url = new URL(baseUrl + "/login.xml");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");

            HashMap<String, String> formData = new HashMap<>();
            formData.put("email", email);
            formData.put("password", password);

            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(HTTPUtil.getPostDataString(formData));
            writer.flush();
            writer.close();
            outputStream.close();

            connection.getResponseCode();


            Element utuElement = XMLUtil.parseXml(connection.getInputStream());
            int userId = XMLUtil.getAndParseIntValueOfChild(utuElement, "user_id");
            int sclassId = XMLUtil.getAndParseIntValueOfChild(utuElement, "sclass_id");
            boolean admin = XMLUtil.getAndParseBooleanValueOfChild(utuElement, "admin");
            currentUser = new User(userId, admin, sclassId);
            return true;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return false;
        } catch (SAXException e) {
            e.printStackTrace();
            return false;
        } finally {
            operationListeners.endOperation();
        }
    }

    public void load(int sclassId) throws IOException, SAXException, NumberFormatException, CollectionUtil.RecordNotFoundException, CollectionUtil.MultipleRecordsWithSameIdException, DateFormatException, SclassDoesNotExistException {
        Sclass sclass = null;
        try {
            sclass = CollectionUtil.findById(getSclasses(), sclassId);
        } catch (CollectionUtil.RecordNotFoundException e) {
            throw new SclassDoesNotExistException(e);
        }
        load(sclass);
    }

    public void load(Sclass sclass) throws IOException, SAXException, NumberFormatException, CollectionUtil.RecordNotFoundException, CollectionUtil.MultipleRecordsWithSameIdException, DateFormatException {
        dataLoaded = false;
        lastSclass = sclass;
        if (!predata.isLoaded()) {
            predata.load();
        }
        loadTimetablesData(sclass);
        loadUtuData(sclass);
        dataLoaded = true;
    }

    private void loadTimetablesData(Sclass sclass) throws IOException, SAXException {
        try {
            operationListeners.startOperation(new TimetablesDataOperation());
            InputStream responseStream = HTTPUtil.openStream(baseUrl + "/api/timetables?sclass_id=" + sclass.getId());
            Element utuElement = XMLUtil.parseXml(responseStream);
            // timetables
            final NodeList timetables = XMLUtil.getNodeList(utuElement, "timetables", "timetable");
            timetablesList.clear();
            lessonsList.clear();
            XMLUtil.forEachElement(timetables, new Action<Element>() {
                @Override
                public void accept(Element parameter) {
                    int id = XMLUtil.getAndParseIntValueOfChild(parameter, "id");
                    String name = XMLUtil.getValueOfChild(parameter, "name");

                    List<Integer> sgroupIds = ArrayUtil.parseIntArray(XMLUtil.getValueOfChild(parameter, "sgroup_ids"));
                    List<Sgroup> sgroups = CollectionUtil.findByIds(predata.sgroupsList, sgroupIds);

                    final ArrayList<SchoolDay> schoolDays = new ArrayList<>();
                    XMLUtil.forEachElement(parameter.getElementsByTagName("day"), new Action<Element>() {
                        @Override
                        public void accept(Element parameter) {
                            try {
                                int id = XMLUtil.getAndParseIntValueOfChild(parameter, "id");
                                Date date = XMLUtil.getAndParseDateValueOfChild(parameter, "date");
                                final ArrayList<Lesson> lessons = new ArrayList<>();
                                XMLUtil.forEachElement(parameter.getElementsByTagName("lesson"), new Action<Element>() {
                                    @Override
                                    public void accept(Element parameter) {
                                        int id = XMLUtil.getAndParseIntValueOfChild(parameter, "id");
                                        int serialNumber = XMLUtil.getAndParseIntValueOfChild(parameter, "serial_number");
                                        String room = XMLUtil.getValueOfChild(parameter, "room");
                                        boolean notNormal = XMLUtil.getAndParseBooleanValueOfChild(parameter, "not_normal");
                                        String notNormalComment = XMLUtil.getValueOfChild(parameter, "not_normal_comment");
                                        String eventName = XMLUtil.getValueOfChild(parameter, "event_name");
                                        Subject subject = CollectionUtil.findById(predata.subjectsList, XMLUtil.getAndParseIntValueOfChild(parameter, "subject_id"));
                                        Teacher teacher = CollectionUtil.findById(predata.teachersList, XMLUtil.getAndParseIntValueOfChild(parameter, "teacher_id"));
                                        Lesson lesson = new Lesson(id, serialNumber, room, notNormal, notNormalComment, eventName, subject, teacher);
                                        lessons.add(lesson);
                                        lessonsList.add(lesson);
                                    }
                                });
                                schoolDays.add(new SchoolDay(id, date, lessons));
                            } catch (ParseException e) {
                                throw new DateFormatException(e);
                            }
                        }
                    });
                    timetablesList.add(new Timetable(id, name, sgroups, schoolDays));
                }
            });
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } finally {
            operationListeners.endOperation();
        }
    }

    private void loadUtuData(Sclass sclass) throws IOException, SAXException {
        try {
            operationListeners.startOperation(new UtuDataOperation());
            InputStream responseStream = HTTPUtil.openStream(baseUrl + "/api/data?sclass_id=" + sclass.getId());

            Element utuElement = XMLUtil.parseXml(responseStream);

            /*
            // no longer used, this info is served by response on login
            Element currentUserElement = XMLUtil.getElement(utuElement, "current_user");
            loggedIn = XMLUtil.getAndParseBooleanValueOfChild(currentUserElement, "logged_in");
            adminLoggedIn = XMLUtil.getAndParseBooleanValueOfChild(currentUserElement, "admin_logged_in");*/

            // additional infos
            final NodeList additionalInfos = XMLUtil.getNodeList(utuElement, "additional_infos_global", "additional_info");
            additionalInfosList.clear();
            XMLUtil.forEachElement(additionalInfos, new Action<Element>() {
                @Override
                public void accept(Element parameter) {
                    int id = XMLUtil.getAndParseIntValueOfChild(parameter, "id");
                    String name = XMLUtil.getValueOfChild(parameter, "name");
                    String url = XMLUtil.getValueOfChild(parameter, "url");
                    final int subjectId = XMLUtil.getAndParseIntValueOfChild(XMLUtil.getElement(parameter, "subject"), "id");
                    Subject subject = CollectionUtil.findById(predata.subjectsList, subjectId);
                    additionalInfosList.add(new AdditionalInfo(id, name, url, subject));
                }
            });

            // events
            final NodeList events = XMLUtil.getNodeList(utuElement, "events", "item");
            eventsList.clear();
            XMLUtil.forEachElement(events, new Action<Element>() {
                @Override
                public void accept(Element parameter) {
                    try {
                        eventsList.add(parseXMLEvent(parameter));
                    } catch (ParseException e) {
                        throw new DateFormatException(e);
                    }
                }
            });

            // exams
            final NodeList exams = XMLUtil.getNodeList(utuElement, "exams", "item");
            examsList.clear();
            XMLUtil.forEachElement(exams, new Action<Element>() {
                @Override
                public void accept(Element parameter) {
                    try {
                        examsList.add(parseXMLExam(parameter));
                    } catch (ParseException e) {
                        throw new DateFormatException(e);
                    }
                }
            });

            // tasks
            final NodeList tasks = XMLUtil.getNodeList(utuElement, "tasks", "item");
            tasksList.clear();
            XMLUtil.forEachElement(tasks, new Action<Element>() {
                @Override
                public void accept(Element parameter) {
                    try {
                        tasksList.add(parseXMLTask(parameter));
                    } catch (ParseException e) {
                        throw new DateFormatException(e);
                    }
                }
            });

            // articles
            final NodeList articles = XMLUtil.getNodeList(utuElement, "articles", "item");
            articlesList.clear();
            XMLUtil.forEachElement(articles, new Action<Element>() {
                @Override
                public void accept(Element parameter) {
                    try {
                        articlesList.add(parseXMLArticle(parameter));
                    } catch (ParseException e) {
                        throw new DateFormatException(e);
                    }
                }
            });
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } finally {
            operationListeners.endOperation();
        }
    }

    private Event parseXMLEvent(Element parameter) throws ParseException {
        int id = XMLUtil.getAndParseIntValueOfChild(parameter, "id");
        String title = XMLUtil.getValueOfChild(parameter, "title");
        String description = XMLUtil.getValueOfChild(parameter, "description");
        String location = XMLUtil.getValueOfChild(parameter, "location");
        int price = XMLUtil.getAndParseIntValueOfChild(parameter, "price");
        Date start = XMLUtil.getAndParseDateValueOfChild(parameter, "start");
        Date end = XMLUtil.getAndParseDateValueOfChild(parameter, "end");
        Date payDate = XMLUtil.getAndParseDateValueOfChild(parameter, "pay_date");
        final int sgroupId = XMLUtil.getAndParseIntValueOfChild(XMLUtil.getElement(parameter, "sgroup"), "id");
        Sgroup sgroup = CollectionUtil.findById(predata.sgroupsList, sgroupId);
        final List<Integer> additionalInfoIds = new ArrayList<>();
        XMLUtil.forEachElement(XMLUtil.getNodeList(parameter, "additional_infos", "additional_info"), new Action<Element>() {
            @Override
            public void accept(Element parameter) {
                additionalInfoIds.add(XMLUtil.getAndParseIntValueOfChild(parameter, "id"));
            }
        });
        List<AdditionalInfo> additionalInfos = CollectionUtil.findByIds(additionalInfosList, additionalInfoIds);
        boolean done = XMLUtil.getAndParseBooleanValueOfChild(parameter, "done");
        return new Event(id, title, description, location, price, start, end, payDate, sgroup, additionalInfos, done);
    }

    private Exam parseXMLExam(Element parameter) throws ParseException {
        int id = XMLUtil.getAndParseIntValueOfChild(parameter, "id");
        String title = XMLUtil.getValueOfChild(parameter, "title");
        String description = XMLUtil.getValueOfChild(parameter, "description");
        Date date = XMLUtil.getAndParseDateValueOfChild(parameter, "date");
        final int subjectId = XMLUtil.getAndParseIntValueOfChild(XMLUtil.getElement(parameter, "subject"), "id");
        Subject subject = CollectionUtil.findById(predata.subjectsList, subjectId);
        final int sgroupId = XMLUtil.getAndParseIntValueOfChild(XMLUtil.getElement(parameter, "sgroup"), "id");
        Sgroup sgroup = CollectionUtil.findById(predata.sgroupsList, sgroupId);
        final List<Integer> additionalInfoIds = new ArrayList<>();
        XMLUtil.forEachElement(XMLUtil.getNodeList(parameter, "additional_infos", "additional_info"), new Action<Element>() {
            @Override
            public void accept(Element parameter) {
                additionalInfoIds.add(XMLUtil.getAndParseIntValueOfChild(parameter, "id"));
            }
        });

        List<Integer> lessonIds = ArrayUtil.parseIntArray(XMLUtil.getValueOfChild(parameter, "lesson_ids"));
        List<Lesson> lessons = CollectionUtil.findByIds(lessonsList, lessonIds);

        List<AdditionalInfo> additionalInfos = CollectionUtil.findByIds(additionalInfosList, additionalInfoIds);
        boolean done = XMLUtil.getAndParseBooleanValueOfChild(parameter, "done");
        String type = XMLUtil.getValueOfChild(parameter, "type");
        Exam.Type realType;
        if (Objects.equals(type, "written_exam"))
            realType = Exam.Type.written;
        else
            realType = Exam.Type.raking;
        return new Exam(id, title, description, date, subject, sgroup, additionalInfos, done, lessons, realType);
    }

    private Task parseXMLTask(Element parameter) throws ParseException {
        int id = XMLUtil.getAndParseIntValueOfChild(parameter, "id");
        String title = XMLUtil.getValueOfChild(parameter, "title");
        String description = XMLUtil.getValueOfChild(parameter, "description");
        Date date = XMLUtil.getAndParseDateValueOfChild(parameter, "date");
        final int subjectId = XMLUtil.getAndParseIntValueOfChild(XMLUtil.getElement(parameter, "subject"), "id");
        Subject subject = CollectionUtil.findById(predata.subjectsList, subjectId);
        final int sgroupId = XMLUtil.getAndParseIntValueOfChild(XMLUtil.getElement(parameter, "sgroup"), "id");
        Sgroup sgroup = CollectionUtil.findById(predata.sgroupsList, sgroupId);
        final List<Integer> additionalInfoIds = new ArrayList<>();
        XMLUtil.forEachElement(XMLUtil.getNodeList(parameter, "additional_infos", "additional_info"), new Action<Element>() {
            @Override
            public void accept(Element parameter) {
                additionalInfoIds.add(XMLUtil.getAndParseIntValueOfChild(parameter, "id"));
            }
        });
        List<Integer> lessonIds = ArrayUtil.parseIntArray(XMLUtil.getValueOfChild(parameter, "lesson_ids"));
        List<Lesson> lessons = CollectionUtil.findByIds(lessonsList, lessonIds);

        List<AdditionalInfo> additionalInfos = CollectionUtil.findByIds(additionalInfosList, additionalInfoIds);
        boolean done = XMLUtil.getAndParseBooleanValueOfChild(parameter, "done");
        return new Task(id, title, description, date, subject, sgroup, additionalInfos, done, lessons);
    }

    private Article parseXMLArticle(Element parameter) throws ParseException {
        int id = XMLUtil.getAndParseIntValueOfChild(parameter, "id");
        String title = XMLUtil.getValueOfChild(parameter, "title");
        String description = XMLUtil.getValueOfChild(parameter, "description");
        Date publishedOn = XMLUtil.getAndParseDateTimeValueOfChild(parameter, "published_on");
        final int sgroupId = XMLUtil.getAndParseIntValueOfChild(XMLUtil.getElement(parameter, "sgroup"), "id");
        Sgroup sgroup = CollectionUtil.findById(predata.sgroupsList, sgroupId);
        final List<Integer> additionalInfoIds = new ArrayList<>();
        XMLUtil.forEachElement(XMLUtil.getNodeList(parameter, "additional_infos", "additional_info"), new Action<Element>() {
            @Override
            public void accept(Element parameter) {
                additionalInfoIds.add(XMLUtil.getAndParseIntValueOfChild(parameter, "id"));
            }
        });
        List<AdditionalInfo> additionalInfos = CollectionUtil.findByIds(additionalInfosList, additionalInfoIds);
        return new Article(id, title, description, publishedOn, sgroup, additionalInfos);
    }

    public List<Teacher> getTeachers() {
        if (predata.loaded)
            return new ArrayList<>(predata.teachersList);
        else
            throw new PredataNotLoadedException();
    }

    public List<Sclass> getSclasses() {
        if (predata.loaded)
            return new ArrayList<>(predata.sclassesList);
        else
            throw new PredataNotLoadedException();
    }

    public List<GroupCategory> getGroupCategories() {
        if (predata.loaded)
            return new ArrayList<>(predata.groupCategoriesList);
        else
            throw new PredataNotLoadedException();
    }

    public List<Subject> getSubjects() {
        if (predata.loaded)
            return new ArrayList<>(predata.subjectsList);
        else
            throw new PredataNotLoadedException();
    }

    public List<Timetable> getTimetablesList() {
        return new ArrayList<>(timetablesList);
    }

    public List<AdditionalInfo> getAdditionalInfosList() {
        return new ArrayList<>(additionalInfosList);
    }

    public List<Event> getEventsList() {
        return new ArrayList<>(eventsList);
    }

    public List<Exam> getExamsList() {
        return new ArrayList<>(examsList);
    }

    public List<Task> getTasksList() {
        return new ArrayList<>(tasksList);
    }

    public List<TEItem> getTEsList() {
        List<TEItem> teItems = new ArrayList<>();
        teItems.addAll(tasksList);
        teItems.addAll(examsList);
        Comparator<TEItem> teItemComparator = new Comparator<TEItem>() {
            @Override
            public int compare(TEItem o1, TEItem o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        };
        Collections.sort(teItems, teItemComparator);
        return teItems;
    }

    public List<Article> getArticlesList() {
        return new ArrayList<>(articlesList);
    }

    public List<Sgroup> getSgroupsList() {
        return predata.sgroupsList;
    }

    public boolean isPredataLoaded() {
        return predata.isLoaded();
    }

    public OperationManager getOperationManager() {
        return operationListeners;
    }

    public boolean isLoaded() {
        return dataLoaded;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public CreateUpdate getEditor() {
        return editor;
    }

    public static class PredataNotLoadedException extends RuntimeException {
        @Override
        public String getMessage() {
            return "This method cannot be called before predata is loaded";
        }
    }

    public static class SclassUnknownException extends Exception {
        @Override
        public String getMessage() {
            return "Sclass needs to be specified by calling load() before calling this method";
        }
    }

    public class AdminRequiredException extends Throwable {
        @Override
        public String getMessage() {
            return "This method can be used only when administrator is logged in.";
        }
    }

    public class CreateUpdate {
        public String[] requestCUEvent(Event event, String title, String description, String location, int price, Date start, Date end, Date payDate, Sgroup sgroup, List<AdditionalInfo> additionalInfos) throws AdminRequiredException, IOException, SclassUnknownException {
            Event eventNew;
            if (event == null) {
                eventNew = new Event(-1, title, description, location, price, start, end, payDate, sgroup, additionalInfos, false);
            } else {
                eventNew = new Event(event.getId(), title, description, location, price, start, end, payDate, sgroup, additionalInfos, false);
            }
            return updateCU(eventNew, event);
        }

        public String[] requestCUExam(Exam exam, String title, String description, Date date, Subject subject, Sgroup sgroup, List<AdditionalInfo> additionalInfos, Exam.Type type) throws AdminRequiredException, IOException, SclassUnknownException {
            Exam examNew;
            if (exam == null) {
                examNew = new Exam(-1, title, description, date, subject, sgroup, additionalInfos, false, new ArrayList<Lesson>(), type);
            } else {
                examNew = new Exam(exam.getId(), title, description, date, subject, sgroup, additionalInfos, false, new ArrayList<Lesson>(), type);
            }
            return updateCU(examNew, exam);
        }

        public String[] requestCUTask(Task task, String title, String description, Date date, Subject subject, Sgroup sgroup, List<AdditionalInfo> additionalInfos) throws AdminRequiredException, IOException, SclassUnknownException {
            Task taskNew;
            if (task == null) {
                taskNew = new Task(-1, title, description, date, subject, sgroup, additionalInfos, false, new ArrayList<Lesson>());
            } else {
                taskNew = new Task(task.getId(), title, description, date, subject, sgroup, additionalInfos, false, new ArrayList<Lesson>());
            }
            return updateCU(taskNew, task);
        }

        public String[] requestCUArticle(Article article, String title, String description, Date publishedOn, Sgroup sgroup, List<AdditionalInfo> additionalInfos) throws AdminRequiredException, IOException, SclassUnknownException {
            Article articleNew;
            if (article == null) {
                articleNew = new Article(-1, title, description, publishedOn, sgroup, additionalInfos);
            } else {
                articleNew = new Article(article.getId(), title, description, publishedOn, sgroup, additionalInfos);
            }
            return updateCU(articleNew, article);
        }

        private String[] updateCU(GenericUtuItem processedItem, GenericUtuItem replacedItem) throws IOException, AdminRequiredException, SclassUnknownException {
            try {
                // check for admin
                if (getCurrentUser() == null || !getCurrentUser().isAdmin())
                    throw new AdminRequiredException();
                // check for sclass
                if (lastSclass == null)
                    throw new SclassUnknownException();

                // TODO allow multiple actions at once
                // operationListeners.startOperation(new LoggingInOperation());

                boolean createMode = false;
                if (processedItem.getId() == -1)
                    createMode = true;

                URL url = new URL(baseUrl + "/api/save");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("POST");

                FormData data = processedItem.getFormData();
                data.put("type", processedItem.getTypeString());
                data.put("exists", processedItem.getId() != -1);
                data.put("sclass_id", lastSclass.getId());

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(HTTPUtil.getPostDataString(data));
                writer.flush();
                writer.close();
                outputStream.close();

                Element rootElement = XMLUtil.parseXml(connection.getInputStream());
                if (rootElement.getElementsByTagName("id").getLength() > 0) {
                    // server returned item - success
                    // update local data
                    try {
                        if (createMode) {
                            if (processedItem instanceof Event) {
                                eventsList.add(parseXMLEvent(rootElement));
                            } else if (processedItem instanceof Exam) {
                                examsList.add(parseXMLExam(rootElement));
                            } else if (processedItem instanceof Task) {
                                tasksList.add(parseXMLTask(rootElement));
                            } else if (processedItem instanceof Article) {
                                articlesList.add(parseXMLArticle(rootElement));
                            }
                        } else {
                            if (processedItem instanceof Event) {
                                eventsList.remove(replacedItem);
                                eventsList.add(parseXMLEvent(rootElement));
                            } else if (processedItem instanceof Exam) {
                                examsList.remove(replacedItem);
                                examsList.add(parseXMLExam(rootElement));
                            } else if (processedItem instanceof Task) {
                                tasksList.remove(replacedItem);
                                tasksList.add(parseXMLTask(rootElement));
                            } else if (processedItem instanceof Article) {
                                articlesList.remove(replacedItem);
                                articlesList.add(parseXMLArticle(rootElement));
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                } else {
                    final ArrayList<String> errors = new ArrayList<>();
                    // server returned errors
                    XMLUtil.forEachElement(rootElement.getElementsByTagName("error"), new Action<Element>() {
                        @Override
                        public void accept(Element parameter) {
                            errors.add(parameter.getTextContent());
                        }
                    });
                    return (String[]) errors.toArray();
                }
            } catch (ParserConfigurationException | SAXException e) {
                e.printStackTrace();
                return new String[]{"Out of date application, please update"};
            }
        }
    }

    private class Predata {

        private boolean loaded;
        private List<Sclass> sclassesList;
        private List<GroupCategory> groupCategoriesList;
        private List<Subject> subjectsList;
        private List<Sgroup> sgroupsList;
        private List<Teacher> teachersList;

        private Predata() {
            sclassesList = new ArrayList<>();
            groupCategoriesList = new ArrayList<>();
            subjectsList = new ArrayList<>();
            sgroupsList = new ArrayList<>();
            teachersList = new ArrayList<>();
            loaded = false;
        }

        private void load() throws IOException, SAXException, NumberFormatException {
            try {
                operationListeners.startOperation(new PredataOperation());
                InputStream responseStream = HTTPUtil.openStream(baseUrl + "/api/pre_data");
                Element utuElement = XMLUtil.parseXml(responseStream);

                // sclasses
                final NodeList sclasses = XMLUtil.getNodeList(utuElement, "sclasses", "sclass");
                sclassesList.clear();
                XMLUtil.forEachElement(sclasses, new Action<Element>() {
                    @Override
                    public void accept(Element parameter) {
                        int id = XMLUtil.getAndParseIntValueOfChild(parameter, "id");
                        String name = XMLUtil.getValueOfChild(parameter, "name");
                        final List<ClassMember> classMembers = new ArrayList<>();
                        XMLUtil.forEachElement(XMLUtil.getElement(parameter, "class_members").getElementsByTagName("class_member"), new Action<Element>() {
                            @Override
                            public void accept(Element parameter) {
                                int id = XMLUtil.getAndParseIntValueOfChild(parameter, "id");
                                String firstName = XMLUtil.getValueOfChild(parameter, "first_name");
                                String lastName = XMLUtil.getValueOfChild(parameter, "last_name");
                                classMembers.add(new ClassMember(id, firstName, lastName));
                            }
                        });
                        sclassesList.add(new Sclass(id, name, classMembers));
                    }
                });

                // group categories
                final NodeList groupCategories = XMLUtil.getNodeList(utuElement, "group_categories", "group_category");
                sgroupsList.clear();
                groupCategoriesList.clear();
                XMLUtil.forEachElement(groupCategories, new Action<Element>() {
                    @Override
                    public void accept(Element parameter) {
                        int id = XMLUtil.getAndParseIntValueOfChild(parameter, "id");
                        String name = XMLUtil.getValueOfChild(parameter, "name");
                        final List<Sgroup> sgroups = new ArrayList<>();
                        XMLUtil.forEachElement(XMLUtil.getElement(parameter, "sgroups").getElementsByTagName("sgroup"), new Action<Element>() {
                            @Override
                            public void accept(Element parameter) {
                                int id = XMLUtil.getAndParseIntValueOfChild(parameter, "id");
                                String name = XMLUtil.getValueOfChild(parameter, "name");
                                Sgroup sgroup = new Sgroup(id, name);
                                sgroups.add(sgroup);
                                sgroupsList.add(sgroup);
                            }
                        });
                        groupCategoriesList.add(new GroupCategory(id, name, sgroups));
                    }
                });

                // add the "all" group
                sgroupsList.add(new Sgroup(-1, "zobrazit pro všechny"));

                // subjects
                final NodeList subjects = XMLUtil.getNodeList(utuElement, "subjects", "subject");
                subjectsList.clear();
                XMLUtil.forEachElement(subjects, new Action<Element>() {
                    @Override
                    public void accept(Element parameter) {
                        int id = XMLUtil.getAndParseIntValueOfChild(parameter, "id");
                        String name = XMLUtil.getValueOfChild(parameter, "name");
                        subjectsList.add(new Subject(id, name));
                    }
                });

                // teachers
                final NodeList teachers = XMLUtil.getNodeList(utuElement, "teachers", "teacher");
                teachersList.clear();
                XMLUtil.forEachElement(teachers, new Action<Element>() {
                    @Override
                    public void accept(Element parameter) {
                        int id = XMLUtil.getAndParseIntValueOfChild(parameter, "id");
                        String name = XMLUtil.getValueOfChild(parameter, "name");
                        String abbr = XMLUtil.getValueOfChild(parameter, "abbr");
                        teachersList.add(new Teacher(id, name, abbr));
                    }
                });

                // completed
                loaded = true;
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } finally {
                operationListeners.endOperation();
            }
        }

        public boolean isLoaded() {
            return loaded;
        }
    }
}
