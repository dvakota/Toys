package dvakota.toys.todolist;

import java.util.*;

/**
 * Date: 6/18/15
 */
public class TaskMaster {
    static final char CATEGORY = 'c';
    static final char TASK = 't';

    Map<Category, List<Task>> category; //row of Tasks for each category
    Map<Task, List<Category>> tasks;

    public TaskMaster() {
        category = new HashMap<Category,List<Task>>();
        tasks = new HashMap<Task, List<Category>>();
    }



    public void addTask(String name, String...categories) {
        Task t = new Task(name);

        //adding an "orphan" task
        if (categories == null || categories.length == 0) {
            //of the specified task already exists, no changes are made
            if (tasks.get(t) == null) tasks.put(t, new ArrayList<Category>());
            say("Task <" + name + " added without a category");
            return;
        }

        Map<String, Category> addToCategories = new HashMap<String, Category>();

        //adding to all categories
        if (categories.length == 1 && categories[0].equals("ALL")) {
            for(Category c : category.keySet()) {
                addToCategories.put(c.name, c);
            }
            //otherwise add task to each spcified category
        } else {
            for (String cname : categories) {
                addToCategories.put(cname, new Category(cname));
            }
        }

        for (String cname : categories) {
            Category cat = addToCategories.get(cname);
            //updating task mapping: create new category if didn't exist
            List<Category> clist = tasks.get(t);
            if (clist == null) clist = new ArrayList<Category>();
            if (!clist.contains(cat)) clist.add(cat);
            tasks.put(t, clist);

            //updating category mapping
            List<Task> tlist = category.get(cat);
            if (tlist == null) tlist = new ArrayList<Task>();
            if (!tlist.contains(t)) tlist.add(t);
            category.put(cat, tlist);
        }
    }

    /* Removes task by name; will be removed from all categories */
    public void removeTaskByName(String name) {
        Task toRemove = new Task(name);
        List<Category> removeFrom = tasks.get(toRemove);
        if (removeFrom == null) {
            say("No tasks named <" + name + "> was found");
            return;
        }
        say("Removing task <" + name + "> :");
        for (Category cat : removeFrom) {
            List<Task> tlist = category.get(cat);
            if (tlist != null) {
                tlist.remove(toRemove);
                say("\tfrom <" + cat.name + ">");
            }
        }
        tasks.remove(toRemove);
    }

    /* Removes task from a specific category (or more) */
    public void removeTaskByCategory(String name, String...categories) {
        Task toRemove = new Task(name);
        //all categories to which a task belongs:
        List<Category> fromCategoryList = tasks.get(toRemove);
        if (fromCategoryList == null) {
            say("Task <"+ name + "> was not found in any categories");
            return;
        }
        say("Removing task <" + name + ">:");
        for(String cname : categories) {
            Category c = new Category(cname);
            //all tasks that belong to the category
            List<Task> fromTaskList = category.get(c);

            //this task no longer points to this category
            fromTaskList.remove(toRemove);
            //this category no longer points to this tast
            fromCategoryList.remove(c);
        }
    }

    public void showCatIntersection(String...names) throws Exception {
        showInter(names, CATEGORY);
    }

    public void showTaskIntersection(String...names) throws Exception {
        showInter(names, TASK);
    }

    public void showIntersection1(String...cnames) {
        Map<Task, Integer> common = new HashMap<Task, Integer>();
        for (String cname : cnames) {
            Category cat = new Category(cname);
            List<Task> tlist = category.get(cat);
            if (tlist == null) continue;
            for (Task task : tlist) {
                Integer count = common.get(task);
                if (count == null) count = 0;
                common.put(task, ++count);
                //debug
                //say("Task " + task.name.toUpperCase() + " found in category " + cname.toUpperCase());
            }
        }
        showCommon(cnames, common);
    }

