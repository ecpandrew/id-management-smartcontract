package org.hyperledger.fabric.samples.assettransfer;

import java.util.Calendar;
import java.util.Date;

public class Utils {




    static String[] getIssueAndExpiracyDate(int amount){
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.YEAR, amount); // to get previous year add 1
        Date expiryDate = cal.getTime();

        return new String[] {today.toString(), expiryDate.toString()};

    }

}
