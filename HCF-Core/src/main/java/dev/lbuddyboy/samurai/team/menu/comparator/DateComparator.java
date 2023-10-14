package dev.lbuddyboy.samurai.team.menu.comparator;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.Comparator;

public class DateComparator implements Comparator<DBObject> {

    @Override
    public int compare(DBObject o1, DBObject o2) {
        return ((BasicDBObject)o1).getDate("time").compareTo(((BasicDBObject)o2).getDate("time"));
    }
}
