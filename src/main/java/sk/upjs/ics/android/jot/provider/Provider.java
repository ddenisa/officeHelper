package sk.upjs.ics.android.jot.provider;

import android.provider.BaseColumns;


public interface Provider {

    public interface Employee extends BaseColumns {
        public static final String TABLE_NAME = "employee";

        public static final String FIRST_NAME = "first_name";

        public static final String LAST_NAME = "last_name";

        public static final String TITLE = "title";

        public static final String DPT = "department";

        public static final String MANAGER_ID = "manager_id";

        public static final String CITY = "city";

        public static final String O_PHONE = "office";

        public static final String C_PHONE = "cell";

        public static final String EMAIL = "email";

        public static final String PIC = "picture";

    }

    public interface Login extends BaseColumns {
        public static final String TABLE_NAME = "login";

        public static final String USER_NAME = "user_name";

        public static final String PASSWORD = "password";
    }

    public interface Event extends BaseColumns {
        public static final String TABLE_NAME = "event";

        public static final String DESCRIPTION = "description";

        public static final String START_MONTH = "s_month";

        public static final String START_DAY = "s_day";

        public static final String START_HOUR = "s_hour";

        public static final String END_MONTH = "e_month";

        public static final String END_DAY = "e_day";

        public static final String END_HOUR = "e_hour";

    }
}
