package com.farast.utuapi.data;

import com.farast.utuapi.data.common.UtuType;
import com.farast.utuapi.data.interfaces.TEItem;
import com.farast.utuapi.data.interfaces.Updatable;
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
    private Notifier notifier;

    private List<AdditionalInfo> additionalInfosList;
    private List<Event> eventsList;
    private List<Exam> examsList;
    private List<Task> tasksList;
    private List<Article> articlesList;
    private List<Timetable> timetablesList;
    private List<Lesson> lessonsList;
    private List<PlannedRakingList> plannedRakingsListsList;
    private List<PlannedRakingRound> plannedRakingRoundsList;
    private List<PlannedRakingEntry> plannedRakingEntriesList;
    private List<Service> servicesList;

    private Service currentService;

    private Comparator<TEItem> tesComparator;
    private Comparator<Event> eventsComparator;
    private Comparator<Article> articlesComparator;
    private Comparator<PlannedRakingEntry> plannedRakingEntriesComparator;

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
        plannedRakingsListsList = new ArrayList<>();
        plannedRakingRoundsList = new ArrayList<>();
        plannedRakingEntriesList = new ArrayList<>();
        servicesList = new ArrayList<>();

        tesComparator = new Comparator<TEItem>() {
            @Override
            public int compare(TEItem o1, TEItem o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        };
        eventsComparator = new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                return o1.getStart().compareTo(o2.getStart());
            }
        };
        articlesComparator = new Comparator<Article>() {
            @Override
            public int compare(Article o1, Article o2) {
                if (!o1.isPublished())
                    return -1;
                if (!o2.isPublished())
                    return 1;
                return o2.getPublishedOn().compareTo(o1.getPublishedOn());
            }
        };
        plannedRakingEntriesComparator = new Comparator<PlannedRakingEntry>() {
            @Override
            public int compare(PlannedRakingEntry o1, PlannedRakingEntry o2) {
                if (o1.isFinished())
                    return -1;
                else if (o2.isFinished())
                    return 1;
                else
                    return 0;
            }
        };

        predata = new Predata();
        editor = new CreateUpdate();
        notifier = new Notifier();

        // automatically save cookies
        CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
    }

    public Object findUtuItem(int id, UtuType itemType) {
        switch (itemType) {
            case ADDITIONAL_INFO:
                return CollectionUtil.findById(additionalInfosList, id);
            case EVENT:
                return CollectionUtil.findById(eventsList, id);
            case TASK:
                return CollectionUtil.findById(tasksList, id);
            case EXAM:
                return CollectionUtil.findById(examsList, id);
            case ARTICLE:
                return CollectionUtil.findById(articlesList, id);
            case SUBJECT:
                return CollectionUtil.findById(predata.subjectsList, id);
            default:
                throw new UnsupportedOperationException("Unable to find this type of item");
        }
    }

    public void loadPredata() throws IOException, SAXException, NumberFormatException {
        predata.load();
    }

    public boolean login(String email, String password) throws IOException {
        if (!predata.isLoaded()) {
            throw new PredataNotLoadedException();
        }
        Operation operation = new LoggingInOperation();
        try {
            operationListeners.startOperation(operation);
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
            String emailUser = XMLUtil.getValueOfChild(utuElement, "email");
            int classMemberId = XMLUtil.getAndParseIntValueOfChild(utuElement, "class_member_id");
            ClassMember classMember = CollectionUtil.findById(predata.classMembersList, classMemberId);
            currentUser = new User(userId, admin, sclassId, emailUser, classMember);
            return true;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return false;
        } catch (SAXException e) {
            e.printStackTrace();
            return false;
        } finally {
            operationListeners.endOperation(operation);
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
            throw new PredataNotLoadedException();
        }
        loadTimetablesData(sclass);
        loadUtuData(sclass);
        dataLoaded = true;
    }

    private void loadTimetablesData(Sclass sclass) throws IOException, SAXException {
        Operation operation = new TimetablesDataOperation();
        try {
            operationListeners.startOperation(operation);
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

                                        Subject subject;
                                        int subjectId = XMLUtil.getAndParseIntValueOfChild(parameter, "subject_id");
                                        if (subjectId == -1)
                                            subject = null;
                                        else
                                            subject = CollectionUtil.findById(predata.subjectsList, subjectId);

                                        Teacher teacher;
                                        int teacherId = XMLUtil.getAndParseIntValueOfChild(parameter, "teacher_id");
                                        if (teacherId == -1)
                                            teacher = null;
                                        else
                                            teacher = CollectionUtil.findById(predata.teachersList, teacherId);
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
            notifier.notifyTimetables();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } finally {
            operationListeners.endOperation(operation);
        }
    }

    private void loadUtuData(Sclass sclass) throws IOException, SAXException {
        Operation operation = new UtuDataOperation();
        try {
            operationListeners.startOperation(operation);
            InputStream responseStream = HTTPUtil.openStream(baseUrl + "/api/data?sclass_id=" + sclass.getId());

            Element utuElement = XMLUtil.parseXml(responseStream);

            // services
            final NodeList services = XMLUtil.getNodeList(utuElement, "services", "service");
            servicesList.clear();
            XMLUtil.forEachElement(services, new Action<Element>() {
                @Override
                public void accept(Element parameter) {
                    try {
                        int id = XMLUtil.getAndParseIntValueOfChild(parameter, "id");
                        Date start = XMLUtil.getAndParseDateValueOfChild(parameter, "service_start");
                        Date end = XMLUtil.getAndParseDateValueOfChild(parameter, "service_end");
                        ClassMember first = CollectionUtil.findById(predata.classMembersList, XMLUtil.getAndParseIntValueOfChild(parameter, "first_mate_id"));
                        ClassMember second = CollectionUtil.findById(predata.classMembersList, XMLUtil.getAndParseIntValueOfChild(parameter, "second_mate_id"));
                        servicesList.add(new Service(id, start, end, first, second));
                    } catch (ParseException e) {
                        throw new DateFormatException(e);
                    }
                }
            });

            // current service
            Element currentServiceElement = XMLUtil.getElement(utuElement, "current_service");
            currentService = null;
            if (XMLUtil.exists(currentServiceElement, "id")) {
                currentService = CollectionUtil.findById(servicesList, XMLUtil.getAndParseIntValueOfChild(utuElement, "id"));
            }

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
            notifier.notifyEvents();

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
            notifier.notifyExams();

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
            notifier.notifyTasks();

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
            notifier.notifyArticles();

            // planned raking lists
            final NodeList plannedRakingLists = XMLUtil.getNodeList(utuElement, "planned_raking_lists", "planned_raking_list");
            plannedRakingsListsList.clear();
            plannedRakingRoundsList.clear();
            plannedRakingEntriesList.clear();
            XMLUtil.forEachElement(plannedRakingLists, new Action<Element>() {
                @Override
                public void accept(Element parameter) {
                    int id = XMLUtil.getAndParseIntValueOfChild(parameter, "id");
                    String title = XMLUtil.getValueOfChild(parameter, "title");
                    Subject subject = CollectionUtil.findById(predata.subjectsList, XMLUtil.getAndParseIntValueOfChild(parameter, "subject_id"));
                    Sgroup sgroup = CollectionUtil.findById(predata.sgroupsList, XMLUtil.getAndParseIntValueOfChild(parameter, "sgroup_id"));
                    int rektPerRound = XMLUtil.getAndParseIntValueOfChild(parameter, "rekt_per_round");
                    List<AdditionalInfo> additionalInfos = parseXMLAdditionalInfos(parameter);

                    final List<PlannedRakingRound> plannedRakingRounds = new ArrayList<PlannedRakingRound>();
                    XMLUtil.forEachElement(XMLUtil.getNodeList(parameter, "planned_raking_rounds", "planned_raking_round"), new Action<Element>() {
                        @Override
                        public void accept(Element parameter) {
                            int id = XMLUtil.getAndParseIntValueOfChild(parameter, "id");
                            int roundNumber = XMLUtil.getAndParseIntValueOfChild(parameter, "number");

                            final List<PlannedRakingEntry> plannedRakingEntries = new ArrayList<>();
                            XMLUtil.forEachElement(XMLUtil.getNodeList(parameter, "planned_raking_entries", "planned_raking_entry"), new Action<Element>() {
                                @Override
                                public void accept(Element parameter) {
                                    int id = XMLUtil.getAndParseIntValueOfChild(parameter, "id");
                                    String description = XMLUtil.getValueOfChild(parameter, "description");
                                    boolean finished = XMLUtil.getAndParseBooleanValueOfChild(parameter, "finished");
                                    String grade = XMLUtil.getValueOfChild(parameter, "grade");
                                    int sortingOrder = XMLUtil.getAndParseIntValueOfChild(parameter, "sorting_order");
                                    ClassMember classMember = CollectionUtil.findById(predata.classMembersList, XMLUtil.getAndParseIntValueOfChild(parameter, "class_member_id"));
                                    PlannedRakingEntry plannedRakingEntry = new PlannedRakingEntry(id, description, finished, grade, sortingOrder, classMember);
                                    plannedRakingEntriesList.add(plannedRakingEntry);
                                    plannedRakingEntries.add(plannedRakingEntry);
                                }
                            });
                            Collections.sort(plannedRakingEntries, plannedRakingEntriesComparator);
                            PlannedRakingRound plannedRakingRound = new PlannedRakingRound(id, roundNumber, plannedRakingEntries);
                            plannedRakingRoundsList.add(plannedRakingRound);
                            plannedRakingRounds.add(plannedRakingRound);
                        }
                    });
                    plannedRakingsListsList.add(new PlannedRakingList(id, title, subject, sgroup, rektPerRound, additionalInfos, plannedRakingRounds));
                }
            });
            notifier.notifyRakings();

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } finally {
            operationListeners.endOperation(operation);
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
        List<AdditionalInfo> additionalInfos = parseXMLAdditionalInfos(parameter);
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

        List<Integer> lessonIds = ArrayUtil.parseIntArray(XMLUtil.getValueOfChild(parameter, "lesson_ids"));
        List<Lesson> lessons = CollectionUtil.findByIds(lessonsList, lessonIds);

        List<AdditionalInfo> additionalInfos = parseXMLAdditionalInfos(parameter);
        boolean done = XMLUtil.getAndParseBooleanValueOfChild(parameter, "done");
        String type = XMLUtil.getValueOfChild(parameter, "type");
        Exam.Type realType;
        if (type.equals("written_exam"))
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

        List<Integer> lessonIds = ArrayUtil.parseIntArray(XMLUtil.getValueOfChild(parameter, "lesson_ids"));
        List<Lesson> lessons = CollectionUtil.findByIds(lessonsList, lessonIds);

        List<AdditionalInfo> additionalInfos = parseXMLAdditionalInfos(parameter);
        boolean done = XMLUtil.getAndParseBooleanValueOfChild(parameter, "done");
        return new Task(id, title, description, date, subject, sgroup, additionalInfos, done, lessons);
    }

    private Article parseXMLArticle(Element parameter) throws ParseException {
        int id = XMLUtil.getAndParseIntValueOfChild(parameter, "id");
        String title = XMLUtil.getValueOfChild(parameter, "title");
        String description = XMLUtil.getValueOfChild(parameter, "description");
        Date publishedOn;
        if (XMLUtil.existsAndNotEmpty(parameter, "published_on"))
            publishedOn = XMLUtil.getAndParseDateTimeValueOfChild(parameter, "published_on");
        else
            publishedOn = null;
        Date showInDetailsUntil;
        if (XMLUtil.existsAndNotEmpty(parameter, "show_in_details_until"))
            showInDetailsUntil = XMLUtil.getAndParseDateTimeValueOfChild(parameter, "show_in_details_until");
        else
            showInDetailsUntil = null;
        final int sgroupId = XMLUtil.getAndParseIntValueOfChild(XMLUtil.getElement(parameter, "sgroup"), "id");
        Sgroup sgroup = CollectionUtil.findById(predata.sgroupsList, sgroupId);
        List<AdditionalInfo> additionalInfos = parseXMLAdditionalInfos(parameter);
        return new Article(id, title, description, publishedOn, showInDetailsUntil, sgroup, additionalInfos);
    }

    private List<AdditionalInfo> parseXMLAdditionalInfos(Element parentItemRoot) {
        final List<Integer> additionalInfoIds = new ArrayList<>();
        XMLUtil.forEachElement(XMLUtil.getNodeList(parentItemRoot, "additional_infos", "additional_info"), new Action<Element>() {
            @Override
            public void accept(Element parameter) {
                additionalInfoIds.add(XMLUtil.getAndParseIntValueOfChild(parameter, "id"));
            }
        });
        return CollectionUtil.findByIds(additionalInfosList, additionalInfoIds);
    }

    public Service getCurrentService() {
        return currentService;
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
        Collections.sort(teItems, tesComparator);
        return teItems;
    }

    public List<Article> getArticlesList() {
        return new ArrayList<>(articlesList);
    }

    public List<Sgroup> getSgroupsList() {
        return new ArrayList<>(predata.sgroupsList);
    }

    public List<PlannedRakingList> getPlannedRakingsListsList() {
        return new ArrayList<>(plannedRakingsListsList);
    }

    public List<PlannedRakingRound> getPlannedRakingRoundsList() {
        return new ArrayList<>(plannedRakingRoundsList);
    }

    public List<PlannedRakingEntry> getPlannedRakingEntriesList() {
        return new ArrayList<>(plannedRakingEntriesList);
    }

    public List<Service> getServices() {
        return new ArrayList<>(servicesList);
    }

    public Sgroup getAllSgroup() {
        return predata.allSgroup;
    }

    public boolean isPredataLoaded() {
        return predata.isLoaded();
    }

    public boolean isAdminLoggedIn() {
        return (currentUser != null && currentUser.isAdmin());
    }

    public OperationManager getOperationManager() {
        return operationListeners;
    }

    public boolean isLoaded() {
        return dataLoaded;
    }

    public Sclass getLastSclass() {
        return lastSclass;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public CreateUpdate getEditor() {
        return editor;
    }

    public Notifier getNotifier() {
        return notifier;
    }

    public interface OnDataSetListener {
        void onDataSetChanged();
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

        public String[] requestCUArticle(Article article, String title, String description, Date publishedOn, Date showInDetailsUntil, Sgroup sgroup, List<AdditionalInfo> additionalInfos) throws AdminRequiredException, IOException, SclassUnknownException {
            Article articleNew;
            if (article == null) {
                articleNew = new Article(-1, title, description, publishedOn, showInDetailsUntil, sgroup, additionalInfos);
            } else {
                articleNew = new Article(article.getId(), title, description, publishedOn, showInDetailsUntil, sgroup, additionalInfos);
            }
            return updateCU(articleNew, article);
        }

        private String[] updateCU(Updatable processedItem, Updatable replacedItem) throws IOException, AdminRequiredException, SclassUnknownException {
            Operation operation = new CUOperation(processedItem.getUtuType(), replacedItem);
            try {
                operationListeners.startOperation(operation);

                // check for admin
                if (getCurrentUser() == null || !getCurrentUser().isAdmin())
                    throw new AdminRequiredException();
                // check for sclass
                if (lastSclass == null)
                    throw new SclassUnknownException();

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
                                notifier.notifyEvents();
                            } else if (processedItem instanceof Exam) {
                                examsList.add(parseXMLExam(rootElement));
                                notifier.notifyExams();
                            } else if (processedItem instanceof Task) {
                                tasksList.add(parseXMLTask(rootElement));
                                notifier.notifyTasks();
                            } else if (processedItem instanceof Article) {
                                articlesList.add(parseXMLArticle(rootElement));
                                notifier.notifyArticles();
                            }
                        } else {
                            if (processedItem instanceof Event) {
                                eventsList.remove(replacedItem);
                                eventsList.add(parseXMLEvent(rootElement));
                                notifier.notifyEvents();
                            } else if (processedItem instanceof Exam) {
                                examsList.remove(replacedItem);
                                examsList.add(parseXMLExam(rootElement));
                                notifier.notifyExams();
                            } else if (processedItem instanceof Task) {
                                tasksList.remove(replacedItem);
                                tasksList.add(parseXMLTask(rootElement));
                                notifier.notifyTasks();
                            } else if (processedItem instanceof Article) {
                                articlesList.remove(replacedItem);
                                articlesList.add(parseXMLArticle(rootElement));
                                notifier.notifyArticles();
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                } else {
                    return parseErrorsFromXML(rootElement);
                }
            } catch (ParserConfigurationException | SAXException e) {
                e.printStackTrace();
                return new String[]{"Either the application or the server is broken, please tell me"};
            } finally {
                operationListeners.endOperation(operation);
            }
        }

        public String[] requestDestroy(Updatable item) throws AdminRequiredException, IOException {
            Operation operation = new DestroyOperation(item);
            try {
                operationListeners.startOperation(operation);
                // check for admin
                if (getCurrentUser() == null || !getCurrentUser().isAdmin())
                    throw new AdminRequiredException();

                URL url = new URL(baseUrl + "/api/destroy");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("POST");

                FormData data = new FormData();
                data.put("id", item.getId());
                data.put("type", item.getTypeString());

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(HTTPUtil.getPostDataString(data));
                writer.flush();
                writer.close();
                outputStream.close();

                Element rootElement = XMLUtil.parseXml(connection.getInputStream());
                if (rootElement.getElementsByTagName("success").getLength() > 0) {
                    // successfully deleted
                    if (item instanceof Event) {
                        eventsList.remove(item);
                        notifier.notifyEvents();
                    } else if (item instanceof Exam) {
                        examsList.remove(item);
                        notifier.notifyExams();
                    } else if (item instanceof Task) {
                        tasksList.remove(item);
                        notifier.notifyTasks();
                    } else if (item instanceof Article) {
                        articlesList.remove(item);
                        notifier.notifyArticles();
                    }
                    return null;
                } else {
                    return parseErrorsFromXML(rootElement);
                }
            } catch (ParserConfigurationException | SAXException e) {
                e.printStackTrace();
                return new String[]{"Either the application or the server is broken, please tell me"};
            } finally {
                operationListeners.endOperation(operation);
            }
        }

        private String[] parseErrorsFromXML(Element errorsElement) {
            final ArrayList<String> errors = new ArrayList<>();
            // server returned errors
            XMLUtil.forEachElement(errorsElement.getElementsByTagName("error"), new Action<Element>() {
                @Override
                public void accept(Element parameter) {
                    errors.add(parameter.getTextContent());
                }
            });
            return (String[]) errors.toArray();
        }
    }

    public class Notifier {
        OnDataSetListener eventsListener;
        OnDataSetListener examsListener;
        OnDataSetListener tasksListener;
        OnDataSetListener articlesListener;
        OnDataSetListener rakingsListener;
        OnDataSetListener timetablesListener;
        OnDataSetListener anyDataListener;

        private void notifyRakings() {
            tryNotify(rakingsListener);
            tryNotify(anyDataListener);
        }

        private void notifyTimetables() {
            tryNotify(timetablesListener);
            tryNotify(anyDataListener);
        }

        private void notifyEvents() {
            Collections.sort(eventsList, eventsComparator);
            tryNotify(eventsListener);
            tryNotify(anyDataListener);
        }

        private void notifyExams() {
            Collections.sort(examsList, tesComparator);
            tryNotify(examsListener);
            tryNotify(anyDataListener);
        }

        private void notifyTasks() {
            Collections.sort(tasksList, tesComparator);
            tryNotify(tasksListener);
            tryNotify(anyDataListener);
        }

        private void notifyArticles() {
            Collections.sort(articlesList, articlesComparator);
            tryNotify(articlesListener);
            tryNotify(anyDataListener);
        }

        private void tryNotify(OnDataSetListener listener) {
            if (listener != null)
                listener.onDataSetChanged();
        }

        public void setEventsListener(OnDataSetListener eventsListener) {
            this.eventsListener = eventsListener;
        }

        public void setExamsListener(OnDataSetListener examsListener) {
            this.examsListener = examsListener;
        }

        public void setTasksListener(OnDataSetListener tasksListener) {
            this.tasksListener = tasksListener;
        }

        public void setArticlesListener(OnDataSetListener articlesListener) {
            this.articlesListener = articlesListener;
        }

        public void setRakingsListener(OnDataSetListener rakingsListener) {
            this.rakingsListener = rakingsListener;
        }

        public void setTimetablesListener(OnDataSetListener timetablesListener) {
            this.timetablesListener = timetablesListener;
        }

        public void setAnyDataListener(OnDataSetListener anyDataListener) {
            this.anyDataListener = anyDataListener;
        }
    }

    private class Predata {

        private Sgroup allSgroup;
        private boolean loaded;
        private List<Sclass> sclassesList;
        private List<GroupCategory> groupCategoriesList;
        private List<Subject> subjectsList;
        private List<Sgroup> sgroupsList;
        private List<Teacher> teachersList;
        private List<ClassMember> classMembersList;

        private Predata() {
            sclassesList = new ArrayList<>();
            groupCategoriesList = new ArrayList<>();
            subjectsList = new ArrayList<>();
            sgroupsList = new ArrayList<>();
            teachersList = new ArrayList<>();
            classMembersList = new ArrayList<>();
            loaded = false;
        }

        private void load() throws IOException, SAXException, NumberFormatException {
            Operation operation = new PredataOperation();
            try {
                operationListeners.startOperation(operation);
                InputStream responseStream = HTTPUtil.openStream(baseUrl + "/api/pre_data");
                Element utuElement = XMLUtil.parseXml(responseStream);

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
                allSgroup = new Sgroup(-1, "zobrazit pro všechny");
                sgroupsList.add(allSgroup);

                // sclasses
                final NodeList sclasses = XMLUtil.getNodeList(utuElement, "sclasses", "sclass");
                classMembersList.clear();
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
                                List<Sgroup> sgroups = CollectionUtil.findByIds(sgroupsList, ArrayUtil.parseIntArray(XMLUtil.getValueOfChild(parameter, "sgroup_ids")));
                                ClassMember classMember = new ClassMember(id, firstName, lastName, sgroups);
                                classMembers.add(classMember);
                                classMembersList.add(classMember);
                            }
                        });
                        sclassesList.add(new Sclass(id, name, classMembers));
                    }
                });

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
                operationListeners.endOperation(operation);
            }
        }

        public boolean isLoaded() {
            return loaded;
        }
    }
}
