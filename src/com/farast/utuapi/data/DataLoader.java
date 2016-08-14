package com.farast.utuapi.data;

import com.farast.utuapi.util.*;
import com.farast.utuapi.util.functional_interfaces.Action;
import com.farast.utuapi.util.operations.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;

/**
 * Created by cendr_000 on 25.07.2016.
 */
public class DataLoader {

    private String baseUrl;

    private Sclass lastSclassLoaded;

    private OperationManager operationListeners = new OperationManager();

    private Predata predata;
    private List<AdditionalInfo> additionalInfosList;
    private List<Event> eventsList;
    private List<Exam> examsList;
    private List<Task> tasksList;
    private List<Article> articlesList;
    private List<Timetable> timetablesList;
    private List<Lesson> lessonsList;

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
    }

    public void loadPredata() throws IOException, SAXException, NumberFormatException {
        predata.load();
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
        lastSclassLoaded = null;
        if (!predata.isLoaded()) {
            predata.load();
        }
        loadTimetablesData(sclass);
        loadUtuData(sclass);
        lastSclassLoaded = sclass;
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
            operationListeners.endOperation();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void loadUtuData(Sclass sclass) throws IOException, SAXException {
        try {
            operationListeners.startOperation(new UtuDataOperation());
            InputStream responseStream = HTTPUtil.openStream(baseUrl + "/api/data?sclass_id=" + sclass.getId());
            Element utuElement = XMLUtil.parseXml(responseStream);

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
                        eventsList.add(new Event(id, title, description, location, price, start, end, payDate, sgroup, additionalInfos, done));
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
                        examsList.add(new Exam(id, title, description, date, subject, sgroup, additionalInfos, done, lessons));
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
                        tasksList.add(new Task(id, title, description, date, subject, sgroup, additionalInfos, done, lessons));
                    } catch (ParseException e) {
                        operationListeners.endOperation();
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
                        articlesList.add(new Article(id, title, description, publishedOn, sgroup, additionalInfos));
                    } catch (ParseException e) {
                        throw new DateFormatException(e);
                    }
                }
            });
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        finally {
            operationListeners.endOperation();
        }
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

    public List<TEItem> getTEsList()
    {
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

    public boolean isPredataLoaded() {
        return predata.isLoaded();
    }

    public OperationManager getOperationManager() {
        return operationListeners;
    }

    public boolean isLoaded() {
        return lastSclassLoaded != null;
    }

    public static class PredataNotLoadedException extends RuntimeException {
        @Override
        public String getMessage() {
            return "This method cannot be called before predata is loaded";
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
            }
            finally {
                operationListeners.endOperation();
            }
        }

        public boolean isLoaded() {
            return loaded;
        }
    }
}