    public void showTaskIntersection1(String...tnames) {
        Map<Category, Integer> common = new HashMap<Category, Integer>();
        for (String tname : tnames) {
            Task task = new Task(tname);
            List<Category> clist = tasks.get(task);
            if (clist == null) continue;
            for (Category cat : clist) {
                Integer count = common.get(cat);
                if (count == null) count = 0;
                common.put(cat, ++count);
                //debug
               // say("Category " + cat.name.toUpperCase() + " found for task " + tname.toUpperCase());
            }
        }
        showCommon(tnames, common);
    }

    private <T> void showInter (String[] tnames, char what) throws Exception {
        Map<?,?> from = what == CATEGORY ? category : tasks;
        String className = what == CATEGORY ? "Category" : "Task";
        Map<T, Integer> common = new HashMap<T, Integer>();
        for (String tname : tnames) {
            Object item = Class.forName(className).newInstance();
            List<?> list = (List<?>) from.get(item);
            if (list == null) continue;
            for (Object o : list) {
                Integer count = common.get(o);
                if (count == null) count = 0;
                common.put((T) o, ++count);
                //debug
                say("Category " + o.toString().toUpperCase() + " found for task " + tname.toUpperCase());
            }
        }
        showCommon(tnames, common);
    }


    /* Removes an entire category. Orphan tasks may remain in the Tasks map */
    public void removeCategory(String cname) {
        Category cat = new Category(cname);
        say("Removing category <" + cname + ">");
        List<Task> tlist = category.get(cat);

        //first, remove this category from task mappings
        for (Task task : tlist) {
            List<Category> clist = tasks.get(task);
            clist.remove(cat);
        }

        //delete category and its task mappings
        category.remove(cat);
    }


    private <T> void showCommon(String[] names, Map<T, Integer> common) {
        say("Common entries for: ");
        for (String s : names) {
            say("\t" + s);
        }
        say("----------------------------");
        boolean hascommon = false;
        for (T item : common.keySet()) {
            if (common.get(item) == names.length) {
                hascommon = true;
                say("\t\t<" + item.toString() + ">");
            }
        }
        if (!hascommon) say("No common entries were found");
    }

    public void showAll(char groupBy, String...params) {
        switch (groupBy) {
            case (CATEGORY) :
                say("View records by category");
                showAll(category, params);
                break;
            case (TASK):
                say("View records by task");
                showAll(tasks, params);
                break;
        }
    }

    private void showAll(Map<?,?> from, String[] params) {
        for (Object o : from.keySet()) {
            if (params == null || params.length == 0
            || contains(o.toString(), params)) {
                say(o.toString());
                List<?> list = (List) from.get(o);
                if (list == null) continue;
                for (Object oo : list) {
                    say("\t" + oo.toString());
                }
            }
        }
    }

    private boolean contains(String s, String[] arr) {
        for (String as : arr) {
            if (as.equals(s)) return true;
        }
        return false;
    }

    private static void say(Object o) {
        System.out.println(o);
    }

    public static void main(String[] args) throws Exception {
        String t1 = "Kill self";
        String t2 = "Buy milk";
        String t3 = "Update facebook";

        String c1 = "General";
        String c2 = "Shopping";
        String c3 = "Work";

        TaskMaster tm = new TaskMaster();
        tm.addTask(t1, c1);
        tm.addTask(t1, c1, c2);
        tm.addTask(t2, c2);
        tm.addTask(t3, c3, c1);

        tm.showAll(CATEGORY);
        tm.showCatIntersection(c1, c2);
        tm.showTaskIntersection(t1);

    }

    private boolean remove(List<Task> tlist, Task ttr, List<Category> clist,
                           Category ctr) {
        return ((tlist.remove(ttr) && clist.remove(ctr)) ||
                            (!tlist.remove(ttr) && !clist.remove(ctr)));
    }

}

class Task {
    String name;
    long forTime;

    public Task(String name) {
        this.name = name;
    }

    public Task(String name, long forTime) {
        this(name);
        this.forTime = forTime;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Task)) return false;
        Task t = (Task) o;
        return (t.name.equals(name));
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}


class Category {
    String name;
    public Category(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        Category c = (Category) o;
        return (name.equals(c.name));
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}